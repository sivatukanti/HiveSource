// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.flush;

import org.datanucleus.store.scostore.Store;
import org.datanucleus.store.scostore.MapStore;
import org.datanucleus.state.ObjectProvider;

public class MapPutOperation implements SCOOperation
{
    final ObjectProvider op;
    final MapStore store;
    private final Object key;
    private final Object value;
    
    public MapPutOperation(final ObjectProvider op, final MapStore store, final Object key, final Object value) {
        this.op = op;
        this.store = store;
        this.key = key;
        this.value = value;
    }
    
    public Object getKey() {
        return this.key;
    }
    
    public Object getValue() {
        return this.value;
    }
    
    @Override
    public void perform() {
        this.store.put(this.op, this.key, this.value);
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
        return "MAP PUT : " + this.op + " field=" + this.store.getOwnerMemberMetaData().getName();
    }
}
