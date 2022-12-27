package org.minidubbo.rpc;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.*;
import io.netty.util.ReferenceCountUtil;
import org.minidubbo.rpc.proxy.ProxyFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.util.Objects;

public class ServiceConfig<T> {
    private T ref;

    private Class<?> interfaceClass;

    private ProxyFactory proxyFactory;

    private InvocationHandler invocationHandler;

    private ServerBootstrap bootstrap;

    private EventLoopGroup bossGroup;

    private EventLoopGroup workerGroup;

    public void setInterfaceClass(Class<?> interfaceClass){
        this.interfaceClass = interfaceClass;
    }

    public void setRef(T ref){
        this.ref = ref;
    }

    public void export(){
        createProxy();
        openHttpServer();
    }
    //生成代理
    private void createProxy() {
        invocationHandler = new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                return method.invoke(ref,args);
            }
        };
    }

    //本地启动一个server，开放一个端口，可以接受网络请求
    private void openHttpServer() {
        bootstrap = new ServerBootstrap();
        bossGroup = new NioEventLoopGroup(1);
        workerGroup = new NioEventLoopGroup(Runtime.getRuntime().availableProcessors());
        bootstrap.group(bossGroup,workerGroup)
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_REUSEADDR, Boolean.TRUE)
                .childOption(ChannelOption.TCP_NODELAY, Boolean.TRUE)
                .childOption(ChannelOption.SO_KEEPALIVE, false)
                .childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                //绑定20883端口
                .localAddress(new InetSocketAddress(20883))
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new HttpServerCodec())
                                .addLast(new HttpObjectAggregator(1024*10))
                                .addLast(new NettyHttpServerHandler(invocationHandler));
                    }
                });

        try{
            this.bootstrap.bind().sync();
            System.out.println("启动了server监听端口:"+20883);
            Runtime.getRuntime().addShutdownHook(new Thread(()->{
                this.shutdown();
            }));
        }catch (Exception e){
            throw new RuntimeException("this.serverBootstrap.bind().sync() fail!",e);
        }
    }

    public void shutdown(){
        if(bossGroup != null){
            bossGroup.shutdownGracefully();
        }
        if(workerGroup != null){
            workerGroup.shutdownGracefully();
        }
    }

    class NettyHttpServerHandler extends ChannelInboundHandlerAdapter{

        private InvocationHandler invocationHandler;

        public NettyHttpServerHandler(InvocationHandler invocationHandler){
            this.invocationHandler = invocationHandler;
        }

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg){
            if(msg instanceof HttpRequest) {
                FullHttpRequest request = (FullHttpRequest)msg;
                String uri = request.uri();
                if("/hello".equals(uri)){
                    //触发操作
                    try {
                        Method helloMethod = interfaceClass.getDeclaredMethod("hello");
                        String result = (String)invocationHandler.invoke(ref, helloMethod, new Object[]{});
                        DefaultFullHttpResponse response =
                                new DefaultFullHttpResponse(HttpVersion.HTTP_1_1,
                                        HttpResponseStatus.OK,
                                        Unpooled.wrappedBuffer(result.getBytes()));
                        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);;
                    }  catch (Throwable throwable) {
                        throwable.printStackTrace();
                    }finally {
                        ReferenceCountUtil.release(request);
                    }
                }
            }
        }
    }
}
