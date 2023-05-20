package org.minidubbo.provider.protocol;

import org.minidubbo.api.HelloService;
import org.minidubbo.provider.impl.HelloServiceImpl;
import org.minidubbo.provider.timeout.HelloServiceTimeoutTest;
import org.minidubbo.rpc.ServiceConfig;

public class ProtocolProviderTest {
    public static void main(String[] args) {
        ServiceConfig<HelloService> serviceConfig = new ServiceConfig();
        HelloService helloService = new HelloServiceImpl();
        serviceConfig.setRef(helloService);
        serviceConfig.setInterfaceClass(HelloService.class);
        serviceConfig.setRegistryAddress("127.0.0.1:2181");
        serviceConfig.export();
    }
}
