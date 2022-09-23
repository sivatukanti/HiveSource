// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.store.raw.xact;

import org.apache.derby.iapi.store.raw.RecordHandle;
import org.apache.derby.iapi.store.raw.ContainerKey;
import org.apache.derby.iapi.util.Matchable;

public final class EscalateContainerKey implements Matchable
{
    private ContainerKey container_key;
    
    public EscalateContainerKey(final ContainerKey container_key) {
        this.container_key = container_key;
    }
    
    public boolean match(final Object o) {
        return o instanceof RecordHandle && this.container_key.equals(((RecordHandle)o).getContainerId());
    }
}
