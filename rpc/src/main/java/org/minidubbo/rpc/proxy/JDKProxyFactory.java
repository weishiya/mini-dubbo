package org.minidubbo.rpc.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.Arrays;

public class JDKProxyFactory implements ProxyFactory{
    @Override
    public <T> T getProxy(Class<T> clazz, InvocationHandler invocationHandler) {
        return (T) Proxy.newProxyInstance(clazz.getClassLoader(), new Class[]{clazz}, invocationHandler);
    }
}
