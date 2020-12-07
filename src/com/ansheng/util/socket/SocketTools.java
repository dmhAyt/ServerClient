package com.ansheng.util.socket;

import com.ansheng.config.SocketConfig;
import com.ansheng.exception.ReadTimeOutException;
import com.ansheng.model.socket.ResultSocket;
import com.ansheng.util.Tools;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.Charset;

public class SocketTools {
    // 简单的数据结构。第一个四位为方法码；第二个四位为数据长度；后面为数据内容。

    /**
     * 发送socket消息，无方法码的
     *
     * @param socket  socket连接
     * @param message 消息
     * @throws IOException 异常信息
     */
    public static void sendData(Socket socket, String message) throws IOException {
        byte[] data = message.getBytes(Charset.forName("UTF-8"));
        sendData(socket, data);
    }

    /**
     * 发送socket消息，有方法码的。
     *
     * @param socket     socket连接
     * @param message    消息
     * @param methodCode 方法码-标识
     * @throws IOException 异常信息
     */
    public static void sendData(Socket socket, String message, int methodCode) throws IOException {
        byte[] data = message.getBytes(Charset.forName("UTF-8"));
        sendData(socket, data, methodCode);
    }

    /**
     * 发送socket消息，无方法码的
     *
     * @param socket socket连接
     * @param data   字节数组
     * @throws IOException 异常信息
     */
    public static void sendData(Socket socket, byte[] data) throws IOException {
        OutputStream outputStream = socket.getOutputStream();
        int is = data.length;
        outputStream.write(Tools.intToBytes(is));
        outputStream.write(data);
        outputStream.flush();
    }

    /**
     * 发送socket消息，有方法码
     *
     * @param socket     socket连接
     * @param data       字节数组
     * @param methodCode 方法码-唯一标识
     * @throws IOException 异常信息
     */
    public static void sendData(Socket socket, byte[] data, int methodCode) throws IOException {
        int len = data.length;
        OutputStream outputStream = socket.getOutputStream();
        outputStream.write(Tools.intToBytes(methodCode));
        outputStream.write(Tools.intToBytes(len));
        outputStream.write(data);
        outputStream.flush();

    }

    // 读取

    /**
     * 读取socket连接的的数据。-无方法码
     *
     * @param socket socket连接
     * @return 返回读取的数据，可为空的哦
     * @throws IOException IO异常
     * @throws ReadTimeOutException 读取超时了
     */
    public static byte[] receiveData(Socket socket,boolean isWait) throws IOException, ReadTimeOutException {
        InputStream in = socket.getInputStream();
        int num = 0;
        if(!isWait && in.available() <= 0) return  null;

        /* 确保读取到的数据是存在的。 */
        while (isWait && in.available() <= 0) {
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (++num * 50 > SocketConfig.getOutTime()) throw new ReadTimeOutException("读取超时了。");
        }

        // 先读取本次的大小
        byte[] dataLen = new byte[4];

        // 确保读取到的数据是存在的。
        while (true) {
            if (in.available() >= 4) {
                in.read(dataLen);
                break;
            }
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (++num * 50 > SocketConfig.getOutTime()) throw new ReadTimeOutException("读取超时了。");
        }
        int dataLength = Tools.bytesToInt(dataLen);

        if (dataLength < 0) throw new ReadTimeOutException("读取不到数据。");

        byte[] data = new byte[dataLength];

        // 确保读取到的数据是存在的。
        while (true && dataLength > 0) {
            if (in.available() >= dataLength) {
                in.read(data);
                break;
            }
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (++num * 50 > SocketConfig.getOutTime()) throw new ReadTimeOutException("读取超时了。");
        }

        return data;

    }

    /**
     * 读取socket连接的的数据。-无方法码
     *
     * @param socket socket连接
     * @return 返回读取的数据
     * @throws IOException IO异常
     * @throws ReadTimeOutException 读取超时了
     */
    public static ResultSocket receiveDataMethod(Socket socket,boolean isWait) throws IOException , ReadTimeOutException {
        ResultSocket result = new ResultSocket();
        InputStream in = socket.getInputStream();

        int num = 0;
        if(!isWait && in.available() <= 0) return  null;

        /* 确保读取到的数据是存在的。 */
        while (in.available() <= 0) {
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (++num * 50 > SocketConfig.getOutTime()) throw new ReadTimeOutException("读取超时了。");
        }

        /* 先读取方法码。 */
        byte[] methodCode = new byte[4];
        /* 确保读取到的数据是存在的。 */
        while (true) {
            if (in.available() >= 4) {
                in.read(methodCode);
                break;
            }
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (++num * 50 > SocketConfig.getOutTime()) throw new ReadTimeOutException("读取超时了。");
        }
        result.setMethodCode(Tools.bytesToInt(methodCode));


        byte[] data = receiveData(socket,true);
        result.setMethodParam(data);
        return result;

    }
    //

}
