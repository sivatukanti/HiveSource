// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.flush;

import org.datanucleus.store.scostore.Store;
import org.datanucleus.store.scostore.ListStore;
import org.datanucleus.state.ObjectProvider;

public class ListAddAtOperation implements SCOOperation
{
    final ObjectProvider op;
    final ListStore store;
    private final Object value;
    private final int index;
    
    public ListAddAtOperation(final ObjectProvider op, final ListStore store, final int index, final Object value) {
        this.op = op;
        this.store = store;
        this.index = index;
        this.value = value;
    }
    
    @Override
    public void perform() {
        this.store.add(this.op, this.value, this.index, -1);
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
        return "COLLECTION ADD-AT : " + this.op + " field=" + this.store.getOwnerMemberMetaData().getName() + " index=" + this.index;
    }
}
