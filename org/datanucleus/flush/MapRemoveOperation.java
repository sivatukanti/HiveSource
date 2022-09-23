// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.flush;

import org.datanucleus.store.scostore.Store;
import org.datanucleus.store.scostore.MapStore;
import org.datanucleus.state.ObjectProvider;

public class MapRemoveOperation implements SCOOperation
{
    final ObjectProvider op;
    final MapStore store;
    private final Object key;
    private final Object value;
    
    public MapRemoveOperation(final ObjectProvider op, final MapStore store, final Object key, final Object val) {
        this.op = op;
        this.store = store;
        this.key = key;
        this.value = val;
    }
    
    public Object getKey() {
        return this.key;
    }
    
    public Object getValue() {
        return this.value;
    }
    
    @Override
    public void perform() {
        if (this.value != null) {
            this.store.remove(this.op, this.key, this.value);
        }
        else {
            this.store.remove(this.op, this.key);
        }
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
        return "MAP REMOVE : " + this.op + " field=" + this.store.getOwnerMemberMetaData().getName();
    }
}
