package org.minidubbo.rpc.nettyHandler;

import io.netty.channel.Channel;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;
import org.minidubbo.rpc.HeartBeat;
import org.minidubbo.rpc.Request;
import org.minidubbo.rpc.Response;
import org.minidubbo.rpc.client.NettyClient;
import org.minidubbo.rpc.exception.ChannelIdleException;

@Slf4j
public class ClientIdleHandler extends ChannelDuplexHandler {

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if(evt instanceof IdleStateEvent){
            IdleState state = ((IdleStateEvent) evt).state();
            if (state == IdleState.WRITER_IDLE) {
                // write heartbeat to server
                //ping
                Request heartBeatReq = new Request(null);
                heartBeatReq.setHeartbeat();
                ctx.writeAndFlush(heartBeatReq);
                log.info("client send ping to server {}",ctx.channel().remoteAddress());
            }else if(state == IdleState.READER_IDLE){
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
            NettyClient client = NettyClient.getClient(channel);
            client.reconnect();
            log.info("no read event client reconnect {}",ctx.channel().remoteAddress());
        }else {
            super.exceptionCaught(ctx, cause);
        }
    }
}
