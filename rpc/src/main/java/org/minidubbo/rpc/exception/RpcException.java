package org.minidubbo.rpc.exception;

import java.io.Serializable;

public class RpcException  extends RuntimeException {

    private int code;

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
