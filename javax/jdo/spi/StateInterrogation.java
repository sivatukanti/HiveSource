// 
// Decompiled by Procyon v0.5.36
// 

package javax.jdo.spi;

import javax.jdo.PersistenceManager;

public interface StateInterrogation
{
    Boolean isPersistent(final Object p0);
    
    Boolean isTransactional(final Object p0);
    
    Boolean isDirty(final Object p0);
    
    Boolean isNew(final Object p0);
    
    Boolean isDeleted(final Object p0);
    
    Boolean isDetached(final Object p0);
    
    PersistenceManager getPersistenceManager(final Object p0);
    
    Object getObjectId(final Object p0);
    
    Object getTransactionalObjectId(final Object p0);
    
    Object getVersion(final Object p0);
    
    boolean makeDirty(final Object p0, final String p1);
}
