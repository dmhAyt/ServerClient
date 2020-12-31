package com.ansheng.single;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.ReentrantLock;

import com.ansheng.config.DaVersionConfig;
import com.ansheng.config.FileWorkerConfig;
import com.ansheng.model.FileRecordInfoModel;
import com.ansheng.model.FileUploadInfoModel;
import com.ansheng.sqlite.UploadRecordTable;
import com.ansheng.util.FileTools;
import com.ansheng.util.OSInfo;

public class FileUploadSingle {
	
	private static final ReentrantLock _objLock = new ReentrantLock();
	private static FileUploadSingle _instance = null;
	private static Map<String,FileUploadInfoModel> _data = new HashMap<String, FileUploadInfoModel>();
	private static UploadRecordTable _uploadTable = null;
	

	private FileUploadSingle() {
		_uploadTable = new UploadRecordTable(); 
	}
	
	/**
	 * get instance
	 * @return
	 */
	public static FileUploadSingle getInstance() {
		if(_instance == null) {
			if(_objLock.tryLock()) {
				try {
					if(_instance == null) {
						_instance = new FileUploadSingle();
					}
				}finally {
					_objLock.unlock();
				}
			}
		}
		return _instance;
	}

	
	/**
	 * 	将文件加入到内存中
	 * @param param
	 */
	public void addParam(FileUploadInfoModel param) {
		_data.put(param.getFileID(), param);
		// 写入文件和数据库
		_uploadTable.insertRow(param);
	}
	
	/**
	 * 	将文件加入到内存中--不加入表
	 * @param param
	 */
	public void addParam(FileUploadInfoModel param,Boolean toTable) {
		_data.put(param.getFileID(), param);
		if(toTable)
			_uploadTable.insertRow(param);
	}
	
	/**
	 *	 删除内容
	 * @param key
	 * @return
	 */
	public FileUploadInfoModel removeParam(String key) {
		FileUploadInfoModel result = null;
		if(_data.containsKey(key)) {
			result = _data.get(key);
			_data.remove(key);
		}
		_uploadTable.deleteData(" and FileID = '"+key+"'");
		return result;
	}
	
	/**
	 *  	获得指定的对象
	 * @param key
	 * @return
	 */
	public FileUploadInfoModel getObjByKey(String key) {
		if(_data.containsKey(key)) {
			return _data.get(key);
		}
		return null;
	}  

	/**
	 * 	 写入到文件中
	 * @throws UnsupportedEncodingException
	 * @throws IOException
	 */
	public void writeToFile() throws UnsupportedEncodingException, IOException {
		DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		Set<String> data =  _data.keySet();
		String versionStr = DaVersionConfig.version + "|" + LocalDateTime.now().format(df);
		StringBuilder sb =new StringBuilder(versionStr);
		sb.append((char)1);
		boolean first = true;
		
		String fileName = FileWorkerConfig.Back_Up_Path + FileWorkerConfig.Back_Up_Db_Name +"_Upload"+ FileWorkerConfig.Back_Up_Db_Type;
		
		Iterator<String> iterator = data.iterator();
		while(iterator.hasNext()) {
			String item = iterator.next();
			if(!_data.containsKey(item)) continue;
			FileUploadInfoModel value = _data.get(item);
			if(!first) {
				sb.append((char)2);
			}
			sb.append(value.getFileID());
			sb.append("|"+value.getFileName());
			sb.append("|"+value.getFileLength());
			sb.append("|"+value.getFileMD5());
			sb.append("|"+value.getShaStr());
			sb.append("|"+value.getUploadTime().toString().replace("T", " "));
			sb.append("|"+value.getUploadUser());
			sb.append("|"+value.getFileLocalPath());
			first = false;
			
			// 写入到文件
			FileTools.appendByte(fileName, sb.toString().getBytes(FileWorkerConfig.File_Encode_Name));
			
			sb = new StringBuilder();
		}
		
		FileTools.appendByte(fileName, OSInfo.getPartLine().getBytes(FileWorkerConfig.File_Encode_Name));
	}
	
}
