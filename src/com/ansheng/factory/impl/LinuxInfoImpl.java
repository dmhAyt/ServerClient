package com.ansheng.factory.impl;

import com.ansheng.factory.SystemInfoInt;
import com.ansheng.model.hardware.CPUModel;
import com.ansheng.model.hardware.HardDiskCapacity;
import com.ansheng.model.hardware.MemoryModel;
import com.ansheng.model.hardware.NetWorkModel;
import com.ansheng.util.LogTools;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class LinuxInfoImpl implements SystemInfoInt {
    @Override
    public HardDiskCapacity getDiskSpaceInfo(String mountName) {
        HardDiskCapacity result = new HardDiskCapacity();
        String commond = "df  ";
        if(!Objects.isNull(mountName) || mountName.length() > 0) commond += mountName;

        Runtime rt = Runtime.getRuntime();
        try {
            Process pr = rt.exec(commond);
            try(BufferedReader in = new BufferedReader(new InputStreamReader(pr.getInputStream()))){
                String str = null;
                String[] strArray = null;
                List<String> fileStreams = new ArrayList<>(20);

                int line = 0;
                while ((str = in.readLine()) != null){
                    line++;
                    if(line <= 1) continue;

                    int m = 0;
                    strArray = str.split(" ");
                    for (String lineStr : strArray) {
                        if(lineStr.length()==0 || lineStr == " ") continue;
                        if(m == 0) {
                            if(fileStreams.contains(lineStr)) break;
                            fileStreams.add(lineStr);
                            m++;
                            continue;
                        }
                        if(m == 1){
                            long total = Long.parseLong(lineStr) * 1000;
                            result.setTotalCapacity(total+result.getTotalCapacity());
                        }else if(m == 2){
                            long used = Long.parseLong(lineStr) * 1000;
                            result.setUsableCapacity(used+result.getUsableCapacity());
                        }else if(m == 3){
                            long avail = Long.parseLong(lineStr) * 1000;
                            result.setAvailableCapacity(avail+result.getAvailableCapacity());
                        }
                        m ++;
                    }
                }
            }
        } catch (IOException e) {
            LogTools.writeError(e);
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
}
