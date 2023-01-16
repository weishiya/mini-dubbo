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

    private final Client[] clients;

    private AtomicLong clientIndex = new AtomicLong(0);

    public DubboInvoker(Class<T> serviceType, URL url, Client[] clients){
        super(serviceType,url);
        this.clients = clients;
    }

    @Override
    protected Result doInvoke(Invocation invocation) {
        Integer timeout = (Integer)invocation.getAttachment().getOrDefault(Consant.TIMEOUT_KEY, Consant.DEFAULT_TIMEOUT);
        long selectedClient = clientIndex.getAndIncrement() % clients.length;
        Client client = clients[(int)selectedClient];
        CompletableFuture reponseFuture = client.request(invocation,timeout);
        AsyncResult result = new AsyncResult(reponseFuture);
        return result;
    }


}
