package com.ansheng.server;

import com.ansheng.constant.MethodCodeConstant;
import com.ansheng.constant.StatusCodeConstant;
import com.ansheng.distribute.FileDistribute;
import com.ansheng.exception.ReadTimeOutException;
import com.ansheng.model.BackupsModel;
import com.ansheng.model.FileRecordInfoModel;
import com.ansheng.model.FileUploadInfoModel;
import com.ansheng.model.HeartBeatModel;
import com.ansheng.model.event.IdentifyModel;
import com.ansheng.model.socket.ResultSocket;
import com.ansheng.myenum.IdentifyEnum;
import com.ansheng.single.BackupsSingle;
import com.ansheng.single.ConfigSingle;
import com.ansheng.single.FileRecordSingle;
import com.ansheng.single.FileUploadSingle;
import com.ansheng.single.HeartSingle;
import com.ansheng.util.FileTools;
import com.ansheng.util.Tools;
import com.ansheng.util.socket.SocketTools;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Iterator;
import java.util.Objects;

public class EventServer {

	private FileRecordSingle _fileRecord = null;
	private FileUploadSingle _fileUpload = null;
	private HeartSingle _heartSingle = null;
	private ConfigSingle _conConfigSingle = null;
	private BackupsSingle _backupsSingle = null;
	

	public EventServer() {
		_fileRecord = FileRecordSingle.getInstance();
		_fileUpload = FileUploadSingle.getInstance();
		_conConfigSingle = ConfigSingle.getInstance();
		_backupsSingle = BackupsSingle.instance();
		try {
			_heartSingle = HeartSingle.getInstance();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 事务端口跳转
	 * 
	 * @param socket 连接的socket
	 */
	public void eventManage(Socket socket) {
		// 获得方法码？
		ResultSocket dataParam = new ResultSocket();
		try {
			dataParam = SocketTools.receiveDataMethod(socket, true);
		} catch (IOException e) {
			e.printStackTrace();
			return;
		} catch (ReadTimeOutException e) {
			e.printStackTrace();
			sendError(socket, StatusCodeConstant.Method_Err_State + "|超时了", dataParam.getMethodCode());
			return;
		}
		// 将data转成指定的编码
		String dataStr = Tools.encodeUTF82Str(dataParam.getMethodParam());
		IdentifyModel identifyModel = null;
		try {
			identifyModel = Tools.json2Bean(dataStr, IdentifyModel.class);
		} catch (Exception ex) {
			sendError(socket, StatusCodeConstant.Method_Err_State + "|身份验证失败--转换对象失败", dataParam.getMethodCode());
			return;
		}

		boolean flage = true;
		if (identifyModel.getOriginServer() <= 0) {
			// 需要调用跟踪服务器获得验证。。

		}

		if (!flage) {
			sendError(socket, StatusCodeConstant.Method_Err_State + "|身份验证失败--错误Token", dataParam.getMethodCode());
			return;
		}
		try {
			switch (dataParam.getMethodCode()) {
			case MethodCodeConstant.Delete_Code: // 删除
				DeleteFile(socket, identifyModel);
				break;
			case MethodCodeConstant.Find_Code: // 查询
				break;
			case MethodCodeConstant.Upload_Code: // 上传
				UploadFile(socket, identifyModel);
				break;
			case MethodCodeConstant.DownLoad_Code: // 下载
				break;
			case MethodCodeConstant.BackUp_Code: // 备份
				break;
			case MethodCodeConstant.Up_Finish_Code:// 上传完成或备份完成通知
				UploadFinishNotice(socket, identifyModel);
				break;
			case MethodCodeConstant.Log_Info_Code:// 日志接口
				break;
			default:
				sendError(socket, StatusCodeConstant.Method_Err_State + "|方法识别失败--无该方法", dataParam.getMethodCode());
				return;
			}
		} finally {
			try {
				socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return;
	}

	/**
	 * 回溯失败给用户。
	 * 
	 * @param socket  连接的soket
	 * @param message 消息
	 * @param code    方法码
	 */
	private void sendError(Socket socket, String message, int code) {
		try {
			SocketTools.sendData(socket, message, code);
			socket.shutdownOutput();
			socket.shutdownInput();
			socket.close();
			return;
		} catch (IOException ioException) {
			ioException.printStackTrace();
			return;
		}

	}

	/**
	 * 上传完成后的通知
	 * @param socket
	 * @param identifyModel
	 */
	private void UploadFinishNotice(Socket socket, IdentifyModel identifyModel) {
		// 文件的唯一码|文件名【含路径】|文件大小|MD5|sha|上传的用户ID|组员ID[n0001|d0001|l0001|s0001]
		String[] params = GetDataPre(socket, 6, MethodCodeConstant.Up_Finish_Code);
		if (params == null)
			return;
		
		FileRecordInfoModel model = _fileRecord.getObjByKey(params[0]);
		if(model != null) {
			model = new FileRecordInfoModel(params[0],params[1],Long.parseLong(params[2]),params[3],params[4],LocalDateTime.now(),params[5],"",0,0); 
		}
		model.addBackUpIDs(params[6]);
		_fileRecord.addParam(model);
		
		try {
			SocketTools.sendData(socket, StatusCodeConstant.Method_Succ_State + "|UF_Success",
					MethodCodeConstant.Up_Finish_Code);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 1. 删除文件
	 * 
	 * @param socket        socket 变量
	 * @param identifyModel 身份信息
	 */
	private void DeleteFile(Socket socket, IdentifyModel identifyModel) {
		// 删除什么文件？哪个用户的？描述是什么？
		// 文件的唯一码|文件名【含路径】|上传的日期【yyyy-MM-dd】|MD5|sha|第一次上传人|取消或删除[0表示取消,1删除]
		// 开始等待用户输入
		String[] params = GetDataPre(socket, 7, MethodCodeConstant.Delete_Code);
		if (params == null)
			return;

		boolean noPower = false;
		
		try {
			switch (params[6]) {
			case "1":
				// 已经存在的删除掉
				FileRecordInfoModel recordModel = _fileRecord.getObjByKey(params[0]);
				if (recordModel != null && Objects.equals(recordModel.getUploadUser(), params[5])) {
					DeleteFileHadUploaded(recordModel,String.join("|", params),identifyModel);
					_fileRecord.removeParam(recordModel.getFileID());
				}else if(recordModel != null && !Objects.equals(recordModel.getUploadUser(), params[5])) {
					noPower = true;
				}
				break;
			case "0":
				// 从上传中的取消掉
				FileUploadInfoModel upData = _fileUpload.getObjByKey(params[0]);
				if (upData != null && Objects.equals(upData.getUploadUser(), params[5])) {
					FileTools.deleteFileExist(upData.getFileLocalPath());
					_fileUpload.removeParam(upData.getFileID());
				}else if(upData != null && !Objects.equals(upData.getUploadUser(), params[5])) {
					noPower = true;
				}
				break;
			default:
				break;
			}
			
			if(noPower) {
				SocketTools.sendData(socket, StatusCodeConstant.Method_Err_State + "|D_Fail:无权限删除。",
						MethodCodeConstant.Delete_Code);
				// 需要作为日志上报。
				return;
			}
			
			SocketTools.sendData(socket, StatusCodeConstant.Method_Succ_State + "|D_Success",
					MethodCodeConstant.Delete_Code);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/**
	 * 删除文件---已经上传的文件
	 * 
	 * @param dataInfo
	 */
	private void DeleteFileHadUploaded(FileRecordInfoModel dataInfo,String dataStr,IdentifyModel identifyModel) {
		// 执行删除操作
		//// 本地
		//// 组员
		if (Objects.nonNull(dataInfo.getFileLocalPath()) && dataInfo.getFileLocalPath().length() > 0) {
			// 存在本地时：根据*拆分出多条
			String[] localPaths = dataInfo.getFileLocalPath().split("*");
			for (String item : localPaths) {
				try {
					FileTools.deleteFileExist(item);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		// 如果是组长和副组长才工作
		if (_conConfigSingle.getIdentity() == IdentifyEnum.GROUP_LEADER
				|| _conConfigSingle.getIdentity() == IdentifyEnum.Deputy_GROUP_LEADER) {

			if (dataInfo.getBackUpIDs() != null && dataInfo.getBackUpIDs().size() > 0) {
				// 调用组员的删除
				Iterator<String> backID = dataInfo.getBackUpIDs().iterator();
				while (backID.hasNext()) {
					String id = backID.next();
					HeartBeatModel model = _heartSingle.getHeartModelByKey(id);
					if (model == null) {
						// 记录到文件中，当它连接的时候告诉它这个文件需要删除。

					}

					// 这里建议是异步处理，否则耗时比较大。
					CallEventServer callServer = new CallEventServer();
					try {
						callServer.callDeleteFile(model.getIP(), model.getFP(), dataStr, identifyModel);
						backID.remove();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
		_fileRecord.removeParam(dataInfo.getFileID());
	}

	/**
	 * 	上传文件的服务端。
	 * 
	 * @param socket        socket连接
	 * @param identifyModel 身份信息
	 */
	private void UploadFile(Socket socket, IdentifyModel identifyModel) {
		String[] params = GetDataPre(socket, 7, MethodCodeConstant.Upload_Code);
		if (params == null)
			return;
		
		// 兩查一加
		// 1. 上传记录中是否有？ -- 有进行断点续传。
		// 2.完成记录中是否有？ -- 有则表示上传完成了。
		long beginIndex = 0;
		int partSize = 1024 * 20;
		boolean fileExist = false;
		FileUploadInfoModel fileUpload = _fileUpload.getObjByKey(params[0]);
		if (fileUpload != null) {
			// 断点续传
			//// 判断文件是否存在？
			////// 存在,读取文件的长度。
			fileExist = FileTools.fileExist(fileUpload.getFileLocalPath());
			if (fileExist) {
				// 读取长度
				beginIndex = FileTools.fileLength(fileUpload.getFileLocalPath());
			}
			////// 不存在,重新写。
		} else {
			FileRecordInfoModel fileRecordInfoModel = _fileRecord.getObjByKey(params[0]);
			if (fileRecordInfoModel != null && Objects.nonNull(fileRecordInfoModel.getFileLocalPath())
					&& fileRecordInfoModel.getFileLocalPath().length() > 0) {
				// 表示已经上传成功了。
				try {
					SocketTools.sendData(socket, StatusCodeConstant.Method_Succ_State + "|U_Success",
							MethodCodeConstant.Upload_Code);
					return;
				} catch (IOException e) {
					e.printStackTrace();
					return;
				}
			}
		}
		
		if (!fileExist) {
			// 加入到上传记录中
			// 文件唯一ID|文件名含后缀|文件的大小|md5|Sha|上传时间【yyyy-MM-dd HH:mm:ss】|上传人
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
			fileUpload = new FileUploadInfoModel(params[0], params[1], Long.parseLong(params[2]), params[3], params[4],
					LocalDateTime.parse(params[5], formatter), params[6], "");
			// 要分配到哪个磁盘呢？--建议优化
			String path = FileDistribute.distributeDisk(params[0], params[1], Long.parseLong(params[2]));
			fileUpload.setFileLocalPath(path);
			_fileUpload.addParam(fileUpload);
		}
		
		RandomAccessFile randomAccessFile = null;
		int time = 3;
		try {
			SocketTools.sendData(socket, StatusCodeConstant.This_Succ_State + "|" + beginIndex + "|" + partSize,
					MethodCodeConstant.Upload_Code);
			// open file stream
			randomAccessFile = new RandomAccessFile(FileTools.fileCreate(fileUpload.getFileLocalPath()), "w");
			
			while (beginIndex < fileUpload.getFileLength() && time > 0) {
				byte[] data = null;
				try {
					data = SocketTools.receiveData(socket, true);
				} catch (ReadTimeOutException e) {
					e.printStackTrace();
					time--;
					continue;
				}
				long begin = Tools.bytesToLong(data, 0, 8);
				
				try {
					// 写入到文件---这里会频繁的打开和释放文件流。。造成不必要的浪费。
					FileTools.writeByte(randomAccessFile, data, begin, 8, data.length - 8);
				} catch (IOException ex) {
					SocketTools.sendData(socket,
							StatusCodeConstant.Repeat_Data_State + "|" + beginIndex + "|" + partSize,
							MethodCodeConstant.Upload_Code);
				}
				
				beginIndex = Math.max(beginIndex, begin + data.length - 8);
				SocketTools.sendData(socket, StatusCodeConstant.This_Succ_State + "|" + beginIndex + "|" + partSize,
						MethodCodeConstant.Upload_Code);// 这里可以把下一个包大小返回
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (Objects.nonNull(randomAccessFile)) {
				try {
					randomAccessFile.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		if (time > 0 && fileUpload.getFileLength() == beginIndex) {
			// 上传完成。检验一下MD5/和sha
			String md5 = FileTools.md5File(fileUpload.getFileLocalPath(), "MD5");
			if (Objects.equals(md5, fileUpload.getFileMD5())) {
				try {
					// 加入到备份队列中
					if(_conConfigSingle.getIdentity() != IdentifyEnum.GROUP_NORMAL) {
						BackupsModel model = new BackupsModel(fileUpload.getFileID(),(byte)0);
						_backupsSingle.addModel(model); 
					}
					
					IdentifyModel sendIndentify = new IdentifyModel();
					sendIndentify.setToken("");
					sendIndentify.setUserCode(_conConfigSingle.getID());
					int orig = _conConfigSingle.getIdentity() == IdentifyEnum.GROUP_LEADER 
							|| _conConfigSingle.getIdentity() == IdentifyEnum.Deputy_GROUP_LEADER ? 4 : 8;
					sendIndentify.setOriginServer(orig);
					//文件的唯一码|文件名【含路径】|文件大小|MD5|sha|上传的用户ID|组员ID[n0001|d0001|l0001|s0001]
					String dataParam = fileUpload.getFileID()+"|"+fileUpload.getFileName()+"|"+fileUpload.getFileLength()+"|"
							+fileUpload.getFileMD5()+"|"+fileUpload.getShaStr()+"|"+fileUpload.getUploadUser()+"|"+_conConfigSingle.getID();
					
					// 通知上级
					CallEventServer callServer = new CallEventServer();
					boolean flage = callServer.callUploadFinish(_conConfigSingle.getGroupIP(),_conConfigSingle.getGroupEventPort()
							,dataParam,sendIndentify);
					
					if(flage) {
						FileRecordInfoModel recordInfoModel = new FileRecordInfoModel(fileUpload,0,0);
						_fileRecord.addParam(recordInfoModel);
						_fileUpload.removeParam(fileUpload.getFileID());
						SocketTools.sendData(socket, StatusCodeConstant.Method_Succ_State + "|U_Success",MethodCodeConstant.Upload_Code);
						
					}else {
						SocketTools.sendData(socket, StatusCodeConstant.Method_Err_State + "|U_Fail",
								MethodCodeConstant.Upload_Code);
					}
					
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		try {
			SocketTools.sendData(socket, StatusCodeConstant.Method_Err_State + "|U_Fail",
					MethodCodeConstant.Upload_Code);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	/**
	 *	 进入具体方法后，业务开始前的数据传输。 数组下标 3和4 一定是MD5和Sha 否则必定失败
	 * 
	 * @param socket     socket连接
	 * @param dataLength 数组的长度
	 * @return
	 */
	private String[] GetDataPre(Socket socket, int dataLength, int methodCode) {
		String[] result = null;
		
		// 发送一个相应，表示连接成功了
		try {
			SocketTools.sendData(socket, StatusCodeConstant.This_Succ_State + "|连接"+methodCode+"方法成功。",
					methodCode);
		} catch (IOException e) {
			e.printStackTrace();
			return result;
		}

		int times = 3;
		while (times-- > 0) {
			byte[] data = null;
			try {
				data = SocketTools.receiveData(socket, true);
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ReadTimeOutException e) {
				// 读取超时了怎么办？重试三次？
				e.printStackTrace();
				continue;
			}
			String dataStr = Tools.encodeUTF82Str(data);
			/**
			 * 文件上传的数据格式为：文件唯一ID|文件名含后缀|文件的大小|md5|Sha|上传时间【yyyy-MM-dd HH:mm:ss】|上传人
			 * 删除文件的数据格式为：第一次上传人|文件的唯一码|文件名【含路径】|上传的日期【yyyy-MM-dd】|MD5|sha|标志
			 */
			result = dataStr.split("|");
			if (result.length != dataLength) {
				try {
					SocketTools.sendData(socket, StatusCodeConstant.This_Err_State + "|长度不足" + dataLength, methodCode);
				} catch (IOException e) {
					e.printStackTrace();
					continue;
				}
			}
			// 校验文件唯一ID；由MD5+|da|+Sha 计算MD5摘要得到！
			if (!Objects.equals(result[0], FileTools.md5(result[3] + "|da|" + result[4]))) {
				try {
					SocketTools.sendData(socket, StatusCodeConstant.This_Err_State + "|文件ID不一致，已断开。", methodCode);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			break;
		}
		return result;
	}

	
}
