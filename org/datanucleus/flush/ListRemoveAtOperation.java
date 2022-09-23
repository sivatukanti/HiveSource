// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.flush;

import org.datanucleus.store.scostore.Store;
import org.datanucleus.store.scostore.ListStore;
import org.datanucleus.state.ObjectProvider;

public class ListRemoveAtOperation implements SCOOperation
{
    final ObjectProvider op;
    final ListStore store;
    private final int index;
    
    public ListRemoveAtOperation(final ObjectProvider op, final ListStore store, final int index) {
        this.op = op;
        this.store = store;
        this.index = index;
    }
    
    @Override
    public void perform() {
        this.store.remove(this.op, this.index, -1);
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
        return "COLLECTION REMOVE-AT : " + this.op + " field=" + this.store.getOwnerMemberMetaData().getName() + " index=" + this.index;
    }
}
