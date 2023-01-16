package org.minidubbo.consumer.timeout;

import org.junit.Test;
import org.minidubbo.api.HelloService;
import org.minidubbo.rpc.ReferenceConfig;

public class TimeoutTest {
    @Test
    public void testTimeout(){
        ReferenceConfig<HelloService> referenceConfig = new ReferenceConfig<>();
        referenceConfig.setInterfaceClass(HelloService.class);
        referenceConfig.setTimeout(1);
        HelloService helloService = referenceConfig.get();
        String result = helloService.hello();
        System.out.println(result);
    }

    @Test
    public void testTimeout2(){
        ReferenceConfig<HelloService> referenceConfig = new ReferenceConfig<>();
        referenceConfig.setInterfaceClass(HelloService.class);
        referenceConfig.setTimeout(6);
        HelloService helloService = referenceConfig.get();
        String result = helloService.hello();
        System.out.println(result);
    }
}
