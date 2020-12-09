package com.ansheng.util;

import java.io.*;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;

import org.jcp.xml.dsig.internal.DigesterOutputStream;

import com.ansheng.config.FileWorkerConfig;

public class FileTools {
	private static final char[] hexCode = "0123456789ABCDEF".toCharArray();
	
	/**
	 *	 当文件存在时删除
	 * @param fileName 文件名
	 * @return	文件不存在时返回false
	 * @throws IOException
	 */
	public static boolean deleteFileExist(String fileName) throws IOException {
		return Files.deleteIfExists(Paths.get(fileName));
	}
	

    /**
     * 	把字符串覆盖写入到文件中。
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
     *	 将字符串写入文件
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
     * 	将字符串写入文件
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
     * 	写字节到文件中
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
     * 	读文件里面的所有内容，--字符
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
     * 	从指定位置读取文件内容
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
     * 1. 获得字符串的md5摘要
     * @param str
     * @return
     */
    public static String md5(String str) {
    	if(Objects.isNull(str) || str.length() <= 0) return "";
    	try {
    		MessageDigest md5 = MessageDigest.getInstance("MD5");
    		md5.update(str.getBytes(FileWorkerConfig.File_Encode_Name));
    		byte[] byteArray = md5.digest();
			return ToHexString(byteArray);
    	}catch(NoSuchAlgorithmException | UnsupportedEncodingException e) {
    		
    	}
    	return "";
    }
    /**
     * 1 获得指定的摘要
     * @param fileName
     * @param algorithm 所请求算法的名称  for example: MD5, SHA1, SHA-256, SHA-384, 
     * @return
     */
    public static String md5File(String fileName,String algorithm) {
    	try (FileInputStream fileInputStream = new FileInputStream(fileName)){        
    		MessageDigest MD5 = MessageDigest.getInstance(algorithm);
	         byte[] buffer = new byte[8192];
	         int length;
	         while ((length = fileInputStream.read(buffer)) != -1) {
	             MD5.update(buffer, 0, length);
	         }
	         return ToHexString(MD5.digest());
    	} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	return "";
    } 
    
    private static String ToHexString(byte[] data) {
        StringBuilder r = new StringBuilder(data.length * 2);
        for (byte b : data) {
            r.append(hexCode[(b >> 4) & 0xF]);
            r.append(hexCode[(b & 0xF)]);
        }
        return r.toString();
    }
    
    /**
     * 	检查文件名是否正确，文件是否存在
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

	/**
	 * 	判断文件是否存在
	 * @param fileLocalPath
	 * @return
	 */
	public static boolean fileExist(String filePath) {
		return Files.exists(Paths.get(filePath), LinkOption.NOFOLLOW_LINKS);
	}

	/**
	 * 	读取文件的长度
	 * @param filePath
	 * @return
	 */
	public static long fileLength(String filePath) {
		File file = new File(filePath);
		if(file.exists()) {
			return file.length();
		}
		return 0L;
	}
	
}
