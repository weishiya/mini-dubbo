package org.minidubbo.rpc.proxy;

import org.minidubbo.rpc.Invoker;
import org.minidubbo.rpc.URL;
import org.minidubbo.rpc.invoker.AbstractProxyInvoker;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class JDKProxyFactory implements ProxyFactory{
    @Override
    public <T> T getProxy(Class<T> clazz, InvocationHandler invocationHandler) {
        return (T) Proxy.newProxyInstance(clazz.getClassLoader(), new Class[]{clazz}, invocationHandler);
    }

    @Override
    public <T> Invoker<T> getInvoker(T ref, Class<T> clazz, URL url) {
        return new AbstractProxyInvoker(ref, clazz, url) {
            @Override
            protected Object doInvoke(Object proxy, String methodName, Class[] parameterTypes, Object[] arguments) throws Throwable {
                Method method = proxy.getClass().getMethod(methodName, parameterTypes);
                return method.invoke(proxy, arguments);
            }
        };
    }
}
