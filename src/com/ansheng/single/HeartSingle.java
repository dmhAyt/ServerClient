package com.ansheng.single;

import com.ansheng.config.FileWorkerConfig;
import com.ansheng.factory.CreateSocketInt;
import com.ansheng.factory.SystemInfoInt;
import com.ansheng.factory.SystemPlatformFactory;
import com.ansheng.factory.impl.CreateSocketImp;
import com.ansheng.model.HeartBeatModel;
import com.ansheng.model.HeartKVModel;
import com.ansheng.model.hardware.CPUModel;
import com.ansheng.model.hardware.HardDiskCapacity;
import com.ansheng.model.hardware.MemoryModel;
import com.ansheng.model.hardware.NetWorkModel;
import com.ansheng.myenum.IdentifyEnum;
import com.ansheng.thread.heart.HeartCreateThread;
import com.ansheng.thread.heart.HeartManageMonitorThread;
import com.ansheng.util.LogTools;
import com.ansheng.util.OSInfo;
import com.ansheng.util.Tools;
import com.ansheng.util.socket.SocketTools;

import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Objects;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.*;

public class HeartSingle {

    private static HeartSingle _instance = null;
    private static final ReentrantLock _objLock = new ReentrantLock();
    private static HeartManageMonitorThread _heartManage = null;
    /**
     * 组长或者副组长管理组员们的心跳socket
     */
    private static ConcurrentSkipListSet<HeartKVModel> _heartSocketList = new ConcurrentSkipListSet<>();
    /**
     * 副组长的心跳信息
     */
    private static ConcurrentHashMap<String,HeartBeatModel> _heartSLeaderData = new ConcurrentHashMap<>(10);
    /**
     * 正常组员的心跳信息
     */
    private static ConcurrentHashMap<String,HeartBeatModel> _heartNormalData = new ConcurrentHashMap<>(50);
    /**
     * 删除组员的心跳信息
     */
    private static ConcurrentHashMap<String,HeartBeatModel> _heartDeleteData = new ConcurrentHashMap<>(50);


    private ConfigSingle config = ConfigSingle.getInstance();

    /**
     * 用于心跳的socket连接
     */
    private static Socket _socket = null;

    private HeartSingle() throws InterruptedException, IOException {
        // 创建socket--组员，组长的区别
        CreateSocketInt createSocketInt = new CreateSocketImp();
        try {
            _socket = createSocketInt.createHeartSocketClient(config.getGroupIP(), config.getGroupHeartPort());
        }catch (IOException e){
            Thread  d = new Thread(new HeartCreateThread(this));
            d.setName("创建心跳socket");
            d.start();
        }
        // 副/正组长需要创建心跳管理
        if(config.getIdentity() == IdentifyEnum.Deputy_GROUP_LEADER || config.getIdentity() == IdentifyEnum.GROUP_LEADER){
            _heartManage = new HeartManageMonitorThread(config.getBindHeartPort());
            Thread  manage = new Thread(_heartManage);
            manage.setName("心跳管理线程");
            manage.start();
        }
    }

    /**
     * 获得对象实例
     * @return  对象
     * @throws IOException  io异常
     * @throws InterruptedException --
     */
    public static HeartSingle getInstance() throws IOException, InterruptedException {
        if(_instance == null){
            if(_objLock.tryLock()) {
                if (_instance == null) {
                    _instance = new HeartSingle();
                }
                _objLock.unlock();
            }
        }
        return _instance;
    }

    /**
     * 当心跳连接时添加到管理集合中
     * @param socket 心跳连接socket
     */
    public void addHeartSocket(HeartKVModel socket){
        _heartSocketList.add(socket);
    }

    /**
     * 执行管理所有心跳--副/正组长执行--每10秒调用此方法
     */
    public void manageHeartInfo() {
        Iterator<HeartKVModel> socketIterator = _heartSocketList.iterator();
        while (socketIterator.hasNext()){
            HeartKVModel currentSocket = socketIterator.next();
            if(currentSocket.getSocketClient().isClosed() || currentSocket.getReadTime() * FileWorkerConfig.Heart_Manage_Time > 60) {
                socketIterator.remove();
                if(!currentSocket.getSocketClient().isClosed()) {
                    try {
                        currentSocket.getSocketClient().close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if(_heartSLeaderData.containsKey(currentSocket.getID())){
                    _heartSLeaderData.remove(currentSocket.getID());
                }else if(_heartNormalData.containsKey(currentSocket.getID())){
                    _heartNormalData.remove(currentSocket.getID());
                }else if(_heartDeleteData.containsKey(currentSocket.getID())){
                    _heartDeleteData.remove(currentSocket.getID());
                }
                continue;
            }
            try {
                byte[] data = SocketTools.receiveData(currentSocket.getSocketClient(),false);
                if(Objects.isNull(data)) {
                    currentSocket.setReadTime(currentSocket.getReadTime() + 1);
                    continue;
                }
                currentSocket.setReadTime(0);

                // 数据是什么还没有定好
                String paramStr = Tools.encodeUTF82Str(data);
                System.out.println("收到心跳"+paramStr);
                HeartBeatModel heartBeatModel = Tools.json2Bean(paramStr,HeartBeatModel.class);
                switch (heartBeatModel.getRole().toLowerCase()){
                    case "normal":
                        _heartNormalData.put(currentSocket.getID(),heartBeatModel);
                        break;
                    case "delete":
                        _heartDeleteData.put(currentSocket.getID(),heartBeatModel);
                        break;
                    case "s_leader":
                        _heartSLeaderData.put(currentSocket.getID(),heartBeatModel);
                        break;
                    default:
                        break;
                }

            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }

    /**
     * 发送心跳信息--每30秒调用此方法
     */
    public void sendHeart(){
        HardDiskCapacity hardDiskCapacity = new HardDiskCapacity();
        hardDiskCapacity = Tools.getDiskSpaceInfo("");
        if(config.getIdentity() == IdentifyEnum.Deputy_GROUP_LEADER || config.getIdentity() == IdentifyEnum.GROUP_LEADER ){
            // 计算一下组员的空间
            CalculationSize(hardDiskCapacity);
        }
        try {
            if(availableHeartSocket(true)) {
                String role = "";
                switch (config.getIdentity()){
                    case Deputy_GROUP_LEADER:
                      role = "S_Leader";
                      break;
                    case GROUP_LEADER:
                        role = "Leader";
                        break;
                    case GROUP_NORMAL:
                        role = "Normal";
                        break;
                    case GROUP_DELETE:
                        role = "Delete";
                        break;
                }

                HeartBeatModel beatInfo = new HeartBeatModel(config.getID(),role,config.getGroupNum(),config.getBindEventPort()
                        ,hardDiskCapacity.getTotalCapacity(),hardDiskCapacity.getUsableCapacity(),hardDiskCapacity.getAvailableCapacity());
                /**
                 * 设置系统名
                 */
                beatInfo.setSysName(OSInfo.getOSName().toString());
                beatInfo.setConnectNum(10);
                SystemInfoInt systemInfoInt =  SystemPlatformFactory.getSystemInfo();
                NetWorkModel net  = systemInfoInt.getNetWorkInfo();
                beatInfo.setNetUsed(String.join("/","",""));
                MemoryModel memoryModel = systemInfoInt.getMemoryInfo();
                beatInfo.setMemoryUsed(String.join("/","",""));
                CPUModel cpuModel = systemInfoInt.getCPUInfo();
                beatInfo.setCPUUsed(0D);

                System.out.println("发送心跳");

                SocketTools.sendData(_socket, Tools.bean2Json(beatInfo));
            }else{
                LogTools.writeInfo("心跳socket连接不上。。");
            }
        } catch (IOException e) {
            e.printStackTrace();
            LogTools.writeError(e);
        }
    }

    /**
     * 计算正常组员的可用空间
     * @param hardDiskCapacity 磁盘容量
     */
    private void CalculationSize(HardDiskCapacity hardDiskCapacity) {
        // 不计算删除组员的大小，副组长带的就是组长的完全备份。
        long totalSize = 0L;
        long totalUsedSize = 0L;
        HashMap<Integer,HeartBeatModel> groupNumMap = new HashMap<>(50);
        Iterator<HeartBeatModel> iterator = _heartNormalData.values().iterator();
        long totalFreeSize = 0L;
        while (iterator.hasNext()){
            HeartBeatModel item =  iterator.next();
            if(groupNumMap.containsKey(item.getGroupNum())){
                HeartBeatModel item2 = groupNumMap.get(new Integer(item.getGroupNum()));
                if(item.getFS() < item2.getFS()) {
                    groupNumMap.put(new Integer(item.getGroupNum()), item);
                    totalFreeSize -= item2.getFS() - item.getFS();
                }
            }else{
                groupNumMap.put(new Integer(item.getGroupNum()),item);
                totalFreeSize+= item.getFS();
                totalSize += item.getTS();
                totalUsedSize += item.getUS();
            }
        }

        hardDiskCapacity.setAvailableCapacity(hardDiskCapacity.getAvailableCapacity()+totalFreeSize);
        hardDiskCapacity.setUsableCapacity(hardDiskCapacity.getUsableCapacity()+totalUsedSize);
        hardDiskCapacity.setTotalCapacity(hardDiskCapacity.getTotalCapacity()+totalSize);
    }

    /**
     * 判断心跳的socket是否仍然可用
     * @param reconnect 不可用时是否重连
     * @return  true可用，false不可用
     */
    public boolean availableHeartSocket(boolean reconnect){
        boolean result = false;
        if(Objects.isNull(_socket)) result = false;
        else result = _socket.isConnected() && !_socket.isClosed();

        if(reconnect && !result){
            CreateSocketInt createSocketInt = new CreateSocketImp();
            try {
                _socket = createSocketInt.createSocketClient(config.getGroupIP(),config.getGroupHeartPort());
                result = _socket.isConnected() && !_socket.isClosed();
            } catch (IOException e) {
                e.printStackTrace();
                result = false;
            }
        }
        return result;
    }

    public void setHeartSocket(Socket socket){
        _socket = socket;
    }
}
