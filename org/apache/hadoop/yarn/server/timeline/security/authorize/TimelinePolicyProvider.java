// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.timeline.security.authorize;

import org.apache.hadoop.yarn.api.ApplicationHistoryProtocolPB;
import org.apache.hadoop.security.authorize.Service;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;
import org.apache.hadoop.security.authorize.PolicyProvider;

@InterfaceAudience.Private
@InterfaceStability.Unstable
public class TimelinePolicyProvider extends PolicyProvider
{
    @Override
    public Service[] getServices() {
        return new Service[] { new Service("security.applicationhistory.protocol.acl", ApplicationHistoryProtocolPB.class) };
    }
}
