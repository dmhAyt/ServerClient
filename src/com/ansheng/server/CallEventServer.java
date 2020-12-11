package com.ansheng.server;

import java.io.IOException;
import java.net.Socket;
import java.util.Objects;

import com.ansheng.constant.MethodCodeConstant;
import com.ansheng.constant.StatusCodeConstant;
import com.ansheng.exception.ReadTimeOutException;
import com.ansheng.factory.CreateSocketInt;
import com.ansheng.factory.impl.CreateSocketImp;
import com.ansheng.model.event.IdentifyModel;
import com.ansheng.model.socket.ResultSocket;
import com.ansheng.myenum.IdentifyEnum;
import com.ansheng.single.ConfigSingle;
import com.ansheng.util.Tools;
import com.ansheng.util.socket.SocketTools;

public class CallEventServer {

	private CreateSocketInt _createSocket = null;
	private ConfigSingle _configSingle = null;
	
	public CallEventServer() {
		_createSocket = new CreateSocketImp();
		_configSingle = ConfigSingle.getInstance();
	}
	
	/***
	 * 1 调用删除接口，从而删除组员存储的文件
	 * @param ip	组员的IP
	 * @param fp	组员的事务端口
	 * @param dataStr	需要删除的数据
	 * @param identifyModel	身份信息
	 * @return	返回是否成功
	 * @throws IOException socket 读写异常。
	 */
	public boolean callDeleteFile(String ip, int fp, String dataStr, IdentifyModel identifyModel) throws IOException {
		// TODO Auto-generated method stub
		
		// 创建一个socket
		Socket socket = null;
		try {
			socket = _createSocket.createSocketClient(ip, fp);
		}catch(IOException ioe) {
			ioe.printStackTrace();
			return false;
		}
		
		// 执行删除
		IdentifyModel sendIndentify = new IdentifyModel();
		sendIndentify.setToken(identifyModel.getToken());
		sendIndentify.setUserCode(identifyModel.getUserCode());
		int orig = _configSingle.getIdentity() == IdentifyEnum.GROUP_LEADER 
					|| _configSingle.getIdentity() == IdentifyEnum.Deputy_GROUP_LEADER ? 4 : identifyModel.getOriginServer();
		sendIndentify.setOriginServer(orig);
		
		String param  = Tools.bean2Json(sendIndentify);
		SocketTools.sendData(socket, param,MethodCodeConstant.Delete_Code);
		
		// 接收一个回应
		ResultSocket codeResult = null;
		try {
			 codeResult = SocketTools.receiveDataMethod(socket, true);
		} catch (ReadTimeOutException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			socket.close();
			return false;
		}
		if(codeResult == null || codeResult.getMethodCode() != 1001) return false;
		int times = 3;
		boolean methodResult = false;
		while(times-- > 0) {
			SocketTools.sendData(socket, dataStr);
			// 读取结果
			String optResult = "";
			byte[] data;
			try {
				data = SocketTools.receiveData(socket, true);
			} catch (ReadTimeOutException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				continue;
			}
			optResult = Tools.encodeUTF82Str(data);
			methodResult= Objects.equals(optResult, "1");
		}
		socket.close();
		return methodResult;
	}
	
	/**
	 * 通知到组长服务器
	 * @param ip	组长服务器的IP或者跟踪服务器的IP
	 * @param port	组长服务器的端口或者跟踪服务器的端口
	 * @param dataStr	需要发送给上级的数据，结构为：文件的唯一码|文件名【含路径】|文件大小|MD5|sha|上传的用户ID|组员ID[n0001|d0001|l0001|s0001]
	 * @param identifyModel	身份，如果是内部通信可以不用Token
	 * @return	返回成功或者失败。
	 * @throws IOException
	 */
	public boolean callUploadFinish(String ip,int port,String dataStr,IdentifyModel identifyModel) throws IOException {
		// 创建一个socket
		Socket socket = null;
		try {
			socket = _createSocket.createSocketClient(ip, port);
		}catch(IOException ioe) {
			ioe.printStackTrace();
			return false;
		}
		
		// 发送链接请求
		String param  = Tools.bean2Json(identifyModel);
		SocketTools.sendData(socket, param,MethodCodeConstant.Up_Finish_Code);
		String[] params  = null;
		
		int times = 3;
		while(times-->0) {
			try {
				ResultSocket resultSocket = SocketTools.receiveDataMethod(socket, true);
				String rStr = Tools.encodeUTF82Str(resultSocket.getMethodParam());
				params = rStr.split("|");
				
				switch(Integer.parseInt(params[0])){
					case StatusCodeConstant.Method_Err_State:
						return false;
					case StatusCodeConstant.This_Err_State:
						return false;
					case StatusCodeConstant.Repeat_Data_State:
						return false;
					case StatusCodeConstant.This_Succ_State:
						break;
					case StatusCodeConstant.Method_Succ_State:
						return true;
					default:
						return false;
				}
			} catch (ReadTimeOutException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} 
		if(times <= 0) return false;
		
		SocketTools.sendData(socket,dataStr);
		
		
		try {
			ResultSocket resultSocket = SocketTools.receiveDataMethod(socket, true);
			String rStr = Tools.encodeUTF82Str(resultSocket.getMethodParam());
			params = rStr.split("|");
			
			switch(Integer.parseInt(params[0])){
				case StatusCodeConstant.Method_Err_State:
					return false;
				case StatusCodeConstant.This_Err_State:
					return false;
				case StatusCodeConstant.Repeat_Data_State:
					return false;
				case StatusCodeConstant.This_Succ_State:
					return true;
				case StatusCodeConstant.Method_Succ_State:
					return true;
				default:
					return false;
			}
		} catch (ReadTimeOutException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}
	
}
