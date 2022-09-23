// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.api.records;

import org.apache.hadoop.yarn.util.Records;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Public
@InterfaceStability.Stable
public abstract class Priority implements Comparable<Priority>
{
    public static final Priority UNDEFINED;
    
    @InterfaceAudience.Public
    @InterfaceStability.Stable
    public static Priority newInstance(final int p) {
        final Priority priority = Records.newRecord(Priority.class);
        priority.setPriority(p);
        return priority;
    }
    
    @InterfaceAudience.Public
    @InterfaceStability.Stable
    public abstract int getPriority();
    
    @InterfaceAudience.Public
    @InterfaceStability.Stable
    public abstract void setPriority(final int p0);
    
    @Override
    public int hashCode() {
        final int prime = 517861;
        int result = 9511;
        result = 517861 * result + this.getPriority();
        return result;
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        final Priority other = (Priority)obj;
        return this.getPriority() == other.getPriority();
    }
    
    @Override
    public int compareTo(final Priority other) {
        return other.getPriority() - this.getPriority();
    }
    
    @Override
    public String toString() {
        return "{Priority: " + this.getPriority() + "}";
    }
    
    static {
        UNDEFINED = newInstance(-1);
    }
}
