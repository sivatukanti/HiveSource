// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.fs.permission;

import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Public
@InterfaceStability.Stable
public enum AclEntryType
{
    USER, 
    GROUP, 
    MASK, 
    OTHER;
    
    @InterfaceStability.Unstable
    @Override
    public String toString() {
        return this.toStringStable();
    }
    
    public String toStringStable() {
        return super.toString();
    }
}
