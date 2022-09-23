// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.datasource.dbcp.pool;

public abstract class BaseObjectPool implements ObjectPool
{
    private volatile boolean closed;
    
    public BaseObjectPool() {
        this.closed = false;
    }
    
    @Override
    public abstract Object borrowObject() throws Exception;
    
    @Override
    public abstract void returnObject(final Object p0) throws Exception;
    
    @Override
    public abstract void invalidateObject(final Object p0) throws Exception;
    
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
    public void addObject() throws Exception, UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void close() throws Exception {
        this.closed = true;
    }
    
    @Override
    @Deprecated
    public void setFactory(final PoolableObjectFactory factory) throws IllegalStateException, UnsupportedOperationException {
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
