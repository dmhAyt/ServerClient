package com.ansheng.single;

import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.locks.ReentrantLock;

import com.ansheng.config.FileWorkerConfig;
import com.ansheng.exception.ReadTimeOutException;
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

public class HeartSingle {

    private static HeartSingle _instance = null;
    private static final ReentrantLock _objLock = new ReentrantLock();
    private static HeartManageMonitorThread _heartManage = null;
    /**
     *	 缁勯暱鎴栬�呭壇缁勯暱绠＄悊缁勫憳浠殑蹇冭烦socket
     */
    private static ConcurrentSkipListSet<HeartKVModel> _heartSocketList = new ConcurrentSkipListSet<>();
    /**
     *	 鍓粍闀跨殑蹇冭烦淇℃伅
     */
    private static ConcurrentHashMap<String,HeartBeatModel> _heartSLeaderData = new ConcurrentHashMap<>(10);
    /**
     * 	姝ｅ父缁勫憳鐨勫績璺充俊鎭�
     */
    private static ConcurrentHashMap<String,HeartBeatModel> _heartNormalData = new ConcurrentHashMap<>(50);
    /**
     * 	鍒犻櫎缁勫憳鐨勫績璺充俊鎭�
     */
    private static ConcurrentHashMap<String,HeartBeatModel> _heartDeleteData = new ConcurrentHashMap<>(50);


    private ConfigSingle config = ConfigSingle.getInstance();

    /**
     * 	鐢ㄤ簬蹇冭烦鐨剆ocket杩炴帴
     */
    private static Socket _socket = null;

    private HeartSingle() throws InterruptedException, IOException {
        // 鍒涘缓socket--缁勫憳锛岀粍闀跨殑鍖哄埆
        CreateSocketInt createSocketInt = new CreateSocketImp();
        try {
            _socket = createSocketInt.createHeartSocketClient(config.getGroupIP(), config.getGroupHeartPort());
        }catch (IOException e){
            Thread  d = new Thread(new HeartCreateThread(this));
            d.setName("鍒涘缓蹇冭烦socket");
            d.start();
        }
        // 鍓�/姝ｇ粍闀块渶瑕佸垱寤哄績璺崇鐞�
        if(config.getIdentity() == IdentifyEnum.Deputy_GROUP_LEADER || config.getIdentity() == IdentifyEnum.GROUP_LEADER){
            _heartManage = new HeartManageMonitorThread(config.getBindHeartPort());
            Thread  manage = new Thread(_heartManage);
            manage.setName("蹇冭烦绠＄悊绾跨▼");
            manage.start();
        }
    }

    /**
     *	 鑾峰緱瀵硅薄瀹炰緥
     * @return  瀵硅薄
     * @throws IOException  io寮傚父
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
     * 	褰撳績璺宠繛鎺ユ椂娣诲姞鍒扮鐞嗛泦鍚堜腑
     * @param socket 蹇冭烦杩炴帴socket
     */
    public void addHeartSocket(HeartKVModel socket){
        _heartSocketList.add(socket);
    }

    /**
     * 	鎵ц绠＄悊鎵�鏈夊績璺�--鍓�/姝ｇ粍闀挎墽琛�--姣�10绉掕皟鐢ㄦ鏂规硶
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
                String ip = currentSocket.getSocketClient().getInetAddress().getHostAddress();
                // 鏁版嵁鏄粈涔堣繕娌℃湁瀹氬ソ
                String paramStr = Tools.encodeUTF82Str(data);
                System.out.println("鏀跺埌蹇冭烦"+paramStr);
                HeartBeatModel heartBeatModel = Tools.json2Bean(paramStr,HeartBeatModel.class);
                heartBeatModel.setIP(ip);
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
            } catch (ReadTimeOutException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

        }
    }

    /**
     *	 鍙戦�佸績璺充俊鎭�--姣�30绉掕皟鐢ㄦ鏂规硶
     */
    public void sendHeart(){
        HardDiskCapacity hardDiskCapacity = new HardDiskCapacity();
        hardDiskCapacity = Tools.getDiskSpaceInfo("");
        if(config.getIdentity() == IdentifyEnum.Deputy_GROUP_LEADER || config.getIdentity() == IdentifyEnum.GROUP_LEADER ){
            // 璁＄畻涓�涓嬬粍鍛樼殑绌洪棿
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
                 *	 璁剧疆绯荤粺鍚�
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

                System.out.println("鍙戦�佸績璺�");

                SocketTools.sendData(_socket, Tools.bean2Json(beatInfo));
            }else{
                LogTools.writeInfo("蹇冭烦socket杩炴帴涓嶄笂銆傘��");
            }
        } catch (IOException e) {
            e.printStackTrace();
            LogTools.writeError(e);
        }
    }

    /**
     * 	通过key获得心跳信息
     * @param key
     * @return
     */
    public HeartBeatModel getHeartModelByKey(String key) {
    	HeartBeatModel result = null;
    	if(_heartSLeaderData.containsKey(key)) {
    		result = _heartSLeaderData.get(key);
    	}else if(_heartNormalData.containsKey(key)) {
    		result = _heartNormalData.get(key);
    	}
    	return result;
    }
    
    /**
     * 	璁＄畻姝ｅ父缁勫憳鐨勫彲鐢ㄧ┖闂�
     * @param hardDiskCapacity 纾佺洏瀹归噺
     */
    private void CalculationSize(HardDiskCapacity hardDiskCapacity) {
        // 涓嶈绠楀垹闄ょ粍鍛樼殑澶у皬锛屽壇缁勯暱甯︾殑灏辨槸缁勯暱鐨勫畬鍏ㄥ浠姐��
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
     * 	鍒ゆ柇蹇冭烦鐨剆ocket鏄惁浠嶇劧鍙敤
     * @param reconnect 涓嶅彲鐢ㄦ椂鏄惁閲嶈繛
     * @return  true鍙敤锛宖alse涓嶅彲鐢�
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
