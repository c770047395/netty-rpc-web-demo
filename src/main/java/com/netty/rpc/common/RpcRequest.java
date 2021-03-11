package com.netty.rpc.common;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class RpcRequest {
    private String requestId;
    private String className;
    private String methodName;
    private Class<?>[] paramTypes;
    private Object[] params;
}
