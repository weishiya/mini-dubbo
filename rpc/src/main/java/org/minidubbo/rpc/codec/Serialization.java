package org.minidubbo.rpc.codec;

public interface Serialization {
    byte FASTJSON = 0;

    byte getSerializationType();

    byte[] serialize(Object obj);
}
