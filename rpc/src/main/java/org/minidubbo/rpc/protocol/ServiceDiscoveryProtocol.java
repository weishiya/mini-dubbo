package org.minidubbo.rpc.protocol;

import org.minidubbo.rpc.Client;
import org.minidubbo.rpc.Invoker;
import org.minidubbo.rpc.URL;
import org.minidubbo.rpc.cluster.*;
import org.minidubbo.rpc.invoker.DubboInvoker;

import java.util.List;

public class ServiceDiscoveryProtocol extends DubboProtocol {


    public ServiceDiscoveryProtocol(String zkpath) {
        super(zkpath);
    }

    @Override
    public <T> Invoker<T> refer(Class<T> type, URL url) {
        //订阅目录
        String category = toCategory(url);

        List<URL> urls = registryService.serviceDiscovery(category);

        Directory directory = new ServiceDiscoveryDirectory(url,type);
        directory.freshProviderUrls(urls);

        return new FailoverClusterInvoker(directory);
    }


    private String toCategory(URL url){
        String interfaceName = url.getInterfaceName();
        return "/dubbo/"+interfaceName+"/providers";
    }
}
