// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.api.records;

import org.apache.hadoop.yarn.util.Records;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Public
@InterfaceStability.Evolving
public abstract class PreemptionContainer
{
    @InterfaceAudience.Private
    @InterfaceStability.Unstable
    public static PreemptionContainer newInstance(final ContainerId id) {
        final PreemptionContainer container = Records.newRecord(PreemptionContainer.class);
        container.setId(id);
        return container;
    }
    
    @InterfaceAudience.Public
    @InterfaceStability.Evolving
    public abstract ContainerId getId();
    
    @InterfaceAudience.Private
    @InterfaceStability.Unstable
    public abstract void setId(final ContainerId p0);
}
