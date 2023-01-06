package org.minidubbo.rpc;

public class RpcResult implements Result{

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
}
