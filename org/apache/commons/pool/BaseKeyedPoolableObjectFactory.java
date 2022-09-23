// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.pool;

public abstract class BaseKeyedPoolableObjectFactory implements KeyedPoolableObjectFactory
{
    public abstract Object makeObject(final Object p0) throws Exception;
    
    public void destroyObject(final Object key, final Object obj) throws Exception {
    }
    
    public boolean validateObject(final Object key, final Object obj) {
        return true;
    }
    
    public void activateObject(final Object key, final Object obj) throws Exception {
    }
    
    public void passivateObject(final Object key, final Object obj) throws Exception {
    }
}
