// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.tracing;

import org.apache.hadoop.ipc.VersionedProtocol;
import org.apache.hadoop.ipc.ProtocolSignature;
import java.util.Iterator;
import com.google.protobuf.ServiceException;
import com.google.protobuf.RpcController;
import java.io.IOException;
import org.apache.hadoop.ipc.RPC;
import org.apache.hadoop.classification.InterfaceAudience;
import java.io.Closeable;

@InterfaceAudience.Private
public class TraceAdminProtocolServerSideTranslatorPB implements TraceAdminProtocolPB, Closeable
{
    private final TraceAdminProtocol server;
    
    public TraceAdminProtocolServerSideTranslatorPB(final TraceAdminProtocol server) {
        this.server = server;
    }
    
    @Override
    public void close() throws IOException {
        RPC.stopProxy(this.server);
    }
    
    @Override
    public TraceAdminPB.ListSpanReceiversResponseProto listSpanReceivers(final RpcController controller, final TraceAdminPB.ListSpanReceiversRequestProto req) throws ServiceException {
        try {
            final SpanReceiverInfo[] descs = this.server.listSpanReceivers();
            final TraceAdminPB.ListSpanReceiversResponseProto.Builder bld = TraceAdminPB.ListSpanReceiversResponseProto.newBuilder();
            for (int i = 0; i < descs.length; ++i) {
                bld.addDescriptions(TraceAdminPB.SpanReceiverListInfo.newBuilder().setId(descs[i].getId()).setClassName(descs[i].getClassName()).build());
            }
            return bld.build();
        }
        catch (IOException e) {
            throw new ServiceException(e);
        }
    }
    
    @Override
    public TraceAdminPB.AddSpanReceiverResponseProto addSpanReceiver(final RpcController controller, final TraceAdminPB.AddSpanReceiverRequestProto req) throws ServiceException {
        try {
            final SpanReceiverInfoBuilder factory = new SpanReceiverInfoBuilder(req.getClassName());
            for (final TraceAdminPB.ConfigPair config : req.getConfigList()) {
                factory.addConfigurationPair(config.getKey(), config.getValue());
            }
            final long id = this.server.addSpanReceiver(factory.build());
            return TraceAdminPB.AddSpanReceiverResponseProto.newBuilder().setId(id).build();
        }
        catch (IOException e) {
            throw new ServiceException(e);
        }
    }
    
    @Override
    public TraceAdminPB.RemoveSpanReceiverResponseProto removeSpanReceiver(final RpcController controller, final TraceAdminPB.RemoveSpanReceiverRequestProto req) throws ServiceException {
        try {
            this.server.removeSpanReceiver(req.getId());
            return TraceAdminPB.RemoveSpanReceiverResponseProto.getDefaultInstance();
        }
        catch (IOException e) {
            throw new ServiceException(e);
        }
    }
    
    @Override
    public long getProtocolVersion(final String protocol, final long clientVersion) throws IOException {
        return 1L;
    }
    
    @Override
    public ProtocolSignature getProtocolSignature(final String protocol, final long clientVersion, final int clientMethodsHash) throws IOException {
        if (!protocol.equals(RPC.getProtocolName(TraceAdminProtocolPB.class))) {
            throw new IOException("Serverside implements " + RPC.getProtocolName(TraceAdminProtocolPB.class) + ". The following requested protocol is unknown: " + protocol);
        }
        return ProtocolSignature.getProtocolSignature(clientMethodsHash, RPC.getProtocolVersion(TraceAdminProtocolPB.class), TraceAdminProtocolPB.class);
    }
}
