// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.datasource.dbcp.pool.impl;

import org.datanucleus.store.rdbms.datasource.dbcp.pool.ObjectPool;
import org.datanucleus.store.rdbms.datasource.dbcp.pool.PoolableObjectFactory;
import org.datanucleus.store.rdbms.datasource.dbcp.pool.ObjectPoolFactory;

public class StackObjectPoolFactory implements ObjectPoolFactory
{
    @Deprecated
    protected PoolableObjectFactory _factory;
    @Deprecated
    protected int _maxSleeping;
    @Deprecated
    protected int _initCapacity;
    
    @Deprecated
    public StackObjectPoolFactory() {
        this(null, 8, 4);
    }
    
    @Deprecated
    public StackObjectPoolFactory(final int maxIdle) {
        this(null, maxIdle, 4);
    }
    
    @Deprecated
    public StackObjectPoolFactory(final int maxIdle, final int initIdleCapacity) {
        this(null, maxIdle, initIdleCapacity);
    }
    
    public StackObjectPoolFactory(final PoolableObjectFactory factory) {
        this(factory, 8, 4);
    }
    
    public StackObjectPoolFactory(final PoolableObjectFactory factory, final int maxIdle) {
        this(factory, maxIdle, 4);
    }
    
    public StackObjectPoolFactory(final PoolableObjectFactory factory, final int maxIdle, final int initIdleCapacity) {
        this._factory = null;
        this._maxSleeping = 8;
        this._initCapacity = 4;
        this._factory = factory;
        this._maxSleeping = maxIdle;
        this._initCapacity = initIdleCapacity;
    }
    
    @Override
    public ObjectPool createPool() {
        return new StackObjectPool(this._factory, this._maxSleeping, this._initCapacity);
    }
    
    public PoolableObjectFactory getFactory() {
        return this._factory;
    }
    
    public int getMaxSleeping() {
        return this._maxSleeping;
    }
    
    public int getInitCapacity() {
        return this._initCapacity;
    }
}
