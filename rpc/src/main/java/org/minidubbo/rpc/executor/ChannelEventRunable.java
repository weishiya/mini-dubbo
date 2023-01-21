package org.minidubbo.rpc.executor;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import org.minidubbo.rpc.event.ChannelState;
import org.minidubbo.rpc.event.EventHandler;

public class ChannelEventRunable implements Runnable{

    private final ChannelHandlerContext ctx;

    private final ChannelState state;

    private final Object message;

    private final EventHandler eventHandler;

    public ChannelEventRunable (ChannelHandlerContext ctx, EventHandler eventHandler, ChannelState state, Object message){
        this.ctx = ctx;
        this.state = state;
        this.eventHandler = eventHandler;
        this.message = message;
    }

    @Override
    public void run() {
        eventHandler.handle(ctx,message);
    }
}
