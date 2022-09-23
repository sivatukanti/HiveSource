// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.api.records;

import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Public
@InterfaceStability.Unstable
public enum NodeState
{
    NEW, 
    RUNNING, 
    UNHEALTHY, 
    DECOMMISSIONED, 
    LOST, 
    REBOOTED;
    
    public boolean isUnusable() {
        return this == NodeState.UNHEALTHY || this == NodeState.DECOMMISSIONED || this == NodeState.LOST;
    }
}
