package com.ansheng.server;

import com.ansheng.exception.ReadTimeOutException;
import com.ansheng.model.FileRecordInfoModel;
import com.ansheng.model.FileUploadInfoModel;
import com.ansheng.model.HeartBeatModel;
import com.ansheng.model.event.IdentifyModel;
import com.ansheng.model.socket.ResultSocket;
import com.ansheng.single.FileRecordSingle;
import com.ansheng.single.FileUploadSingle;
import com.ansheng.single.HeartSingle;
import com.ansheng.util.FileTools;
import com.ansheng.util.Tools;
import com.ansheng.util.socket.SocketTools;

import java.io.IOException;
import java.net.Socket;
import java.text.DateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Iterator;
import java.util.Objects;

public class EventServer {
	
	private FileRecordSingle _fileRecord = null;
	private FileUploadSingle _fileUpload = null;
	private HeartSingle  _heartSingle = null;
	
	public EventServer() {
		_fileRecord = FileRecordSingle.getInstance();
		_fileUpload = FileUploadSingle.getInstance();
		try {
			_heartSingle = HeartSingle.getInstance();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
    /**
     *	事务端口跳转
     * @param socket 连接的socket
     */
	public void eventManage(Socket socket){
        // 获得方法码？
        ResultSocket dataParam = new ResultSocket();
        try {
             dataParam =  SocketTools.receiveDataMethod(socket,true);
        } catch (IOException e) {
            e.printStackTrace();
             return;
        } catch (ReadTimeOutException e) {
            e.printStackTrace();
            sendError(socket,"超时了",-1000);
            return;
         }
        // 将data转成指定的编码
        String dataStr = Tools.encodeUTF82Str(dataParam.getMethodParam());
        IdentifyModel identifyModel = null;
        try {
             identifyModel  = Tools.json2Bean(dataStr, IdentifyModel.class);
         }catch (Exception ex){
             sendError(socket,"身份验证失败--转换对象失败",-2001);
             return;
         }

        boolean flage = true;
        if(identifyModel.getOriginServer() <= 0){
            // 需要调用跟踪服务器获得验证。。
        	

        }

        if(!flage){
            sendError(socket,"身份验证失败--错误Token",-2002);
            return;
        }
        try {
	        switch(dataParam.getMethodCode()){
	            case 1000: // 删除
	            	DeleteFile(socket,identifyModel);
	                break;
	            case 2000: // 查询
	                break;
	            case 3000: // 上传
	            	UploadFile(socket,identifyModel);
	                break;
	            case 4000: // 下载
	                break;
	            case 5000: // 备份
	                break;
	            default:
	                sendError(socket,"方法识别失败--无该方法",-1002);
	                return;
	        }
        }finally {
			try {
				socket.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
        return;
    }



	/**
     * 	回溯失败给用户。
     * @param socket 连接的soket
     * @param message	消息
     * @param code	方法码
     */
    private void sendError(Socket socket,String message,int code){
        try {
            SocketTools.sendData(socket,message,code);
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
     * 1. 删除文件
     * @param socket socket  变量
     * @param identifyModel	身份信息
     */
    private void DeleteFile(Socket socket,IdentifyModel identifyModel){
        // 发送一个相应，表示连接成功了
        try {
            SocketTools.sendData(socket,"连接删除方法成功。",1001);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 删除什么文件？哪个用户的？描述是什么？
        // 开始等待用户输入
        boolean repeat = true;
        int times = 3;
        while(repeat && times > 0) {      
        	byte[] data = null;
	        try {
				 data = SocketTools.receiveData(socket, true);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ReadTimeOutException e) {
				// 读取超时了怎么办？重试三次？
				times --;
				e.printStackTrace();
				continue;
			}
	        repeat = false;
	        String dataStr = Tools.encodeUTF82Str(data);
	        // dataStr 的格式为：第一次上传人|文件的唯一码|文件名【含路径】|上传的日期【yyyy-MM-dd】|MD5|sha|标志
	        String[] params = dataStr.split("|");
	        
	        if(params.length != 7) {
				try {
					SocketTools.sendData(socket, "长度没有到7");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	        }
	        
        	FileRecordInfoModel dataInfo  = _fileRecord.getObjByKey(params[1]);
        	if(dataInfo == null) {
	        	switch(params[6]) {
	        		case "1":
	        			dataInfo = _fileRecord.getObjByMD5(params[4]);
	        			break;
	        		case "2":
	        			dataInfo = _fileRecord.getObjBySha(params[5]);
	        			break;
	        		case "3":
	        			dataInfo = _fileRecord.getObjByMd5AndSha(params[4],params[5]);
	        			break;
	    			default:
					try {
						SocketTools.sendData(socket, "未知的查找标识！");
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
	    				break;
	        	}
        	}
        	
        	if(dataInfo == null || Objects.equals(dataInfo.getUploadUser(),params[0])) {
				try {
					SocketTools.sendData(socket, "1");
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
        	}
        	// 执行删除操作
        	//// 本地
        	//// 组员
        	if(Objects.nonNull(dataInfo.getFileLocalPath()) && dataInfo.getFileLocalPath().length() > 0){
        		// 存在本地时：根据*拆分出多条
        		String[] localPaths = dataInfo.getFileLocalPath().split("*");
        		for(String item : localPaths) {
        			try {
						FileTools.deleteFileExist(item);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
        		}
        	} 
        	if(dataInfo.getBackUpIDs() != null && dataInfo.getBackUpIDs().size() > 0) {
        		// 调用组员的删除
        		Iterator<String> backID = dataInfo.getBackUpIDs().iterator();
        		while(backID.hasNext()) {
        			String id = backID.next();
        			HeartBeatModel model = _heartSingle.getHeartModelByKey(id);
        			if(model == null) {
        				// 记录到文件中，当它连接的时候告诉它这个文件需要删除。
        				
        				
        			}
        			
        			// 这里建议是异步处理，否则耗时比较大。
        			CallEventServer  callServer = new CallEventServer();
        			try {
						callServer.callDeleteFile(model.getIP(),model.getFP(),dataStr,identifyModel);
						backID.remove();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
        		}
        	}
        	_fileRecord.removeParam(dataInfo.getFileID());
        	try {
				SocketTools.sendData(socket,"1");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }

    }
    
    private void UploadFile(Socket socket, IdentifyModel identifyModel) {
    	 // 发送一个相应，表示连接成功了
        try {
            SocketTools.sendData(socket,"连接上传方法成功。",3001);
        } catch (IOException e) {
            e.printStackTrace();
        }
        // 需要上传什么文件呢？
        // 文件唯一ID|文件名含后缀|文件的大小|md5|Sha|上传时间【yyyy-MM-dd HH:mm:ss】|上传人
        // 开始等待用户输入
        int times = 3;
        String[] params = null;
        while(times-- > 0) {      
        	byte[] data = null;
	        try {
				 data = SocketTools.receiveData(socket, true);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ReadTimeOutException e) {
				// 读取超时了怎么办？重试三次？
				e.printStackTrace();
				continue;
			}
	        String dataStr = Tools.encodeUTF82Str(data);
	        // dataStr 的格式为：文件唯一ID|文件名含后缀|文件的大小|md5|Sha|上传时间【yyyy-MM-dd HH:mm:ss】|上传人
	        params = dataStr.split("|");
	        if(params.length != 7) {
				try {
					SocketTools.sendData(socket, "长度没有到7");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	        }
	        // 校验文件唯一ID；由MD5+|da|+Sha 计算MD5摘要得到！
	        if(!Objects.equals(params[0], FileTools.md5(params[3]+"|da|"+params[4]))) {
	        	try {
					SocketTools.sendData(socket,"文件ID不一致，已断开。");
					return;
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	        }
	        break;
        }
        
        // 查询
        // 1. 上传记录中是否有？ -- 有进行断点续传。
        // 2.完成记录中是否有？ -- 有则表示上传完成了。
        long beginIndex = 0;
        int partSize = 1024 * 20;
        boolean fileExist = false;
        FileUploadInfoModel fileUpload = _fileUpload.getObjByKey(params[0]);
        if(fileUpload != null) {
        	// 断点续传
        	//// 判断文件是否存在？
        	////// 存在,读取文件的长度。
        	fileExist = FileTools.fileExist(fileUpload.getFileLocalPath());
        	if(fileExist) {
        		// 读取长度
        		beginIndex = FileTools.fileLength(fileUpload.getFileLocalPath());
        	}
    		////// 不存在,重新写。
        }else {
        	FileRecordInfoModel fileRecordInfoModel = _fileRecord.getObjByKey(params[0]);
        	if(fileRecordInfoModel != null) {
        		// 表示已经上传成功了。
        		try {
					SocketTools.sendData(socket, "U_Success");
					return;
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					return;
				}
        	}
        }
        
        if(!fileExist) {
        	// 加入到上传记录中
        	//文件唯一ID|文件名含后缀|文件的大小|md5|Sha|上传时间【yyyy-MM-dd HH:mm:ss】|上传人
        	DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        	fileUpload = new FileUploadInfoModel(params[0],params[1],Long.parseLong(params[2])
        			,params[3],params[4],LocalDateTime.parse(params[5],formatter) ,params[6],"");
        	// 要分配到哪个磁盘呢？
        }
        
        
       
        
	}
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
}
