package org.minidubbo.consumer.protocol;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.minidubbo.api.HelloService;
import org.minidubbo.rpc.ReferenceConfig;

import java.util.concurrent.CountDownLatch;
@Slf4j
public class ProtocolTest {
    @Test
    public void test() throws InterruptedException {
        ReferenceConfig<HelloService> referenceConfig = new ReferenceConfig<>();
        referenceConfig.setInterfaceClass(HelloService.class);
        referenceConfig.setShareconnections(false);
        referenceConfig.setConnections(2);
        referenceConfig.setTimeout(3);
        HelloService helloService = referenceConfig.get();

        for (int i = 0; i < 10; i++) {
            new Thread(new SendTask(helloService),"thread "+i).start();
        }
        new CountDownLatch(1).await();
    }

    class SendTask implements Runnable{
        private final HelloService helloService;
        public SendTask(HelloService helloService){
            this.helloService = helloService;
        }
        @Override
        public void run() {
            for (int i = 0; i < 10; i++) {
                String result = helloService.hello();
                log.info("线程{} 第 {} 次调用hello方法，收到数据:{}",Thread.currentThread().getName(),i,result);
            }
        }
    }
}
