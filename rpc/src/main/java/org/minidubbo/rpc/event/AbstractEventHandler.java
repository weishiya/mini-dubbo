package org.minidubbo.rpc.event;

import io.netty.channel.ChannelHandlerContext;

public abstract class AbstractEventHandler<T> implements EventHandler{

    private ChannelState state;

    protected AbstractEventHandler(ChannelState state){
        this.state = state;
    }

    @Override
    public ChannelState getChannelState() {
        return state;
    }

    @Override
    public void handle(ChannelHandlerContext ctx, Object message){
        doHandle(ctx,message);
    }

    protected abstract void doHandle(ChannelHandlerContext ctx, Object message);
}
