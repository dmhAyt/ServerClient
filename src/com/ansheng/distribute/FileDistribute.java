package com.ansheng.distribute;

import com.ansheng.factory.SystemInfoInt;
import com.ansheng.factory.SystemPlatformFactory;
import com.ansheng.model.hardware.MountInfo;
import com.ansheng.util.OSInfo;

import java.util.*;

/**
 * 本类主要负责文件的分配，如文件放置在哪个磁盘，组员间该谁和谁备份等业务处理
 * @author Administrator
 *
 */
public class FileDistribute {
	private static SystemInfoInt _sysInfoInt = null;
	
	public FileDistribute() {
		_sysInfoInt = SystemPlatformFactory.getSystemInfo();
	}
	
	
	/**
	 * 将文件分配到哪一个磁盘？
	 * @param fileID	文件的唯一ID
	 * @param fileName	文件名
	 * @param fileLength	文件大小
	 * @return 返回文件存储的完整路径。
	 */
	public static String distributeDisk(String fileID,String fileName,long fileLength) {
		// 读取所有的盘符、挂载点，并获得空闲大小。
		//// 最简单的轮询，放不下再放到其他盘符、挂载点
		//// 去掉不需要的挂载点或者盘符。
		List<MountInfo> mountLst = _sysInfoInt.getDiskInfo(null);
		String path = "";
		for(MountInfo m : mountLst) {
			if(m.getAvailableCapacity() < (fileLength + 5 * 1024 * 1024 * 1024)) {
				path = m.getMountPath();
			}
		}
		path  = String.join(OSInfo.getFilePart(), path,fileID,fileName);
		return path;
	}
	
}
