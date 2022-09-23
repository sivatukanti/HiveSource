// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.flush;

import org.datanucleus.store.scostore.Store;
import org.datanucleus.store.scostore.CollectionStore;
import org.datanucleus.state.ObjectProvider;

public class CollectionAddOperation implements SCOOperation
{
    final ObjectProvider op;
    final CollectionStore store;
    private final Object value;
    
    public CollectionAddOperation(final ObjectProvider op, final CollectionStore store, final Object value) {
        this.op = op;
        this.store = store;
        this.value = value;
    }
    
    public Object getValue() {
        return this.value;
    }
    
    @Override
    public void perform() {
        this.store.add(this.op, this.value, -1);
    }
    
    @Override
    public Store getStore() {
        return this.store;
    }
    
    @Override
    public ObjectProvider getObjectProvider() {
        return this.op;
    }
    
    @Override
    public String toString() {
        return "COLLECTION ADD : " + this.op + " field=" + this.store.getOwnerMemberMetaData().getName();
    }
}
