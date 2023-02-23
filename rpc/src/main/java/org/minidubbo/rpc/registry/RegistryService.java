package org.minidubbo.rpc.registry;

import org.minidubbo.rpc.URL;

public interface RegistryService {
    void start();
    void register(URL url);
    void unregister(URL url);
    void subscribe(URL url);
    void unsubscribe(URL url);
    void destory();
}
