package com.ansheng.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class FileRecordInfoModel extends FileUploadInfoModel{
	
	/**
	 * 	文件状态【0-正常，1-标志删除，2-删除了】
	 */
	private int FileState;
	/**
	 * 	备份的组员唯一ID列表	【n0001,n0002】
	 */
	private List<String> BackUpIDs = new ArrayList<String>(10);
	/**
	 * 	文件下载的次数。
	 */
	private int FileDownLoadNum;
	
	
	public FileRecordInfoModel() {
		super();
	}
	
	
	
	
	public FileRecordInfoModel(FileUploadInfoModel model , int fileState, List<String> backUpIDs,
			int fileDownLoadNum) {
		super(model.getFileID(), model.getFileName(), model.getFileLength(), model.getFileMD5()
				, model.getShaStr(), model.getUploadTime(), model.getUploadUser(), model.getFileLocalPath());
		FileState = fileState;
		BackUpIDs = backUpIDs;
		FileDownLoadNum = fileDownLoadNum;
	}


	
	public FileRecordInfoModel(String fileID, String fileName, long fileLength, String fileMD5, String shaStr,
			LocalDateTime uploadTime, String uploadUser, String fileLocalPath, int fileState, List<String> backUpIDs,
			int fileDownLoadNum) {
		super(fileID, fileName, fileLength, fileMD5, shaStr, uploadTime, uploadUser, fileLocalPath);
		FileState = fileState;
		BackUpIDs = backUpIDs;
		FileDownLoadNum = fileDownLoadNum;
	}



	/**
	 * 	获得文件状态。【0-正常，1-标志删除，2-删除了】
	 * @return
	 */
	public int getFileState() {
		return FileState;
	}
	/**
	 * 	设置文件的状态
	 * @param fileState
	 */
	public void setFileState(int fileState) {
		if(fileState < 0 || fileState > 2) fileState = 0;
		FileState = fileState;
	}
	
	/**
	 * 	获得备份的组员ID，
	 * 	可能为null。
	 * 	可能为多个。
	 * @return
	 */
	public List<String> getBackUpIDs() {
		return BackUpIDs;
	}
	
	/**
	 * 	设置备份的组员ID，
	 * 	可能为null。
	 * 	可能为多个。
	 * @return
	 */
	public void setBackUpIDs(List<String> backUpIDs) {
		BackUpIDs = backUpIDs;
	}
	
	/**
	 * 	获得文件下载次数
	 * @return
	 */
	public int getFileDownLoadNum() {
		return FileDownLoadNum;
	}
	
	/**
	 * 	文件加1
	 */
	public void addFileDownLoadNum() {
		long len = (long) this.FileDownLoadNum + 1;
		if(len > Integer.MAX_VALUE) this.FileDownLoadNum = 0;
		FileDownLoadNum += 1;
	}

	@Override
	public String toString() {
		return "FileRecordInfoModel [FileState=" + FileState + ", BackUpIDs=" + BackUpIDs + ", FileDownLoadNum="
				+ FileDownLoadNum + ", toString()=" + super.toString() + "]";
	}
	
	
	
	
	
}
