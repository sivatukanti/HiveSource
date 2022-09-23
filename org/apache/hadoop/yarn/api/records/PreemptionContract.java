// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.api.records;

import org.apache.hadoop.yarn.util.Records;
import java.util.Set;
import java.util.List;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Public
@InterfaceStability.Evolving
public abstract class PreemptionContract
{
    @InterfaceAudience.Private
    @InterfaceStability.Unstable
    public static PreemptionContract newInstance(final List<PreemptionResourceRequest> req, final Set<PreemptionContainer> containers) {
        final PreemptionContract contract = Records.newRecord(PreemptionContract.class);
        contract.setResourceRequest(req);
        contract.setContainers(containers);
        return contract;
    }
    
    @InterfaceAudience.Public
    @InterfaceStability.Evolving
    public abstract List<PreemptionResourceRequest> getResourceRequest();
    
    @InterfaceAudience.Private
    @InterfaceStability.Unstable
    public abstract void setResourceRequest(final List<PreemptionResourceRequest> p0);
    
    @InterfaceAudience.Public
    @InterfaceStability.Evolving
    public abstract Set<PreemptionContainer> getContainers();
    
    @InterfaceAudience.Private
    @InterfaceStability.Unstable
    public abstract void setContainers(final Set<PreemptionContainer> p0);
}
