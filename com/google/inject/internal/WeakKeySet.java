// 
// Decompiled by Procyon v0.5.36
// 

package com.google.inject.internal;

import com.google.inject.internal.util.$Sets;
import com.google.inject.internal.util.$SourceProvider;
import com.google.inject.internal.util.$Maps;
import com.google.inject.Key;
import java.util.Set;
import java.util.Map;

final class WeakKeySet
{
    private Map<String, Set<Object>> backingSet;
    
    public void add(final Key<?> key, Object source) {
        if (this.backingSet == null) {
            this.backingSet = (Map<String, Set<Object>>)$Maps.newHashMap();
        }
        if (source instanceof Class || source == $SourceProvider.UNKNOWN_SOURCE) {
            source = null;
        }
        final String k = key.toString();
        Set<Object> sources = this.backingSet.get(k);
        if (sources == null) {
            sources = $Sets.newLinkedHashSet();
            this.backingSet.put(k, sources);
        }
        sources.add(Errors.convert(source));
    }
    
    public boolean contains(final Key<?> key) {
        return this.backingSet != null && this.backingSet.containsKey(key.toString());
    }
    
    public Set<Object> getSources(final Key<?> key) {
        return this.backingSet.get(key.toString());
    }
}
