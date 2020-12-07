package com.ansheng.single;

import java.util.concurrent.*;
import java.util.concurrent.locks.ReentrantLock;

public class ThreadPoolSingle {

    private static ThreadPoolSingle _instance = null;
    private static ReentrantLock _objLock = new ReentrantLock();
    private static Executor _threadPool = null;

    private ThreadPoolSingle(){
            _threadPool = Executors.newCachedThreadPool();
    }

    public  static ThreadPoolSingle getInstance(){
        if(_instance == null){
            if(_objLock.tryLock()){
                if(_instance == null){
                    _instance = new ThreadPoolSingle();
                }
                _objLock.unlock();
            }
        }
        return _instance;
    }

    public void execute(Runnable runnable){
        _threadPool.execute(runnable);
    }
}
