// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.pool;

import java.util.NoSuchElementException;

public interface KeyedObjectPool
{
    Object borrowObject(final Object p0) throws Exception, NoSuchElementException, IllegalStateException;
    
    void returnObject(final Object p0, final Object p1) throws Exception;
    
    void invalidateObject(final Object p0, final Object p1) throws Exception;
    
    void addObject(final Object p0) throws Exception, IllegalStateException, UnsupportedOperationException;
    
    int getNumIdle(final Object p0) throws UnsupportedOperationException;
    
    int getNumActive(final Object p0) throws UnsupportedOperationException;
    
    int getNumIdle() throws UnsupportedOperationException;
    
    int getNumActive() throws UnsupportedOperationException;
    
    void clear() throws Exception, UnsupportedOperationException;
    
    void clear(final Object p0) throws Exception, UnsupportedOperationException;
    
    void close() throws Exception;
    
    void setFactory(final KeyedPoolableObjectFactory p0) throws IllegalStateException, UnsupportedOperationException;
}
