package org.minidubbo.rpc.nettyHandler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import org.minidubbo.rpc.codec.ProtocolHeader;

import java.util.List;


public class DubboEncodeHandler extends MessageToMessageEncoder {

    @Override
    protected void encode(ChannelHandlerContext ctx, Object msg, List out) {
        byte[] header = new byte[ProtocolHeader.HEADER_LENGTH];


    }
}
