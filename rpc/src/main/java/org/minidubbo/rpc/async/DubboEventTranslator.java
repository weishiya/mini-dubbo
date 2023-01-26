package org.minidubbo.rpc.async;

import com.lmax.disruptor.EventTranslatorOneArg;
import com.lmax.disruptor.EventTranslatorTwoArg;
import io.netty.channel.ChannelHandlerContext;

public class DubboEventTranslator implements EventTranslatorTwoArg<org.minidubbo.rpc.async.DubboEvent,Object, ChannelHandlerContext> {


    @Override
    public void translateTo(DubboEvent dubboEvent, long l, Object msg, ChannelHandlerContext channelHandlerContext) {
        dubboEvent.setMsg(msg);
        dubboEvent.setCtx(channelHandlerContext);
    }
}
