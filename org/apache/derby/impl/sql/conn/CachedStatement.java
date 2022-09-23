// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.conn;

import org.apache.derby.iapi.sql.Statement;
import org.apache.derby.impl.sql.GenericStatement;
import org.apache.derby.impl.sql.GenericPreparedStatement;
import org.apache.derby.iapi.services.cache.Cacheable;

public class CachedStatement implements Cacheable
{
    private GenericPreparedStatement ps;
    private Object identity;
    
    public GenericPreparedStatement getPreparedStatement() {
        return this.ps;
    }
    
    public void clean(final boolean b) {
    }
    
    public Cacheable setIdentity(final Object identity) {
        this.identity = identity;
        (this.ps = new GenericPreparedStatement((Statement)identity)).setCacheHolder(this);
        return this;
    }
    
    public Cacheable createIdentity(final Object o, final Object o2) {
        return null;
    }
    
    public void clearIdentity() {
        this.ps.setCacheHolder(null);
        this.identity = null;
        this.ps = null;
    }
    
    public Object getIdentity() {
        return this.identity;
    }
    
    public boolean isDirty() {
        return false;
    }
}
