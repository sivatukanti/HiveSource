// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.api.protocolrecords;

import org.apache.hadoop.yarn.proto.YarnServiceProtos;
import java.util.EnumSet;
import org.apache.hadoop.yarn.util.Records;
import org.apache.hadoop.yarn.api.records.NMToken;
import org.apache.hadoop.yarn.api.records.Container;
import java.util.List;
import java.nio.ByteBuffer;
import org.apache.hadoop.yarn.api.records.ApplicationAccessType;
import java.util.Map;
import org.apache.hadoop.yarn.api.records.Resource;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Public
@InterfaceStability.Stable
public abstract class RegisterApplicationMasterResponse
{
    @InterfaceAudience.Private
    @InterfaceStability.Unstable
    public static RegisterApplicationMasterResponse newInstance(final Resource minCapability, final Resource maxCapability, final Map<ApplicationAccessType, String> acls, final ByteBuffer key, final List<Container> containersFromPreviousAttempt, final String queue, final List<NMToken> nmTokensFromPreviousAttempts) {
        final RegisterApplicationMasterResponse response = Records.newRecord(RegisterApplicationMasterResponse.class);
        response.setMaximumResourceCapability(maxCapability);
        response.setApplicationACLs(acls);
        response.setClientToAMTokenMasterKey(key);
        response.setContainersFromPreviousAttempts(containersFromPreviousAttempt);
        response.setNMTokensFromPreviousAttempts(nmTokensFromPreviousAttempts);
        response.setQueue(queue);
        return response;
    }
    
    @InterfaceAudience.Public
    @InterfaceStability.Stable
    public abstract Resource getMaximumResourceCapability();
    
    @InterfaceAudience.Private
    @InterfaceStability.Unstable
    public abstract void setMaximumResourceCapability(final Resource p0);
    
    @InterfaceAudience.Public
    @InterfaceStability.Stable
    public abstract Map<ApplicationAccessType, String> getApplicationACLs();
    
    @InterfaceAudience.Private
    @InterfaceStability.Unstable
    public abstract void setApplicationACLs(final Map<ApplicationAccessType, String> p0);
    
    @InterfaceAudience.Public
    @InterfaceStability.Stable
    public abstract ByteBuffer getClientToAMTokenMasterKey();
    
    @InterfaceAudience.Public
    @InterfaceStability.Stable
    public abstract void setClientToAMTokenMasterKey(final ByteBuffer p0);
    
    @InterfaceAudience.Public
    @InterfaceStability.Stable
    public abstract String getQueue();
    
    @InterfaceAudience.Public
    @InterfaceStability.Stable
    public abstract void setQueue(final String p0);
    
    @InterfaceAudience.Public
    @InterfaceStability.Unstable
    public abstract List<Container> getContainersFromPreviousAttempts();
    
    @InterfaceAudience.Private
    @InterfaceStability.Unstable
    public abstract void setContainersFromPreviousAttempts(final List<Container> p0);
    
    @InterfaceAudience.Public
    @InterfaceStability.Stable
    public abstract List<NMToken> getNMTokensFromPreviousAttempts();
    
    @InterfaceAudience.Private
    @InterfaceStability.Unstable
    public abstract void setNMTokensFromPreviousAttempts(final List<NMToken> p0);
    
    @InterfaceAudience.Public
    @InterfaceStability.Unstable
    public abstract EnumSet<YarnServiceProtos.SchedulerResourceTypes> getSchedulerResourceTypes();
    
    @InterfaceAudience.Private
    @InterfaceStability.Unstable
    public abstract void setSchedulerResourceTypes(final EnumSet<YarnServiceProtos.SchedulerResourceTypes> p0);
}
