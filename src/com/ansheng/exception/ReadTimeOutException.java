package com.ansheng.exception;

public class ReadTimeOutException extends Exception {
    public  ReadTimeOutException(String message){
        super(message);
    }

    public ReadTimeOutException(){
        super();
    }

}
