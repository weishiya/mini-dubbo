package org.minidubbo.rpc.invoker;

import io.netty.channel.Channel;
import org.minidubbo.common.Consant;
import org.minidubbo.rpc.*;
import org.minidubbo.rpc.exception.RpcException;
import org.minidubbo.rpc.result.AsyncResult;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicLong;

public class DubboInvoker<T> extends AbstractInvoker<T> {

    private final Client client;

    private AtomicLong clientIndex = new AtomicLong(0);

    public DubboInvoker(Class<T> serviceType, URL url, Client client){
        super(serviceType,url);
        this.client = client;
    }

    @Override
    protected Result doInvoke(Invocation invocation) {
        Integer timeout = (Integer)invocation.getAttachment().getOrDefault(Consant.TIMEOUT_KEY, Consant.DEFAULT_TIMEOUT);
        CompletableFuture reponseFuture = client.request(invocation,timeout);
        AsyncResult result = new AsyncResult(reponseFuture);
        return result;
    }


}
