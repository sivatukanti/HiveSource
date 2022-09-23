// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.pool.impl;

import org.apache.commons.pool.ObjectPool;
import org.apache.commons.pool.PoolableObjectFactory;
import org.apache.commons.pool.ObjectPoolFactory;

public class StackObjectPoolFactory implements ObjectPoolFactory
{
    protected PoolableObjectFactory _factory;
    protected int _maxSleeping;
    protected int _initCapacity;
    
    public StackObjectPoolFactory() {
        this(null, 8, 4);
    }
    
    public StackObjectPoolFactory(final int maxIdle) {
        this(null, maxIdle, 4);
    }
    
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
    
    public ObjectPool createPool() {
        return new StackObjectPool(this._factory, this._maxSleeping, this._initCapacity);
    }
}
