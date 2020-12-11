package com.ansheng.factory.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import com.ansheng.factory.SystemInfoInt;
import com.ansheng.model.hardware.CPUModel;
import com.ansheng.model.hardware.HardDiskCapacity;
import com.ansheng.model.hardware.MemoryModel;
import com.ansheng.model.hardware.MountInfo;
import com.ansheng.model.hardware.NetWorkModel;
import com.ansheng.util.LogTools;

public class LinuxInfoImpl implements SystemInfoInt {
	
    @Override
    public HardDiskCapacity getDiskSpaceInfo(String mountName) {
        HardDiskCapacity result = new HardDiskCapacity();
        
		List<MountInfo>  paramLst = getMountInfo("");
		for(MountInfo item : paramLst) {
			result.setAvailableCapacity(item.getAvailableCapacity()+result.getAvailableCapacity());
			result.setUsableCapacity(item.getUsableCapacity()+result.getUsableCapacity());
			result.setTotalCapacity(item.getTotalCapacity()+result.getTotalCapacity());
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

	@Override
	public List<MountInfo> getDiskInfo(List<String> exclude) {
		// TODO Auto-generated method stub
		List<MountInfo>  result = getMountInfo("");
		if(exclude == null || exclude.size() <= 0) return result;
		
		Iterator<MountInfo> iter  = result.iterator();
		while(iter.hasNext()) {
			MountInfo info = iter.next();
			if(exclude.contains(info.getMountPath())) iter.remove();
		}
		return result; 
	}
	
	private List<MountInfo> getMountInfo(String mountName){
		List<MountInfo>  result = new ArrayList<MountInfo>();
		String commond = "df  ";
        if(!Objects.isNull(mountName) || mountName.length() > 0) commond += mountName;

        Runtime rt = Runtime.getRuntime();
        try {
            Process pr = rt.exec(commond);
            try(BufferedReader in = new BufferedReader(new InputStreamReader(pr.getInputStream()))){
                String str = null;
                List<String> fileStreams = new ArrayList<>(20);

                int line = 0;
                while ((str = in.readLine()) != null){
                    line++;
                    if(line <= 1) continue;

                    int m = 0;
                    String[] strArray = str.split(" ");
                    MountInfo mountInfo = null;
                    for (String lineStr : strArray) {
                        if(lineStr.length()==0 || lineStr == " ") continue;
                        if(m == 0) {
                            if(fileStreams.contains(lineStr)) break;
                            fileStreams.add(lineStr);
                            mountInfo = new MountInfo();
                            mountInfo.setMountName(lineStr);
                            mountInfo.setMountPath(lineStr);
                            m++;
                            continue;
                        }
                        if(m == 1){
                            long total = Long.parseLong(lineStr) * 1000;
                            mountInfo.setTotalCapacity(total);
                        }else if(m == 2){
                            long used = Long.parseLong(lineStr) * 1000;
                            mountInfo.setUsableCapacity(used);
                        }else if(m == 3){
                            long avail = Long.parseLong(lineStr) * 1000;
                            mountInfo.setAvailableCapacity(avail);
                        }
                        m ++;
                    }
                    
                    if(mountInfo != null) result.add(mountInfo);
                    
                }
            }
        } catch (IOException e) {
            LogTools.writeError(e);
        }
        return result;
	}
	
}
