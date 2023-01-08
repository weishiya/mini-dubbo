package org.minidubbo.rpc.exception;

import java.io.Serializable;

public class RpcException  extends RuntimeException {

    public static final int TIMEOUT = 501;

    public static final int CHANNEL_NOT_CONNECTED = 502;

    public static final int UNKNOWN = 503;

    public static final int INTERNAL = 504;

    public static final int SERVICE_NOT_FOUND = 504;

    private int code = UNKNOWN;

    public int getCode(){
        return code;
    }

    public RpcException(String message, Throwable cause) {
        super(message, cause);
    }

    public RpcException(int code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
    }

    public RpcException(int code, String message) {
        super(message);
        this.code = code;
    }

    public RpcException(int code, Throwable cause) {
        super(cause);
        this.code = code;
    }
}
