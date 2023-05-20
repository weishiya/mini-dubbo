package org.minidubbo.adapter.spring;

import org.minidubbo.api.HelloService;
import org.minidubbo.rpc.ReferenceConfig;
import org.springframework.beans.factory.FactoryBean;

import java.lang.reflect.Constructor;

public class ReferenceBean<T> implements FactoryBean {
    private Class<T> targetInterface;

    public ReferenceBean(Class<T> targetInterface){
        this.targetInterface = targetInterface;
    }

    @Override
    public T getObject() throws Exception {
        //todo 缓存reference
        ReferenceConfig referenceConfig = new ReferenceConfig();
        referenceConfig.setInterfaceClass(targetInterface);
        referenceConfig.setRegistryAddress("127.0.0.1:2181");
        Object o = referenceConfig.get();
        return (T) o;
    }

    @Override
    public Class<?> getObjectType() {
        return targetInterface;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }
}
