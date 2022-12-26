package org.minidubbo.consumer;

import org.minidubbo.api.HelloService;
import org.minidubbo.rpc.ReferenceConfig;

public class Consumer {

    private HelloService helloService;

    public String sayHello(){
        String result = helloService.hello("i am consumer");
        return result;
    }

    public static void main(String[] args) {
        ReferenceConfig<HelloService> referenceConfig = new ReferenceConfig<>();
        referenceConfig.setInterfaceClass(HelloService.class);
        HelloService helloService = referenceConfig.get();
        String result = helloService.hello("i am consumer");
        System.out.println(result);
    }
}