package org.minidubbo.rpc;

/**
 * 协议类
 */
public interface Protocol {
    /**
     * 创建invoker
     * @param type
     * @param url
     * @param <T>
     * @return
     */
    <T> Invoker<T> refer(Class<T> type, URL url);

    /**
     * 进行服务暴露
     * @param invoker
     * @param <T>
     * @return
     */
    <T> Exporter<T> export(Invoker<T> invoker);

    void destory();
}
