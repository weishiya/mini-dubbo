package org.minidubbo.rpc.registry;

import org.minidubbo.rpc.URL;

import java.util.List;

public interface RegistryService {
    void start();
    void register(URL url);
    void unregister(URL url);
    List<URL> serviceDiscovery(String category);
    void subscribe(URL url);
    void unsubscribe(URL url);
    void destory();
}
