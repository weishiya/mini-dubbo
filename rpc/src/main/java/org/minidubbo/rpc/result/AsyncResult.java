package org.minidubbo.rpc.result;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

public class AsyncResult extends RpcResult {
    private static final long serialVersionUID = -1L;
    private final CompletableFuture responseFuture;

    public AsyncResult(CompletableFuture responseFuture){
        this.responseFuture = responseFuture;
    }

    public Future getResponseFuture(){
        return responseFuture;
    }
}
