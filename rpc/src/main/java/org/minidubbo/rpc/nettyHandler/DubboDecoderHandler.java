package org.minidubbo.rpc.nettyHandler;

import com.alibaba.fastjson.JSON;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import lombok.extern.slf4j.Slf4j;
import org.minidubbo.common.Bytes;
import org.minidubbo.rpc.Request;
import org.minidubbo.rpc.codec.ProtocolHeader;

import java.util.List;
@Slf4j
public class DubboDecoderHandler extends ByteToMessageDecoder {
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        int capacity = in.capacity();
        if(capacity < ProtocolHeader.HEADER_LENGTH){
            return;
        }
        byte[] header = new byte[ProtocolHeader.HEADER_LENGTH];
        in.readBytes(header);
        Object o = decodeHeadr(header);
        log.info(JSON.toJSONString(o));
    }

    Object decodeHeadr(byte[] header){
        Object obj = null;
        if(header[2]==ProtocolHeader.REQUEST_FLAG){
            long requestId = Bytes.bytes2long(header, 10);
            Request request = new Request(requestId);
            int i = Bytes.bytes2int(header, 18);
            obj =  new Request(requestId,null);
        }
        return obj;
    }
}
