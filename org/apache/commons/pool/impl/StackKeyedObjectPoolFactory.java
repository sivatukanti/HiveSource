// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.pool.impl;

import org.apache.commons.pool.KeyedObjectPool;
import org.apache.commons.pool.KeyedPoolableObjectFactory;
import org.apache.commons.pool.KeyedObjectPoolFactory;

public class StackKeyedObjectPoolFactory implements KeyedObjectPoolFactory
{
    protected KeyedPoolableObjectFactory _factory;
    protected int _maxSleeping;
    protected int _initCapacity;
    
    public StackKeyedObjectPoolFactory() {
        this(null, 8, 4);
    }
    
    public StackKeyedObjectPoolFactory(final int max) {
        this(null, max, 4);
    }
    
    public StackKeyedObjectPoolFactory(final int max, final int init) {
        this(null, max, init);
    }
    
    public StackKeyedObjectPoolFactory(final KeyedPoolableObjectFactory factory) {
        this(factory, 8, 4);
    }
    
    public StackKeyedObjectPoolFactory(final KeyedPoolableObjectFactory factory, final int max) {
        this(factory, max, 4);
    }
    
    public StackKeyedObjectPoolFactory(final KeyedPoolableObjectFactory factory, final int max, final int init) {
        this._factory = null;
        this._maxSleeping = 8;
        this._initCapacity = 4;
        this._factory = factory;
        this._maxSleeping = max;
        this._initCapacity = init;
    }
    
    public KeyedObjectPool createPool() {
        return new StackKeyedObjectPool(this._factory, this._maxSleeping, this._initCapacity);
    }
}
