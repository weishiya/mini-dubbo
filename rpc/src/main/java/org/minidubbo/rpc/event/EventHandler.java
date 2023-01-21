package org.minidubbo.rpc.event;

import io.netty.channel.ChannelHandlerContext;

public interface EventHandler {
    void handle(ChannelHandlerContext ctx, Object message);

    ChannelState getChannelState();
}
