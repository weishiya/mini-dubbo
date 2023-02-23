package org.minidubbo.rpc;


import org.minidubbo.common.Consant;
import org.minidubbo.common.NetUtil;
import org.minidubbo.rpc.protocol.DubboProtocol;
import org.minidubbo.rpc.proxy.JDKProxyFactory;
import org.minidubbo.rpc.proxy.ProxyFactory;

import java.lang.reflect.InvocationHandler;
import java.net.InetAddress;

public class ReferenceConfig<T> extends AbstractMethodConfig{

    private T ref;

    private Class<?> interfaceClass;

    private ProxyFactory proxyFactory;

    private InvocationHandler invocationHandler;

    private Protocol protocol;

    private URL url;

    private String group;

    private String version;

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

        initProtocol();
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
        URL url = new URL();
        InetAddress localAddress = NetUtil.getLocalAddress();
        url.setIp(localAddress.getHostAddress());
        url.setProtocol(Consant.CONSUMER_PROTOCOL);
        url.setInterfaceName(interfaceClass.getName());
        if(group != null){
            url.putParams(Consant.GROUP_KEY,group);
        }
        if(version!=null){
            url.putParams(Consant.VERSION_KEY,group);
        }
        if(getTimeout() != null && getTimeout() > 0){
            url.putParams(Consant.TIMEOUT_KEY,getTimeout());
        }
        url.putParams(Consant.CONNECTIONS_KEY,this.getConnections());
        url.putParams(Consant.SHARE_CONNECTIONS_KEY,this.isShareconnections());
        this.url = url;
    }

    private void initProtocol() {
        protocol = new DubboProtocol("");
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
