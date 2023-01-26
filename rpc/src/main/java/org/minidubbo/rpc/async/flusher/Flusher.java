package org.minidubbo.rpc.async.flusher;

import com.lmax.disruptor.EventHandler;
import io.netty.channel.ChannelHandlerContext;

import java.util.concurrent.ExecutorService;

public interface Flusher<T> {
    void add(Object msg, ChannelHandlerContext ctx);
    void start();
    void shutdown();
    void handleWith(EventHandler<T>... eventHandler);
}
