// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.tracing;

import java.util.Iterator;
import com.google.protobuf.ServiceException;
import org.apache.hadoop.ipc.ProtobufHelper;
import com.google.protobuf.RpcController;
import java.util.ArrayList;
import java.io.IOException;
import org.apache.hadoop.ipc.RPC;
import org.apache.hadoop.classification.InterfaceAudience;
import java.io.Closeable;
import org.apache.hadoop.ipc.ProtocolTranslator;

@InterfaceAudience.Private
public class TraceAdminProtocolTranslatorPB implements TraceAdminProtocol, ProtocolTranslator, Closeable
{
    private final TraceAdminProtocolPB rpcProxy;
    
    public TraceAdminProtocolTranslatorPB(final TraceAdminProtocolPB rpcProxy) {
        this.rpcProxy = rpcProxy;
    }
    
    @Override
    public void close() throws IOException {
        RPC.stopProxy(this.rpcProxy);
    }
    
    @Override
    public SpanReceiverInfo[] listSpanReceivers() throws IOException {
        final ArrayList<SpanReceiverInfo> infos = new ArrayList<SpanReceiverInfo>(1);
        try {
            final TraceAdminPB.ListSpanReceiversRequestProto req = TraceAdminPB.ListSpanReceiversRequestProto.newBuilder().build();
            final TraceAdminPB.ListSpanReceiversResponseProto resp = this.rpcProxy.listSpanReceivers(null, req);
            for (final TraceAdminPB.SpanReceiverListInfo info : resp.getDescriptionsList()) {
                infos.add(new SpanReceiverInfo(info.getId(), info.getClassName()));
            }
        }
        catch (ServiceException e) {
            throw ProtobufHelper.getRemoteException(e);
        }
        return infos.toArray(new SpanReceiverInfo[infos.size()]);
    }
    
    @Override
    public long addSpanReceiver(final SpanReceiverInfo info) throws IOException {
        try {
            final TraceAdminPB.AddSpanReceiverRequestProto.Builder bld = TraceAdminPB.AddSpanReceiverRequestProto.newBuilder();
            bld.setClassName(info.getClassName());
            for (final SpanReceiverInfo.ConfigurationPair configPair : info.configPairs) {
                final TraceAdminPB.ConfigPair tuple = TraceAdminPB.ConfigPair.newBuilder().setKey(configPair.getKey()).setValue(configPair.getValue()).build();
                bld.addConfig(tuple);
            }
            final TraceAdminPB.AddSpanReceiverResponseProto resp = this.rpcProxy.addSpanReceiver(null, bld.build());
            return resp.getId();
        }
        catch (ServiceException e) {
            throw ProtobufHelper.getRemoteException(e);
        }
    }
    
    @Override
    public void removeSpanReceiver(final long spanReceiverId) throws IOException {
        try {
            final TraceAdminPB.RemoveSpanReceiverRequestProto req = TraceAdminPB.RemoveSpanReceiverRequestProto.newBuilder().setId(spanReceiverId).build();
            this.rpcProxy.removeSpanReceiver(null, req);
        }
        catch (ServiceException e) {
            throw ProtobufHelper.getRemoteException(e);
        }
    }
    
    @Override
    public Object getUnderlyingProxyObject() {
        return this.rpcProxy;
    }
}
