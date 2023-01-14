package org.minidubbo.rpc.codec;

import com.alibaba.fastjson.JSON;

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
}
