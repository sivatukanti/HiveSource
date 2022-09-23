// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.flush;

import org.datanucleus.store.scostore.Store;
import org.datanucleus.store.scostore.ListStore;
import org.datanucleus.state.ObjectProvider;

public class ListSetOperation implements SCOOperation
{
    final ObjectProvider op;
    final ListStore store;
    private final int index;
    private final Object value;
    boolean allowCascadeDelete;
    
    public ListSetOperation(final ObjectProvider op, final ListStore store, final int index, final Object value, final boolean allowCascadeDelete) {
        this.allowCascadeDelete = true;
        this.op = op;
        this.store = store;
        this.index = index;
        this.value = value;
        this.allowCascadeDelete = allowCascadeDelete;
    }
    
    @Override
    public void perform() {
        this.store.set(this.op, this.index, this.value, this.allowCascadeDelete);
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
        return "COLLECTION SET : " + this.op + " field=" + this.store.getOwnerMemberMetaData().getName() + " index=" + this.index;
    }
}
