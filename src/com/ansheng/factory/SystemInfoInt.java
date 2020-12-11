package com.ansheng.factory;

import java.util.List;

import com.ansheng.model.hardware.CPUModel;
import com.ansheng.model.hardware.HardDiskCapacity;
import com.ansheng.model.hardware.MemoryModel;
import com.ansheng.model.hardware.MountInfo;
import com.ansheng.model.hardware.NetWorkModel;

public interface SystemInfoInt {
    /**
     * 获得当时磁盘的使用情况
     * @param mountName 盘符或者挂载
     * @return  返回磁盘使用信息
     */
    HardDiskCapacity getDiskSpaceInfo(String mountName);

    /**
     * 获得当时网络使用情况
     * @return  网络的使用信息
     */
    NetWorkModel getNetWorkInfo();

    /**
     * 获得当时CPU的使用情况
     * @return  cpu的使用情况
     */
    CPUModel getCPUInfo();

    /**
     * 获得内存的使用情况
     * @return
     */
    MemoryModel getMemoryInfo();
    
    /**
     * 获得盘符或者挂载点的大小及可用大小
     * @param exclude	需要排除的盘符或者挂载点
     * @return	返回容量信息
     */
    List<MountInfo> getDiskInfo(List<String> exclude);
}
