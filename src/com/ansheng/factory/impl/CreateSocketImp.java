package com.ansheng.factory.impl;

import com.ansheng.factory.CreateSocketInt;
import com.ansheng.single.ConfigSingle;
import com.ansheng.util.socket.SocketTools;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;

public class CreateSocketImp implements CreateSocketInt {

    @Override
    public ServerSocket createSocketServer(int port) throws IOException {
        // 开始监听
        ServerSocket _socketServer = new ServerSocket();
        SocketAddress address = new InetSocketAddress(port);
        _socketServer.bind(address);
        return _socketServer;
    }

    @Override
    public Socket createSocketClient(String ip, int port) throws IOException {
        Socket _hClientSocket = new Socket(ip,port);
        if(_hClientSocket.isConnected());
        _hClientSocket.setKeepAlive(true);
        return _hClientSocket;
    }

    @Override
    public Socket createHeartSocketClient(String ip, int port) throws IOException {
        Socket _hClientSocket = new Socket(ip,port);
        if(_hClientSocket.isConnected()){
            // 发送一条数据，表示ID
            SocketTools.sendData(_hClientSocket, ConfigSingle.getInstance().getID());
        }
        _hClientSocket.setKeepAlive(true);
        return _hClientSocket;
    }
}
