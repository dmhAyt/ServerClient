package com.ansheng.sqlite;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import com.ansheng.model.FileRecordInfoModel;

public class RecordTable {
	private final String table_name = "RecordTable";
	private final String create_table_sql = "create table " + table_name + " (FileID char(50) PRIMARY KEY     NOT NULL"
			+ ",FileName varchar(100) not null,FileLength integer not null" + ",FileMD5 char(50) ,ShaStr char(50) "
			+ ",UploadTime datetime not null,UploadUser varchar(150)"
			+ ",FileLocalPath text,FileState int2,BackUpIDs text,FileDownLoadNum int)";
	private final String insert_row_sql = "insert into " + table_name
			+ " (FileID,FileName,FileLength,FileMD5,ShaStr,UploadTime,UploadUser,FileLocalPath,FileState,BackUpIDs,FileDownLoadNum) "
			+ "values ('%s','%s',%d,'%s','%s','%s','%s','%s',%d,'%s',%d)";
	private final String insert_row_sql_2 = "insert into " + table_name
			+ " (FileID,FileName,FileLength,FileMD5,ShaStr,UploadTime,UploadUser,FileLocalPath,FileState,BackUpIDs,FileDownLoadNum) "
			+ "values (?,?,?,?,?,?,?,?,?,?,?)";

	private final String find_all_row = "select * from '" + table_name + "' ;";

	private final String delete_row = "delete from " + table_name + " where 1=1 ";

	private static SqliteDB _db = new SqliteDB();

	public RecordTable() {

		if (!_db.tableExist("RecordTable")) {
			_db.createTable(create_table_sql);
		}

	}

	public int insertRow(FileRecordInfoModel model) {

		String ff = String.join(",", model.getBackUpIDs());

		DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		String str = String.format(insert_row_sql, model.getFileID(), model.getFileName(), model.getFileLength(),
				model.getFileMD5(), model.getShaStr(), model.getUploadTime().format(df), model.getUploadUser(),
				model.getFileLocalPath(), model.getFileState(), ff, model.getFileDownLoadNum());
		return _db.insertRow(str);
	}

	public int insertRowByParam(FileRecordInfoModel model) {

		String ff = String.join(",", model.getBackUpIDs());
		DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		Object[] param = new Object[11];
		param[0] = model.getFileID();
		param[1] = model.getFileName();
		param[2] = model.getFileLength();
		param[3] = model.getFileMD5();
		param[4] = model.getShaStr();
		param[5] = model.getUploadTime().format(df);
		param[6] = model.getUploadUser();
		param[7] = model.getFileLocalPath();
		param[8] = model.getFileState();
		param[9] = ff;
		param[10] = model.getFileDownLoadNum();
		return _db.insertRow(insert_row_sql_2, param);
	}

	public java.util.List<FileRecordInfoModel> findAllRows() throws Exception {
		return findRows(find_all_row);
	}

	public java.util.List<FileRecordInfoModel> findRows(String sql) throws Exception {
		java.util.List<FileRecordInfoModel> result = new ArrayList<FileRecordInfoModel>();
		java.util.List<HashMap<String, Object>> resultSet = _db.findRows(sql);

		for (int i = 0; i < resultSet.size(); i++) {
			FileRecordInfoModel model = new FileRecordInfoModel();
			Iterator<String> keys = resultSet.get(i).keySet().iterator();
			while (keys.hasNext()) {
				String key = keys.next();
				Object value = resultSet.get(i).get(key);
				switch (key) {
				case "FileID":
					model.setFileID(value.toString());
					break;
				case "FileName":
					model.setFileName(value.toString());
					break;
				case "FileLength":
					model.setFileLength(Long.parseLong(value.toString()));
					break;
				case "FileMD5":
					model.setFileMD5(value.toString());
					break;
				case "ShaStr":
					model.setShaStr(value.toString());
					break;
				case "UploadTime":
					DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
					switch (value.toString().length()) {
					case 8:
						formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
						break;
					case 10:
						formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
						break;
					case 12:
						formatter = DateTimeFormatter.ofPattern("HH:mm:ss.SSS");
						break;
					case 19:
						formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
						break;
					case 23:
						formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
						break;
					default:
						formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
						break;
					}
					model.setUploadTime(LocalDateTime.parse(value.toString(), formatter));
					break;
				case "UploadUser":
					model.setUploadUser(value.toString());
					break;
				case "FileLocalPath":
					model.setFileLocalPath(value.toString());
					break;
				case "FileState":
					model.setFileState(Integer.parseInt(value.toString()));
					break;
				case "FileDownLoadNum":
					model.setFileDownLoadNum(Integer.parseInt(value.toString()));
					break;
				case "BackUpIDs":
					String strs = value.toString();
					if (strs == null)
						break;
					String[] strArr = strs.split(",");
					for (String str : strArr)
						model.addBackUpIDs(str);
					break;
				default:
					break;
				}
			}
			result.add(model);
		}

		return result;
	}

	public int updateData(String sql) throws SQLException {
		return _db.updateData(sql);
	}

	public int updateData(String sql, Object[] param) throws SQLException {
		return _db.updateData(sql, param);
	}

	public int deleteData(String where) {
		String sql = delete_row;
		if (!where.trim().startsWith("and"))
			sql += " and " + where;
		else
			sql += " " + where;
		return _db.deleteData(sql);
	}

	/**
	 * 删除行数据
	 * 
	 * @param where where条件 “kk>1”等
	 * @param param 参数数组
	 * @return
	 * @throws SQLException
	 */
	public int deleteData(String where, Object[] param) throws SQLException {
		String sql = delete_row;
		if (!where.startsWith("and") || !where.startsWith(" and"))
			sql += " and " + where;
		else
			sql += " " + where;
		return _db.deleteData(sql, param);
	}

}
