package org.minidubbo.rpc.codec;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.minidubbo.rpc.Invocation;
import org.minidubbo.rpc.Result;
import org.minidubbo.rpc.RpcInvocation;
import org.minidubbo.rpc.result.RpcResult;

import java.nio.charset.Charset;

public class FastjsonSerialization implements Serialization{
    @Override
    public byte getSerializationType() {
        return Serialization.FASTJSON;
    }

    @Override
    public byte[] serialize(Object obj) {
        byte[] bytes = JSON.toJSONBytes(obj);
        return bytes;
    }

    public static RpcInvocation deseriablizeRequestBody(byte[] body) {
        return JSON.parseObject(body, RpcInvocation.class);
    }

    public static Result deseriablizeResponseBody(byte[] body) {
        return JSON.parseObject(body, Result.class);
    }
}
