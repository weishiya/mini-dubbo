package org.minidubbo.rpc.nettyHandler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.minidubbo.rpc.Result;
import org.minidubbo.rpc.exception.RpcException;

public abstract class RequestHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        try{
            //这里就拿到了本地调用的结果,可以封装返回
            Result result = reply(ctx, msg);
        }catch (RpcException rpcException){

        }
    }

    public abstract Result reply(ChannelHandlerContext ctx, Object msg) throws RpcException;
}
