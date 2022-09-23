// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.store.raw;

import org.apache.derby.iapi.error.StandardException;

public interface RePreparable
{
    void reclaimPrepareLocks(final Transaction p0, final LockingPolicy p1) throws StandardException;
}
