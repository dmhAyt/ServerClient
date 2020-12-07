package com.ansheng.model.socket;

public class ResultSocket {

    private int MethodCode ;
    private byte[] MethodParam;

    public int getMethodCode() {
        return MethodCode;
    }

    public void setMethodCode(int methodCode) {
        MethodCode = methodCode;
    }

    public byte[] getMethodParam() {
        return MethodParam;
    }

    public void setMethodParam(byte[] methodParam) {
        MethodParam = methodParam;
    }
}
