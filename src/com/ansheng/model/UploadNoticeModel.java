package com.ansheng.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class UploadNoticeModel {
	private String FileID;
	private String FinishTime;
	private String GroupID;
	private String UploadUser;
	private String FileMD5;
	private String FileSha;
	
	
	/**
	 * 文件ID
	 * @return
	 */
	public String getFileID() {
		return FileID;
	}
	/**
	 * 设置文件ID
	 * @param fileID
	 */
	public void setFileID(String fileID) {
		FileID = fileID;
	}
	/**
	 * 获得上传成功的时间，格式为"yyyy-MM-dd HH:mm:ss"
	 * @return
	 */
	public String getFinishTime() {
		return FinishTime;
	}
	/**
	 * 设置上传成功的时间，格式为"yyyy-MM-dd HH:mm:ss"
	 * @param finishTime
	 */
	public void setFinishTime(String finishTime) {
		FinishTime = finishTime;
	}
	
	/**
	 * 设置上传成功的时间
	 * @param finishTime
	 */
	public void setFTime(LocalDateTime finishTime) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		FinishTime = finishTime.format(formatter);
	}
	
	/**
	 * 获得组长的ID
	 * @return
	 */
	public String getGroupID() {
		return GroupID;
	}
	/**
	 * 设置组长ID
	 * @param groupID
	 */
	public void setGroupID(String groupID) {
		GroupID = groupID;
	}
	/**
	 * 获得上传人账号
	 * @return
	 */
	public String getUploadUser() {
		return UploadUser;
	}
	/**
	 * 设置上传人的账号
	 * @param uploadUser
	 */
	public void setUploadUser(String uploadUser) {
		UploadUser = uploadUser;
	}
	public String getFileMD5() {
		return FileMD5;
	}
	public void setFileMD5(String fileMD5) {
		FileMD5 = fileMD5;
	}
	public String getFileSha() {
		return FileSha;
	}
	public void setFileSha(String fileSha) {
		FileSha = fileSha;
	}
	
	
}
