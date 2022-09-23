// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.datasource.dbcp.pool;

public abstract class BaseKeyedPoolableObjectFactory implements KeyedPoolableObjectFactory
{
    @Override
    public abstract Object makeObject(final Object p0) throws Exception;
    
    @Override
    public void destroyObject(final Object key, final Object obj) throws Exception {
    }
    
    @Override
    public boolean validateObject(final Object key, final Object obj) {
        return true;
    }
    
    @Override
    public void activateObject(final Object key, final Object obj) throws Exception {
    }
    
    @Override
    public void passivateObject(final Object key, final Object obj) throws Exception {
    }
}
