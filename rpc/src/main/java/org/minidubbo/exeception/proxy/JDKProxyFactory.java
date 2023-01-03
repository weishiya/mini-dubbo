package org.minidubbo.exeception.proxy;

import org.minidubbo.exeception.Invoker;
import org.minidubbo.exeception.URL;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

public class JDKProxyFactory implements ProxyFactory{
    @Override
    public <T> T getProxy(Class<T> clazz, InvocationHandler invocationHandler) {
        return (T) Proxy.newProxyInstance(clazz.getClassLoader(), new Class[]{clazz}, invocationHandler);
    }

    @Override
    public <T> Invoker<T> getInvoker(T ref, Class<T> clazz, URL url) {
        return null;
    }
}
