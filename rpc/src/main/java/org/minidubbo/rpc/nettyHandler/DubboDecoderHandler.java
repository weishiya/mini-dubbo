package org.minidubbo.rpc.nettyHandler;

import com.alibaba.fastjson.JSON;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import lombok.extern.slf4j.Slf4j;
import org.minidubbo.common.Bytes;
import org.minidubbo.rpc.Invocation;
import org.minidubbo.rpc.Request;
import org.minidubbo.rpc.Response;
import org.minidubbo.rpc.Result;
import org.minidubbo.rpc.codec.FastjsonSerialization;
import org.minidubbo.rpc.codec.ProtocolHeader;
import org.minidubbo.rpc.exception.RpcException;
import org.minidubbo.rpc.exception.SerialException;
import org.minidubbo.rpc.result.RpcResult;

import java.nio.charset.Charset;
import java.util.List;
@Slf4j
public class DubboDecoderHandler extends ByteToMessageDecoder {
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        int capacity = in.readableBytes();
        if(capacity < ProtocolHeader.HEADER_LENGTH){
            return;
        }
        //标记开始读的位置
        int markReaderIndex = in.readerIndex();
        byte[] header = new byte[ProtocolHeader.HEADER_LENGTH];
        //in.readBytes(header);
        in.readBytes(header,0,ProtocolHeader.HEADER_LENGTH);
        //解析消息头
        Object obj = decodeHeader(header);

        int bodyLength = Bytes.bytes2int(header, 18);
        byte[] body = new byte[bodyLength];
        //如果不够，那么说明有拆包
        if(capacity - in.readerIndex() < bodyLength){
            in.readerIndex(markReaderIndex);
            return;
        }
        in.readBytes(body);
        //解析消息体
        decodeBody(obj,header,body);
        out.add(obj);
        //log.info("receive message {}",JSON.toJSONString(obj));
    }

    /**
     *  解析消息头
     * @param header
     * @return Request or Response
     */
    Object decodeHeader(byte[] header){
        Object obj = null;
        long requestId = Bytes.bytes2long(header, 10);
        //如果是正常请求或者响应
        if(header[4] == ProtocolHeader.REQUEST_OR_RESPONSE){
            //如果是请求
            if(header[2]==ProtocolHeader.REQUEST_FLAG){
                Request request = new Request(requestId,null);
                obj =  request;
            }
            //否则是response
            else {
                Response response = new Response(requestId);
                obj = response;
            }
        }
//        else if(header[4] == ProtocolHeader.HEARTBEAT_FLAG){
//
//        }

        return obj;
    }

    private void decodeBody(Object obj,byte[] header, byte[] body) throws SerialException {
        byte serialTyoe = header[5];
        if(obj instanceof Request){
            Invocation data = deSerializeRequestBody(serialTyoe, body);
            ((Request) obj).setData(data);
        }
        //如果是返回对象
        else {
            int code = Bytes.bytes2int(header, 6);
            ((Response)obj).setStatus(code);
            //如果是OK,说明没有报错
            if(code == Response.OK){
                Result result = deSerializeResponseBody(serialTyoe, body);
                ((Response) obj).setData(result);
            }else {
                String errorMessage = new String(body, Charset.defaultCharset());
                ((Response) obj).setErrorMessage(errorMessage);
            }
        }

    }

    Invocation deSerializeRequestBody(byte serialType, byte[] body) throws SerialException {
        if(serialType == FastjsonSerialization.FASTJSON){
            return FastjsonSerialization.deseriablizeRequestBody(body);
        }
        throw new SerialException(Response.BAD_REQUEST,"can not serialize");
    }

    Result deSerializeResponseBody(byte serialType, byte[] body) throws SerialException {
        if(serialType == FastjsonSerialization.FASTJSON){
            return FastjsonSerialization.deseriablizeResponseBody(body);
        }
        throw new SerialException(Response.BAD_REQUEST,"can not serialize");
    }
}
