package com.ansheng;

import com.ansheng.myenum.IdentifyEnum;
import com.ansheng.single.ConfigSingle;
import com.ansheng.single.HeartSingle;
import com.ansheng.single.ThreadPoolSingle;
import com.ansheng.thread.EventManageThread;
import com.ansheng.thread.heart.HeartManageThread;
import com.ansheng.thread.heart.HeartSendThread;
import com.ansheng.util.Tools;

import java.io.IOException;
import java.util.Objects;

public class Main {
    public static void main(String[] args) {
        //args = new String[]{"l0001", "1","leader","127.0.0.1","5206", "5207", "5204" ,"5205"};

        // 拿配置文件。
        boolean configResult =  GetConfig(args);
        if(!configResult) return;
        // 连接心跳线程。
        try {
            HeartSingle.getInstance();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        } catch (InterruptedException e) {
            e.printStackTrace();
            return;
        }
        // 启动心跳管理线程
        ThreadPoolSingle.getInstance().execute(new HeartSendThread());

        ConfigSingle configSingle =ConfigSingle.getInstance();
        if(configSingle.getIdentity() == IdentifyEnum.Deputy_GROUP_LEADER
                || configSingle.getIdentity() == IdentifyEnum.GROUP_LEADER){
            ThreadPoolSingle.getInstance().execute(new HeartManageThread());
        }


        // 启动绑定线程
        Thread eventThread = new Thread(new EventManageThread());
        eventThread.setName("事务线程");
        eventThread.start();

        //功能
        //// 恢复处理
        //RecoveryServer recoveryServer = new RecoveryServer();
        //recoveryServer.workerRecovery();

        //// 分发功能
        //// 心跳功能
        //// 组员管理
        //// 文件上传下载
        //// 文件信息修改功能
        try {
            System.in.read();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static boolean GetConfig(String[] param){
        // id groupNum 身份 组长IP 组长心跳端口 组长事务端口 本机绑定的心跳端口 本机绑定的事务端口
        // n0001|d0001|l0001|s0001 1|2|3 normal|delete|leader|s_leader 127.0.0.1 5200 5201 5200 5201
        int result = 0;
        if(param.length <= 0){
            System.out.println("尝试从系统配置文件中获得绑定信息....");
            result = Tools.getConfigFromSysFile();
        }else{
            String option = param[0];
            if(Objects.equals("-p",option)){
                System.out.println("尝试从指定路径的配置文件中获得绑定信息....");

            }else if(Objects.equals("-c",option) || param.length >= 8){
                System.out.println("尝试从输入参数中获得绑定信息....");
                result= Tools.getConfigFromArgs(param,true);
            }
        }
        switch (result){
            case -1:
                System.out.println("    身份和ID不符合....");
                break;
            case -2:
                System.out.println("    未知身份....");
                break;
            case -3:
                System.out.println("    系统配置文件不存在....");
                break;
            default:
                System.out.println("    获取配置成功...");
                break;
        }
        return result == 0;
    }
}
