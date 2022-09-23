// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.ha.protocolPB;

import org.apache.hadoop.ipc.VersionedProtocol;
import org.apache.hadoop.ipc.ProtocolSignature;
import org.apache.hadoop.ipc.RPC;
import java.io.IOException;
import com.google.protobuf.ServiceException;
import org.apache.hadoop.ha.proto.ZKFCProtocolProtos;
import com.google.protobuf.RpcController;
import org.apache.hadoop.ha.ZKFCProtocol;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Private
@InterfaceStability.Stable
public class ZKFCProtocolServerSideTranslatorPB implements ZKFCProtocolPB
{
    private final ZKFCProtocol server;
    
    public ZKFCProtocolServerSideTranslatorPB(final ZKFCProtocol server) {
        this.server = server;
    }
    
    @Override
    public ZKFCProtocolProtos.CedeActiveResponseProto cedeActive(final RpcController controller, final ZKFCProtocolProtos.CedeActiveRequestProto request) throws ServiceException {
        try {
            this.server.cedeActive(request.getMillisToCede());
            return ZKFCProtocolProtos.CedeActiveResponseProto.getDefaultInstance();
        }
        catch (IOException e) {
            throw new ServiceException(e);
        }
    }
    
    @Override
    public ZKFCProtocolProtos.GracefulFailoverResponseProto gracefulFailover(final RpcController controller, final ZKFCProtocolProtos.GracefulFailoverRequestProto request) throws ServiceException {
        try {
            this.server.gracefulFailover();
            return ZKFCProtocolProtos.GracefulFailoverResponseProto.getDefaultInstance();
        }
        catch (IOException e) {
            throw new ServiceException(e);
        }
    }
    
    @Override
    public long getProtocolVersion(final String protocol, final long clientVersion) throws IOException {
        return RPC.getProtocolVersion(ZKFCProtocolPB.class);
    }
    
    @Override
    public ProtocolSignature getProtocolSignature(final String protocol, final long clientVersion, final int clientMethodsHash) throws IOException {
        if (!protocol.equals(RPC.getProtocolName(ZKFCProtocolPB.class))) {
            throw new IOException("Serverside implements " + RPC.getProtocolName(ZKFCProtocolPB.class) + ". The following requested protocol is unknown: " + protocol);
        }
        return ProtocolSignature.getProtocolSignature(clientMethodsHash, RPC.getProtocolVersion(ZKFCProtocolPB.class), HAServiceProtocolPB.class);
    }
}
