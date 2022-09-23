// 
// Decompiled by Procyon v0.5.36
// 

package javax.jdo;

import javax.transaction.Synchronization;

public interface Transaction
{
    void begin();
    
    void commit();
    
    void rollback();
    
    boolean isActive();
    
    boolean getRollbackOnly();
    
    void setRollbackOnly();
    
    void setNontransactionalRead(final boolean p0);
    
    boolean getNontransactionalRead();
    
    void setNontransactionalWrite(final boolean p0);
    
    boolean getNontransactionalWrite();
    
    void setRetainValues(final boolean p0);
    
    boolean getRetainValues();
    
    void setRestoreValues(final boolean p0);
    
    boolean getRestoreValues();
    
    void setOptimistic(final boolean p0);
    
    boolean getOptimistic();
    
    String getIsolationLevel();
    
    void setIsolationLevel(final String p0);
    
    void setSynchronization(final Synchronization p0);
    
    Synchronization getSynchronization();
    
    PersistenceManager getPersistenceManager();
    
    void setSerializeRead(final Boolean p0);
    
    Boolean getSerializeRead();
}
