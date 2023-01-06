package org.minidubbo.rpc.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;
import org.minidubbo.rpc.URL;


@Slf4j
public class NettyServer {

    private ServerBootstrap bootstrap;

    private EventLoopGroup bossGroup;

    private EventLoopGroup workerGroup;

    private final URL url;

    private final ChannelHandler requestHandler;

    public NettyServer(URL url, ChannelHandler requestHandler){
        this.url = url;
        this.requestHandler = requestHandler;
    }

    public void open() throws Throwable{
        bootstrap = new ServerBootstrap();
        bossGroup = new NioEventLoopGroup(1);
        workerGroup = new NioEventLoopGroup(Runtime.getRuntime().availableProcessors());
        bootstrap.group(bossGroup,workerGroup)
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_REUSEADDR, Boolean.TRUE)
                .childOption(ChannelOption.TCP_NODELAY, Boolean.TRUE)
                .childOption(ChannelOption.SO_KEEPALIVE, true)
                .childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ChannelPipeline pipeline = ch.pipeline();
                        //todo 添加编解码器
                        pipeline.addLast(requestHandler);
                    }
                });
        // bind
        String ip = url.getIp();
        int port = url.getPort();
        ChannelFuture channelFuture = bootstrap.bind(ip,port);
        channelFuture.syncUninterruptibly();

        if(channelFuture.isSuccess()){
            log.info("minidubbo provider server started at port {}",port);
        }else {
            log.info("minidubbo provider server started failed");
        }
    }
}
