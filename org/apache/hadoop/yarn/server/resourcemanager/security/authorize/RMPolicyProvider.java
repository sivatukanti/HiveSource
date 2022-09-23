// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.resourcemanager.security.authorize;

import org.apache.hadoop.ha.HAServiceProtocol;
import org.apache.hadoop.yarn.api.ContainerManagementProtocolPB;
import org.apache.hadoop.yarn.server.api.ResourceManagerAdministrationProtocolPB;
import org.apache.hadoop.yarn.api.ApplicationMasterProtocolPB;
import org.apache.hadoop.yarn.api.ApplicationClientProtocolPB;
import org.apache.hadoop.yarn.server.api.ResourceTrackerPB;
import org.apache.hadoop.security.authorize.Service;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;
import org.apache.hadoop.security.authorize.PolicyProvider;

@InterfaceAudience.Private
@InterfaceStability.Unstable
public class RMPolicyProvider extends PolicyProvider
{
    private static RMPolicyProvider rmPolicyProvider;
    private static final Service[] resourceManagerServices;
    
    private RMPolicyProvider() {
    }
    
    @InterfaceAudience.Private
    @InterfaceStability.Unstable
    public static RMPolicyProvider getInstance() {
        if (RMPolicyProvider.rmPolicyProvider == null) {
            synchronized (RMPolicyProvider.class) {
                if (RMPolicyProvider.rmPolicyProvider == null) {
                    RMPolicyProvider.rmPolicyProvider = new RMPolicyProvider();
                }
            }
        }
        return RMPolicyProvider.rmPolicyProvider;
    }
    
    @Override
    public Service[] getServices() {
        return RMPolicyProvider.resourceManagerServices;
    }
    
    static {
        RMPolicyProvider.rmPolicyProvider = null;
        resourceManagerServices = new Service[] { new Service("security.resourcetracker.protocol.acl", ResourceTrackerPB.class), new Service("security.applicationclient.protocol.acl", ApplicationClientProtocolPB.class), new Service("security.applicationmaster.protocol.acl", ApplicationMasterProtocolPB.class), new Service("security.resourcemanager-administration.protocol.acl", ResourceManagerAdministrationProtocolPB.class), new Service("security.containermanagement.protocol.acl", ContainerManagementProtocolPB.class), new Service("security.ha.service.protocol.acl", HAServiceProtocol.class) };
    }
}
