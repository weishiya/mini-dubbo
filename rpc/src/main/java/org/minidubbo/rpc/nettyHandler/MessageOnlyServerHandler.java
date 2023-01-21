package org.minidubbo.rpc.nettyHandler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.minidubbo.common.flusher.ParallelFlusher;
import org.minidubbo.rpc.Request;
import org.minidubbo.rpc.event.ChannelState;
import org.minidubbo.rpc.event.EventHandler;
import org.minidubbo.rpc.executor.ChannelEventRunable;

import java.util.concurrent.ExecutorService;

/**
 * 读写事件由线程池处理
 */
public class MessageOnlyServerHandler extends ChannelInboundHandlerAdapter {

    private ExecutorService executor;

    private EventHandler requestEventHandler;


    public MessageOnlyServerHandler(ExecutorService executor,EventHandler requestEventHandler){
        this.executor = executor;
        this.requestEventHandler = requestEventHandler;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg){
        if(msg instanceof Request){
            executor.execute(new ChannelEventRunable(ctx,requestEventHandler, ChannelState.REQUEST,msg));
        }
    }
}
