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

        // 获得配置
        boolean configResult =  GetConfig(args);
        if(!configResult) return;
        // 设置心跳监听或者发送心跳
        try {
            HeartSingle.getInstance();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        } catch (InterruptedException e) {
            e.printStackTrace();
            return;
        }
        // 定时发送心跳信息
        ThreadPoolSingle.getInstance().execute(new HeartSendThread());

        ConfigSingle configSingle =ConfigSingle.getInstance();
        if(configSingle.getIdentity() == IdentifyEnum.Deputy_GROUP_LEADER
                || configSingle.getIdentity() == IdentifyEnum.GROUP_LEADER){
            ThreadPoolSingle.getInstance().execute(new HeartManageThread());
        }


        // 运行事务端口监听
        Thread eventThread = new Thread(new EventManageThread());
        eventThread.setName("浜嬪姟绾跨▼");
        eventThread.start();

        try {
            System.in.read();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static boolean GetConfig(String[] param){
        // id groupNum 身份 组长的IP 组长的心跳端口组长的事务端口本机的心跳监听端口 本机的事务端口
        // n0001|d0001|l0001|s0001 1|2|3 normal|leader|s_leader 127.0.0.1 5200 5201 5200 5201
        int result = 0;
        if(param.length <= 0){
            System.out.println("get system config from local file ... ");
            result = Tools.getConfigFromSysFile();
        }else{
            String option = param[0];
            if(Objects.equals("-p",option)){
                System.out.println("get system config  from remote file ..");

            }else if(Objects.equals("-c",option) || param.length >= 8){
                System.out.println("get system config  from parameter ");
                result= Tools.getConfigFromArgs(param,true);
            }
        }
        switch (result){
            case -1:
                System.out.println("    身份和角色ID不对应....");
                break;
            case -2:
                System.out.println("    未知身份....");
                break;
            case -3:
                System.out.println("    配置文件不存在....");
                break;
            default:
                System.out.println("    未知配置...");
                break;
        }
        return result == 0;
    }
}
