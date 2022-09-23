// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.catalog;

import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.sql.dictionary.TableDescriptor;
import org.apache.derby.iapi.services.cache.Cacheable;
import org.apache.derby.catalog.UUID;

class OIDTDCacheable extends TDCacheable
{
    private UUID identity;
    
    OIDTDCacheable(final DataDictionaryImpl dataDictionaryImpl) {
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
        this.identity = ((UUID)o).cloneMe();
        this.td = (TableDescriptor)o2;
        if (this.td != null) {
            return this;
        }
        return null;
    }
    
    public Cacheable setIdentity(final Object o) throws StandardException {
        this.identity = ((UUID)o).cloneMe();
        this.td = this.dd.getUncachedTableDescriptor(this.identity);
        if (this.td != null) {
            this.dd.addTableDescriptorToOtherCache(this.td, this);
            return this;
        }
        return null;
    }
}
