package com.ansheng.server.inter.impl;

import com.ansheng.model.FileUploadInfoModel;
import com.ansheng.server.inter.RecoveryInfoInt;
import com.ansheng.single.FileUploadSingle;
import com.ansheng.sqlite.UploadRecordTable;

/**
 * 	恢复正在上传的文件到内存中。
 * @author Administrator 2020-12-30
 *
 */
public class UploadRecoveryImpl implements RecoveryInfoInt {
	private  FileUploadSingle _fileSingle = FileUploadSingle.getInstance(); 
	
	@Override
	public void recovery() {
		UploadRecordTable uploadTable = new UploadRecordTable(); 
		java.util.List<FileUploadInfoModel> lst = null;
		try {
			lst = uploadTable.findAllRows();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		for(FileUploadInfoModel model : lst)
			_fileSingle.addParam(model,false);
		
		// 读取配置,看是否需要扫描文件。--以后加
	}

}
