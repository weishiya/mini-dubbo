package org.minidubbo.rpc.event;

import io.netty.channel.ChannelHandlerContext;

public abstract class AbstractEventHandler implements EventHandler{

    private ChannelState state;

    protected AbstractEventHandler(ChannelState state){
        this.state = state;
    }

    public ChannelState getState() {
        return state;
    }

    @Override
    public void handle(ChannelHandlerContext ctx, Object message,ChannelState state){
        if(this.state == state){
            doHandle(ctx,message);
        }else {
            throw new RuntimeException("can not handle this kind of event");
        }
    }

    protected abstract void doHandle(ChannelHandlerContext ctx, Object message);
}
