package org.minidubbo.rpc;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class InvokerInvocationHandler implements InvocationHandler {

    private Invoker invoker;

    public InvokerInvocationHandler(Invoker invoker){
        this.invoker = invoker;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Invocation invocation = createInvocation(invoker.getUrl().getServiceKey(),invoker.getInterface().getName(), method.getName(),method.getParameterTypes(), args,invoker.getUrl());
        return invoker.invoke(invocation).recreate();
    }

    private Invocation createInvocation(String serviceKey,String className,String method,Class<?>[] parameterTypes,Object[] args,URL url){
        Invocation invocation = new RpcInvocation(serviceKey,method,className,parameterTypes,args,url.getParams());
        return invocation;
    }
}
