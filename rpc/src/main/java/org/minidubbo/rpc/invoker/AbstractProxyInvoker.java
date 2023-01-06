package org.minidubbo.rpc.invoker;

import org.minidubbo.rpc.*;
import org.minidubbo.rpc.exception.RpcException;

public abstract class AbstractProxyInvoker<T> implements Invoker {
    private final T proxy;

    private final Class<T> type;

    private final URL url;

    public AbstractProxyInvoker(T proxy, Class<T> type, URL url){
        if (proxy == null) {
            throw new IllegalArgumentException("proxy == null");
        }
        if (type == null) {
            throw new IllegalArgumentException("interface == null");
        }
        if (!type.isInstance(proxy)) {
            throw new IllegalArgumentException(proxy.getClass().getName() + " not implement interface " + type);
        }
        this.proxy = proxy;
        this.type = type;
        this.url = url;
    }

    @Override
    public Class getInterface() {
        return type;
    }

    @Override
    public Result invoke(Invocation invocation) throws RpcException {
        Result result = new RpcResult();

        try {
            Object  value = doInvoke(proxy, invocation.getMethodName(), invocation.getParameterTypes(), invocation.getArguments());
            result.setValue(value);
            return result;

        } catch (Throwable throwable) {
            throw new RpcException(throwable.getMessage(),throwable);
        }

    }

    protected abstract Object doInvoke(T proxy, String methodName, Class<?>[] parameterTypes, Object[] arguments) throws Throwable;

    @Override
    public URL getUrl() {
        return url;
    }
}
