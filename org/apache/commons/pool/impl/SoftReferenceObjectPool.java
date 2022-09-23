// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.pool.impl;

import java.lang.ref.Reference;
import java.util.Iterator;
import java.lang.ref.SoftReference;
import java.util.NoSuchElementException;
import org.apache.commons.pool.PoolUtils;
import java.util.ArrayList;
import java.lang.ref.ReferenceQueue;
import org.apache.commons.pool.PoolableObjectFactory;
import java.util.List;
import org.apache.commons.pool.ObjectPool;
import org.apache.commons.pool.BaseObjectPool;

public class SoftReferenceObjectPool extends BaseObjectPool implements ObjectPool
{
    private List _pool;
    private PoolableObjectFactory _factory;
    private final ReferenceQueue refQueue;
    private int _numActive;
    
    public SoftReferenceObjectPool() {
        this._pool = null;
        this._factory = null;
        this.refQueue = new ReferenceQueue();
        this._numActive = 0;
        this._pool = new ArrayList();
        this._factory = null;
    }
    
    public SoftReferenceObjectPool(final PoolableObjectFactory factory) {
        this._pool = null;
        this._factory = null;
        this.refQueue = new ReferenceQueue();
        this._numActive = 0;
        this._pool = new ArrayList();
        this._factory = factory;
    }
    
    public SoftReferenceObjectPool(final PoolableObjectFactory factory, final int initSize) throws Exception, IllegalArgumentException {
        this._pool = null;
        this._factory = null;
        this.refQueue = new ReferenceQueue();
        this._numActive = 0;
        if (factory == null) {
            throw new IllegalArgumentException("factory required to prefill the pool.");
        }
        this._pool = new ArrayList(initSize);
        this._factory = factory;
        PoolUtils.prefill(this, initSize);
    }
    
    public synchronized Object borrowObject() throws Exception {
        this.assertOpen();
        Object obj = null;
        boolean newlyCreated = false;
        while (null == obj) {
            if (this._pool.isEmpty()) {
                if (null == this._factory) {
                    throw new NoSuchElementException();
                }
                newlyCreated = true;
                obj = this._factory.makeObject();
            }
            else {
                final SoftReference ref = this._pool.remove(this._pool.size() - 1);
                obj = ref.get();
                ref.clear();
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
                    try {
                        this._factory.destroyObject(obj);
                    }
                    catch (Throwable t2) {}
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
    
    public synchronized void returnObject(final Object obj) throws Exception {
        boolean success = !this.isClosed();
        if (this._factory != null) {
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
        final boolean shouldDestroy = !success;
        --this._numActive;
        if (success) {
            this._pool.add(new SoftReference<Object>(obj, this.refQueue));
        }
        this.notifyAll();
        if (shouldDestroy && this._factory != null) {
            try {
                this._factory.destroyObject(obj);
            }
            catch (Exception ex) {}
        }
    }
    
    public synchronized void invalidateObject(final Object obj) throws Exception {
        --this._numActive;
        if (this._factory != null) {
            this._factory.destroyObject(obj);
        }
        this.notifyAll();
    }
    
    public synchronized void addObject() throws Exception {
        this.assertOpen();
        if (this._factory == null) {
            throw new IllegalStateException("Cannot add objects without a factory.");
        }
        final Object obj = this._factory.makeObject();
        boolean success = true;
        if (!this._factory.validateObject(obj)) {
            success = false;
        }
        else {
            this._factory.passivateObject(obj);
        }
        final boolean shouldDestroy = !success;
        if (success) {
            this._pool.add(new SoftReference<Object>(obj, this.refQueue));
            this.notifyAll();
        }
        if (shouldDestroy) {
            try {
                this._factory.destroyObject(obj);
            }
            catch (Exception ex) {}
        }
    }
    
    public synchronized int getNumIdle() {
        this.pruneClearedReferences();
        return this._pool.size();
    }
    
    public synchronized int getNumActive() {
        return this._numActive;
    }
    
    public synchronized void clear() {
        if (null != this._factory) {
            final Iterator iter = this._pool.iterator();
            while (iter.hasNext()) {
                try {
                    final Object obj = iter.next().get();
                    if (null == obj) {
                        continue;
                    }
                    this._factory.destroyObject(obj);
                }
                catch (Exception e) {}
            }
        }
        this._pool.clear();
        this.pruneClearedReferences();
    }
    
    public void close() throws Exception {
        super.close();
        this.clear();
    }
    
    public synchronized void setFactory(final PoolableObjectFactory factory) throws IllegalStateException {
        this.assertOpen();
        if (0 < this.getNumActive()) {
            throw new IllegalStateException("Objects are already active");
        }
        this.clear();
        this._factory = factory;
    }
    
    private void pruneClearedReferences() {
        Reference ref;
        while ((ref = this.refQueue.poll()) != null) {
            try {
                this._pool.remove(ref);
            }
            catch (UnsupportedOperationException uoe) {}
        }
    }
}
