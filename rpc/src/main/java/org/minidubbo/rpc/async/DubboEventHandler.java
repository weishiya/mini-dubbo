package org.minidubbo.rpc.async;

import com.lmax.disruptor.EventHandler;
import io.netty.channel.ChannelHandlerContext;

public abstract class DubboEventHandler  implements EventHandler<DubboEvent> {
    protected Class<?> clazz;
    public DubboEventHandler(Class<?> clazz){
        this.clazz = clazz;
    }

    protected Class type(){
        return clazz;
    }

    @Override
    public void onEvent(DubboEvent dubboEvent, long l, boolean b) throws Exception {
        Class type = this.type();
        if(dubboEvent.match(type)){
            Object msg = dubboEvent.getMsg();
            ChannelHandlerContext ctx = dubboEvent.getCtx();
            handleMsg(ctx,msg);
        }
    }

    protected abstract void handleMsg(ChannelHandlerContext ctx,Object msg);


}
