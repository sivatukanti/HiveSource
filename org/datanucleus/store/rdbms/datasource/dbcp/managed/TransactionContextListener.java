// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.datasource.dbcp.managed;

public interface TransactionContextListener
{
    void afterCompletion(final TransactionContext p0, final boolean p1);
}
