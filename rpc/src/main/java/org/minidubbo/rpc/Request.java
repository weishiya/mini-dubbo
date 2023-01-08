package org.minidubbo.rpc;

import java.io.Serializable;
import java.util.concurrent.atomic.AtomicLong;

public class Request implements Serializable {

    private final static AtomicLong ID_GENERATOR = new AtomicLong(0);
    private final long id;
    private final Object data;
    public Request(Object data){
        this.data = data;
        this.id = ID_GENERATOR.getAndIncrement();
    }

    public Long getId(){
        return id;
    }

    public Object getData(){
        return data;
    }
}
