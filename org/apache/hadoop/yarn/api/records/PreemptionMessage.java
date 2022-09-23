// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.api.records;

import org.apache.hadoop.yarn.util.Records;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Public
@InterfaceStability.Evolving
public abstract class PreemptionMessage
{
    @InterfaceAudience.Private
    @InterfaceStability.Unstable
    public static PreemptionMessage newInstance(final StrictPreemptionContract set, final PreemptionContract contract) {
        final PreemptionMessage message = Records.newRecord(PreemptionMessage.class);
        message.setStrictContract(set);
        message.setContract(contract);
        return message;
    }
    
    @InterfaceAudience.Public
    @InterfaceStability.Evolving
    public abstract StrictPreemptionContract getStrictContract();
    
    @InterfaceAudience.Private
    @InterfaceStability.Unstable
    public abstract void setStrictContract(final StrictPreemptionContract p0);
    
    @InterfaceAudience.Public
    @InterfaceStability.Evolving
    public abstract PreemptionContract getContract();
    
    @InterfaceAudience.Private
    @InterfaceStability.Unstable
    public abstract void setContract(final PreemptionContract p0);
}
