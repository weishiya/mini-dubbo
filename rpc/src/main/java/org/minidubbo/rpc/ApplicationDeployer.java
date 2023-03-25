package org.minidubbo.rpc;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public enum ApplicationDeployer {

    INSTANCE;


    private static Set<Protocol> protocols = new HashSet<>();

    private volatile boolean start = false;

    public void addProtocol(Protocol protocol){
        protocols.add(protocol);
    }

    public void init(){
        if(start == false){
            synchronized (ApplicationDeployer.class){
                if(start == false){
                    Runtime.getRuntime().addShutdownHook(new Thread(new ShutdownTask()));
                }
            }
        }
    }

    private class ShutdownTask implements Runnable{

        @Override
        public void run() {
            if(protocols!=null && protocols.size()>0){
                protocols.forEach(Protocol::destory);
            }
        }
    }
}
