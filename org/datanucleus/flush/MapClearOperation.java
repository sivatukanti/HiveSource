// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.flush;

import org.datanucleus.store.scostore.Store;
import org.datanucleus.store.scostore.MapStore;
import org.datanucleus.state.ObjectProvider;

public class MapClearOperation implements SCOOperation
{
    final ObjectProvider op;
    final MapStore store;
    
    public MapClearOperation(final ObjectProvider op, final MapStore store) {
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
        return "MAP CLEAR : " + this.op + " field=" + this.store.getOwnerMemberMetaData().getName();
    }
}
