// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.connectionpool;

import org.datanucleus.store.StoreManager;

public interface ConnectionPoolFactory
{
    ConnectionPool createConnectionPool(final StoreManager p0);
}
