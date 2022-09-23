// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.state;

import org.datanucleus.cache.CachedPC;
import org.datanucleus.metadata.AbstractClassMetaData;
import org.datanucleus.store.FieldValues;
import org.datanucleus.ExecutionContext;

public interface ObjectProviderFactory
{
    void close();
    
    void disconnectObjectProvider(final ObjectProvider p0);
    
    ObjectProvider newForHollow(final ExecutionContext p0, final Class p1, final Object p2);
    
    ObjectProvider newForHollowPreConstructed(final ExecutionContext p0, final Object p1, final Object p2);
    
    ObjectProvider newForHollow(final ExecutionContext p0, final Class p1, final Object p2, final FieldValues p3);
    
    ObjectProvider newForPersistentClean(final ExecutionContext p0, final Object p1, final Object p2);
    
    @Deprecated
    ObjectProvider newForHollowPopulatedAppId(final ExecutionContext p0, final Class p1, final FieldValues p2);
    
    ObjectProvider newForEmbedded(final ExecutionContext p0, final Object p1, final boolean p2, final ObjectProvider p3, final int p4);
    
    ObjectProvider newForEmbedded(final ExecutionContext p0, final AbstractClassMetaData p1, final ObjectProvider p2, final int p3);
    
    ObjectProvider newForPersistentNew(final ExecutionContext p0, final Object p1, final FieldValues p2);
    
    ObjectProvider newForTransactionalTransient(final ExecutionContext p0, final Object p1);
    
    ObjectProvider newForDetached(final ExecutionContext p0, final Object p1, final Object p2, final Object p3);
    
    ObjectProvider newForPNewToBeDeleted(final ExecutionContext p0, final Object p1);
    
    ObjectProvider newForCachedPC(final ExecutionContext p0, final Object p1, final CachedPC p2);
}
