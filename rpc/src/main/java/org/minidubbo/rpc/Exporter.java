package org.minidubbo.rpc;

/**
 * 服务暴露对象
 * @param <T>
 */
public interface Exporter<T> {
    /**
     * 获取invoker
     * @return
     */
    Invoker<T> getInvoker();

    /**
     * 获取servicekey
     * @return
     */
    String getServiceKey();

    void exported();

    boolean isExported();
}
