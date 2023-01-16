package org.minidubbo.provider;

import org.junit.Test;
import org.minidubbo.api.HelloService;
import org.minidubbo.provider.impl.HelloServiceImpl;
import org.minidubbo.provider.timeout.HelloServiceTimeoutTest;
import org.minidubbo.rpc.ServiceConfig;

public class ProviderTest {
    public static void main(String[] args) {
        ServiceConfig<HelloService> serviceConfig = new ServiceConfig();
        HelloService helloService = new HelloServiceTimeoutTest();
        serviceConfig.setRef(helloService);
        serviceConfig.setInterfaceClass(HelloService.class);
        serviceConfig.export();
    }
}
