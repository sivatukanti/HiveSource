// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.store.raw.data;

import org.apache.derby.iapi.store.raw.ContainerKey;
import java.util.Observer;

abstract class ContainerActionOnCommit implements Observer
{
    protected ContainerKey identity;
    
    protected ContainerActionOnCommit(final ContainerKey identity) {
        this.identity = identity;
    }
    
    public int hashCode() {
        return this.identity.hashCode();
    }
    
    public boolean equals(final Object o) {
        return o instanceof ContainerActionOnCommit && this.identity.equals(((ContainerActionOnCommit)o).identity) && this.getClass().equals(o.getClass());
    }
}
