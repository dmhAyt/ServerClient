package com.ansheng.single;

import java.util.Enumeration;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

import com.ansheng.model.BackupsModel;

public class BackupsSingle {
	
	private static ReentrantLock _obj_lock = new ReentrantLock();
	private static ReentrantLock _state_lock = new ReentrantLock();
	private static BackupsSingle _instance = null;
	private static ConcurrentHashMap<String, BackupsModel> _hashData = new ConcurrentHashMap<String, BackupsModel>();
	private static Boolean _thread_run = false;
	
	private BackupsSingle() {
		
	}
	
	
	public static BackupsSingle instance() {
		if(_instance == null) {
			if(_obj_lock.tryLock()) {
				if(_instance == null) {
					_instance = new BackupsSingle();
				}
			}
			_obj_lock.unlock();
		}
		return _instance;
	}
	
	
	public void addModel(BackupsModel model) {
		
		_hashData.put(model.getFileId(), model);
		beginOrEndThread(true);
	}
	
	public BackupsModel removeModel(String key) {
		if(!_hashData.containsKey(key)) return null;
		BackupsModel result = _hashData.get(key);
		_hashData.remove(key);
		return result;
	}

	public BackupsModel getModel(String key) {
		if(!_hashData.containsKey(key)) return null;
		return _hashData.get(key);
	}
	
	public Enumeration<String> getKeys(){
		return _hashData.keys();
	} 
	
	public int getLstSize() {
		return _hashData.size();
	} 
	
	/**
	 * 修改线程运行状态为false
	 * @return 返回false，表示线程得继续运行。
	 */

	public Boolean alterThreadToCloseState() {
		return beginOrEndThread(false);
	}
	

	/**
	 * 修改线程运行状态。
	 * @param targetState true:表示线程运行状态。false表示线程关闭状态
	 * @return false可能是因为数组没有清空。
	 */
	private Boolean beginOrEndThread(Boolean targetState) {
		Boolean result = false;
		if(_thread_run != targetState) {
			if(_state_lock.tryLock()) {
				if(_thread_run != targetState) {
					if(targetState) {
						// 开一个线程
						Thread thread = new Thread(new BackupsThread());
						thread.setName("备份线程");
						thread.start();
						_thread_run = true;
						result = true;
					}else {
						if(_hashData.size() <= 0) {
							_thread_run = false;
							result = true;
						}
					}
				}
				_state_lock.unlock();
			}
		}else {
			return true;
		}
		return result;
	}
}


class BackupsThread implements Runnable{
	private BackupsSingle _backups = BackupsSingle.instance();
	
	@Override
	public void run() {
		while(true) {
			Enumeration<String> keys = _backups.getKeys();
			while(keys.hasMoreElements()) {
				String key = keys.nextElement();
				BackupsModel model = _backups.getModel(key);
				if(model == null) continue;
				// 处理备份逻辑
				if(model.getBackupsState() == 0) {
					// 需要分配备份的组员
					
				}
				if(model.getBackupsState() == 1 || model.getBackupsState() == 2) {
					// 直接备份
					
				}else if(model.getBackupsState() == 3) {
					continue;
				}else if(model.getBackupsState() == 4) {
					_backups.removeModel(model.getFileId());
				}
			}
			
			if(_backups.getLstSize() <= 0) {
				if(!_backups.alterThreadToCloseState()) break;
			}
		}
	}
}


