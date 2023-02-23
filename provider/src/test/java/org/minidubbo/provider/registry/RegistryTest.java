package org.minidubbo.provider.registry;

import org.minidubbo.api.HelloService;
import org.minidubbo.provider.impl.HelloServiceImpl;
import org.minidubbo.rpc.ServiceConfig;

public class RegistryTest {
    public static void main(String[] args) {
        ServiceConfig<HelloService> serviceConfig = new ServiceConfig();
        HelloService helloService = new HelloServiceImpl();
        serviceConfig.setRegistryAddress("127.0.0.1:2181");
        serviceConfig.setRef(helloService);
        serviceConfig.setInterfaceClass(HelloService.class);
        serviceConfig.export();
    }
}
