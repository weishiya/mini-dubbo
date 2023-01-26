package org.minidubbo.rpc.async;

import io.netty.channel.ChannelHandlerContext;

public class DubboEvent<T> {

    private T msg;

    private ChannelHandlerContext ctx;

    public T getMsg() {
        return msg;
    }

    public void setMsg(T msg) {
        this.msg = msg;
    }

    public ChannelHandlerContext getCtx() {
        return ctx;
    }

    public void setCtx(ChannelHandlerContext ctx) {
        this.ctx = ctx;
    }

    public boolean match(Class<?> clazz){
        return clazz.isInstance(getMsg());
    }


}
