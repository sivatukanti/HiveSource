// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.ipc.protocolPB;

import java.io.IOException;
import com.google.protobuf.ServiceException;
import com.google.protobuf.RpcController;
import org.apache.hadoop.ipc.proto.RefreshCallQueueProtocolProtos;
import org.apache.hadoop.ipc.RefreshCallQueueProtocol;

public class RefreshCallQueueProtocolServerSideTranslatorPB implements RefreshCallQueueProtocolPB
{
    private final RefreshCallQueueProtocol impl;
    private static final RefreshCallQueueProtocolProtos.RefreshCallQueueResponseProto VOID_REFRESH_CALL_QUEUE_RESPONSE;
    
    public RefreshCallQueueProtocolServerSideTranslatorPB(final RefreshCallQueueProtocol impl) {
        this.impl = impl;
    }
    
    @Override
    public RefreshCallQueueProtocolProtos.RefreshCallQueueResponseProto refreshCallQueue(final RpcController controller, final RefreshCallQueueProtocolProtos.RefreshCallQueueRequestProto request) throws ServiceException {
        try {
            this.impl.refreshCallQueue();
        }
        catch (IOException e) {
            throw new ServiceException(e);
        }
        return RefreshCallQueueProtocolServerSideTranslatorPB.VOID_REFRESH_CALL_QUEUE_RESPONSE;
    }
    
    static {
        VOID_REFRESH_CALL_QUEUE_RESPONSE = RefreshCallQueueProtocolProtos.RefreshCallQueueResponseProto.newBuilder().build();
    }
}
