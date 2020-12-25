package com.ansheng.sqlite;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Objects;

import org.sqlite.SQLiteConfig.Pragma;
import org.sqlite.SQLiteConnection;

import com.alibaba.fastjson.asm.Type;
import com.ansheng.model.FileRecordInfoModel;

public class SqliteDB {
	private  Connection _connect = null;
	
	
	public SqliteDB() {
		_connect = GetConnect();
	} 
	
	
	public void createTable(String tableStruc) {
		try(Statement stmt = _connect.createStatement()){
			stmt.executeUpdate(tableStruc);
			//_connect.close();
		}catch(Exception ex) {
			
		}
	}
	
	public int insertRow(String sql) {
		try(Statement stmt  = _connect.createStatement()){
			
			return stmt.executeUpdate(sql);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return 0;
		}
		
	}
	
	public int insertRow(String sql,Object[] param) {
		try(PreparedStatement stmt  = _connect.prepareStatement(sql)){
			// 下标从1开始
			int index = 1;
			for(Object obj : param) {
				stmt.setObject(index, obj);
			}
			return stmt.executeUpdate(sql);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return 0;
		}
		
	}

	public ResultSet findRows(String sql) {
		try(Statement stmt = _connect.createStatement()){
			ResultSet result =  stmt.executeQuery(sql);
			return result;
		}catch(Exception ex) {
			return null;
		}
	}
	
	public <T extends Object> java.util.List<T> findRows(String sql,Class<T> tClass) throws Exception{
		
		ResultSet resultSet = findRows(sql);
		return transferClass(resultSet,tClass);
	}
	
	public ResultSet findRows(String sql,Object[] param) {
		
		try(PreparedStatement stmt = _connect.prepareStatement(sql)){
			// 下标从1开始
			int index = 1;
			for(Object obj : param) {
				stmt.setObject(index, obj);
			}
			return stmt.executeQuery(sql);
		}catch(Exception ex) {
			return null;
		}
	} 
	
	
	public <T extends Object> java.util.List<T> findRows(String sql,Object[] param,Class<T> tClass) throws Exception{
		ResultSet resultSet = findRows(sql,param);
		return transferClass(resultSet,tClass);
	}
	
	
 	public int updateData(String sql) throws SQLException {
		try(Statement stmt  = _connect.createStatement()){
			return stmt.executeUpdate(sql);
		}
	}
	
 	public int updateData(String sql,Object[] param) throws SQLException {
 		try(PreparedStatement stmt  = _connect.prepareStatement(sql)){
 			// 下标从1开始
			int index = 1;
			for(Object obj : param) {
				stmt.setObject(index, obj);
			}
			return stmt.executeUpdate(sql);
		}
 	}
 	
	public int deleteData(String sql) {
		try(Statement stmt  = _connect.createStatement()){
			return stmt.executeUpdate(sql);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return 0;
		}
	}
	
	public int deleteData(String sql,Object[] param) throws SQLException {
		try(PreparedStatement stmt  = _connect.prepareStatement(sql)){
 			// 下标从1开始
			int index = 1;
			for(Object obj : param) {
				stmt.setObject(index, obj);
			}
			return stmt.executeUpdate(sql);
		}
	}

	public Boolean tableExist(String tableName) {
		String sql = "select count(*) from sqlite_master where type='table' and name='"+tableName+"'";
		try(Statement stmt  = _connect.createStatement()){
			ResultSet result =  stmt.executeQuery(sql);
			int num =  result == null ? 0 : result.getFetchSize();
			result.close();
			return num > 0;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}
	
	private <T extends Object> java.util.List<T> transferClass(ResultSet resultSet,Class<T> tClass) throws Exception{
		java.util.List<T> result = new ArrayList<T>();
		if(Objects.isNull(resultSet) || resultSet.getFetchSize() == 0) return result;
		@SuppressWarnings("unchecked")
		Constructor<T>[] constru = (Constructor<T>[]) tClass.getConstructors();
		T model = null;
		for(Constructor<T> con : constru) {
			if(con.getParameterCount() == 0) {
				con.setAccessible(true);
				model = con.newInstance();
				break;
			} 
		}
		if(model == null)  new Exception("请提供无参构造器。");
		
		do {
			Class<? extends Object> modelClass = model.getClass();
			// 这里循环行？
			// 循环列
			ResultSetMetaData metaData = resultSet.getMetaData();
			for(int i = 0 ; i < metaData.getColumnCount(); i ++) {
				// 这里实时反射
				String fieldName = metaData.getColumnName(i);
				Field  field = modelClass.getField(fieldName);
				if(field == null) continue;
				switch(field.getType().toString()) {
				case "int":
					field.setInt(model, resultSet.getInt(fieldName));
					break;
				case "byte":
					field.setByte(model, resultSet.getByte(fieldName));
					break;
				case "short":
					field.setShort(model, resultSet.getShort(fieldName));
					break;
				case "float":
					field.setFloat(model, resultSet.getFloat(fieldName));
					break;
				case "double":
					field.setDouble(model, resultSet.getDouble(fieldName));
					break;
				case "String":
					field.set(model, resultSet.getString(fieldName));
					break;
				case "Boolean":
					field.setBoolean(model, resultSet.getBoolean(fieldName));
					break;
				default:
					field.set(model, resultSet.getObject(fieldName));
					break;
				}
			}
			result.add(model);
		}
		while(resultSet.next()); 	
		resultSet.close();
		return result;
	} 
	
	
	
	private Connection GetConnect() {
		if(this._connect ==  null) {
			try {
				Class.forName("org.sqlite.JDBC");
				_connect = DriverManager.getConnection("jdbc:sqlite:test2.db");
			} catch (ClassNotFoundException | SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return _connect;
	}
	
	
	
}
