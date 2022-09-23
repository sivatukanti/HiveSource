// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.catalog;

import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.sql.dictionary.SPSDescriptor;
import org.apache.derby.iapi.services.cache.Cacheable;

class SPSNameCacheable implements Cacheable
{
    private TableKey identity;
    private SPSDescriptor spsd;
    private final DataDictionaryImpl dd;
    
    SPSNameCacheable(final DataDictionaryImpl dd) {
        this.dd = dd;
    }
    
    public void clearIdentity() {
        if (this.spsd != null) {
            this.dd.spsCacheEntryRemoved(this.spsd);
            this.spsd = null;
            this.identity = null;
        }
    }
    
    public Object getIdentity() {
        return this.identity;
    }
    
    public Cacheable createIdentity(final Object o, final Object o2) {
        this.identity = (TableKey)o;
        this.spsd = (SPSDescriptor)o2;
        if (this.spsd != null) {
            this.dd.spsCacheEntryAdded(this.spsd);
            try {
                this.spsd.loadGeneratedClass();
            }
            catch (StandardException ex) {}
            return this;
        }
        return null;
    }
    
    public Cacheable setIdentity(final Object o) throws StandardException {
        this.identity = (TableKey)o;
        this.spsd = this.dd.getUncachedSPSDescriptor(this.identity);
        if (this.spsd != null) {
            this.dd.spsCacheEntryAdded(this.spsd);
            try {
                this.spsd.loadGeneratedClass();
            }
            catch (StandardException ex) {}
            return this;
        }
        return null;
    }
    
    public void clean(final boolean b) {
    }
    
    public boolean isDirty() {
        return false;
    }
    
    public SPSDescriptor getSPSDescriptor() {
        return this.spsd;
    }
}
