package com.ansheng.server.inter.impl;

import com.ansheng.model.FileRecordInfoModel;
import com.ansheng.server.inter.RecoveryInfoInt;
import com.ansheng.single.FileRecordSingle;
import com.ansheng.sqlite.RecordTable;

public class RecordRecoveryImpl implements RecoveryInfoInt {
	
	private FileRecordSingle _recordSingle = FileRecordSingle.getInstance();
	
	@Override
	public void recovery() {
		// TODO Auto-generated method stub
		RecordTable recordTable = new RecordTable(); 
		java.util.List<FileRecordInfoModel> lst = null;
		try {
			lst = recordTable.findAllRows();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		for(FileRecordInfoModel model : lst)
			_recordSingle.addParam(model,false);
			// 读取配置,看是否需要扫描文件。--以后加
	}

}
