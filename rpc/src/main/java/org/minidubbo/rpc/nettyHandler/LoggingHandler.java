package org.minidubbo.rpc.nettyHandler;

import com.alibaba.fastjson.JSON;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;

import io.netty.channel.ChannelPromise;
import lombok.extern.slf4j.Slf4j;

import java.net.SocketAddress;

@Slf4j
@ChannelHandler.Sharable
public class LoggingHandler extends ChannelDuplexHandler {

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        log.info("{} connect remote address {} success",ctx.channel().localAddress(),ctx.channel().remoteAddress());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        log.info("{} disconnect remote address {}",ctx.channel().localAddress(),ctx.channel().remoteAddress());
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        log.info("receive message:{} ", JSON.toJSONString(msg));
        ctx.fireChannelRead(msg);
    }

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        log.info("send message:{} ", JSON.toJSONString(msg));
        ctx.write(msg,promise);
    }


}
