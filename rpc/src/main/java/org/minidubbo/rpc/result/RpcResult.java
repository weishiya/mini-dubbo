package org.minidubbo.rpc.result;

import org.minidubbo.rpc.Result;

/**
 * 包装provider端应用返回的结果
 */
public class RpcResult implements Result {

    private static final long serialVersionUID = -1L;

    private Object value;

    private Throwable exception;

    @Override
    public Object getValue() {
        return value;
    }

    @Override
    public void setValue(Object value) {
        this.value = value;
    }

    @Override
    public Throwable getException() {
        return exception;
    }

    @Override
    public void setException(Throwable t) {
        this.exception = t;
    }

    @Override
    public boolean hasException() {
        return exception==null;
    }

    @Override
    public Object recreate() throws Throwable{
        if(exception!=null){
            throw exception;
        }
        return value;
    }
}
