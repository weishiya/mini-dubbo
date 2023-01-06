package org.minidubbo.rpc;

/**
 * 网络调用入参
 */
public interface Invocation {
    /**
     * 获取服务key  group/interfaceName:version
     * @return
     */
    String getProtocolServiceKey();

    /**
     * 获取调用方法
     *
     * @return method name.
     * @serial
     */
    String getMethodName();

    /**
     * 获取服务名
     *
     * @return
     */
    String getServiceName();

    /**
     * 获取参数类型
     *
     * @return parameter types.
     * @serial
     */
    Class<?>[] getParameterTypes();

    /**
     * 获取参数
     *
     * @return arguments.
     * @serial
     */
    Object[] getArguments();
}
