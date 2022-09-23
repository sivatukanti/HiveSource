// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store;

import org.datanucleus.state.ObjectProvider;

public interface ObjectReferencingStoreManager
{
    void notifyObjectIsOutdated(final ObjectProvider p0);
}
