package com.ansheng.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class UploadNoticeModel {
	private String fileId;
	private String finishTime;
	private String uploadUser;
	private String fileMD5;
	private String fileSha;
	
	
	/**
	 * 文件ID
	 * @return
	 */
	public String getFileID() {
		return fileId;
	}
	/**
	 * 设置文件ID
	 * @param fileID
	 */
	public void setFileID(String fileID) {
		fileId = fileID;
	}
	/**
	 * 获得上传成功的时间，格式为"yyyy-MM-dd HH:mm:ss"
	 * @return
	 */
	public String getFinishTime() {
		return finishTime;
	}
	/**
	 * 设置上传成功的时间，格式为"yyyy-MM-dd HH:mm:ss"
	 * @param finishTime
	 */
	public void setFinishTime(String finishTime) {
		finishTime = finishTime;
	}
	
	/**
	 * 设置上传成功的时间
	 * @param finishTime
	 */
	public void setFTime(LocalDateTime finishTime) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		this.finishTime = finishTime.format(formatter);
	}
	
	/**
	 * 获得上传人账号
	 * @return
	 */
	public String getUploadUser() {
		return uploadUser;
	}
	/**
	 * 设置上传人的账号
	 * @param uploadUser
	 */
	public void setUploadUser(String uploadUser) {
		this.uploadUser = uploadUser;
	}
	public String getFileMD5() {
		return fileMD5;
	}
	public void setFileMD5(String fileMD5) {
		fileMD5 = fileMD5;
	}
	public String getFileSha() {
		return fileSha;
	}
	public void setFileSha(String fileSha) {
		fileSha = fileSha;
	}
	
	
}
