// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.ipc;

import org.apache.hadoop.ipc.protobuf.RpcHeaderProtos;

public class RpcServerException extends RpcException
{
    private static final long serialVersionUID = 1L;
    
    public RpcServerException(final String message) {
        super(message);
    }
    
    public RpcServerException(final String message, final Throwable cause) {
        super(message, cause);
    }
    
    public RpcHeaderProtos.RpcResponseHeaderProto.RpcStatusProto getRpcStatusProto() {
        return RpcHeaderProtos.RpcResponseHeaderProto.RpcStatusProto.ERROR;
    }
    
    public RpcHeaderProtos.RpcResponseHeaderProto.RpcErrorCodeProto getRpcErrorCodeProto() {
        return RpcHeaderProtos.RpcResponseHeaderProto.RpcErrorCodeProto.ERROR_RPC_SERVER;
    }
}
