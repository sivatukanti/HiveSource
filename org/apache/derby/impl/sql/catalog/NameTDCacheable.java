// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.catalog;

import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.sql.dictionary.TableDescriptor;
import org.apache.derby.iapi.services.cache.Cacheable;

class NameTDCacheable extends TDCacheable
{
    private TableKey identity;
    
    NameTDCacheable(final DataDictionaryImpl dataDictionaryImpl) {
        super(dataDictionaryImpl);
    }
    
    public void clearIdentity() {
        this.identity = null;
        this.td = null;
    }
    
    public Object getIdentity() {
        return this.identity;
    }
    
    public Cacheable createIdentity(final Object o, final Object o2) {
        this.identity = (TableKey)o;
        this.td = (TableDescriptor)o2;
        if (this.td != null) {
            return this;
        }
        return null;
    }
    
    public Cacheable setIdentity(final Object o) throws StandardException {
        final DataDictionaryImpl dd = this.dd;
        final TableKey identity = (TableKey)o;
        this.identity = identity;
        this.td = dd.getUncachedTableDescriptor(identity);
        if (this.td != null) {
            this.dd.addTableDescriptorToOtherCache(this.td, this);
            return this;
        }
        return null;
    }
}
