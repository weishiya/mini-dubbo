package org.minidubbo.adapter.spring;

import org.minidubbo.adapter.spring.annotation.MiniDubboReference;
import org.minidubbo.api.HelloService;
import org.springframework.stereotype.Component;

@Component
public class HelloController {
    private int id;
    @MiniDubboReference
    HelloService helloService;
}
