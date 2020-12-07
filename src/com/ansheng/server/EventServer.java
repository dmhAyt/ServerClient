package com.ansheng.server;

import com.ansheng.exception.ReadTimeOutException;
import com.ansheng.model.event.IdentifyModel;
import com.ansheng.model.socket.ResultSocket;
import com.ansheng.util.Tools;
import com.ansheng.util.socket.SocketTools;

import java.io.IOException;
import java.net.Socket;

public class EventServer {
    /**
     * 事务端口跳转
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

        switch(dataParam.getMethodCode()){
            case 1000: // 删除
                break;
            case 2000: // 查询
                break;
            case 3000: // 上传
                break;
            case 4000: // 下载
                break;
            case 5000: // 备份
                break;
            default:
                sendError(socket,"方法识别失败--无该方法",-1002);
                return;
        }
        return;
    }

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

    private void DeleteFile(Socket socket,IdentifyModel identifyModel){
        // 发送一个相应，表示连接成功了
        try {
            SocketTools.sendData(socket,"连接删除方法成功。",1001);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 删除什么文件？哪个用户的？描述是什么？


    }

}
