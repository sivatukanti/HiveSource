// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.datasource.dbcp.pool.impl;

import java.util.Map;
import java.util.Iterator;
import org.datanucleus.store.rdbms.datasource.dbcp.pool.PoolUtils;
import java.util.NoSuchElementException;
import java.util.Stack;
import org.datanucleus.store.rdbms.datasource.dbcp.pool.KeyedPoolableObjectFactory;
import java.util.HashMap;
import org.datanucleus.store.rdbms.datasource.dbcp.pool.KeyedObjectPool;
import org.datanucleus.store.rdbms.datasource.dbcp.pool.BaseKeyedObjectPool;

public class StackKeyedObjectPool extends BaseKeyedObjectPool implements KeyedObjectPool
{
    protected static final int DEFAULT_MAX_SLEEPING = 8;
    protected static final int DEFAULT_INIT_SLEEPING_CAPACITY = 4;
    @Deprecated
    protected HashMap _pools;
    @Deprecated
    protected KeyedPoolableObjectFactory _factory;
    @Deprecated
    protected int _maxSleeping;
    @Deprecated
    protected int _initSleepingCapacity;
    @Deprecated
    protected int _totActive;
    @Deprecated
    protected int _totIdle;
    @Deprecated
    protected HashMap _activeCount;
    
    public StackKeyedObjectPool() {
        this(null, 8, 4);
    }
    
    public StackKeyedObjectPool(final int max) {
        this(null, max, 4);
    }
    
    public StackKeyedObjectPool(final int max, final int init) {
        this(null, max, init);
    }
    
    public StackKeyedObjectPool(final KeyedPoolableObjectFactory factory) {
        this(factory, 8);
    }
    
    public StackKeyedObjectPool(final KeyedPoolableObjectFactory factory, final int max) {
        this(factory, max, 4);
    }
    
    public StackKeyedObjectPool(final KeyedPoolableObjectFactory factory, final int max, final int init) {
        this._pools = null;
        this._factory = null;
        this._maxSleeping = 8;
        this._initSleepingCapacity = 4;
        this._totActive = 0;
        this._totIdle = 0;
        this._activeCount = null;
        this._factory = factory;
        this._maxSleeping = ((max < 0) ? 8 : max);
        this._initSleepingCapacity = ((init < 1) ? 4 : init);
        this._pools = new HashMap();
        this._activeCount = new HashMap();
    }
    
    @Override
    public synchronized Object borrowObject(final Object key) throws Exception {
        this.assertOpen();
        Stack stack = this._pools.get(key);
        if (null == stack) {
            stack = new Stack();
            stack.ensureCapacity((this._initSleepingCapacity > this._maxSleeping) ? this._maxSleeping : this._initSleepingCapacity);
            this._pools.put(key, stack);
        }
        Object obj = null;
        do {
            boolean newlyMade = false;
            if (!stack.empty()) {
                obj = stack.pop();
                --this._totIdle;
            }
            else {
                if (null == this._factory) {
                    throw new NoSuchElementException("pools without a factory cannot create new objects as needed.");
                }
                obj = this._factory.makeObject(key);
                newlyMade = true;
            }
            if (null != this._factory && null != obj) {
                try {
                    this._factory.activateObject(key, obj);
                    if (!this._factory.validateObject(key, obj)) {
                        throw new Exception("ValidateObject failed");
                    }
                    continue;
                }
                catch (Throwable t) {
                    PoolUtils.checkRethrow(t);
                    try {
                        this._factory.destroyObject(key, obj);
                    }
                    catch (Throwable t2) {
                        PoolUtils.checkRethrow(t2);
                    }
                    finally {
                        obj = null;
                    }
                    if (newlyMade) {
                        throw new NoSuchElementException("Could not create a validated object, cause: " + t.getMessage());
                    }
                    continue;
                }
            }
        } while (obj == null);
        this.incrementActiveCount(key);
        return obj;
    }
    
    @Override
    public synchronized void returnObject(final Object key, final Object obj) throws Exception {
        this.decrementActiveCount(key);
        Label_0055: {
            if (null != this._factory) {
                if (this._factory.validateObject(key, obj)) {
                    try {
                        this._factory.passivateObject(key, obj);
                        break Label_0055;
                    }
                    catch (Exception ex) {
                        this._factory.destroyObject(key, obj);
                    }
                }
                return;
            }
        }
        if (this.isClosed()) {
            if (null != this._factory) {
                try {
                    this._factory.destroyObject(key, obj);
                }
                catch (Exception ex2) {}
            }
            return;
        }
        Stack stack = this._pools.get(key);
        if (null == stack) {
            stack = new Stack();
            stack.ensureCapacity((this._initSleepingCapacity > this._maxSleeping) ? this._maxSleeping : this._initSleepingCapacity);
            this._pools.put(key, stack);
        }
        final int stackSize = stack.size();
        if (stackSize >= this._maxSleeping) {
            Object staleObj;
            if (stackSize > 0) {
                staleObj = stack.remove(0);
                --this._totIdle;
            }
            else {
                staleObj = obj;
            }
            if (null != this._factory) {
                try {
                    this._factory.destroyObject(key, staleObj);
                }
                catch (Exception ex3) {}
            }
        }
        stack.push(obj);
        ++this._totIdle;
    }
    
    @Override
    public synchronized void invalidateObject(final Object key, final Object obj) throws Exception {
        this.decrementActiveCount(key);
        if (null != this._factory) {
            this._factory.destroyObject(key, obj);
        }
        this.notifyAll();
    }
    
    @Override
    public synchronized void addObject(final Object key) throws Exception {
        this.assertOpen();
        if (this._factory == null) {
            throw new IllegalStateException("Cannot add objects without a factory.");
        }
        final Object obj = this._factory.makeObject(key);
        try {
            if (!this._factory.validateObject(key, obj)) {
                return;
            }
        }
        catch (Exception e2) {
            try {
                this._factory.destroyObject(key, obj);
            }
            catch (Exception ex) {}
            return;
        }
        this._factory.passivateObject(key, obj);
        Stack stack = this._pools.get(key);
        if (null == stack) {
            stack = new Stack();
            stack.ensureCapacity((this._initSleepingCapacity > this._maxSleeping) ? this._maxSleeping : this._initSleepingCapacity);
            this._pools.put(key, stack);
        }
        final int stackSize = stack.size();
        if (stackSize >= this._maxSleeping) {
            Object staleObj;
            if (stackSize > 0) {
                staleObj = stack.remove(0);
                --this._totIdle;
            }
            else {
                staleObj = obj;
            }
            try {
                this._factory.destroyObject(key, staleObj);
            }
            catch (Exception e) {
                if (obj == staleObj) {
                    throw e;
                }
            }
        }
        else {
            stack.push(obj);
            ++this._totIdle;
        }
    }
    
    @Override
    public synchronized int getNumIdle() {
        return this._totIdle;
    }
    
    @Override
    public synchronized int getNumActive() {
        return this._totActive;
    }
    
    @Override
    public synchronized int getNumActive(final Object key) {
        return this.getActiveCount(key);
    }
    
    @Override
    public synchronized int getNumIdle(final Object key) {
        try {
            return this._pools.get(key).size();
        }
        catch (Exception e) {
            return 0;
        }
    }
    
    @Override
    public synchronized void clear() {
        for (final Object key : this._pools.keySet()) {
            final Stack stack = this._pools.get(key);
            this.destroyStack(key, stack);
        }
        this._totIdle = 0;
        this._pools.clear();
        this._activeCount.clear();
    }
    
    @Override
    public synchronized void clear(final Object key) {
        final Stack stack = this._pools.remove(key);
        this.destroyStack(key, stack);
    }
    
    private synchronized void destroyStack(final Object key, final Stack stack) {
        if (null == stack) {
            return;
        }
        if (null != this._factory) {
            final Iterator it = stack.iterator();
            while (it.hasNext()) {
                try {
                    this._factory.destroyObject(key, it.next());
                }
                catch (Exception e) {}
            }
        }
        this._totIdle -= stack.size();
        this._activeCount.remove(key);
        stack.clear();
    }
    
    @Override
    public synchronized String toString() {
        final StringBuffer buf = new StringBuffer();
        buf.append(this.getClass().getName());
        buf.append(" contains ").append(this._pools.size()).append(" distinct pools: ");
        for (final Object key : this._pools.keySet()) {
            buf.append(" |").append(key).append("|=");
            final Stack s = this._pools.get(key);
            buf.append(s.size());
        }
        return buf.toString();
    }
    
    @Override
    public void close() throws Exception {
        super.close();
        this.clear();
    }
    
    @Override
    @Deprecated
    public synchronized void setFactory(final KeyedPoolableObjectFactory factory) throws IllegalStateException {
        if (0 < this.getNumActive()) {
            throw new IllegalStateException("Objects are already active");
        }
        this.clear();
        this._factory = factory;
    }
    
    public synchronized KeyedPoolableObjectFactory getFactory() {
        return this._factory;
    }
    
    private int getActiveCount(final Object key) {
        try {
            return this._activeCount.get(key);
        }
        catch (NoSuchElementException e) {
            return 0;
        }
        catch (NullPointerException e2) {
            return 0;
        }
    }
    
    private void incrementActiveCount(final Object key) {
        ++this._totActive;
        final Integer old = this._activeCount.get(key);
        if (null == old) {
            this._activeCount.put(key, new Integer(1));
        }
        else {
            this._activeCount.put(key, new Integer(old + 1));
        }
    }
    
    private void decrementActiveCount(final Object key) {
        --this._totActive;
        final Integer active = this._activeCount.get(key);
        if (null != active) {
            if (active <= 1) {
                this._activeCount.remove(key);
            }
            else {
                this._activeCount.put(key, new Integer(active - 1));
            }
        }
    }
    
    public Map getPools() {
        return this._pools;
    }
    
    public int getMaxSleeping() {
        return this._maxSleeping;
    }
    
    public int getInitSleepingCapacity() {
        return this._initSleepingCapacity;
    }
    
    public int getTotActive() {
        return this._totActive;
    }
    
    public int getTotIdle() {
        return this._totIdle;
    }
    
    public Map getActiveCount() {
        return this._activeCount;
    }
}
