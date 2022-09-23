// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus;

import java.util.Map;
import javax.transaction.Synchronization;

public interface Transaction
{
    public static final String TRANSACTION_ISOLATION_OPTION = "transaction.isolation";
    
    void close();
    
    void begin();
    
    void commit();
    
    void rollback();
    
    boolean isActive();
    
    boolean getIsActive();
    
    void preFlush();
    
    void flush();
    
    void end();
    
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
    
    void setSerializeRead(final Boolean p0);
    
    Boolean getSerializeRead();
    
    void setSynchronization(final Synchronization p0);
    
    Synchronization getSynchronization();
    
    boolean isCommitting();
    
    void addTransactionEventListener(final TransactionEventListener p0);
    
    void removeTransactionEventListener(final TransactionEventListener p0);
    
    void bindTransactionEventListener(final TransactionEventListener p0);
    
    Map<String, Object> getOptions();
    
    void setOption(final String p0, final int p1);
    
    void setOption(final String p0, final boolean p1);
    
    void setOption(final String p0, final String p1);
    
    void setOption(final String p0, final Object p1);
}
