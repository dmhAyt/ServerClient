package com.ansheng.model;

import java.time.LocalDateTime;

public class FileUploadInfoModel {
	/**
	 * 	文件唯一ID
	 */
	private String FileID;
	/**
	 * 	文件名。含后缀
	 */
	private String FileName;
	/**
	 * 	文件的长度【文件大小】
	 */
	private long   FileLength;
	/**
	 *  	文件MD5摘要
	 */
	private String FileMD5;
	/**
	 * 	文件Sha的摘要
	 */
	private String ShaStr;
	/**
	 * 	文件上传的时间
	 */
	private LocalDateTime UploadTime;
	/**
	 * 	文件的上传的用户
	 */
	private String UploadUser;
	/**
	 * 	文件本地存储的地址，含文件类型。【可能没有；可能有多个，多个时使用*】
	 */
	private String FileLocalPath;
	
	
	
	
	public FileUploadInfoModel(String fileID, String fileName, long fileLength, String fileMD5, String shaStr,
			LocalDateTime uploadTime, String uploadUser, String fileLocalPath) {
		super();
		this.FileID = fileID;
		this.FileName = fileName;
		this.FileLength = fileLength;
		this.FileMD5 = fileMD5;
		this.ShaStr = shaStr;
		this.UploadTime = uploadTime;
		this.UploadUser = uploadUser;
		this.FileLocalPath = fileLocalPath;
	}

	public FileUploadInfoModel() {
		super();
	}
	
	public String getFileID() {
		return FileID;
	}
	public void setFileID(String fileID) {
		FileID = fileID;
	}
	public String getFileName() {
		return FileName;
	}
	public void setFileName(String fileName) {
		FileName = fileName;
	}
	public long getFileLength() {
		return FileLength;
	}
	public void setFileLength(long fileLength) {
		FileLength = fileLength;
	}
	public String getFileMD5() {
		return FileMD5;
	}
	public void setFileMD5(String fileMD5) {
		FileMD5 = fileMD5;
	}
	public LocalDateTime getUploadTime() {
		return UploadTime;
	}
	public void setUploadTime(LocalDateTime uploadTime) {
		UploadTime = uploadTime;
	}
	public String getUploadUser() {
		return UploadUser;
	}
	public void setUploadUser(String uploadUser) {
		UploadUser = uploadUser;
	}
	public String getFileLocalPath() {
		return FileLocalPath;
	}
	public void setFileLocalPath(String fileLocalPath) {
		FileLocalPath = fileLocalPath;
	}
	public String getShaStr() {
		return ShaStr;
	}
	public void setShaStr(String shaStr) {
		ShaStr = shaStr;
	}
	@Override
	public String toString() {
		return "FileUploadInfoModel [FileID=" + FileID + ", FileName=" + FileName + ", FileLength=" + FileLength
				+ ", FileMD5=" + FileMD5 + ", ShaStr=" + ShaStr + ", UploadTime=" + UploadTime + ", UploadUser="
				+ UploadUser + ", FileLocalPath=" + FileLocalPath + "]";
	}
	
	
	
}
