// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.ipc.protocolPB;

import org.apache.hadoop.ipc.RpcClientUtil;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.List;
import com.google.protobuf.ServiceException;
import org.apache.hadoop.ipc.ProtobufHelper;
import org.apache.hadoop.ipc.proto.GenericRefreshProtocolProtos;
import java.util.Arrays;
import org.apache.hadoop.ipc.RefreshResponse;
import java.util.Collection;
import java.io.IOException;
import org.apache.hadoop.ipc.RPC;
import com.google.protobuf.RpcController;
import java.io.Closeable;
import org.apache.hadoop.ipc.GenericRefreshProtocol;
import org.apache.hadoop.ipc.ProtocolMetaInterface;

public class GenericRefreshProtocolClientSideTranslatorPB implements ProtocolMetaInterface, GenericRefreshProtocol, Closeable
{
    private static final RpcController NULL_CONTROLLER;
    private final GenericRefreshProtocolPB rpcProxy;
    
    public GenericRefreshProtocolClientSideTranslatorPB(final GenericRefreshProtocolPB rpcProxy) {
        this.rpcProxy = rpcProxy;
    }
    
    @Override
    public void close() throws IOException {
        RPC.stopProxy(this.rpcProxy);
    }
    
    @Override
    public Collection<RefreshResponse> refresh(final String identifier, final String[] args) throws IOException {
        final List<String> argList = Arrays.asList(args);
        try {
            final GenericRefreshProtocolProtos.GenericRefreshRequestProto request = GenericRefreshProtocolProtos.GenericRefreshRequestProto.newBuilder().setIdentifier(identifier).addAllArgs(argList).build();
            final GenericRefreshProtocolProtos.GenericRefreshResponseCollectionProto resp = this.rpcProxy.refresh(GenericRefreshProtocolClientSideTranslatorPB.NULL_CONTROLLER, request);
            return this.unpack(resp);
        }
        catch (ServiceException se) {
            throw ProtobufHelper.getRemoteException(se);
        }
    }
    
    private Collection<RefreshResponse> unpack(final GenericRefreshProtocolProtos.GenericRefreshResponseCollectionProto collection) {
        final List<GenericRefreshProtocolProtos.GenericRefreshResponseProto> responseProtos = collection.getResponsesList();
        final List<RefreshResponse> responses = new ArrayList<RefreshResponse>();
        for (final GenericRefreshProtocolProtos.GenericRefreshResponseProto rp : responseProtos) {
            final RefreshResponse response = this.unpack(rp);
            responses.add(response);
        }
        return responses;
    }
    
    private RefreshResponse unpack(final GenericRefreshProtocolProtos.GenericRefreshResponseProto proto) {
        String message = null;
        String sender = null;
        int returnCode = -1;
        if (proto.hasUserMessage()) {
            message = proto.getUserMessage();
        }
        if (proto.hasExitStatus()) {
            returnCode = proto.getExitStatus();
        }
        if (proto.hasSenderName()) {
            sender = proto.getSenderName();
        }
        final RefreshResponse response = new RefreshResponse(returnCode, message);
        response.setSenderName(sender);
        return response;
    }
    
    @Override
    public boolean isMethodSupported(final String methodName) throws IOException {
        return RpcClientUtil.isMethodSupported(this.rpcProxy, GenericRefreshProtocolPB.class, RPC.RpcKind.RPC_PROTOCOL_BUFFER, RPC.getProtocolVersion(GenericRefreshProtocolPB.class), methodName);
    }
    
    static {
        NULL_CONTROLLER = null;
    }
}
