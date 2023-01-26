package org.minidubbo.rpc.async;

import com.lmax.disruptor.EventFactory;

public class DubboEventFactory implements EventFactory<DubboEvent> {

    @Override
    public DubboEvent newInstance() {
        return new DubboEvent();
    }
}
