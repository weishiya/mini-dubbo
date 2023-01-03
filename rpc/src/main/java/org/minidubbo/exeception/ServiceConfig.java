package org.minidubbo.exeception;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.*;
import io.netty.util.ReferenceCountUtil;
import org.minidubbo.common.Consant;
import org.minidubbo.common.NetUtil;
import org.minidubbo.exeception.proxy.JDKProxyFactory;
import org.minidubbo.exeception.proxy.ProxyFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.InetSocketAddress;

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
            doExport(invoker);

            this.exported = true;
            System.out.println("export success");

        }
    }

    private void initProtocol() {
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
    }

    private void initProxyFactory() {
        proxyFactory = new JDKProxyFactory();
    }

    private Invoker<?> createInvoker() {
        return proxyFactory.getInvoker(ref, (Class) interfaceClass, providerUrl);
    }


    private void doExport(Invoker<?> invoker) {
        protocol.export(invoker);
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
