package com.ansheng.util;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

public class FileTools {


    /**
     * 把字符串覆盖写入到文件中。
     * @param fileName  文件名
     * @param message   信息
     * @throws IOException  IO异常
     */
    public static void writeStr(String fileName,String message) throws IOException {
        CheckArgument(fileName,true);
        try(FileWriter writer = new FileWriter(new File(fileName))){
            writer.write(message);
        }
    }

    /**
     * 将字符串写入文件
     * @param fileName 文件名
     * @param message   消息
     * @throws IOException  文件异常
     */
    public static void appendStr(String fileName,String message) throws IOException {
        CheckArgument(fileName,true);
        try(FileWriter writer = new FileWriter(new File(fileName),true)){
           writer.append(message);
        }

    }

    /**
     * 将字符串写入文件
     * @param fileName 文件名
     * @param bytes   消息,字符串
     * @throws IOException  文件异常
     */
    public static void appendByte(String fileName,byte[] bytes) throws IOException {
        CheckArgument(fileName,true);
        try(FileOutputStream outputStream = new FileOutputStream(new File(fileName),true)){
            outputStream.write(bytes);
        }
    }

    /**
     * 写字节到文件中
     * @param fileName 文件名
     * @param bytes 字节数组
     * @param beginIndex    写入文件的开始位置。
     * @param create    如果文件不存在时是否需要创建。【包括文件夹】
     * @throws IOException  文件异常
     */
    public static void writeByte(String fileName,byte[] bytes,long beginIndex,boolean create) throws IOException {
        CheckArgument(fileName,create);
        if(beginIndex < 0) throw new IllegalArgumentException("文件开始位置不能为0");
        try(RandomAccessFile randomAccessFile = new RandomAccessFile(new File(fileName),"w")){
            if(randomAccessFile.length() < beginIndex) new IllegalArgumentException("开始位置不能大于文件大.");
            randomAccessFile.seek(beginIndex);
            randomAccessFile.write(bytes);
        }
    }

    /**
     * 读文件里面的所有内容，--字符
     * @param fileName  文件名
     * @return  返回文件的所有内容
     * @throws IOException  文件异常
     */
    public static String readAllText(String fileName) throws IOException {
        CheckArgument(fileName,false);
        StringBuffer result= new StringBuffer("");
        try(Reader reader = new InputStreamReader(new FileInputStream(fileName))){
            char[] chars = new char[1024];
            int i = 0;
            while ((i = reader.read(chars)) > 0){
                String str = new String(chars,0,i);
                result.append(str);
            }
        }
        return result.toString();
    }

    /**
     * 从指定位置读取文件内容
     * @param fileName  文件名
     * @param beginIndex    从该位置开始读。当文件大小小于该值时报错
     * @param byteLen   需要读取的长度，当文件长度不够时自动缩小。
     * @return  返回内容。
     * @throws IOException 参数异常，文件不存在
     */
    public static byte[] readBytes(String fileName,long beginIndex,int byteLen) throws IOException {
        CheckArgument(fileName, false);
        byte[] result = null;
        if (beginIndex < 0) throw new IllegalArgumentException("文件开始位置不能为0");
        try (RandomAccessFile accessFile = new RandomAccessFile(new File(fileName), "r")) {
            if (accessFile.length() < beginIndex) throw new IllegalArgumentException("开始位置大于文件长度");
            int length = (accessFile.length() > beginIndex + byteLen) ? byteLen
                    : (int) (accessFile.length() - beginIndex);
            result = new byte[length];
            int num = accessFile.read(result);
        }
        return result;
    }

    /**
     * 检查文件名是否正确，文件是否存在
     *
     * @param fileName      文件名
     * @throws IOException 文件异常
     */
    private static void CheckArgument(String fileName,boolean createWExist) throws IOException {
        if (Objects.isNull(fileName) || fileName.length() <= 0) throw new IllegalArgumentException("文件地址不正确");
        if (!Files.exists(Paths.get(fileName))) {
            if (createWExist) {
                // 获得文件夹
                Path path = Paths.get(fileName);
                Path pathParent = path.getParent();
                if (!Files.exists(pathParent)) Files.createDirectories(pathParent);
                Files.createFile(Paths.get(fileName));
            } else throw new IllegalArgumentException("文件不存在");
        }
    }

}
