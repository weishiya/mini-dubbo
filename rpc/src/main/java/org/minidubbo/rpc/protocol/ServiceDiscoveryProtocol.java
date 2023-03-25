package org.minidubbo.rpc.protocol;

import lombok.extern.slf4j.Slf4j;
import org.minidubbo.rpc.*;
import org.minidubbo.rpc.cluster.*;
import org.minidubbo.rpc.invoker.DubboInvoker;

import java.util.List;

@Slf4j
public class ServiceDiscoveryProtocol implements Protocol {

    private DubboProtocol protocol;

    public ServiceDiscoveryProtocol(String zkpath) {
        protocol = new DubboProtocol(zkpath);
    }

    @Override
    public <T> Invoker<T> refer(Class<T> type, URL url) {
        //订阅目录
        String category = toCategory(url);

        List<URL> urls = null;
        try {
            urls = protocol.getRegistryService().serviceDiscovery(category);
        } catch (Exception e) {
            log.error("service discovery failed {} ",url.toString(),e);
        }

        Directory directory = new ServiceDiscoveryDirectory(url,type);
        directory.setProtocol(protocol);
        directory.freshProviderUrls(urls);

        return new FailoverClusterInvoker(directory);
    }

    @Override
    public <T> Exporter<T> export(Invoker<T> invoker) {
        return null;
    }

    @Override
    public void destory() {
        protocol.destory();
    }


    private String toCategory(URL url){
        String interfaceName = url.getInterfaceName();
        return "/minidubbo/"+interfaceName+"/providers";
    }
}
