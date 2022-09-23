// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.catalog;

import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.services.stream.HeaderPrintWriter;
import org.apache.derby.iapi.sql.dictionary.TableDescriptor;
import org.apache.derby.iapi.services.cache.Cacheable;

abstract class TDCacheable implements Cacheable
{
    protected TableDescriptor td;
    protected final DataDictionaryImpl dd;
    
    TDCacheable(final DataDictionaryImpl dd) {
        this.dd = dd;
    }
    
    public void clean(final boolean b) {
    }
    
    public boolean isDirty() {
        return false;
    }
    
    public TableDescriptor getTableDescriptor() {
        return this.td;
    }
    
    protected boolean checkConsistency(final TableDescriptor tableDescriptor, final Object o, final HeaderPrintWriter headerPrintWriter) throws StandardException {
        return true;
    }
}
