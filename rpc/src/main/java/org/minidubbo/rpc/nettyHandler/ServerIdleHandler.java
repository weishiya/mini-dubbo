package org.minidubbo.rpc.nettyHandler;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;
import org.minidubbo.rpc.Request;
import org.minidubbo.rpc.exception.ChannelIdleException;
@Slf4j
public class ServerIdleHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if(evt instanceof IdleStateEvent){
            IdleState state = ((IdleStateEvent) evt).state();
            if(state == IdleState.READER_IDLE){
                throw new ChannelIdleException();
            }
        }else {
            super.userEventTriggered(ctx,evt);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        if(cause instanceof ChannelIdleException){
            Channel channel = ctx.channel();
            ctx.close();
            log.info("no read event server close channel {}",ctx.channel().remoteAddress());
        }else {
            super.exceptionCaught(ctx, cause);
        }
    }
}
