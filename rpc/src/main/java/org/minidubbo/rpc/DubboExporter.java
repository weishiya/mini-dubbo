package org.minidubbo.rpc;

public class DubboExporter<T> implements Exporter{

    private final String serviceKey;

    private Invoker<T> invoker;

    private boolean status = false;

    public DubboExporter(String serviceKey,Invoker<T> invoker){
        this.serviceKey = serviceKey;
        this.invoker = invoker;
    }

    @Override
    public Invoker getInvoker() {
        return invoker;
    }

    @Override
    public String getServiceKey() {
        return serviceKey;
    }

    @Override
    public void exported() {
        this.status = true;
    }

    @Override
    public boolean isExported() {
        return status;
    }
}
