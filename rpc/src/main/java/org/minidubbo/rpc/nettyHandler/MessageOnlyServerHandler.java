package org.minidubbo.rpc.nettyHandler;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.minidubbo.rpc.Request;
import org.minidubbo.rpc.async.DubboEvent;
import org.minidubbo.rpc.async.flusher.Flusher;

import java.util.concurrent.ExecutorService;

/**
 * 读写事件由线程池处理
 */
@ChannelHandler.Sharable
public class MessageOnlyServerHandler extends ChannelInboundHandlerAdapter {

    private ExecutorService executor;

    private Flusher<DubboEvent> flusher;

    public MessageOnlyServerHandler(Flusher<DubboEvent> flusher){
        this.flusher = flusher;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg){
        if(msg instanceof Request){
            flusher.add(msg,ctx);
        }
    }
}
