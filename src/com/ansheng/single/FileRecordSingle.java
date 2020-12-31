package com.ansheng.single;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.locks.ReentrantLock;

import com.ansheng.config.DaVersionConfig;
import com.ansheng.config.FileWorkerConfig;
import com.ansheng.model.FileRecordInfoModel;
import com.ansheng.sqlite.RecordTable;
import com.ansheng.util.FileTools;
import com.ansheng.util.OSInfo;

public class FileRecordSingle {
	
	private static final ReentrantLock _objLock = new ReentrantLock();
	private static FileRecordSingle _instance = null;
	private static Map<String,FileRecordInfoModel> _data = new HashMap<String, FileRecordInfoModel>();
	private static RecordTable _recordTable = new RecordTable(); 
	
	private FileRecordSingle() {
		
		java.util.List<FileRecordInfoModel> lst = null;
		try {
			lst = _recordTable.findAllRows();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		for(FileRecordInfoModel model : lst)
			_data.put(model.getFileID(), model);
	}
	
	/**
	 * get instance
	 * @return
	 */
	public static FileRecordSingle getInstance() {
		if(_instance == null) {
			if(_objLock.tryLock()) {
				try {
					if(_instance == null) {
						_instance = new FileRecordSingle();
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
	public void addParam(FileRecordInfoModel param) {
		addParam(param,true);
	}
	
	/**
	 * 	将文件加入到内存中
	 * @param param
	 */
	public void addParam(FileRecordInfoModel param,Boolean toTable) {
		_data.put(param.getFileID(), param);
		// 写入文件和写入数据库
		if(toTable)
			_recordTable.insertRow(param);
	}
	
	/**
	 * 删除内容
	 * @param key
	 * @return
	 */
	public FileRecordInfoModel removeParam(String key) {
		FileRecordInfoModel result = null;
		if(_data.containsKey(key)) {
			result = _data.get(key);
			_data.remove(key);
		}
		// 写入文件和写入数据库
		_recordTable.deleteData(" and FileID = '"+key+"'");
		return result;
	}
	
	/**
	 *  	获得指定的对象
	 * @param key
	 * @return
	 */
	public FileRecordInfoModel getObjByKey(String key) {
		if(_data.containsKey(key)) {
			return _data.get(key);
		}
		return null;
	}  
	
	/**
	 * 	通过文件的md5摘要获得对象
	 * @param md5
	 * @return
	 */
	public FileRecordInfoModel getObjByMD5(String md5) {
		return getObjByMd5AndSha(md5,"",0);
	}
	
	/**
	 * 	通过sha摘要获得对象
	 * @param sha 
	 * @return
	 */
	public FileRecordInfoModel getObjBySha(String sha) {
		return getObjByMd5AndSha("",sha,1);
	}
	
	/**
	 * 	通过md5和sha摘要获得对象
	 * @param md5
	 * @param sha
	 * @return
	 */
	public FileRecordInfoModel getObjByMd5AndSha(String md5,String sha) {
		return getObjByMd5AndSha(md5,sha,2);
	}
	
	
	public void writeToFile() throws UnsupportedEncodingException, IOException {
		DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		Set<String> data =  _data.keySet();
		String versionStr = DaVersionConfig.version + "|" + LocalDateTime.now().format(df);
		StringBuilder sb =new StringBuilder(versionStr);
		sb.append((char)1);
		boolean first = true;
		
		String fileName = FileWorkerConfig.Back_Up_Path + FileWorkerConfig.Back_Up_Db_Name + FileWorkerConfig.Back_Up_Db_Type;
		
		Iterator<String> iterator = data.iterator();
		while(iterator.hasNext()) {
			String item = iterator.next();
			if(!_data.containsKey(item)) continue;
			FileRecordInfoModel value = _data.get(item);
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
			sb.append("|"+value.getFileState());
			String strr = "";
			for(String str : value.getBackUpIDs()) {
				if(strr.equals("")) {
					strr = str;
				}else {
					strr = String.join(",", strr,str);
				}
			}
			sb.append("|"+strr);
			sb.append("|"+value.getFileDownLoadNum());
			first = false;
			
			// 写入到文件
			FileTools.appendByte(fileName, sb.toString().getBytes(FileWorkerConfig.File_Encode_Name));
			
			sb = new StringBuilder();
		}
		
		FileTools.appendByte(fileName, OSInfo.getPartLine().getBytes(FileWorkerConfig.File_Encode_Name));
	}
	
	
	
	/**
	 * 	从内存中获得已经传入的文件列表
	 * @param md5	文件MD5
	 * @param sha	文件Sha签名
	 * @param type	比较类型；0-只比较md5;1-只比较Sha;2-Md5和sha都比较
	 * @return
	 */
	private FileRecordInfoModel getObjByMd5AndSha(String md5,String sha,int type) {
		Set<String> data =  _data.keySet();
		Iterator<String> iterator = data.iterator();
		while(iterator.hasNext()) {
			String item = iterator.next();
			if(!_data.containsKey(item)) continue;
			FileRecordInfoModel value = _data.get(item);
			switch(type) {
				case 0:
					if(Objects.equals(value.getFileMD5(),md5)) {
						return value;
					}
					break;
				case 1: 
					if(Objects.equals(value.getShaStr(),sha)) {
						return value;
					}
					break;
				case 2:
					if(Objects.equals(value.getFileMD5(),md5) 
							&& Objects.equals(value.getShaStr(),sha)) {
						return value;
					}
					break;
				default :
					break;
			}
		}
		return null;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
