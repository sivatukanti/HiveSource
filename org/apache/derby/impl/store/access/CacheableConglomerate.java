// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.store.access;

import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.store.access.conglomerate.TransactionManager;
import org.apache.derby.iapi.store.raw.ContainerKey;
import org.apache.derby.iapi.store.access.conglomerate.Conglomerate;
import org.apache.derby.iapi.services.cache.Cacheable;

class CacheableConglomerate implements Cacheable
{
    private final RAMAccessManager accessManager;
    private Long conglomid;
    private Conglomerate conglom;
    
    CacheableConglomerate(final RAMAccessManager accessManager) {
        this.accessManager = accessManager;
    }
    
    protected Conglomerate getConglom() {
        return this.conglom;
    }
    
    public Cacheable setIdentity(final Object o) throws StandardException {
        this.conglomid = (Long)o;
        final long longValue = this.conglomid;
        this.conglom = this.accessManager.getFactoryFromConglomId(longValue).readConglomerate(this.accessManager.getCurrentTransactionContext().getTransaction(), new ContainerKey(0L, longValue));
        return this;
    }
    
    public Cacheable createIdentity(final Object o, final Object o2) throws StandardException {
        this.conglomid = (Long)o;
        this.conglom = (Conglomerate)o2;
        return this;
    }
    
    public void clearIdentity() {
        this.conglomid = null;
        this.conglom = null;
    }
    
    public Object getIdentity() {
        return this.conglomid;
    }
    
    public boolean isDirty() {
        return false;
    }
    
    public void clean(final boolean b) throws StandardException {
    }
}
