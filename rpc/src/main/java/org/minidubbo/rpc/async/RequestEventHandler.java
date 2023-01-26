package org.minidubbo.rpc.async;

import com.lmax.disruptor.EventHandler;
import io.netty.channel.ChannelHandlerContext;
import org.minidubbo.rpc.Invocation;
import org.minidubbo.rpc.Request;
import org.minidubbo.rpc.Response;
import org.minidubbo.rpc.Result;
import org.minidubbo.rpc.exception.RpcException;

public abstract class RequestEventHandler extends DubboEventHandler {

    public RequestEventHandler() {
        super(Request.class);
    }


    @Override
    protected void handleMsg(ChannelHandlerContext ctx, Object msg) {
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
                response.setErrorMessage(rpcException.getMessage());
            }
            else if(rpcException.getCode() == RpcException.SERVICE_NOT_FOUND){
                response.setStatus(Response.SERVICE_NOT_FOUND);
                response.setErrorMessage(rpcException.getMessage());
            }
        }catch (Exception e){
            response.setStatus(Response.APP_ERROR);
            response.setErrorMessage(e.getMessage());
        }
        ctx.writeAndFlush(response);
    }

    protected abstract Result reply(ChannelHandlerContext ctx, Invocation invocation);
}
