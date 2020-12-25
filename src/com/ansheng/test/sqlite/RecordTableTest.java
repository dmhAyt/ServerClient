package com.ansheng.test.sqlite;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.ansheng.model.FileRecordInfoModel;
import com.ansheng.sqlite.RecordTable;

class RecordTableTest {

	@Test
	void test() {
		//fail("Not yet implemented");
		RecordTable rt = new RecordTable();
	}
	@Test
	void testInsert() {
		//fail("Not yet implemented");
		RecordTable rt = new RecordTable();
		FileRecordInfoModel model = new FileRecordInfoModel();
		model.setFileID("文件ID2");
		model.setFileName("文件名");
		model.setFileLength(10);
		model.setFileMD5("文件MD5");
		model.setShaStr("sha");
		model.setUploadTime(LocalDateTime.now());
		model.setUploadUser("上传人");
		model.setFileLocalPath("本地路径");
		model.setFileState(0);
		model.addBackUpIDs("12");;
		model.addFileDownLoadNum();;
		int i = rt.insertRow(model);
		int nu = 0;
	}
	@Test
	void testFindALl() {
		RecordTable rt = new RecordTable();
		List<FileRecordInfoModel> models = null;
		try {
			 models = rt.findAllRows();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		int i = 0;
	}
	@Test
	void delete() {
		RecordTable rt = new RecordTable();
		int num = -10;
		try {
			 num = rt.deleteData("and fileID='文件ID3'");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		int i = 0;
	}
}
