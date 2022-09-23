// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store;

import org.datanucleus.state.ObjectProvider;
import org.datanucleus.ExecutionContext;

public interface StorePersistenceHandler
{
    void close();
    
    void batchStart(final ExecutionContext p0, final PersistenceBatchType p1);
    
    void batchEnd(final ExecutionContext p0, final PersistenceBatchType p1);
    
    void insertObject(final ObjectProvider p0);
    
    void insertObjects(final ObjectProvider... p0);
    
    void updateObject(final ObjectProvider p0, final int[] p1);
    
    void deleteObject(final ObjectProvider p0);
    
    void deleteObjects(final ObjectProvider... p0);
    
    void fetchObject(final ObjectProvider p0, final int[] p1);
    
    void locateObject(final ObjectProvider p0);
    
    void locateObjects(final ObjectProvider[] p0);
    
    Object findObject(final ExecutionContext p0, final Object p1);
    
    Object[] findObjects(final ExecutionContext p0, final Object[] p1);
}
