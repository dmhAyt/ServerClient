package com.ansheng.model.event;

public class IdentifyModel {

    private String token;
    private String userCode;
    /**
     * 0:表示客户端；2:表示跟踪服务器；4：表示组长-副；8：表示组员
     */
    private int    originServer;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUserCode() {
        return userCode;
    }

    public void setUserCode(String userCode) {
        this.userCode = userCode;
    }

    public int getOriginServer() {
        return originServer;
    }

    public void setOriginServer(int originServer) {
        this.originServer = originServer;
    }
}
