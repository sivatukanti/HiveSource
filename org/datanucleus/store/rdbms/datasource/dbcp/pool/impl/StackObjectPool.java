// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.datasource.dbcp.pool.impl;

import java.util.Iterator;
import org.datanucleus.store.rdbms.datasource.dbcp.pool.PoolUtils;
import java.util.NoSuchElementException;
import org.datanucleus.store.rdbms.datasource.dbcp.pool.PoolableObjectFactory;
import java.util.Stack;
import org.datanucleus.store.rdbms.datasource.dbcp.pool.ObjectPool;
import org.datanucleus.store.rdbms.datasource.dbcp.pool.BaseObjectPool;

public class StackObjectPool extends BaseObjectPool implements ObjectPool
{
    protected static final int DEFAULT_MAX_SLEEPING = 8;
    protected static final int DEFAULT_INIT_SLEEPING_CAPACITY = 4;
    @Deprecated
    protected Stack _pool;
    @Deprecated
    protected PoolableObjectFactory _factory;
    @Deprecated
    protected int _maxSleeping;
    @Deprecated
    protected int _numActive;
    
    @Deprecated
    public StackObjectPool() {
        this(null, 8, 4);
    }
    
    @Deprecated
    public StackObjectPool(final int maxIdle) {
        this(null, maxIdle, 4);
    }
    
    @Deprecated
    public StackObjectPool(final int maxIdle, final int initIdleCapacity) {
        this(null, maxIdle, initIdleCapacity);
    }
    
    public StackObjectPool(final PoolableObjectFactory factory) {
        this(factory, 8, 4);
    }
    
    public StackObjectPool(final PoolableObjectFactory factory, final int maxIdle) {
        this(factory, maxIdle, 4);
    }
    
    public StackObjectPool(final PoolableObjectFactory factory, final int maxIdle, final int initIdleCapacity) {
        this._pool = null;
        this._factory = null;
        this._maxSleeping = 8;
        this._numActive = 0;
        this._factory = factory;
        this._maxSleeping = ((maxIdle < 0) ? 8 : maxIdle);
        final int initcapacity = (initIdleCapacity < 1) ? 4 : initIdleCapacity;
        (this._pool = new Stack()).ensureCapacity((initcapacity > this._maxSleeping) ? this._maxSleeping : initcapacity);
    }
    
    @Override
    public synchronized Object borrowObject() throws Exception {
        this.assertOpen();
        Object obj = null;
        boolean newlyCreated = false;
        while (null == obj) {
            if (!this._pool.empty()) {
                obj = this._pool.pop();
            }
            else {
                if (null == this._factory) {
                    throw new NoSuchElementException();
                }
                obj = this._factory.makeObject();
                newlyCreated = true;
                if (obj == null) {
                    throw new NoSuchElementException("PoolableObjectFactory.makeObject() returned null.");
                }
            }
            if (null != this._factory && null != obj) {
                try {
                    this._factory.activateObject(obj);
                    if (!this._factory.validateObject(obj)) {
                        throw new Exception("ValidateObject failed");
                    }
                    continue;
                }
                catch (Throwable t) {
                    PoolUtils.checkRethrow(t);
                    try {
                        this._factory.destroyObject(obj);
                    }
                    catch (Throwable t2) {
                        PoolUtils.checkRethrow(t2);
                    }
                    finally {
                        obj = null;
                    }
                    if (newlyCreated) {
                        throw new NoSuchElementException("Could not create a validated object, cause: " + t.getMessage());
                    }
                    continue;
                }
            }
        }
        ++this._numActive;
        return obj;
    }
    
    @Override
    public synchronized void returnObject(Object obj) throws Exception {
        boolean success = !this.isClosed();
        if (null != this._factory) {
            if (!this._factory.validateObject(obj)) {
                success = false;
            }
            else {
                try {
                    this._factory.passivateObject(obj);
                }
                catch (Exception e) {
                    success = false;
                }
            }
        }
        boolean shouldDestroy = !success;
        --this._numActive;
        if (success) {
            Object toBeDestroyed = null;
            if (this._pool.size() >= this._maxSleeping) {
                shouldDestroy = true;
                toBeDestroyed = this._pool.remove(0);
            }
            this._pool.push(obj);
            obj = toBeDestroyed;
        }
        this.notifyAll();
        if (shouldDestroy) {
            try {
                this._factory.destroyObject(obj);
            }
            catch (Exception ex) {}
        }
    }
    
    @Override
    public synchronized void invalidateObject(final Object obj) throws Exception {
        --this._numActive;
        if (null != this._factory) {
            this._factory.destroyObject(obj);
        }
        this.notifyAll();
    }
    
    @Override
    public synchronized int getNumIdle() {
        return this._pool.size();
    }
    
    @Override
    public synchronized int getNumActive() {
        return this._numActive;
    }
    
    @Override
    public synchronized void clear() {
        if (null != this._factory) {
            final Iterator it = this._pool.iterator();
            while (it.hasNext()) {
                try {
                    this._factory.destroyObject(it.next());
                }
                catch (Exception e) {}
            }
        }
        this._pool.clear();
    }
    
    @Override
    public void close() throws Exception {
        super.close();
        this.clear();
    }
    
    @Override
    public synchronized void addObject() throws Exception {
        this.assertOpen();
        if (this._factory == null) {
            throw new IllegalStateException("Cannot add objects without a factory.");
        }
        Object obj = this._factory.makeObject();
        boolean success = true;
        if (!this._factory.validateObject(obj)) {
            success = false;
        }
        else {
            this._factory.passivateObject(obj);
        }
        boolean shouldDestroy = !success;
        if (success) {
            Object toBeDestroyed = null;
            if (this._pool.size() >= this._maxSleeping) {
                shouldDestroy = true;
                toBeDestroyed = this._pool.remove(0);
            }
            this._pool.push(obj);
            obj = toBeDestroyed;
        }
        this.notifyAll();
        if (shouldDestroy) {
            try {
                this._factory.destroyObject(obj);
            }
            catch (Exception ex) {}
        }
    }
    
    @Override
    @Deprecated
    public synchronized void setFactory(final PoolableObjectFactory factory) throws IllegalStateException {
        this.assertOpen();
        if (0 < this.getNumActive()) {
            throw new IllegalStateException("Objects are already active");
        }
        this.clear();
        this._factory = factory;
    }
    
    public synchronized PoolableObjectFactory getFactory() {
        return this._factory;
    }
    
    public int getMaxSleeping() {
        return this._maxSleeping;
    }
}
