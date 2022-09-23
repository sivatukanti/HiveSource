// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.pool;

public abstract class BaseKeyedObjectPool implements KeyedObjectPool
{
    private volatile boolean closed;
    
    public BaseKeyedObjectPool() {
        this.closed = false;
    }
    
    public abstract Object borrowObject(final Object p0) throws Exception;
    
    public abstract void returnObject(final Object p0, final Object p1) throws Exception;
    
    public abstract void invalidateObject(final Object p0, final Object p1) throws Exception;
    
    public void addObject(final Object key) throws Exception, UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }
    
    public int getNumIdle(final Object key) throws UnsupportedOperationException {
        return -1;
    }
    
    public int getNumActive(final Object key) throws UnsupportedOperationException {
        return -1;
    }
    
    public int getNumIdle() throws UnsupportedOperationException {
        return -1;
    }
    
    public int getNumActive() throws UnsupportedOperationException {
        return -1;
    }
    
    public void clear() throws Exception, UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }
    
    public void clear(final Object key) throws Exception, UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }
    
    public void close() throws Exception {
        this.closed = true;
    }
    
    public void setFactory(final KeyedPoolableObjectFactory factory) throws IllegalStateException, UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }
    
    protected final boolean isClosed() {
        return this.closed;
    }
    
    protected final void assertOpen() throws IllegalStateException {
        if (this.isClosed()) {
            throw new IllegalStateException("Pool not open");
        }
    }
}
