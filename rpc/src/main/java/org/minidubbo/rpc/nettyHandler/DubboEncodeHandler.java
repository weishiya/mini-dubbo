package org.minidubbo.rpc.nettyHandler;

import com.alibaba.fastjson.JSON;
import com.sun.org.apache.xpath.internal.operations.String;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.extern.slf4j.Slf4j;
import org.minidubbo.common.Bytes;
import org.minidubbo.rpc.Request;
import org.minidubbo.rpc.Response;
import org.minidubbo.rpc.codec.ProtocolHeader;
import org.minidubbo.rpc.codec.Serialization;

import java.nio.charset.Charset;

@Slf4j
public class DubboEncodeHandler extends MessageToByteEncoder {

    private Serialization serialization;

    public DubboEncodeHandler(Serialization serialization){
        this.serialization = serialization;
    }


    @Override
    protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out) throws Exception {
        if(msg instanceof Request){
            Request req = (Request) msg;
            byte[] header = new byte[ProtocolHeader.HEADER_LENGTH];
            //魔法数
            Bytes.short2bytes(ProtocolHeader.MAGIC,header);
            header[2] = ProtocolHeader.REQUEST_FLAG;
            header[3] = ProtocolHeader.TWO_WAY_FLAG;
            header[4] = ProtocolHeader.REQUEST_OR_RESPONSE;
            header[5] = serialization.getSerializationType();
            //request无需设置状态码
            //Bytes.int2bytes(0,header,6);
            Bytes.long2bytes(req.getId().longValue(),header,10);

            byte[] body = serialization.serialize(req.getData());
            //写入body的长度
            Bytes.int2bytes(body.length,header,18);
            out.writeBytes(header);
            out.writeBytes(body);
        }
        else if(msg instanceof Response){
            Response response = (Response) msg;
            byte[] header = new byte[ProtocolHeader.HEADER_LENGTH];
            //魔法数
            Bytes.short2bytes(ProtocolHeader.MAGIC,header);
            header[2] = ProtocolHeader.RESPONSE_FLAG;
            header[3] = ProtocolHeader.TWO_WAY_FLAG;
            header[4] = ProtocolHeader.REQUEST_OR_RESPONSE;
            header[5] = serialization.getSerializationType();
            //写入状态码
            Bytes.int2bytes(response.getStatus(),header,6);
            //写入id
            Bytes.long2bytes(response.getId().longValue(),header,10);
            byte[] body = new byte[0];
            if(response.getStatus() == Response.OK){
                body = serialization.serialize(response.getData());
            }else {
                body = response.getErrorMessage().getBytes(Charset.defaultCharset());
            }
            //写入body的长度
            Bytes.int2bytes(body.length,header,18);
            out.writeBytes(header);
            out.writeBytes(body);
        }
    }
}
