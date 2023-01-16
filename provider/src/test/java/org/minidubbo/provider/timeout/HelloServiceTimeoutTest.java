package org.minidubbo.provider.timeout;

import org.minidubbo.api.HelloService;

import java.util.concurrent.TimeUnit;

public class HelloServiceTimeoutTest implements HelloService {
    @Override
    public String hello() {
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {

        }
        return "hello,provider";
    }
}
