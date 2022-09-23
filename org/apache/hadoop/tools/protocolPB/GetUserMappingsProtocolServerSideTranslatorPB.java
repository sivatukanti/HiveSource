// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.tools.protocolPB;

import java.io.IOException;
import com.google.protobuf.ServiceException;
import org.apache.hadoop.tools.proto.GetUserMappingsProtocolProtos;
import com.google.protobuf.RpcController;
import org.apache.hadoop.tools.GetUserMappingsProtocol;

public class GetUserMappingsProtocolServerSideTranslatorPB implements GetUserMappingsProtocolPB
{
    private final GetUserMappingsProtocol impl;
    
    public GetUserMappingsProtocolServerSideTranslatorPB(final GetUserMappingsProtocol impl) {
        this.impl = impl;
    }
    
    @Override
    public GetUserMappingsProtocolProtos.GetGroupsForUserResponseProto getGroupsForUser(final RpcController controller, final GetUserMappingsProtocolProtos.GetGroupsForUserRequestProto request) throws ServiceException {
        String[] groups;
        try {
            groups = this.impl.getGroupsForUser(request.getUser());
        }
        catch (IOException e) {
            throw new ServiceException(e);
        }
        final GetUserMappingsProtocolProtos.GetGroupsForUserResponseProto.Builder builder = GetUserMappingsProtocolProtos.GetGroupsForUserResponseProto.newBuilder();
        for (final String g : groups) {
            builder.addGroups(g);
        }
        return builder.build();
    }
}
