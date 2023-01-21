package org.minidubbo.rpc.invoker;

import org.minidubbo.common.Consant;
import org.minidubbo.rpc.Invocation;
import org.minidubbo.rpc.Invoker;
import org.minidubbo.rpc.Result;
import org.minidubbo.rpc.URL;
import org.minidubbo.rpc.exception.RpcException;
import org.minidubbo.rpc.result.AsyncResult;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public abstract class AbstractInvoker<T> implements Invoker {
    private final Class<T> type;

    private final URL url;

    public AbstractInvoker(Class<T> serviceType, URL url){
        this.type = serviceType;
        this.url = url;
    }

    @Override
    public Class getInterface() {
        return type;
    }

    @Override
    public Result invoke(Invocation invocation) throws RpcException {
        Integer timeout = (Integer)invocation.getAttachment().getOrDefault(Consant.TIMEOUT_KEY, Consant.DEFAULT_TIMEOUT);
        Result result = doInvoke(invocation);
        try {
            //这里阻塞，等待响应回来时，唤醒线程
            Object o = ((AsyncResult) result).getResponseFuture().get(timeout, TimeUnit.SECONDS);
            result.setValue(o);
        } catch (TimeoutException e) {
            result.setException(new RpcException(RpcException.TIMEOUT,invocation.getMethodName()+" timeout",e));
        }catch (InterruptedException | ExecutionException e){
            result.setException(new RpcException(RpcException.UNKNOWN,e.getMessage()));
        }
        return result;
    }

    @Override
    public URL getUrl() {
        return url;
    }

    protected abstract Result doInvoke(Invocation invocation);

}