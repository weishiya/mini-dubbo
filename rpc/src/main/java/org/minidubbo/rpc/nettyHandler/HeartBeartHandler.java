package org.minidubbo.rpc.nettyHandler;

import io.netty.channel.*;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.minidubbo.common.Consant;
import org.minidubbo.rpc.HeartBeat;
import org.minidubbo.rpc.Request;
import org.minidubbo.rpc.Response;
import org.minidubbo.rpc.exception.ChannelIdleException;

import java.util.concurrent.CountDownLatch;

@Slf4j
public class HeartBeartHandler extends ChannelDuplexHandler {


    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception{
        Channel channel = ctx.channel();
        //如果是心跳
        if((msg instanceof HeartBeat) && ((HeartBeat) msg).isHeartbeat()){

            if(msg instanceof Request){
                log.info("receive ping from channel {}",channel.remoteAddress());
                new CountDownLatch(1).await();
                //返回pong
                Request request = (Request)msg;
                Response response = new Response(request.getId());
                response.setHeartbeat();
                response.setStatus(Response.OK);
                ctx.writeAndFlush(response);
            }
            else if(msg instanceof Response){
                log.info("receive pong from channel {}",channel.remoteAddress());
            }
            return;
        }

        ctx.fireChannelRead(msg);
    }
}
