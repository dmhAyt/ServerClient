package com.ansheng.model;

import com.sun.xml.internal.ws.util.InjectionPlan;

import java.net.Socket;

public class HeartKVModel implements Comparable<HeartKVModel> {
    private String ID;
    private Socket SocketClient;
    private int ReadTime;

    public int getReadTime() {
        return ReadTime;
    }

    public void setReadTime(int readTime) {
        ReadTime = readTime;
    }

    public HeartKVModel(String ID, Socket client){
        this.ID = ID;
        this.SocketClient = client;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public Socket getSocketClient() {
        return SocketClient;
    }

    public void setSocketClient(Socket socketClient) {
        SocketClient = socketClient;
    }

    @Override
    public int compareTo(HeartKVModel o) {
        if(o==null) return -1;
        return  this.getID().compareTo(o.ID);
    }
}
