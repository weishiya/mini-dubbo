package org.minidubbo.rpc.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;
import org.minidubbo.rpc.Client;
import org.minidubbo.rpc.DefaultFuture;
import org.minidubbo.rpc.Request;
import org.minidubbo.rpc.URL;
import org.minidubbo.rpc.codec.FastjsonSerialization;
import org.minidubbo.rpc.exception.RpcException;
import org.minidubbo.rpc.nettyHandler.*;
import org.minidubbo.rpc.timer.HashedWheelTimer;
import org.minidubbo.rpc.timer.Timeout;
import org.minidubbo.rpc.timer.Timer;
import org.minidubbo.rpc.timer.TimerTask;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Slf4j
public class NettyClient implements Client {

    private Channel channel;

    private Bootstrap bootstrap;

    private EventLoopGroup eventLoopGroup;

    private URL url;

    private ChannelHandler clientHandler;

    private boolean isConnected = false;

    private static Map<Channel,NettyClient> CLIENT_MAP = new ConcurrentHashMap<>();

    static Timer TIMER = new HashedWheelTimer();

    public NettyClient(URL url, ChannelHandler clientHandler){
        this.url = url;
        this.clientHandler = clientHandler;
        open();
    }

    public static NettyClient getClient(Channel channel){
        return CLIENT_MAP.get(channel);
    }

    private void open(){
        bootstrap = new Bootstrap();
        eventLoopGroup = new NioEventLoopGroup();
        bootstrap.group(eventLoopGroup)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .option(ChannelOption.TCP_NODELAY, true)
                .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {

                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new DubboEncodeHandler(new FastjsonSerialization()));
                        ch.pipeline().addLast(new DubboDecoderHandler());
                        ch.pipeline().addLast(new LoggingHandler());
                        ch.pipeline().addLast(new IdleStateHandler(60,30,120, TimeUnit.SECONDS));
                        ch.pipeline().addLast(new ClientIdleHandler());
                        ch.pipeline().addLast(new HeartBeartHandler());

                        ch.pipeline().addLast(clientHandler);
                    }
                });
    }

    @Override
    public CompletableFuture request(Object data,int timeout) {
        if(!isConnect()){
            throw new RpcException(RpcException.CHANNEL_NOT_CONNECTED,"channel not connect host "+url.getIp());
        }
        Request request = new Request(data);
        channel.writeAndFlush(request);
        return new DefaultFuture(channel,request,timeout);
    }

    @Override
    public void connect() {
        if(isConnect()){
            return;
        }
        synchronized (this){
            if(isConnect()){
                return;
            }
            ChannelFuture connectFuture = bootstrap.connect(url.getIp(), url.getPort()).syncUninterruptibly();
            boolean b = connectFuture.awaitUninterruptibly(3000);
            if(b && connectFuture.isSuccess()){
                this.isConnected = true;
                Channel channel = connectFuture.channel();
                NettyClient.this.channel = channel;
                CLIENT_MAP.put(channel,this);
            }else {
                log.warn("can not connect server {} {}",url.getIp(), url.getPort());
            }
        }

    }

    @Override
    public boolean isConnect() {
        return channel!=null && channel.isActive();
    }

    @Override
    public void close() {
        channel.close();
        eventLoopGroup.shutdownGracefully();
    }

    @Override
    public void reconnect() {
        channel.close().addListener((future) -> {
           if(future.isSuccess()){
               this.connect();
           }
        });
    }


}
