package org.minidubbo.rpc;

import org.minidubbo.common.Consant;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class RpcInvocation implements Invocation, Serializable {

    private static final long serialVersionUID = -1L;

    private String serviceKey;

    private String methodName;

    private String serviceName;

    private Class<?>[] parameterTypes;

    private Object[] arguments;

    private Map<String,Object> attachment;

    public RpcInvocation(String protocolServiceKey,String methodName,String serviceName,
                         Class<?>[] parameterTypes,Object[] arguments,Map<String,Object> attachment){
        this.serviceKey = protocolServiceKey;
        this.methodName = methodName;
        this.parameterTypes = parameterTypes;
        this.arguments = arguments;
        this.attachment = attachment;
        this.attachment = new HashMap<>();
        if (attachment != null){
            //超时时间
            this.attachment.put(Consant.TIMEOUT_KEY,attachment.get(Consant.TIMEOUT_KEY));
        }
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

    @Override
    public Map<String, Object> getAttachment() {
        return attachment;
    }


}
