// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.util;

import org.apache.htrace.core.Span;
import org.apache.hadoop.ipc.CallerContext;
import org.apache.htrace.core.Tracer;
import com.google.protobuf.ByteString;
import org.apache.hadoop.ipc.protobuf.RpcHeaderProtos;
import org.apache.hadoop.ipc.RPC;
import org.apache.hadoop.ipc.protobuf.IpcConnectionContextProtos;
import org.apache.hadoop.security.SaslRpcServer;
import org.apache.hadoop.security.UserGroupInformation;
import java.io.IOException;
import java.io.DataInput;

public abstract class ProtoUtil
{
    public static int readRawVarint32(final DataInput in) throws IOException {
        byte tmp = in.readByte();
        if (tmp >= 0) {
            return tmp;
        }
        int result = tmp & 0x7F;
        if ((tmp = in.readByte()) >= 0) {
            result |= tmp << 7;
        }
        else {
            result |= (tmp & 0x7F) << 7;
            if ((tmp = in.readByte()) >= 0) {
                result |= tmp << 14;
            }
            else {
                result |= (tmp & 0x7F) << 14;
                if ((tmp = in.readByte()) >= 0) {
                    result |= tmp << 21;
                }
                else {
                    result |= (tmp & 0x7F) << 21;
                    result |= (tmp = in.readByte()) << 28;
                    if (tmp < 0) {
                        for (int i = 0; i < 5; ++i) {
                            if (in.readByte() >= 0) {
                                return result;
                            }
                        }
                        throw new IOException("Malformed varint");
                    }
                }
            }
        }
        return result;
    }
    
    public static IpcConnectionContextProtos.IpcConnectionContextProto makeIpcConnectionContext(final String protocol, final UserGroupInformation ugi, final SaslRpcServer.AuthMethod authMethod) {
        final IpcConnectionContextProtos.IpcConnectionContextProto.Builder result = IpcConnectionContextProtos.IpcConnectionContextProto.newBuilder();
        if (protocol != null) {
            result.setProtocol(protocol);
        }
        final IpcConnectionContextProtos.UserInformationProto.Builder ugiProto = IpcConnectionContextProtos.UserInformationProto.newBuilder();
        if (ugi != null) {
            if (authMethod == SaslRpcServer.AuthMethod.KERBEROS) {
                ugiProto.setEffectiveUser(ugi.getUserName());
            }
            else if (authMethod != SaslRpcServer.AuthMethod.TOKEN) {
                ugiProto.setEffectiveUser(ugi.getUserName());
                if (ugi.getRealUser() != null) {
                    ugiProto.setRealUser(ugi.getRealUser().getUserName());
                }
            }
        }
        result.setUserInfo(ugiProto);
        return result.build();
    }
    
    public static UserGroupInformation getUgi(final IpcConnectionContextProtos.IpcConnectionContextProto context) {
        if (context.hasUserInfo()) {
            final IpcConnectionContextProtos.UserInformationProto userInfo = context.getUserInfo();
            return getUgi(userInfo);
        }
        return null;
    }
    
    public static UserGroupInformation getUgi(final IpcConnectionContextProtos.UserInformationProto userInfo) {
        UserGroupInformation ugi = null;
        final String effectiveUser = userInfo.hasEffectiveUser() ? userInfo.getEffectiveUser() : null;
        final String realUser = userInfo.hasRealUser() ? userInfo.getRealUser() : null;
        if (effectiveUser != null) {
            if (realUser != null) {
                final UserGroupInformation realUserUgi = UserGroupInformation.createRemoteUser(realUser);
                ugi = UserGroupInformation.createProxyUser(effectiveUser, realUserUgi);
            }
            else {
                ugi = UserGroupInformation.createRemoteUser(effectiveUser);
            }
        }
        return ugi;
    }
    
    static RpcHeaderProtos.RpcKindProto convert(final RPC.RpcKind kind) {
        switch (kind) {
            case RPC_BUILTIN: {
                return RpcHeaderProtos.RpcKindProto.RPC_BUILTIN;
            }
            case RPC_WRITABLE: {
                return RpcHeaderProtos.RpcKindProto.RPC_WRITABLE;
            }
            case RPC_PROTOCOL_BUFFER: {
                return RpcHeaderProtos.RpcKindProto.RPC_PROTOCOL_BUFFER;
            }
            default: {
                return null;
            }
        }
    }
    
    public static RPC.RpcKind convert(final RpcHeaderProtos.RpcKindProto kind) {
        switch (kind) {
            case RPC_BUILTIN: {
                return RPC.RpcKind.RPC_BUILTIN;
            }
            case RPC_WRITABLE: {
                return RPC.RpcKind.RPC_WRITABLE;
            }
            case RPC_PROTOCOL_BUFFER: {
                return RPC.RpcKind.RPC_PROTOCOL_BUFFER;
            }
            default: {
                return null;
            }
        }
    }
    
    public static RpcHeaderProtos.RpcRequestHeaderProto makeRpcRequestHeader(final RPC.RpcKind rpcKind, final RpcHeaderProtos.RpcRequestHeaderProto.OperationProto operation, final int callId, final int retryCount, final byte[] uuid) {
        final RpcHeaderProtos.RpcRequestHeaderProto.Builder result = RpcHeaderProtos.RpcRequestHeaderProto.newBuilder();
        result.setRpcKind(convert(rpcKind)).setRpcOp(operation).setCallId(callId).setRetryCount(retryCount).setClientId(ByteString.copyFrom(uuid));
        final Span span = Tracer.getCurrentSpan();
        if (span != null) {
            result.setTraceInfo(RpcHeaderProtos.RPCTraceInfoProto.newBuilder().setTraceId(span.getSpanId().getHigh()).setParentId(span.getSpanId().getLow()).build());
        }
        final CallerContext callerContext = CallerContext.getCurrent();
        if (callerContext != null && callerContext.isContextValid()) {
            final RpcHeaderProtos.RPCCallerContextProto.Builder contextBuilder = RpcHeaderProtos.RPCCallerContextProto.newBuilder().setContext(callerContext.getContext());
            if (callerContext.getSignature() != null) {
                contextBuilder.setSignature(ByteString.copyFrom(callerContext.getSignature()));
            }
            result.setCallerContext(contextBuilder);
        }
        return result.build();
    }
}
