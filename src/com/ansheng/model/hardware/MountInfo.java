package com.ansheng.model.hardware;

/**
 * 磁盘的挂载点名。或者是盘符信息
 * @author Administrator
 *
 */
public class MountInfo extends HardDiskCapacity {
	/**
	 * 盘符或者挂载点名,文件盘等
	 */
	private String mountName;
	/**
	 * 盘符或者挂载点路径
	 */
	private String mountPath;
	/**
	 * 盘符或者挂载点描述
	 */
	private String mountRemark;
	public String getMountName() {
		return mountName;
	}
	public void setMountName(String mountName) {
		this.mountName = mountName;
	}
	public String getMountPath() {
		return mountPath;
	}
	public void setMountPath(String mountPath) {
		this.mountPath = mountPath;
	}
	public String getMountRemark() {
		return mountRemark;
	}
	public void setMountRemark(String mountRemark) {
		this.mountRemark = mountRemark;
	}
	
	
	
	
}
