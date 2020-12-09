package com.ansheng.config;

import com.ansheng.util.Tools;

public class FileWorkerConfig {

    private FileWorkerConfig(){}

    public static String File_Encode_Name = "UTF-8";
    
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
    /**
     * 记录保存的地址
     */
    public static String Back_Up_Path = Tools.getRunPath("BackUp");
    /**
     * 记录保存的数据库文件
     */
    public static String Back_Up_Db_Name = "BackDB";
    /**
     * 记录保存的数据库文件类型---不公开配置
     */
    public static String Back_Up_Db_Type = ".da";
    /**
     * 启用数据库备份。
     */
    public static boolean Back_Up_Db_Enable = true;
}
