package org.minidubbo.adapter.spring;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class Test {
    public static void main(String[] args) {
        ApplicationContext applicationContext = new AnnotationConfigApplicationContext("org.minidubbo.adapter.spring");
        HelloController helloController = (HelloController)applicationContext.getAutowireCapableBeanFactory().getBean("helloController");
        System.out.println(helloController.helloService.hello());
    }
}
