// 
// Decompiled by Procyon v0.5.36
// 

package com.google.inject.internal.util;

import java.io.Serializable;

class $ImmutableEntry<K, V> extends $AbstractMapEntry<K, V> implements Serializable
{
    private final K key;
    private final V value;
    private static final long serialVersionUID = 0L;
    
    $ImmutableEntry(@$Nullable final K key, @$Nullable final V value) {
        this.key = key;
        this.value = value;
    }
    
    @Override
    public K getKey() {
        return this.key;
    }
    
    @Override
    public V getValue() {
        return this.value;
    }
}
