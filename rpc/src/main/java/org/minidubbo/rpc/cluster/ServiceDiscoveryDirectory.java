package org.minidubbo.rpc.cluster;

import org.minidubbo.rpc.Invoker;
import org.minidubbo.rpc.Protocol;
import org.minidubbo.rpc.URL;

import java.util.ArrayList;
import java.util.List;

public class ServiceDiscoveryDirectory<T> implements Directory{

    private List<URL> providerUrls;

    private URL consumerUrl;

    private Class<T> clazz;

    private Protocol protocol;

    private List<Invoker> allInvokers = new ArrayList<>();

    public ServiceDiscoveryDirectory(URL consumerUrl,Class<T> clazz){
        this.consumerUrl = consumerUrl;
        this.clazz = clazz;
    }


    @Override
    public void freshProviderUrls(List providerUrls) {
        this.providerUrls = providerUrls;
        doRefresh();
    }



    @Override
    public Class<T> getInterface() {
        return clazz;
    }

    @Override
    public URL getConsumerUrl() {
        return consumerUrl;
    }

    @Override
    public List<Invoker> getAllInvokers() {
        return allInvokers;
    }

    @Override
    public void setProtocol(Protocol protocol){
        this.protocol = protocol;
    }

    private void doRefresh() {
        if(providerUrls == null){
            return;
        }
        for (URL providerUrl : providerUrls) {
            Invoker<T> invoker = protocol.refer(clazz, providerUrl);
            allInvokers.add(invoker);
        }
    }
}
