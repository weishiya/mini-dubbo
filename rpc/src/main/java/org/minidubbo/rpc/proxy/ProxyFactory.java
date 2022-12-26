package org.minidubbo.rpc.proxy;

import java.lang.reflect.InvocationHandler;

/**
 *
 */
public interface ProxyFactory {
    /**
     * generate proxy
     * @param clazz
     * @param invocationHandler
     * @param <T>
     * @return
     */
    <T> T getProxy(Class<T> clazz, InvocationHandler invocationHandler);
}
