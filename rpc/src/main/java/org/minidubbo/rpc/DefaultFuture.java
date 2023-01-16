package org.minidubbo.rpc;

import com.sun.org.apache.regexp.internal.RE;
import io.netty.channel.Channel;
import org.minidubbo.common.Consant;
import org.minidubbo.rpc.timer.HashedWheelTimer;
import org.minidubbo.rpc.timer.Timeout;
import org.minidubbo.rpc.timer.Timer;
import org.minidubbo.rpc.timer.TimerTask;

import java.util.Map;
import java.util.concurrent.*;

public class DefaultFuture extends CompletableFuture {

    private final Long id;

    private final int timeout;

    private Channel channel;

    private Request request;

    private Timeout timeoutCheckTask;

    private static final Timer TIMER = new HashedWheelTimer();

    private static final Map<Long/*requestId*/, CompletableFuture> FUTURE_MAP = new ConcurrentHashMap<>();

    public DefaultFuture(Channel channel,Request request,int timeout){
        this.channel = channel;
        this.request = request;
        this.timeout = timeout;
        this.id = request.getId();
        FUTURE_MAP.put(request.getId(),this);
        timeoutCheck(this);
    }

    public Long getId(){
        return id;
    }

    private void timeoutCheck(DefaultFuture future){
        TimeoutCheckTask task = new TimeoutCheckTask(future.getId());
        timeoutCheckTask = TIMER.newTimeout(task, this.timeout, TimeUnit.SECONDS);
    }

    public void cancelTimecheck(){
        timeoutCheckTask.cancel();
    }

    public static CompletableFuture getCompletableFuture(long requestId){
        return FUTURE_MAP.remove(requestId);
    }

    private static class TimeoutCheckTask implements TimerTask {

        private final Long requestID;

        TimeoutCheckTask(Long requestID) {
            this.requestID = requestID;
        }

        @Override
        public void run(Timeout timeout) {
            DefaultFuture future =(DefaultFuture) DefaultFuture.getCompletableFuture(requestID);
            if (future == null || future.isDone()) {
                return;
            }

            notifyTimeout(future);

        }

        private void notifyTimeout(DefaultFuture future) {
            // create exception response.
            Response timeoutResponse = new Response(future.getId());
            // set timeout status.
            timeoutResponse.setStatus(Response.TIME_OUT);
            timeoutResponse.setErrorMessage("already timeout");
            // handle response.
            CompletableFuture completableFuture = getCompletableFuture(future.getId());
            completableFuture.completeExceptionally(new TimeoutException());
            future.timeoutCheckTask.isExpired();
        }
    }
}
