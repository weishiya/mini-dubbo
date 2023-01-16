package org.minidubbo.rpc;

public abstract class AbstractServiceConfig {

    private Boolean shareconnections = true;

    private int connections = 1;

    public int getConnections() {
        return connections;
    }

    public void setConnections(int connections) {
        this.connections = connections;
    }

    public Boolean isShareconnections() {
        return shareconnections;
    }

    public void setShareconnections(boolean shareconnections) {
        this.shareconnections = shareconnections;
    }
}
