// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.flush;

import org.datanucleus.store.scostore.Store;
import org.datanucleus.store.scostore.CollectionStore;
import org.datanucleus.state.ObjectProvider;

public class CollectionClearOperation implements SCOOperation
{
    final ObjectProvider op;
    final CollectionStore store;
    
    public CollectionClearOperation(final ObjectProvider op, final CollectionStore store) {
        this.op = op;
        this.store = store;
    }
    
    @Override
    public void perform() {
        this.store.clear(this.op);
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
        return "COLLECTION CLEAR : " + this.op + " field=" + this.store.getOwnerMemberMetaData().getName();
    }
}
