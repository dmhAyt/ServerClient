package com.ansheng.server.inter;

/**
 * 	用于程序启动时对文件进行恢复。
 * @author dengan 2020-12-30
 *
 */
public interface RecoveryInfoInt {
	/**
	 * 	具体恢复逻辑
	 */
	void recovery();
}
