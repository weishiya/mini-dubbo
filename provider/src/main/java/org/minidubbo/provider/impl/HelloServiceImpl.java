package org.minidubbo.provider.impl;

import org.minidubbo.api.HelloService;

public class HelloServiceImpl implements HelloService {
    @Override
    public String hello(String args) {
        return "hello:"+args;
    }
}
