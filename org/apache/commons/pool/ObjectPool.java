// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.pool;

import java.util.NoSuchElementException;

public interface ObjectPool
{
    Object borrowObject() throws Exception, NoSuchElementException, IllegalStateException;
    
    void returnObject(final Object p0) throws Exception;
    
    void invalidateObject(final Object p0) throws Exception;
    
    void addObject() throws Exception, IllegalStateException, UnsupportedOperationException;
    
    int getNumIdle() throws UnsupportedOperationException;
    
    int getNumActive() throws UnsupportedOperationException;
    
    void clear() throws Exception, UnsupportedOperationException;
    
    void close() throws Exception;
    
    void setFactory(final PoolableObjectFactory p0) throws IllegalStateException, UnsupportedOperationException;
}
