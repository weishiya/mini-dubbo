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

    private Protocol protocol;

    private String consumerUrl;

    private URL url;

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
        //初始化代理工厂
        initProxyFactory();
        //构建url
        buildUrl();
        //创建invoker
        Invoker invoker = createInvoker();
        //创建代理类所需要的invocationHandler
        initInvocationHandler(invoker);
        //创建代理
        createProxy();
    }
    //初始化代理工厂
    private void initProxyFactory(){
        proxyFactory = new JDKProxyFactory();
    }

    private void buildUrl(){
        this.consumerUrl = "consumer://127.0.0.1:20880/"+interfaceClass.getName();
        url = parse(consumerUrl);
    }

    private URL parse(String consumerUrl) {
        return null;
    }
    private Invoker createInvoker(){
        return protocol.refer(interfaceClass,url);
    }
    //初始化代理类要做的事情
    private void initInvocationHandler(Invoker invoker) {
        invocationHandler = new InvokerInvocationHandler(invoker);
    }
    //create proxy
    private void createProxy(){
        ref = (T) proxyFactory.getProxy(interfaceClass,invocationHandler);
    }
}
