// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.datasource.dbcp.pool.impl;

import org.datanucleus.store.rdbms.datasource.dbcp.pool.KeyedObjectPool;
import org.datanucleus.store.rdbms.datasource.dbcp.pool.KeyedPoolableObjectFactory;
import org.datanucleus.store.rdbms.datasource.dbcp.pool.KeyedObjectPoolFactory;

public class StackKeyedObjectPoolFactory implements KeyedObjectPoolFactory
{
    @Deprecated
    protected KeyedPoolableObjectFactory _factory;
    @Deprecated
    protected int _maxSleeping;
    @Deprecated
    protected int _initCapacity;
    
    public StackKeyedObjectPoolFactory() {
        this(null, 8, 4);
    }
    
    public StackKeyedObjectPoolFactory(final int maxSleeping) {
        this(null, maxSleeping, 4);
    }
    
    public StackKeyedObjectPoolFactory(final int maxSleeping, final int initialCapacity) {
        this(null, maxSleeping, initialCapacity);
    }
    
    public StackKeyedObjectPoolFactory(final KeyedPoolableObjectFactory factory) {
        this(factory, 8, 4);
    }
    
    public StackKeyedObjectPoolFactory(final KeyedPoolableObjectFactory factory, final int maxSleeping) {
        this(factory, maxSleeping, 4);
    }
    
    public StackKeyedObjectPoolFactory(final KeyedPoolableObjectFactory factory, final int maxSleeping, final int initialCapacity) {
        this._factory = null;
        this._maxSleeping = 8;
        this._initCapacity = 4;
        this._factory = factory;
        this._maxSleeping = maxSleeping;
        this._initCapacity = initialCapacity;
    }
    
    @Override
    public KeyedObjectPool createPool() {
        return new StackKeyedObjectPool(this._factory, this._maxSleeping, this._initCapacity);
    }
    
    public KeyedPoolableObjectFactory getFactory() {
        return this._factory;
    }
    
    public int getMaxSleeping() {
        return this._maxSleeping;
    }
    
    public int getInitialCapacity() {
        return this._initCapacity;
    }
}
