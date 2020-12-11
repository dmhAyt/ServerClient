package com.ansheng.constant;

public class StatusCodeConstant {
	/**
	 * 	-1000		表示失败，需要重新连接socket;(可认为本次失败了)
	 *	-1001		表示本次失败，无需重新连接socket,重新发送数据即可;不能和之前的数据一样，仍有可能失败；
	 *	0				表示本次失败【重传】，需要重新传相同的数据。【应用于上传和下载中数据块传输】
	 *	1000			本次请求成功，请继续。
	 *	2000 		方法成功完成。
	 */
	/**
	 * -1000 表示失败，需要重新连接socket;(可认为本次失败了)
	 */
	public static final int Method_Err_State  = -1000;
	/**
	 * -1001		表示本次失败，无需重新连接socket,重新发送数据即可;不能和之前的数据一样，仍有可能失败；
	 */
	public static final int This_Err_State  = -1001;
	/**
	 * 0				表示本次失败【重传】，需要重新传相同的数据。【应用于上传和下载中数据块传输】
	 */
	public static final int Repeat_Data_State  = 0;
	/**
	 * 1000			本次请求成功，请继续。
	 */
	public static final int This_Succ_State  = 1000;
	/**
	 * 2000 		方法成功完成。
	 */
	public static final int Method_Succ_State = 2000;
	
}
