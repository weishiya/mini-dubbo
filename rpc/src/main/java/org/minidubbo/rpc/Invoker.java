package org.minidubbo.rpc;

import org.minidubbo.rpc.exception.RpcException;

/**
 * consumer发起网络调用、provider触发本地调用
 * @param <T>
 */
public interface Invoker<T> {
    /**
     * 获取代理的class
     * @return
     */
    Class<T> getInterface();

    /**
     * 发起网络调用
     * @param invocation
     * @return
     */
    Result invoke(Invocation invocation) throws RpcException;

    URL getUrl();
}
