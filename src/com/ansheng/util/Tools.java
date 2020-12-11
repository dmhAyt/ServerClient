package com.ansheng.util;

import com.alibaba.fastjson.JSON;
import com.ansheng.constant.FileNameConstant;
import com.ansheng.factory.SystemInfoInt;
import com.ansheng.factory.SystemPlatformFactory;
import com.ansheng.model.hardware.HardDiskCapacity;
import com.ansheng.myenum.IdentifyEnum;
import com.ansheng.single.ConfigSingle;
import sun.rmi.runtime.Log;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Objects;

public class Tools {

    /**
     * 获得程序运行的绝对路径下的文件夹
     * @return 程序运行的绝对路径。
     */
    public static  String getRunPath(String folder){
        String runPath =   System.getProperty("user.dir");
        if(!Objects.isNull(folder) && !Objects.equals("",folder)){
            runPath = String.join("/",runPath,folder);
        }
        if(!runPath.endsWith("/")) runPath += "/";
        if(Files.notExists(Paths.get(runPath))) {
            try {
                Files.createDirectories(Paths.get(runPath));
            } catch (IOException e) {
                LogTools.writeError(e);
            }
        }
        return  runPath;
    }

    /**
     * 获得记录文件的存储路径
     * @return 返回文件的绝对路径
     */
    public static  String getRecordFilePath(){
        String runPath = getRunPath("");
        String path = FileNameConstant.FILE_RECORD_PATH;
        path  += "/"+FileNameConstant.FILE_RECORD_NAME;
        String fileName = String.join("/",runPath,path);
        return  fileName;
    }

    /**
     * 获得已经挂载的磁盘的所有的可用空间
     * @param mountName 挂载名。当为“”时代表全部统计
     * @return 可用空间，单位为 B
     */
    public static  HardDiskCapacity getDiskSpaceInfo(String mountName){
        HardDiskCapacity result = null;
        SystemInfoInt systemInfoInt =  SystemPlatformFactory.getSystemInfo();
        result = systemInfoInt.getDiskSpaceInfo(mountName);
        LogTools.writeInfo("获得的磁盘信息："+result.toString());
        return  result;
    }

    /**
     * int类型转byte数组。
     * @param param int 值
     * @return  返回byte数组
     */
    public static  byte[] intToBytes(int param){

        byte[] result = new byte[4];
        result[0] = (byte)((param >> 24) & 0xFF);
        result[1] = (byte)((param >> 16) & 0xFF);
        result[2] = (byte)((param >> 8) & 0xFF);
        result[3] = (byte)(param & 0xFF);
        return result;


    }

    /**
     * 将byte[] 数组转为int类型
     * @param param byte[] 数据
     * @return  int数据
     */
    public static int bytesToInt(byte[] param){
        int value=0;
        for(int i = 0; i < 4; i++) {
            int shift= (3-i) * 8;
            value +=(param [i] & 0xFF) << shift;
        }
        return value;
    }

    /**
     * 将long类型转为byte[] 类型
     * @param param
     * @return
     */
    public static byte[] longToByte(long param) {
    	ByteBuffer _buffer = ByteBuffer.allocate(8); 
    	_buffer.putLong(param);
    	return _buffer.array();
    }
    /***
     * 将byte[] 类型转为 long类型
     * @param param
     * @return
     */
    public static long bytesToLong(byte[] param) {
		ByteBuffer buffer = ByteBuffer.allocate(8); 
		buffer.put(param, 0, param.length);
	    //flip方法将Buffer从写模式切换到读模式，调用flip()方法会将position设回0，从头读起
	    buffer.flip();
	    return buffer.getLong();
    }
    /**
     * 将byte[] 类型转为long类型
     * @param param	数组
     * @param begin	开始位置
     * @param length	长度
     * @return
     */
    public static long bytesToLong(byte[] param,int begin,int length) {
    	ByteBuffer buffer = ByteBuffer.allocate(8); 
		buffer.put(param, begin, length);
	    //flip方法将Buffer从写模式切换到读模式，调用flip()方法会将position设回0，从头读起
	    buffer.flip();
	    return buffer.getLong();
    }
    
    /**
     * 使用阿里的fastJson把对象转成json字符串。
     * @param obj   对象数据
     * @return  返回json字符串
     */
    public static String bean2Json(Object obj){
        return JSON.toJSONString(obj);
    }

    /**
     * 使用阿里的fastjson将json字符串转为java bean
     * @param jsonStr   json字符串
     * @param tClass    Java bean 类型
     * @param <T>   Java bean 类型
     * @return  返回对象，
     */
    public static <T> T json2Bean(String jsonStr,Class<T> tClass){
        return JSON.parseObject(jsonStr,tClass);
    }

    /**
     * 将数组转为utf8编码的字符串
     * @param param 字节数组
     * @return  返回utf8编码的字符串
     */
    public static String encodeUTF82Str(byte[] param){
        return encode2Str(param,"UTF-8");
    }

    /**
     * 将字节数组转为指定的编码格式的字符串
     * @param param 字节数组
     * @param encodeStr 编码
     * @return  指定编码格式的字符串
     */
    public static String encode2Str(byte[] param,String encodeStr){
        try {
            String result = new String(param,encodeStr);
            return result;
        } catch (UnsupportedEncodingException e) {
            return "";
        }
    }

    /**
     *
     * @param strings   参数字符串
     * @return  返回状态
     * 0 成功。
     * -1 身份与ID不对应
     * -2 未知身份
     * @exception  NumberFormatException 字符串转换成int类型异常 
     */
    public static int getConfigFromArgs(String[] strings,boolean writeToFile){
        // id groupNum 身份 组长IP 组长心跳端口 组长事务端口 本机绑定的心跳端口 本机绑定的事务端口
        // n0001|d0001|l0001|s0001 1|2|3 normal|delete|leader|s_leader 127.0.0.1 5200 5201 5200 5201
        String[] param = strings;
        if(Objects.equals("-c",strings[0].toLowerCase())) param = Arrays.copyOfRange(strings,1,strings.length);

        ConfigSingle configSingle = ConfigSingle.getInstance();
        for (int i = 0; i <param.length; i++) {
            switch (i){
                case 0:
                    configSingle.setID(param[i]);
                    break;
                case 1:
                    configSingle.setGroupNum(Integer.parseInt(param[i]));
                    break;
                case 2:
                    switch (param[i].toLowerCase()){
                        case "normal":
                            if(!configSingle.getID().toLowerCase().startsWith("n")) return -1;
                            configSingle.setIdentity(IdentifyEnum.GROUP_NORMAL);
                            break;
                        case "leader":
                            if(!configSingle.getID().toLowerCase().startsWith("l")) return -1;
                            configSingle.setIdentity(IdentifyEnum.GROUP_LEADER);
                            break;
                        case "s_leader":
                            if(!configSingle.getID().toLowerCase().startsWith("s")) return -1;
                            configSingle.setIdentity(IdentifyEnum.Deputy_GROUP_LEADER);
                            break;
                        default:
                            return  -2;
                    }
                    break;
                case 3:
                    configSingle.setGroupIP(param[i]);
                    break;
                case 4:
                    configSingle.setGroupHeartPort(Integer.parseInt(param[i]));
                    break;
                case 5:
                    configSingle.setGroupEventPort(Integer.parseInt(param[i]));
                    break;
                case 6:
                    configSingle.setBindHeartPort(Integer.parseInt(param[i]));
                    break;
                case 7:
                    configSingle.setBindEventPort(Integer.parseInt(param[i]));
                    break;
            }
        }

        // 写入系统的配置文件
        if(writeToFile){
            String runPath = getRunPath("Config/")+"config.txt";
            try {
                FileTools.writeStr(runPath,bean2Json(strings));
            } catch (IOException e) {
                e.printStackTrace();
                LogTools.writeError(e);
            }
        }

        return 0;
    }

    /**
     * 从系统配置文件中获取配置
     * @return  返回状态
     * 0 成功。
     * -1 身份与ID不对应
     * -2 未知身份
     * -3 配置文件不存在
     * @exception  NumberFormatException 字符串转换成int类型异常
     */
    public static int getConfigFromSysFile(){
        String runPath = getRunPath("Config/")+"config.txt";
        String param = "";
        if(Files.notExists(Paths.get(runPath))) return -3;
        try {
           param =  FileTools.readAllText(runPath);
        } catch (IOException e) {
            e.printStackTrace();
            LogTools.writeError(e);
        }
        String[] strings =  json2Bean(param,String[].class);
        return getConfigFromArgs(strings,false);
    }

    public static int getProcessID(){
        RuntimeMXBean runtimeMXBean = ManagementFactory.getRuntimeMXBean();
        String str = runtimeMXBean.getName().split("@")[0];
        return Integer.valueOf(str).intValue();
    }
}
