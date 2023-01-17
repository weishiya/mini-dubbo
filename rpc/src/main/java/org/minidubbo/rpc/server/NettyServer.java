package org.minidubbo.rpc.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;
import org.minidubbo.rpc.URL;
import org.minidubbo.rpc.codec.FastjsonSerialization;
import org.minidubbo.rpc.nettyHandler.DubboDecoderHandler;
import org.minidubbo.rpc.nettyHandler.DubboEncodeHandler;
import org.minidubbo.rpc.nettyHandler.LoggingHandler;


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
                .childOption(ChannelOption.TCP_NODELAY, Boolean.TRUE)
                .childOption(ChannelOption.SO_KEEPALIVE, true)
                .childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ChannelPipeline pipeline = ch.pipeline();
                        pipeline.addLast(new DubboEncodeHandler(new FastjsonSerialization()));
                        pipeline.addLast(new DubboDecoderHandler());
                        pipeline.addLast(new LoggingHandler());
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
