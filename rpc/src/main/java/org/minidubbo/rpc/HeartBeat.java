package org.minidubbo.rpc;

import java.io.Serializable;

public class HeartBeat implements Serializable {
    private boolean heartbeat = false;

    public void setHeartbeat(){
        this.heartbeat = true;
    }

    public boolean isHeartbeat(){
        return heartbeat;
    }
}
