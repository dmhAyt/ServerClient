package com.ansheng.sqlite;

import java.sql.SQLException;
import java.time.format.DateTimeFormatter;

import com.ansheng.model.FileUploadInfoModel;

public class UploadRecordTable {
	private final String table_name = "UploadRecordTable";
	private final String create_table_sql = "create table "+table_name+" (FileID char(50) PRIMARY KEY     NOT NULL"
			+ ",FileName varchar(100) not null,FileLength integer not null"
			+ ",FileMD5 char(50) ,ShaStr char(50) "
			+ ",UploadTime datetime not null,UploadUser varchar(150)"
			+ ",FileLocalPath text)";
	private final String insert_row_sql = "insert into "+table_name+" (FileID,FileName,FileLength,FileMD5,ShaStr,UploadTime,UploadUser,FileLocalPath) "
			+ "values ('%s','%s',%d,'%s','%s','%s','%s','%s',%d,'%s',%d)";
	private final String insert_row_sql_2 = "insert into "+table_name+" (FileID,FileName,FileLength,FileMD5,ShaStr,UploadTime,UploadUser,FileLocalPath) "
			+ "values (?,?,?,?,?,?,?,?,?,?,?)";
	
	private final String find_all_row = "select * from "+table_name+" where 1=1 ";
	
	private final String delete_row  = "delete from "+ table_name +" where 1=1 ";
	
	private static SqliteDB _db = new SqliteDB();
	
	public UploadRecordTable() {
		if(!_db.tableExist("RecordTable")) {
			_db.createTable(create_table_sql);
		}
		
	}
	
	public int insertRow(FileUploadInfoModel model) {
		
		DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		String str = String.format(insert_row_sql, model.getFileID(),model.getFileName(),model.getFileLength(),model.getFileMD5()
				,model.getShaStr(),model.getUploadTime().format(df),model.getUploadUser(),model.getFileLocalPath());
		return _db.insertRow(str);
	}
	
	public int insertRowByParam(FileUploadInfoModel model) {
		
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
		return _db.insertRow(insert_row_sql_2,param);
	}
	
	public java.util.List<FileUploadInfoModel> findAllRows() throws Exception{
		return findRows(find_all_row);
	}
	
	public java.util.List<FileUploadInfoModel> findRows(String sql) throws Exception{
		return _db.findRows(sql,FileUploadInfoModel.class);
	}

	
	public int updateData(String sql) throws SQLException {
		return _db.updateData(sql);
	}

	public int updateData(String sql,Object[] param) throws SQLException {
		return _db.updateData(sql, param);
	}

	public int deleteData(String where) {
		String sql = delete_row;
		if(!where.startsWith("and") || !where.startsWith(" and")) sql +=  " and " + where; 
		else sql += " " + where;
		return _db.deleteData(sql);
	}

	/**
	 * 	删除行数据
	 * @param where where条件 “kk>1”等
	 * @param param	参数数组
	 * @return
	 * @throws SQLException
	 */
	public int deleteData(String where,Object[] param) throws SQLException {
		String sql = delete_row;
		if(!where.startsWith("and") || !where.startsWith(" and")) sql +=  " and " + where; 
		else sql += " " + where;
		return _db.deleteData(sql,param);
	}

	













}
