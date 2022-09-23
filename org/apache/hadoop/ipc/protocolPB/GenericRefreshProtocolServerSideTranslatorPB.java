// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.ipc.protocolPB;

import java.util.Iterator;
import org.apache.hadoop.ipc.RefreshResponse;
import java.util.Collection;
import java.util.List;
import java.io.IOException;
import com.google.protobuf.ServiceException;
import org.apache.hadoop.ipc.proto.GenericRefreshProtocolProtos;
import com.google.protobuf.RpcController;
import org.apache.hadoop.ipc.GenericRefreshProtocol;

public class GenericRefreshProtocolServerSideTranslatorPB implements GenericRefreshProtocolPB
{
    private final GenericRefreshProtocol impl;
    
    public GenericRefreshProtocolServerSideTranslatorPB(final GenericRefreshProtocol impl) {
        this.impl = impl;
    }
    
    @Override
    public GenericRefreshProtocolProtos.GenericRefreshResponseCollectionProto refresh(final RpcController controller, final GenericRefreshProtocolProtos.GenericRefreshRequestProto request) throws ServiceException {
        try {
            final List<String> argList = request.getArgsList();
            final String[] args = argList.toArray(new String[argList.size()]);
            if (!request.hasIdentifier()) {
                throw new ServiceException("Request must contain identifier");
            }
            final Collection<RefreshResponse> results = this.impl.refresh(request.getIdentifier(), args);
            return this.pack(results);
        }
        catch (IOException e) {
            throw new ServiceException(e);
        }
    }
    
    private GenericRefreshProtocolProtos.GenericRefreshResponseCollectionProto pack(final Collection<RefreshResponse> responses) {
        final GenericRefreshProtocolProtos.GenericRefreshResponseCollectionProto.Builder b = GenericRefreshProtocolProtos.GenericRefreshResponseCollectionProto.newBuilder();
        for (final RefreshResponse response : responses) {
            final GenericRefreshProtocolProtos.GenericRefreshResponseProto.Builder respBuilder = GenericRefreshProtocolProtos.GenericRefreshResponseProto.newBuilder();
            respBuilder.setExitStatus(response.getReturnCode());
            respBuilder.setUserMessage(response.getMessage());
            respBuilder.setSenderName(response.getSenderName());
            b.addResponses(respBuilder);
        }
        return b.build();
    }
}
