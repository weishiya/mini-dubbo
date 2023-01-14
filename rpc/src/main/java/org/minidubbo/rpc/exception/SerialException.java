package org.minidubbo.rpc.exception;

public class SerialException extends Exception{
    private int code;
    public SerialException(int code, String message) {
        super(message);
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
