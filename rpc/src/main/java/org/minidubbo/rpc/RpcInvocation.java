package org.minidubbo.rpc;

import java.io.Serializable;

public class RpcInvocation implements Invocation, Serializable {

    private static final long serialVersionUID = -1L;

    private String serviceKey;

    private String methodName;

    private String serviceName;

    private Class<?>[] parameterTypes;

    private Object[] arguments;

    public RpcInvocation(String serviceKey,String methodName,String serviceName,Class<?>[] parameterTypes,Object[] arguments){
        this.serviceKey = serviceKey;
        this.methodName = methodName;
        this.parameterTypes = parameterTypes;
        this.arguments = arguments;
    }

    @Override
    public String getProtocolServiceKey() {
        return serviceKey;
    }

    @Override
    public String getMethodName() {
        return methodName;
    }

    @Override
    public String getServiceName() {
        return serviceName;
    }

    @Override
    public Class<?>[] getParameterTypes() {
        return parameterTypes;
    }

    @Override
    public Object[] getArguments() {
        return arguments;
    }
}
