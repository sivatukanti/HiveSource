// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.ipc;

import org.apache.hadoop.ipc.protobuf.RpcHeaderProtos;

public class RpcNoSuchProtocolException extends RpcServerException
{
    private static final long serialVersionUID = 1L;
    
    public RpcNoSuchProtocolException(final String message) {
        super(message);
    }
    
    @Override
    public RpcHeaderProtos.RpcResponseHeaderProto.RpcStatusProto getRpcStatusProto() {
        return RpcHeaderProtos.RpcResponseHeaderProto.RpcStatusProto.ERROR;
    }
    
    @Override
    public RpcHeaderProtos.RpcResponseHeaderProto.RpcErrorCodeProto getRpcErrorCodeProto() {
        return RpcHeaderProtos.RpcResponseHeaderProto.RpcErrorCodeProto.ERROR_NO_SUCH_PROTOCOL;
    }
}
