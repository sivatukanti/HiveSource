// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.htrace.shaded.fasterxml.jackson.annotation;

import java.util.HashMap;
import java.util.Map;

public class SimpleObjectIdResolver implements ObjectIdResolver
{
    private Map<ObjectIdGenerator.IdKey, Object> _items;
    
    public SimpleObjectIdResolver() {
        this._items = new HashMap<ObjectIdGenerator.IdKey, Object>();
    }
    
    @Override
    public void bindItem(final ObjectIdGenerator.IdKey id, final Object ob) {
        if (this._items.containsKey(id)) {
            throw new IllegalStateException("Already had POJO for id (" + id.key.getClass().getName() + ") [" + id + "]");
        }
        this._items.put(id, ob);
    }
    
    @Override
    public Object resolveId(final ObjectIdGenerator.IdKey id) {
        return this._items.get(id);
    }
    
    @Override
    public boolean canUseFor(final ObjectIdResolver resolverType) {
        return resolverType.getClass() == this.getClass();
    }
    
    @Override
    public ObjectIdResolver newForDeserialization(final Object context) {
        return this;
    }
}
