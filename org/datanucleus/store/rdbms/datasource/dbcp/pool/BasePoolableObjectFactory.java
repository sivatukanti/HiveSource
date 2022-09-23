// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.datasource.dbcp.pool;

public abstract class BasePoolableObjectFactory implements PoolableObjectFactory
{
    @Override
    public abstract Object makeObject() throws Exception;
    
    @Override
    public void destroyObject(final Object obj) throws Exception {
    }
    
    @Override
    public boolean validateObject(final Object obj) {
        return true;
    }
    
    @Override
    public void activateObject(final Object obj) throws Exception {
    }
    
    @Override
    public void passivateObject(final Object obj) throws Exception {
    }
}
