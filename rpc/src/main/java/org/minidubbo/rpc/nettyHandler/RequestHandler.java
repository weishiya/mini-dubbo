package org.minidubbo.rpc.nettyHandler;

import com.alibaba.fastjson.JSON;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;
import org.minidubbo.rpc.Invocation;
import org.minidubbo.rpc.Request;
import org.minidubbo.rpc.Response;
import org.minidubbo.rpc.Result;
import org.minidubbo.rpc.exception.RpcException;

import java.net.SocketAddress;

@Slf4j
@ChannelHandler.Sharable
public abstract class RequestHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        SocketAddress socketAddress = ctx.channel().remoteAddress();
        log.info("client connected ,it's address is: {}",socketAddress);
        ctx.fireChannelActive();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        log.info("receive messsage ", JSON.toJSONString(msg));
        if(!(msg instanceof Request)){
            Response response = new Response(-1);
            response.setStatus(Response.BAD_REQUEST);
        }
        Request request = ((Request) msg);
        Object data = request.getData();
        Invocation invocation = (Invocation) data;
        Response response = new Response(request.getId());
        try{
            //这里就拿到了本地调用的结果,可以封装返回
            Result result = reply(ctx, invocation);
            response.setData(result);
        }catch (RpcException rpcException){
            if(rpcException.getCode() == RpcException.INTERNAL){
                response.setStatus(Response.INTERNAL_ERROR);
            }
            else if(rpcException.getCode() == RpcException.SERVICE_NOT_FOUND){
                response.setStatus(Response.SERVICE_NOT_FOUND);
            }
        }catch (Exception e){
            response.setStatus(Response.APP_ERROR);
            response.setErrorMessage(e.getMessage());
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        SocketAddress socketAddress = ctx.channel().remoteAddress();
        log.info("client disconnected ,it's address is: {}",socketAddress);
        ctx.fireChannelActive();
    }

    public abstract Result reply(ChannelHandlerContext ctx, Object msg) throws RpcException;
}
