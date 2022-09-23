// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.security.protocolPB;

import java.io.IOException;
import com.google.protobuf.ServiceException;
import com.google.protobuf.RpcController;
import org.apache.hadoop.security.proto.RefreshUserMappingsProtocolProtos;
import org.apache.hadoop.security.RefreshUserMappingsProtocol;

public class RefreshUserMappingsProtocolServerSideTranslatorPB implements RefreshUserMappingsProtocolPB
{
    private final RefreshUserMappingsProtocol impl;
    private static final RefreshUserMappingsProtocolProtos.RefreshUserToGroupsMappingsResponseProto VOID_REFRESH_USER_GROUPS_MAPPING_RESPONSE;
    private static final RefreshUserMappingsProtocolProtos.RefreshSuperUserGroupsConfigurationResponseProto VOID_REFRESH_SUPERUSER_GROUPS_CONFIGURATION_RESPONSE;
    
    public RefreshUserMappingsProtocolServerSideTranslatorPB(final RefreshUserMappingsProtocol impl) {
        this.impl = impl;
    }
    
    @Override
    public RefreshUserMappingsProtocolProtos.RefreshUserToGroupsMappingsResponseProto refreshUserToGroupsMappings(final RpcController controller, final RefreshUserMappingsProtocolProtos.RefreshUserToGroupsMappingsRequestProto request) throws ServiceException {
        try {
            this.impl.refreshUserToGroupsMappings();
        }
        catch (IOException e) {
            throw new ServiceException(e);
        }
        return RefreshUserMappingsProtocolServerSideTranslatorPB.VOID_REFRESH_USER_GROUPS_MAPPING_RESPONSE;
    }
    
    @Override
    public RefreshUserMappingsProtocolProtos.RefreshSuperUserGroupsConfigurationResponseProto refreshSuperUserGroupsConfiguration(final RpcController controller, final RefreshUserMappingsProtocolProtos.RefreshSuperUserGroupsConfigurationRequestProto request) throws ServiceException {
        try {
            this.impl.refreshSuperUserGroupsConfiguration();
        }
        catch (IOException e) {
            throw new ServiceException(e);
        }
        return RefreshUserMappingsProtocolServerSideTranslatorPB.VOID_REFRESH_SUPERUSER_GROUPS_CONFIGURATION_RESPONSE;
    }
    
    static {
        VOID_REFRESH_USER_GROUPS_MAPPING_RESPONSE = RefreshUserMappingsProtocolProtos.RefreshUserToGroupsMappingsResponseProto.newBuilder().build();
        VOID_REFRESH_SUPERUSER_GROUPS_CONFIGURATION_RESPONSE = RefreshUserMappingsProtocolProtos.RefreshSuperUserGroupsConfigurationResponseProto.newBuilder().build();
    }
}
