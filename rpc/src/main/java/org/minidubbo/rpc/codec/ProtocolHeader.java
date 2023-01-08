package org.minidubbo.rpc.codec;
/**
 * dubbo协议的格式以bit为单位的如下
 * 0-15位魔法数
 * 16位 数据包类型，response 或者 request，0=response，1=request
 * 17位 调用方式，单向调用，双向调用，0=单向，1=双向
 * 18位 事件标识， 0=正常请求或者响应数据包，1=心跳包
 * 19-23位 序列化方式
 * 24-31位 状态码 20=OK
 * 31-95位 请求编号
 * 96-127位 数据体长度
 * 为了更直观，取消掉或运算，我们直接用byte，也就是：
 * 0-1字节 魔法值
 * 2字节 数据包类型
 * 3字节 调用方式
 * 4字节 事件标识
 * 5字节 序列方式
 * 6-9字节 状态码
 * 10-13字节 请求编号
 * 14-18字节 数据体长度
 * 多用 3个字节，却更直观一些
 */
public interface ProtocolHeader {
    //请求头长度
    byte HEADER_LENGTH = 19;
    //魔法值
    short MAGIC = (short) 0xdabb;

    byte RESPONSE_FLAG = 0;

    byte REQUEST_FLAG = 1;

    byte ONE_WAY_FLAG = 0;

    byte TWO_WAY_FLAG = 1;

    byte REQUEST_OR_RESPONSE = 0;

    byte HEARTBEAT_FLAG = 1;
}
