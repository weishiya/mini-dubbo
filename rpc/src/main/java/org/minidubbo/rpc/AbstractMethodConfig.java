package org.minidubbo.rpc;

public abstract class AbstractMethodConfig extends AbstractServiceConfig{
    //超时时间
    private Integer timeout;

    public Integer getTimeout() {
        return timeout;
    }

    public void setTimeout(Integer timeout) {
        this.timeout = timeout;
    }
}
