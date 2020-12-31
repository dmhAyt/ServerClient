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
import java.util.HashMap;
import java.util.Objects;

import org.sqlite.SQLiteConfig.Pragma;
import org.sqlite.SQLiteConnection;
import org.sqlite.core.DB;

import com.alibaba.fastjson.asm.Type;
import com.ansheng.model.FileRecordInfoModel;

/**
 * sqliteDB 的处理的基类
 * @author Administrator
 *
 */
public class SqliteDB {
	private  Connection _connect = null;
	
	/**
	 * 
	 */
	public SqliteDB() {
		_connect = GetConnect();
	} 
	
	/**
	 * 	创建表，
	 * @param tableStruc 建表语句。
	 */
	public void createTable(String tableStruc) {
		try(Statement stmt = _connect.createStatement()){
			stmt.executeUpdate(tableStruc);
			//_connect.close();
		}catch(Exception ex) {
			
		}
	}
	/**
	 * 	插入行信息
	 * @param sql insert语句
	 * @return	返回插入的行数。
	 */
	public int insertRow(String sql) {
		try(Statement stmt  = _connect.createStatement()){
			
			return stmt.executeUpdate(sql);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return 0;
		}
		
	}
	/**
	 * 	插入行信息
	 * @param sql	insert 语句
	 * @param param	参数列表。sql中使用?代替。
	 * @return	返回插入的行数
	 */
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
	/**
	 * 	查表信息
	 * @param sql	select 语句
	 * @return	返回数据列表
	 */
	public java.util.List<HashMap<String,Object>> findRows(String sql) {
		try(Statement stmt= _connect.createStatement()){
			ResultSet result =  stmt.executeQuery(sql);
			java.util.List<HashMap<String,Object>> results = toList(result);
			result.close();
			return results;
		}catch(Exception ex) {
			return null;
		}
	}
	/**
	 * 	查表信息
	 * @param <T> 泛型，表对应的类
	 * @param sql	select 语句
	 * @param tClass	类的Class
	 * @return	数据列表
	 * @throws Exception
	 */
	public <T extends Object> java.util.List<T> findRows(String sql,Class<T> tClass) throws Exception{
		try(Statement stmt= _connect.createStatement()){
			ResultSet result =  stmt.executeQuery(sql);
			return transferClass(result,tClass);
		}
		//ResultSet resultSet = findRows(sql);
	}
	
	/**
	 * 	查询行信息，避免SQL注入。
	 * @param sql	查询语句
	 * @param param	对应的参数
	 * @return
	 */
	public java.util.List<HashMap<String,Object>> findRows(String sql,Object[] param) {
		
		try(PreparedStatement stmt = _connect.prepareStatement(sql)){
			// 下标从1开始
			int index = 1;
			for(Object obj : param) {
				stmt.setObject(index, obj);
			}
			ResultSet  result=  stmt.executeQuery(sql);
			java.util.List<HashMap<String,Object>> results = toList(result);
			result.close();
			return results;
		}catch(Exception ex) {
			return null;
		}
	} 
	
	/**
	 * 	查询行信息，避免SQL注入。
	 * @param <T>
	 * @param sql
	 * @param param
	 * @param tClass
	 * @return
	 * @throws Exception
	 */
	public <T extends Object> java.util.List<T> findRows(String sql,Object[] param,Class<T> tClass) throws Exception{
		try(PreparedStatement stmt = _connect.prepareStatement(sql)){
			// 下标从1开始
			int index = 1;
			for(Object obj : param) {
				stmt.setObject(index, obj);
			}
			ResultSet  resultSet =  stmt.executeQuery(sql);
			return transferClass(resultSet,tClass);
		}catch(Exception ex) {
			return null;
		}
		
	}
	
	/**
	 * 	更新表。
	 * @param sql
	 * @return 受影响的行数
	 * @throws SQLException
	 */
 	public int updateData(String sql) throws SQLException {
		try(Statement stmt  = _connect.createStatement()){
			return stmt.executeUpdate(sql);
		}
	}
	/**
	 * 	更新表。
	 * @param sql
	 * @param param
	 * @return
	 * @throws SQLException
	 */
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
 	/**
 	 * 	删除表的数据
 	 * @param sql
 	 * @return
 	 */
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
	/**
	 * 	判断表是否存在
	 * @param tableName
	 * @return
	 */
	public Boolean tableExist(String tableName) {
		String sql = "select count(*) from sqlite_master where type='table' and name='"+tableName+"'";
		try(Statement stmt  = _connect.createStatement()){
			ResultSet result =  stmt.executeQuery(sql);
			int num = 0;
			while(result.next()) {
				num = result.getInt(1);
			}
			result.close();
			return num > 0;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	private <T extends Object> java.util.List<T> transferClass(ResultSet resultSet,Class<T> tClass) throws Exception{		
		java.util.List<T> result = new ArrayList<T>();
		//if(Objects.isNull(resultSet) || resultSet.getFetchSize() == 0) return result;
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
		
		int count = resultSet.getMetaData().getColumnCount();
		while(resultSet.next()){
			Class modelClass = model.getClass();
			// 这里循环行？
			// 循环列
			//ResultSetMetaData metaData = resultSet.getMetaData();
			for(int i = 1 ; i <= count; i ++) {
				Class model1 = modelClass;
				String fieldName = resultSet.getMetaData().getColumnName(i);
				for(; model1 != Object.class;) {
					Field[] fields = model1.getDeclaredFields();
					for(Field field : fields) {
						if(field.getName().equals(fieldName)){
							field.setAccessible(true);
							// 这里实时反射
							String str = field.getType().getName();
							switch(field.getType().getName().toLowerCase()) {
							case "int":
								field.setInt(model, resultSet.getInt(fieldName));
								break;
							case "byte":
								field.setByte(model, resultSet.getByte(fieldName));
								break;
							case "short":
								field.setShort(model, resultSet.getShort(fieldName));
								break;
							case "long":
								field.setLong(model, resultSet.getLong(fieldName));
								break;
							case "float":
								field.setFloat(model, resultSet.getFloat(fieldName));
								break;
							case "double":
								field.setDouble(model, resultSet.getDouble(fieldName));
								break;
							case "java.lang.string":
								field.set(model, resultSet.getString(fieldName));
								break;
							case "boolean":
								field.setBoolean(model, resultSet.getBoolean(fieldName));
								break;
							case "java.time.localdatetime":
								DateTimeFormatter formatter =DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
								field.set(model, LocalDateTime.parse(resultSet.getString(fieldName),formatter));
								break;
							default:
								field.set(model, resultSet.getObject(fieldName));
								break;
							}
						}
					}
					model1 =  model1.getSuperclass();
				}
				
			}
			result.add(model);
		} 	
		resultSet.close();
		return result;
	} 
	
	
	public java.util.List<HashMap<String,Object>> toList(ResultSet set) throws SQLException {
		java.util.List<HashMap<String,Object>> result = new ArrayList<HashMap<String,Object>>();
		while(set.next()){
			int count = set.getMetaData().getColumnCount();
			HashMap<String,Object> item = new HashMap<String, Object>();
			for(int i = 1 ; i <= count; i ++) {
				String fieldName = set.getMetaData().getColumnName(i);
				item.put(fieldName, set.getObject(fieldName));
			}
			result.add(item);
		}
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
