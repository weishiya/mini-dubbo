package org.minidubbo.rpc;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

public interface Client {

    CompletableFuture request(Object data);

    void connect();

    boolean isConnect();

    void close();
}
