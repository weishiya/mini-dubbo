package org.minidubbo.exeception;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class InvokerInvocationHandler implements InvocationHandler {

    private Invoker invoker;

    public InvokerInvocationHandler(Invoker invoker){
        this.invoker = invoker;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Invocation invocation = createInvocation(invoker.getInterface().getName(), method.getName(), args);
        return invoker.invoke(invocation);
    }

    private Invocation createInvocation(String className,String method,Object[] args){
        return null;
    }
}
