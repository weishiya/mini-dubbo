package org.minidubbo.provider.impl;

import org.minidubbo.api.HelloService;
import org.minidubbo.rpc.ServiceConfig;

import java.util.concurrent.CountDownLatch;

public class Provider {
    public static void main(String[] args) throws InterruptedException {
        ServiceConfig<HelloService> serviceConfig = new ServiceConfig();
        HelloService helloService = new HelloServiceImpl();
        serviceConfig.setRef(helloService);
        serviceConfig.setInterfaceClass(HelloService.class);
        serviceConfig.export();
        new CountDownLatch(1).await();
    }
}
