package com.ansheng.single;

import com.ansheng.myenum.IdentifyEnum;

import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

public class ConfigSingle {

//    private static final Object _lock_key = new Object();
    private static ConfigSingle _instance = null;
    private static ReentrantLock _lockKey = new ReentrantLock();
    private ConfigSingle(){


    }

    /**
     * 获得对象
     * @return  返回 ConfigSingle 对象
     */
    public static ConfigSingle getInstance() {
        if(Objects.isNull(_instance)){
            if(_lockKey.tryLock()){
                if(Objects.isNull(_instance)){
                    _instance = new ConfigSingle();
                }
                _lockKey.unlock();
            }
        }
        return _instance;
    }

    /**
     * 机械唯一 ID
     */
    private String ID;

    /**
     * 组内编号
     */
    private int GroupNum = 0;
    /**
     * 身份
     */
    private IdentifyEnum Identity = IdentifyEnum.GROUP_NORMAL;
    /**
     * 组长的IP;如果是组长时为记录服务器的
     */
    private String GroupIP  = "";
    /**
     * 组长的Port;如果是组长时为记录服务器的
     */
    private int GroupHeartPort = 5200;
    /**
     * 组长的事务端口
     */
    private int GroupEventPort = 5200;


    private int BindHeartPort = 5200;

    private int BindEventPort = 5200;

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public int getGroupNum() {
        return GroupNum;
    }

    public void setGroupNum(int groupNum) {
        GroupNum = groupNum;
    }

    public IdentifyEnum getIdentity() {
        return Identity;
    }

    public void setIdentity(IdentifyEnum identity) {
        Identity = identity;
    }

    public String getGroupIP() {
        return GroupIP;
    }

    public void setGroupIP(String groupIP) {
        GroupIP = groupIP;
    }

    public int getGroupHeartPort() {
        return GroupHeartPort;
    }

    public void setGroupHeartPort(int groupHeartPort) {
        GroupHeartPort = groupHeartPort;
    }

    public int getGroupEventPort() {
        return GroupEventPort;
    }

    public void setGroupEventPort(int groupEventPort) {
        GroupEventPort = groupEventPort;
    }

    public int getBindHeartPort() {
        return BindHeartPort;
    }

    public void setBindHeartPort(int bindHeartPort) {
        BindHeartPort = bindHeartPort;
    }

    public int getBindEventPort() {
        return BindEventPort;
    }

    public void setBindEventPort(int bindEventPort) {
        BindEventPort = bindEventPort;
    }
}
