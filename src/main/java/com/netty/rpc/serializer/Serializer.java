package com.netty.rpc.serializer;

import java.io.IOException;

public interface Serializer {
    byte[] serializer(Object object) throws IOException;
    <T> T deserializer(Class<T> clazz, byte[] bytes) throws IOException;
}
