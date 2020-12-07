package com.ansheng.factory;

import com.ansheng.factory.impl.LinuxInfoImpl;
import com.ansheng.factory.impl.WindowsInfoImpl;
import com.ansheng.util.OSInfo;

public class SystemPlatformFactory {

    /**
     * 根据不同的系统，创建不同的平台对象
     * @return  平台相关对象
     */
    public static SystemInfoInt getSystemInfo(){
        if(OSInfo.isWindows()){
            return new WindowsInfoImpl();
        }else if(OSInfo.isLinux()){
            return new LinuxInfoImpl();
        }
        return null;
    }

}
