package com.ansheng.model;

import java.util.LinkedList;
import java.util.List;

/**
 * 单线程备份，暂时不考虑多线程。
 * @author dengan 创建时间 2020-12-29
 *
 */
public class BackupsModel {
	/**
	 * 文件唯一ID
	 */
	private String fileId;
	/**
	 * 备份状态。
	 * 0：准备备份-未分配组员
	 * 1：准备备份-已经分配组员
	 * 2：正在备份-不是组员备份阶段
	 * 3：正在备份-组员备份阶段
	 * 4：备份完成
	 */
	private byte   backupsState;
	/**
	 * 需要备份的组员列表【组员ID列表】
	 */
	private List<String> backupsLst = new LinkedList<String>();
	/**
	 * 已经备份完成的组员ID。
	 */
	private List<String> backupsFinishLst = new LinkedList<String>();
	
	
	
	public BackupsModel(String fileId, byte backupsState) {
		super();
		this.fileId = fileId;
		this.backupsState = backupsState;
	}
	
	public BackupsModel() {
		
	}
	
	/**
	 * 获得文件唯一ID
	 * @return
	 */
	public String getFileId() {
		return fileId;
	}
	/**
	 * 设置文件唯一ID
	 * @param fileID
	 */
	public void setFileId(String fileId) {
		this.fileId = fileId;
	}
	/**
	 * 获得备份状态
	 * 0：准备备份-未分配组员
	 * 1：准备备份-已经分配组员
	 * 2：正在备份-不是组员备份阶段
	 * 3：正在备份-组员备份阶段
	 * 4：备份完成
	 * @return
	 */
	public byte getBackupsState() {
		return backupsState;
	}
	/**
	 * 设置备份状态，备份状态如下：
	 * 0：准备备份-未分配组员
	 * 1：准备备份-已经分配组员
	 * 2：正在备份-不是组员备份阶段
	 * 3：正在备份-组员备份阶段
	 * 4：备份完成
	 * @param backupsState
	 */
	public void setBackupsState(byte backupsState) {
		this.backupsState = backupsState;
	}
	/**
	 * 添加组员ID进入备份数组
	 * @param groupNumberID
	 */
	public void addBackupsLst(String groupNumberId) {
		this.backupsLst.add(groupNumberId);
	}
	/**
	 * 增加完成的ID进入到完成组。
	 * @param groupNumberID
	 */
	public void addBackupsFinishLst(String groupNumberId) {
		if(this.backupsLst.contains(groupNumberId)) {
			this.backupsLst.remove(groupNumberId);
		}
		this.backupsFinishLst.add(groupNumberId);
	}
	/**
	 * 获得备份组的下一个成员ID，如果没有了将返回null
	 * @return
	 */
	public String getBackupsNumber() {
		if(this.backupsLst.size() == 0) {
			return null;
		}
		String number = this.backupsLst.get(this.backupsLst.size() - 1);
		return number;
	}
	/**
	 * 获得备份组的长度
	 * @return
	 */
	public int getBackupsLstCount() {
		return this.backupsLst.size();
	}
	/**
	 * 获得完成组列表
	 * @return
	 */
	public List<String> getFinishBackpus() {
		return this.backupsFinishLst;
	};
	/**
	 * 获得完成组的长度
	 * @return
	 */
	public int getFinishLstSize() {
		return this.backupsFinishLst.size();
	}






}
