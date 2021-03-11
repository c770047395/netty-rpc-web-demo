package com.netty.rpc.serializer;

import com.alibaba.fastjson.JSON;

import java.io.IOException;

public class JSONSerializer implements Serializer {
    @Override
    public byte[] serializer(Object object) throws IOException {
        return JSON.toJSONBytes(object);
    }

    @Override
    public <T> T deserializer(Class<T> clazz, byte[] bytes) throws IOException {
        return JSON.parseObject(bytes,clazz);
    }
}
