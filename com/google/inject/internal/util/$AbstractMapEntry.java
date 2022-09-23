// 
// Decompiled by Procyon v0.5.36
// 

package com.google.inject.internal.util;

import java.util.Map;

public abstract class $AbstractMapEntry<K, V> implements Map.Entry<K, V>
{
    public abstract K getKey();
    
    public abstract V getValue();
    
    public V setValue(final V value) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public boolean equals(@$Nullable final Object object) {
        if (object instanceof Map.Entry) {
            final Map.Entry<?, ?> that = (Map.Entry<?, ?>)object;
            return $Objects.equal(this.getKey(), that.getKey()) && $Objects.equal(this.getValue(), that.getValue());
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        final K k = this.getKey();
        final V v = this.getValue();
        return ((k == null) ? 0 : k.hashCode()) ^ ((v == null) ? 0 : v.hashCode());
    }
    
    @Override
    public String toString() {
        return this.getKey() + "=" + this.getValue();
    }
}
