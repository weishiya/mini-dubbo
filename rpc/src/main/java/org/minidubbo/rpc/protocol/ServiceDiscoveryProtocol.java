package org.minidubbo.rpc.protocol;

import org.minidubbo.rpc.*;
import org.minidubbo.rpc.cluster.*;
import org.minidubbo.rpc.invoker.DubboInvoker;

import java.util.List;

public class ServiceDiscoveryProtocol implements Protocol {

    private DubboProtocol protocol;

    public ServiceDiscoveryProtocol(String zkpath) {
        protocol = new DubboProtocol(zkpath);
    }

    @Override
    public <T> Invoker<T> refer(Class<T> type, URL url) {
        //订阅目录
        String category = toCategory(url);

        List<URL> urls = protocol.getRegistryService().serviceDiscovery(category);

        Directory directory = new ServiceDiscoveryDirectory(url,type);
        directory.freshProviderUrls(urls);
        directory.setProtocol(protocol);

        return new FailoverClusterInvoker(directory);
    }

    @Override
    public <T> Exporter<T> export(Invoker<T> invoker) {
        return null;
    }


    private String toCategory(URL url){
        String interfaceName = url.getInterfaceName();
        return "/dubbo/"+interfaceName+"/providers";
    }
}
