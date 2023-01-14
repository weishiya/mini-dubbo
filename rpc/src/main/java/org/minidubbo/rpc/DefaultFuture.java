package org.minidubbo.rpc;

import io.netty.channel.Channel;
import org.minidubbo.rpc.timer.HashedWheelTimer;
import org.minidubbo.rpc.timer.Timeout;
import org.minidubbo.rpc.timer.Timer;
import org.minidubbo.rpc.timer.TimerTask;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;

public class DefaultFuture extends CompletableFuture {

    private Channel channel;

    private Request request;

    private static final Timer TIMER = new HashedWheelTimer();

    private static final Map<Long/*requestId*/, CompletableFuture> FUTURE_MAP = new ConcurrentHashMap<>();

    public DefaultFuture(Channel channel,Request request){
        this.channel = channel;
        this.request = request;
        FUTURE_MAP.put(request.getId(),this);

    }

    public static CompletableFuture getCompletableFuture(long requestId){
        return FUTURE_MAP.remove(requestId);
    }


}