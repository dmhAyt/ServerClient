package com.ansheng.factory.impl;

import com.ansheng.factory.SystemInfoInt;
import com.ansheng.model.hardware.CPUModel;
import com.ansheng.model.hardware.HardDiskCapacity;
import com.ansheng.model.hardware.MemoryModel;
import com.ansheng.model.hardware.NetWorkModel;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class WindowsInfoImpl implements SystemInfoInt {
    @Override
    public HardDiskCapacity getDiskSpaceInfo(String mountName) {
        HardDiskCapacity result = new HardDiskCapacity();
        List<String> mountNames = new ArrayList<>();
        if(Objects.isNull(mountName) || mountName.isEmpty() || mountName.length() <= 0)
            mountNames = getDiskPartition();
        else mountNames.add(mountName);

        for (String mountNameItem : mountNames) {
            File file = new File(mountNameItem);
            result.setTotalCapacity(file.getTotalSpace()+result.getTotalCapacity());
            result.setAvailableCapacity(file.getFreeSpace()+result.getAvailableCapacity());
            result.setUsableCapacity(file.getTotalSpace()-file.getFreeSpace()+result.getUsableCapacity());
        }
        return result;
    }

    @Override
    public NetWorkModel getNetWorkInfo() {

        return null;
    }

    @Override
    public CPUModel getCPUInfo() {
        return null;
    }

    @Override
    public MemoryModel getMemoryInfo() {
        return null;
    }


    /**
     * 获得windows下的所有盘符
     * @return 所有的盘符
     */
    private List<String> getDiskPartition() {
        File[] files = File.listRoots();
        List<String> result = new ArrayList<>(files.length);
        // 去掉系统盘
        for (File file: files) {
            result.add(file.getAbsolutePath());
        }
        return  result;
    }
}
