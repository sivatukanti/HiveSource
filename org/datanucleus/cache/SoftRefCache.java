// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.cache;

import java.util.Collection;
import java.util.Set;
import org.datanucleus.util.SoftValueMap;
import org.datanucleus.state.ObjectProvider;
import java.util.Map;

public class SoftRefCache implements Level1Cache
{
    private Map<Object, ObjectProvider> softCache;
    
    public SoftRefCache() {
        this.softCache = (Map<Object, ObjectProvider>)new SoftValueMap();
    }
    
    @Override
    public ObjectProvider put(final Object key, final ObjectProvider value) {
        return this.softCache.put(key, value);
    }
    
    @Override
    public ObjectProvider get(final Object key) {
        return this.softCache.get(key);
    }
    
    @Override
    public boolean containsKey(final Object key) {
        return this.softCache.containsKey(key);
    }
    
    @Override
    public ObjectProvider remove(final Object key) {
        return this.softCache.remove(key);
    }
    
    @Override
    public void clear() {
        this.softCache.clear();
    }
    
    @Override
    public boolean containsValue(final Object value) {
        return this.softCache.containsValue(value);
    }
    
    @Override
    public Set entrySet() {
        return this.softCache.entrySet();
    }
    
    @Override
    public boolean isEmpty() {
        return this.softCache.isEmpty();
    }
    
    @Override
    public Set keySet() {
        return this.softCache.keySet();
    }
    
    @Override
    public void putAll(final Map t) {
        this.softCache.putAll(t);
    }
    
    @Override
    public int size() {
        return this.softCache.size();
    }
    
    @Override
    public Collection values() {
        return this.softCache.values();
    }
}
