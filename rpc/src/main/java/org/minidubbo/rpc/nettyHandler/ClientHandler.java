package org.minidubbo.rpc.nettyHandler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.minidubbo.rpc.DefaultFuture;
import org.minidubbo.rpc.Response;
import org.minidubbo.rpc.Result;
import org.minidubbo.rpc.exception.RpcException;

import java.util.concurrent.CompletableFuture;

public class ClientHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

            if(msg instanceof Response){
                Response response = (Response)msg;
                if(response.getStatus()!=Response.OK){
                    throw new Exception(response.getErrorMessage());
                }
                DefaultFuture completableFuture = (DefaultFuture)DefaultFuture.getCompletableFuture(response.getId());
                if(completableFuture == null || completableFuture.isCancelled()){
                    return;
                }
                Object value = ((Result) response.getData()).getValue();
                completableFuture.complete(value);
                completableFuture.cancelTimecheck();
            }

    }
}
