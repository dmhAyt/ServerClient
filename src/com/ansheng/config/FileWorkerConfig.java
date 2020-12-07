package com.ansheng.config;

public class FileWorkerConfig {

    private FileWorkerConfig(){}

    /**
     * 工作线程数
     */
    public static int Core_Thread_Num = 2;
    /**
     * 最大工作线程数
     */
    public static int Max_Thread_Num = 5;

    /**
     * 心跳发送的时间，单位为秒
     */
    public static int Heart_Send_Time=30;
    /**
     * 心跳管理的周期处理，默认为5秒，每5秒接收一次心跳信息。
     */
    public static int Heart_Manage_Time = 5;

}
