package org.minidubbo.rpc;

import java.io.Serializable;

public class Response extends HeartBeat implements Serializable {

    public static final int OK = 20;

    public static final int APP_ERROR= 30;

    public static final int INTERNAL_ERROR= 40;

    public static final byte SERVICE_NOT_FOUND = 50;

    public static final int BAD_REQUEST = 60;

    public static final int TIME_OUT = 70;

    private final long id;
    private Object data;
    private int status = Response.OK;
    private String errorMessage;

    public Response(long id){

        this.id = id;
    }

    public Long getId(){
        return id;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
