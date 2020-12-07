package com.ansheng.thread;

import com.ansheng.factory.CreateSocketInt;
import com.ansheng.factory.impl.CreateSocketImp;
import com.ansheng.single.ThreadPoolSingle;
import sun.rmi.runtime.RuntimeUtil;
import sun.util.resources.cldr.so.CurrencyNames_so;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class EventManageThread implements Runnable {

    private int _port = 5201;
    private boolean _stop = false;

    @Override
    public void run() {
        CreateSocketInt createSocketInt = new CreateSocketImp();
        ServerSocket eventSocket = null;
        try {
            eventSocket = createSocketInt.createSocketServer(_port);
        } catch (IOException e) {
            e.printStackTrace();
        }

        while (!_stop){
            try {
                Socket socket =  eventSocket.accept();
                ThreadPoolSingle.getInstance().execute(new EventThread(socket));

                Thread.sleep(10);
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    public void stopThread(){
        this._stop  = true;
    }

}
