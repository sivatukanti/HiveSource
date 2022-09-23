// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.api.records;

import org.apache.hadoop.yarn.util.Records;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Public
@InterfaceStability.Stable
public abstract class Resource implements Comparable<Resource>
{
    @InterfaceAudience.Public
    @InterfaceStability.Stable
    public static Resource newInstance(final int memory, final int vCores) {
        final Resource resource = Records.newRecord(Resource.class);
        resource.setMemory(memory);
        resource.setVirtualCores(vCores);
        return resource;
    }
    
    @InterfaceAudience.Public
    @InterfaceStability.Stable
    public abstract int getMemory();
    
    @InterfaceAudience.Public
    @InterfaceStability.Stable
    public abstract void setMemory(final int p0);
    
    @InterfaceAudience.Public
    @InterfaceStability.Evolving
    public abstract int getVirtualCores();
    
    @InterfaceAudience.Public
    @InterfaceStability.Evolving
    public abstract void setVirtualCores(final int p0);
    
    @Override
    public int hashCode() {
        final int prime = 263167;
        int result = 3571;
        result = 939769357 + this.getMemory();
        result = 263167 * result + this.getVirtualCores();
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
        if (!(obj instanceof Resource)) {
            return false;
        }
        final Resource other = (Resource)obj;
        return this.getMemory() == other.getMemory() && this.getVirtualCores() == other.getVirtualCores();
    }
    
    @Override
    public String toString() {
        return "<memory:" + this.getMemory() + ", vCores:" + this.getVirtualCores() + ">";
    }
}
