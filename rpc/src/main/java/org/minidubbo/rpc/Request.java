package org.minidubbo.rpc;

import java.io.Serializable;
import java.util.concurrent.atomic.AtomicLong;

public class Request implements Serializable {

    private final static AtomicLong ID_GENERATOR = new AtomicLong(0);
    private final long id;
    private Object data;
    public Request(long id,Object data){
        this.id = id;
        this.data = data;
    }
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

    public void setData(Object data){
        this.data = data;
    }
}
