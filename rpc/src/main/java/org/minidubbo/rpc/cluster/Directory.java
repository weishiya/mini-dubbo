package org.minidubbo.rpc.cluster;

import org.minidubbo.rpc.Invoker;
import org.minidubbo.rpc.Protocol;
import org.minidubbo.rpc.URL;

import java.util.List;

public interface Directory<T> {

    Class<T> getInterface();

    URL getConsumerUrl();

    void freshProviderUrls(List<URL> providerUrls);

    List<Invoker<T>> getAllInvokers();

    void setProtocol(Protocol protocol);
}
