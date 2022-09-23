// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.cache;

import java.util.Collection;
import java.util.Set;
import org.datanucleus.util.WeakValueMap;
import org.datanucleus.state.ObjectProvider;
import java.util.Map;

public class WeakRefCache implements Level1Cache
{
    private Map<Object, ObjectProvider> weakCache;
    
    public WeakRefCache() {
        this.weakCache = (Map<Object, ObjectProvider>)new WeakValueMap();
    }
    
    @Override
    public ObjectProvider put(final Object key, final ObjectProvider value) {
        return this.weakCache.put(key, value);
    }
    
    @Override
    public ObjectProvider get(final Object key) {
        return this.weakCache.get(key);
    }
    
    @Override
    public boolean containsKey(final Object key) {
        return this.weakCache.containsKey(key);
    }
    
    @Override
    public ObjectProvider remove(final Object key) {
        return this.weakCache.remove(key);
    }
    
    @Override
    public void clear() {
        this.weakCache.clear();
    }
    
    @Override
    public boolean containsValue(final Object value) {
        return this.weakCache.containsValue(value);
    }
    
    @Override
    public Set entrySet() {
        return this.weakCache.entrySet();
    }
    
    @Override
    public boolean isEmpty() {
        return this.weakCache.isEmpty();
    }
    
    @Override
    public Set keySet() {
        return this.weakCache.keySet();
    }
    
    @Override
    public void putAll(final Map t) {
        this.weakCache.putAll(t);
    }
    
    @Override
    public int size() {
        return this.weakCache.size();
    }
    
    @Override
    public Collection values() {
        return this.weakCache.values();
    }
}
