// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.security.protocolPB;

import java.io.IOException;
import com.google.protobuf.ServiceException;
import com.google.protobuf.RpcController;
import org.apache.hadoop.security.proto.RefreshAuthorizationPolicyProtocolProtos;
import org.apache.hadoop.security.authorize.RefreshAuthorizationPolicyProtocol;

public class RefreshAuthorizationPolicyProtocolServerSideTranslatorPB implements RefreshAuthorizationPolicyProtocolPB
{
    private final RefreshAuthorizationPolicyProtocol impl;
    private static final RefreshAuthorizationPolicyProtocolProtos.RefreshServiceAclResponseProto VOID_REFRESH_SERVICE_ACL_RESPONSE;
    
    public RefreshAuthorizationPolicyProtocolServerSideTranslatorPB(final RefreshAuthorizationPolicyProtocol impl) {
        this.impl = impl;
    }
    
    @Override
    public RefreshAuthorizationPolicyProtocolProtos.RefreshServiceAclResponseProto refreshServiceAcl(final RpcController controller, final RefreshAuthorizationPolicyProtocolProtos.RefreshServiceAclRequestProto request) throws ServiceException {
        try {
            this.impl.refreshServiceAcl();
        }
        catch (IOException e) {
            throw new ServiceException(e);
        }
        return RefreshAuthorizationPolicyProtocolServerSideTranslatorPB.VOID_REFRESH_SERVICE_ACL_RESPONSE;
    }
    
    static {
        VOID_REFRESH_SERVICE_ACL_RESPONSE = RefreshAuthorizationPolicyProtocolProtos.RefreshServiceAclResponseProto.newBuilder().build();
    }
}
