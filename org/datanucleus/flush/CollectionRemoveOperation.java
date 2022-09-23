// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.flush;

import org.datanucleus.store.scostore.Store;
import org.datanucleus.store.scostore.CollectionStore;
import org.datanucleus.state.ObjectProvider;

public class CollectionRemoveOperation implements SCOOperation
{
    final ObjectProvider op;
    final CollectionStore store;
    private final Object value;
    private final boolean allowCascadeDelete;
    
    public CollectionRemoveOperation(final ObjectProvider op, final CollectionStore store, final Object value, final boolean allowCascadeDelete) {
        this.op = op;
        this.store = store;
        this.value = value;
        this.allowCascadeDelete = allowCascadeDelete;
    }
    
    public Object getValue() {
        return this.value;
    }
    
    @Override
    public void perform() {
        this.store.remove(this.op, this.value, -1, this.allowCascadeDelete);
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
        return "COLLECTION REMOVE : " + this.op + " field=" + this.store.getOwnerMemberMetaData().getName();
    }
}
