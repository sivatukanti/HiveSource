// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.pool;

public interface PoolableObjectFactory
{
    Object makeObject() throws Exception;
    
    void destroyObject(final Object p0) throws Exception;
    
    boolean validateObject(final Object p0);
    
    void activateObject(final Object p0) throws Exception;
    
    void passivateObject(final Object p0) throws Exception;
}
