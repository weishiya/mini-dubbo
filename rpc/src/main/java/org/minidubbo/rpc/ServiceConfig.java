package org.minidubbo.rpc;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import lombok.extern.slf4j.Slf4j;
import org.minidubbo.common.Consant;
import org.minidubbo.common.NetUtil;
import org.minidubbo.rpc.protocol.DubboProtocol;
import org.minidubbo.rpc.proxy.JDKProxyFactory;
import org.minidubbo.rpc.proxy.ProxyFactory;

import java.lang.reflect.InvocationHandler;
import java.net.InetAddress;
@Slf4j
public class ServiceConfig<T> {
    private T ref;

    private Class<?> interfaceClass;

    private String group;

    private String version;

    private ProxyFactory proxyFactory;

    private Protocol protocol;

    private URL providerUrl;

    private InvocationHandler invocationHandler;

    private ServerBootstrap bootstrap;

    private EventLoopGroup bossGroup;

    private EventLoopGroup workerGroup;

    private volatile boolean exported = false;

    public void setInterfaceClass(Class<?> interfaceClass){
        this.interfaceClass = interfaceClass;
    }

    public void setRef(T ref){
        this.ref = ref;
    }

    public void export(){
        if(exported){
            return;
        }
        synchronized (this){
            if(exported){
                return;
            }
            //初始化protocol
            initProtocol();
            //初始化代理工厂
            initProxyFactory();
            //构建provdierUrl
            buildURL();
            //创建invoker
            Invoker<?> invoker = createInvoker();
            //发布服务
            Exporter<?> exporter = doExport(invoker);

            if(exporter.isExported()){
                this.exported = true;
                log.info("export success");
            }else {
                log.info("export failed");
            }


        }
    }

    private void initProtocol() {
        protocol = new DubboProtocol();
    }

    private void buildURL() {
        URL url = new URL();
        InetAddress localAddress = NetUtil.getLocalAddress();
        url.setIp(localAddress.getHostAddress());
        url.setPort(Consant.PORT);
        url.setProtocol(Consant.DUBBO_PROTOCOL);
        url.setInterfaceName(interfaceClass.getName());
        if(group != null){
            url.putParams(Consant.GROUP_KEY,group);
        }
        if(version!=null){
            url.putParams(Consant.VERSION_KEY,group);
        }
        this.providerUrl = url;
    }

    private void initProxyFactory() {
        proxyFactory = new JDKProxyFactory();
    }

    private Invoker<?> createInvoker() {
        return proxyFactory.getInvoker(ref, (Class) interfaceClass, providerUrl);
    }


    private  Exporter<?> doExport(Invoker<?> invoker) {
        return protocol.export(invoker);
    }

    public void shutdown(){
        if(bossGroup != null){
            bossGroup.shutdownGracefully();
        }
        if(workerGroup != null){
            workerGroup.shutdownGracefully();
        }
    }
}
