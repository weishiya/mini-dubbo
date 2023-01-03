package org.minidubbo.exeception;

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
}
