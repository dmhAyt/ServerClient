package com.ansheng.thread.heart;

import com.ansheng.factory.CreateSocketInt;
import com.ansheng.factory.impl.CreateSocketImp;
import com.ansheng.model.HeartBeatModel;
import com.ansheng.model.HeartKVModel;
import com.ansheng.single.HeartSingle;
import com.ansheng.util.LogTools;
import com.ansheng.util.Tools;
import com.ansheng.util.socket.SocketTools;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class HeartManageMonitorThread implements Runnable {

    private int _port = 5200;
    private boolean _stop = false;

    public HeartManageMonitorThread(int port){
        this._port = port;
    }

    @Override
    public void run() {
        CreateSocketInt createSocketInt = new CreateSocketImp();
        ServerSocket heartManageThread = null;
        try {
             heartManageThread = createSocketInt.createSocketServer(_port);
        } catch (IOException e) {
            e.printStackTrace();
            LogTools.writeError(e);
        }
        while(!_stop){
            try {
                Socket socket = heartManageThread.accept();

                // 我需要读取你的 ID
                byte[] data = SocketTools.receiveData(socket,true);
                String str ="ID";

                if(data!=null)
                    str = Tools.encodeUTF82Str(data);
                //String hostAddr = socket.getInetAddress().getHostAddress();
                HeartKVModel kv = new HeartKVModel(str,socket);
                HeartSingle.getInstance().addHeartSocket(kv);
                Thread.sleep(10);
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
                LogTools.writeError(e);
            }
        }
    }

    public void stopthread(){
        _stop = true;
    }

}
