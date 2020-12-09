package com.ansheng.util;

import java.io.IOException;
import java.nio.charset.Charset;
import java.text.MessageFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;

import com.ansheng.config.FileWorkerConfig;

public class LogTools {

    /**
     * 写日志
     * @param message 写日志
     */
    public static void writeInfo(String message){
        try {
            LocalDate  date = LocalDate.now();
            String messages = MessageFormat.format("[{0,time}] 消息：{1}\r\n\r\n", new Date(),message);
            String path = Tools.getRunPath("logs/");
            String fileName = path +date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))+"_info.txt";
            FileTools.appendByte(fileName,messages.getBytes(FileWorkerConfig.File_Encode_Name));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * 写异常信息
     * @param ex 异常信息
     */
    public static void writeError(Exception ex){
        try {
            LocalDate  date = LocalDate.now();
            StackTraceElement track =  ex.getStackTrace()[0];
            String messages = MessageFormat.format("[{0,time}]{1}\r\n[**ex class]{2}\r\n[ex method]{3}\r\n" +
                            "[ex track]{4}\r\n\r\n",
                    new Date(),ex.getMessage(),track.getFileName(),track.getMethodName(),track.toString());

            String path = Tools.getRunPath("logs/");
            String fileName = path + date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))+"_error.txt";
            FileTools.appendByte(fileName,messages.getBytes(FileWorkerConfig.File_Encode_Name));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
