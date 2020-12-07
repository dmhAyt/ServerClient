package com.ansheng.thread.heart;

import com.ansheng.config.FileWorkerConfig;
import com.ansheng.single.HeartSingle;
import com.ansheng.util.LogTools;

import java.io.IOException;
import java.util.Objects;

/**
 * 心跳信息管理线程
 */
public class HeartManageThread implements  Runnable {
    @Override
    public void run() {
        HeartSingle heartSingle = null;
        try {
            heartSingle = HeartSingle.getInstance();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        while (true) {
            if(!Objects.isNull(heartSingle))
                heartSingle.manageHeartInfo();

            try{
                Thread.sleep(FileWorkerConfig.Heart_Manage_Time * 1000);
            }catch (InterruptedException e) {
                e.printStackTrace();
                LogTools.writeError(e);
            }

        }
    }
}
