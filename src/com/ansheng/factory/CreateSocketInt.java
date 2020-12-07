package com.ansheng.factory;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public interface CreateSocketInt {
    /**
     * 创建socket监听
     * @param port 监听的端口
     */
     ServerSocket createSocketServer(int port) throws IOException;

    /**
     * 创建一个普通的Socket连接
     * @param ip    IP
     * @param port  端口
     * @return  返回创建好的socket对象
     * @throws IOException  IO异常
     */
    Socket createSocketClient(String ip,int port) throws IOException;

    /**
     * 创建一个心跳socket
     * @param ip ip
     * @param port  端口
     * @return  socket对象
     * @throws IOException IO异常
     */
    Socket createHeartSocketClient(String ip,int port) throws IOException;

}
