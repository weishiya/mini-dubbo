package org.minidubbo.rpc.codec;

public interface Serialization {
    int FASTJSON = 0;

    byte getSerializationType();

    byte[] serialize(Object obj);
}
