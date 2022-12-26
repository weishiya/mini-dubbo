package org.minidubbo.rpc;

import org.minidubbo.rpc.proxy.JDKProxyFactory;
import org.minidubbo.rpc.proxy.ProxyFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class ReferenceConfig<T> {

    private T ref;

    private Class<?> interfaceClass;

    private ProxyFactory proxyFactory;

    private InvocationHandler invocationHandler;

    public void setInterfaceClass(Class<?> interfaceClass){
        this.interfaceClass = interfaceClass;
    }

    public T get(){
        if(ref == null){
            synchronized (this){
                if(ref == null){
                    //初始化形成代理对象
                    init();
                }
            }
        }
        return ref;
    }
    //init
    private void init(){
        initProxyFactory();
        initInvocationHandler();
        createProxy();
    }
    //初始化代理工厂
    private void initProxyFactory(){
        proxyFactory = new JDKProxyFactory();
    }
    //初始化代理要做的事情
    private void initInvocationHandler() {
        invocationHandler = new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                //发起远程调用,这里先简单打印一下
                System.out.println("rpc 1.0");
                return "success";
            }
        };
    }
    //create proxy
    private void createProxy(){
        ref = (T) proxyFactory.getProxy(interfaceClass,invocationHandler);
    }
}
