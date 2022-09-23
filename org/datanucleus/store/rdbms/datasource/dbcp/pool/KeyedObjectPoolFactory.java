// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.datasource.dbcp.pool;

public interface KeyedObjectPoolFactory
{
    KeyedObjectPool createPool() throws IllegalStateException;
}
