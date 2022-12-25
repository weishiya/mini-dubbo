package org.minidubbo.consumer;

import org.minidubbo.api.HelloService;

public class Consumer {

    private HelloService helloService;

    public String sayHello(){
        String result = helloService.hello("i am consumer");
        return result;
    }
}
