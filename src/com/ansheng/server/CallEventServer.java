package com.ansheng.server;

import java.io.IOException;
import java.net.Socket;
import java.util.Objects;

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
		SocketTools.sendData(socket, param,1000);
		
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
	
	
}
