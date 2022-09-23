// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.cache;

import java.util.Collection;
import java.util.Set;
import java.util.HashMap;
import org.datanucleus.state.ObjectProvider;
import java.util.Map;

public class StrongRefCache implements Level1Cache
{
    private Map<Object, ObjectProvider> cache;
    
    public StrongRefCache() {
        this.cache = new HashMap<Object, ObjectProvider>();
    }
    
    @Override
    public ObjectProvider put(final Object key, final ObjectProvider value) {
        return this.cache.put(key, value);
    }
    
    @Override
    public ObjectProvider get(final Object key) {
        return this.cache.get(key);
    }
    
    @Override
    public boolean containsKey(final Object key) {
        return this.cache.containsKey(key);
    }
    
    @Override
    public ObjectProvider remove(final Object key) {
        return this.cache.remove(key);
    }
    
    @Override
    public void clear() {
        this.cache.clear();
    }
    
    @Override
    public boolean containsValue(final Object value) {
        return this.cache.containsValue(value);
    }
    
    @Override
    public Set entrySet() {
        return this.cache.entrySet();
    }
    
    @Override
    public boolean isEmpty() {
        return this.cache.isEmpty();
    }
    
    @Override
    public Set keySet() {
        return this.cache.keySet();
    }
    
    @Override
    public void putAll(final Map t) {
        this.cache.putAll(t);
    }
    
    @Override
    public int size() {
        return this.cache.size();
    }
    
    @Override
    public Collection values() {
        return this.cache.values();
    }
}
