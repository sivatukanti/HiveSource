// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hive.service.cli;

import org.apache.hive.service.cli.thrift.THandleIdentifier;

public abstract class Handle
{
    private final HandleIdentifier handleId;
    
    public Handle() {
        this.handleId = new HandleIdentifier();
    }
    
    public Handle(final HandleIdentifier handleId) {
        this.handleId = handleId;
    }
    
    public Handle(final THandleIdentifier tHandleIdentifier) {
        this.handleId = new HandleIdentifier(tHandleIdentifier);
    }
    
    public HandleIdentifier getHandleIdentifier() {
        return this.handleId;
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = 31 * result + ((this.handleId == null) ? 0 : this.handleId.hashCode());
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
        if (!(obj instanceof Handle)) {
            return false;
        }
        final Handle other = (Handle)obj;
        if (this.handleId == null) {
            if (other.handleId != null) {
                return false;
            }
        }
        else if (!this.handleId.equals(other.handleId)) {
            return false;
        }
        return true;
    }
    
    @Override
    public abstract String toString();
}
