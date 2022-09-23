// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.api.records;

import org.apache.hadoop.yarn.util.Records;
import java.util.Set;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Public
@InterfaceStability.Evolving
public abstract class StrictPreemptionContract
{
    @InterfaceAudience.Private
    @InterfaceStability.Unstable
    public static StrictPreemptionContract newInstance(final Set<PreemptionContainer> containers) {
        final StrictPreemptionContract contract = Records.newRecord(StrictPreemptionContract.class);
        contract.setContainers(containers);
        return contract;
    }
    
    @InterfaceAudience.Public
    @InterfaceStability.Evolving
    public abstract Set<PreemptionContainer> getContainers();
    
    @InterfaceAudience.Private
    @InterfaceStability.Unstable
    public abstract void setContainers(final Set<PreemptionContainer> p0);
}
