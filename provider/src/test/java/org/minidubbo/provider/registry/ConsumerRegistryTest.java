package org.minidubbo.provider.registry;

import org.minidubbo.api.HelloService;
import org.minidubbo.rpc.ReferenceConfig;

import java.util.concurrent.CountDownLatch;

public class ConsumerRegistryTest {
    public static void main(String[] args) throws InterruptedException {
        ReferenceConfig<HelloService> referenceConfig = new ReferenceConfig<>();
        referenceConfig.setInterfaceClass(HelloService.class);
        referenceConfig.setRegistryAddress("127.0.0.1:2181");
        HelloService helloService = referenceConfig.get();
        String hello = helloService.hello();
        System.out.println(hello);
        new CountDownLatch(1).await();
    }
}
