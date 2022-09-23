// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.datasource.dbcp.pool;

public interface ObjectPoolFactory
{
    ObjectPool createPool() throws IllegalStateException;
}
