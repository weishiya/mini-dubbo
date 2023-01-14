package org.minidubbo.rpc.nettyHandler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.minidubbo.common.Bytes;
import org.minidubbo.rpc.Request;
import org.minidubbo.rpc.Response;
import org.minidubbo.rpc.codec.ProtocolHeader;
import org.minidubbo.rpc.codec.Serialization;


public class DubboEncodeHandler extends MessageToByteEncoder {

    private Serialization serialization;

    public DubboEncodeHandler(Serialization serialization){
        this.serialization = serialization;
    }


    @Override
    protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out) throws Exception {
        if(msg instanceof Request){
            Request req = (Request) msg;
            byte[] bytes = new byte[ProtocolHeader.HEADER_LENGTH];
            //魔法数
            Bytes.short2bytes(ProtocolHeader.MAGIC,bytes);
            bytes[2] = ProtocolHeader.REQUEST_FLAG;
            bytes[3] = ProtocolHeader.TWO_WAY_FLAG;
            bytes[4] = ProtocolHeader.REQUEST_OR_RESPONSE;
            bytes[5] = serialization.getSerializationType();
            //request无需设置状态码
            //Bytes.int2bytes(0,bytes,6);
            Bytes.long2bytes(req.getId().longValue(),bytes,10);

            byte[] body = serialization.serialize(req.getData());
            //写入body的长度
            Bytes.int2bytes(body.length,bytes,18);
            out.writeBytes(bytes);
            out.writeBytes(body);
        }

    }
}
