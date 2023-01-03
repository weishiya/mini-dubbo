package org.minidubbo.exeception.proxy;

import org.minidubbo.exeception.Invoker;
import org.minidubbo.exeception.URL;

import java.lang.reflect.InvocationHandler;

/**
 * 生成代理的工厂
 */
public interface ProxyFactory {
    /**
     * 生成代理
     * @param clazz
     * @param invocationHandler
     * @param <T>
     * @return
     */
    <T> T getProxy(Class<T> clazz, InvocationHandler invocationHandler);

    /**
     * 生成invoker，provider端生成
     * @param ref
     * @param clazz
     * @param url
     * @param <T>
     * @return
     */
    <T> Invoker<T> getInvoker(T ref, Class<T> clazz, URL url);
}
