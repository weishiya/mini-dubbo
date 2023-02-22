package org.minidubbo.consumer.heartbeat;

import org.junit.Test;
import org.minidubbo.api.HelloService;
import org.minidubbo.consumer.protocol.ProtocolTest;
import org.minidubbo.rpc.ReferenceConfig;

import java.util.concurrent.CountDownLatch;

public class HeartBeatTest {
    @Test
    public void test() throws InterruptedException {
        ReferenceConfig<HelloService> referenceConfig = new ReferenceConfig<>();
        referenceConfig.setInterfaceClass(HelloService.class);
        referenceConfig.setShareconnections(true);
        referenceConfig.setConnections(1);
        referenceConfig.setTimeout(3);
        HelloService helloService = referenceConfig.get();
        helloService.hello();
        new CountDownLatch(1).await();
    }
}
