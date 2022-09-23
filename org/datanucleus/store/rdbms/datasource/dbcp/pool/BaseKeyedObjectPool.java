// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.datasource.dbcp.pool;

public abstract class BaseKeyedObjectPool implements KeyedObjectPool
{
    private volatile boolean closed;
    
    public BaseKeyedObjectPool() {
        this.closed = false;
    }
    
    @Override
    public abstract Object borrowObject(final Object p0) throws Exception;
    
    @Override
    public abstract void returnObject(final Object p0, final Object p1) throws Exception;
    
    @Override
    public abstract void invalidateObject(final Object p0, final Object p1) throws Exception;
    
    @Override
    public void addObject(final Object key) throws Exception, UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public int getNumIdle(final Object key) throws UnsupportedOperationException {
        return -1;
    }
    
    @Override
    public int getNumActive(final Object key) throws UnsupportedOperationException {
        return -1;
    }
    
    @Override
    public int getNumIdle() throws UnsupportedOperationException {
        return -1;
    }
    
    @Override
    public int getNumActive() throws UnsupportedOperationException {
        return -1;
    }
    
    @Override
    public void clear() throws Exception, UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void clear(final Object key) throws Exception, UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void close() throws Exception {
        this.closed = true;
    }
    
    @Override
    @Deprecated
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
