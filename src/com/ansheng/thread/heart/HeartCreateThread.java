package com.ansheng.thread.heart;

import com.ansheng.factory.CreateSocketInt;
import com.ansheng.factory.impl.CreateSocketImp;
import com.ansheng.single.ConfigSingle;
import com.ansheng.single.HeartSingle;
import com.ansheng.util.LogTools;

import java.io.IOException;
import java.net.Socket;

public class HeartCreateThread  implements Runnable{
    private HeartSingle _config = null;
    public  HeartCreateThread(HeartSingle single){
        this._config = single;
    }
    @Override
    public void run() {
        CreateSocketInt createSocketInt = new CreateSocketImp();
        ConfigSingle config = ConfigSingle.getInstance();
        while (true) {
            try {
              Socket _socket = createSocketInt.createHeartSocketClient(config.getGroupIP(), config.getGroupHeartPort());
              System.out.println("创建心跳socket成功..");
              _config.setHeartSocket(_socket);
              break;
            } catch (IOException e) {
                LogTools.writeError(e);
                System.out.println("创建心跳socket失败，10秒后重试..");
            }
            try {
                Thread.sleep(10 * 1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
