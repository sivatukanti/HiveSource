// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.pool;

public abstract class BasePoolableObjectFactory implements PoolableObjectFactory
{
    public abstract Object makeObject() throws Exception;
    
    public void destroyObject(final Object obj) throws Exception {
    }
    
    public boolean validateObject(final Object obj) {
        return true;
    }
    
    public void activateObject(final Object obj) throws Exception {
    }
    
    public void passivateObject(final Object obj) throws Exception {
    }
}
