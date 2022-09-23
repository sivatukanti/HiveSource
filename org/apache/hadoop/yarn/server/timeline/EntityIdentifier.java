// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.timeline;

import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Private
@InterfaceStability.Unstable
public class EntityIdentifier implements Comparable<EntityIdentifier>
{
    private String id;
    private String type;
    
    public EntityIdentifier(final String id, final String type) {
        this.id = id;
        this.type = type;
    }
    
    public String getId() {
        return this.id;
    }
    
    public String getType() {
        return this.type;
    }
    
    @Override
    public int compareTo(final EntityIdentifier other) {
        final int c = this.type.compareTo(other.type);
        if (c != 0) {
            return c;
        }
        return this.id.compareTo(other.id);
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = 31 * result + ((this.id == null) ? 0 : this.id.hashCode());
        result = 31 * result + ((this.type == null) ? 0 : this.type.hashCode());
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
        final EntityIdentifier other = (EntityIdentifier)obj;
        if (this.id == null) {
            if (other.id != null) {
                return false;
            }
        }
        else if (!this.id.equals(other.id)) {
            return false;
        }
        if (this.type == null) {
            if (other.type != null) {
                return false;
            }
        }
        else if (!this.type.equals(other.type)) {
            return false;
        }
        return true;
    }
    
    @Override
    public String toString() {
        return "{ id: " + this.id + ", type: " + this.type + " }";
    }
}
