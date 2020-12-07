package com.ansheng.thread;

import com.ansheng.server.EventServer;

import java.io.IOException;
import java.net.Socket;

public class EventThread implements Runnable {

    private Socket _socket = null;

    public EventThread(Socket socket) {
        this._socket = socket;
    }

    @Override
    public void run() {
        EventServer server = new EventServer();
        server.eventManage(_socket);

        if(!_socket.isClosed()){
            try {
                _socket.shutdownInput();
                _socket.shutdownOutput();
                _socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
}
