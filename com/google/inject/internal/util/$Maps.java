// 
// Decompiled by Procyon v0.5.36
// 

package com.google.inject.internal.util;

import java.util.Map;
import java.util.IdentityHashMap;
import java.util.TreeMap;
import java.util.LinkedHashMap;
import java.util.HashMap;

public final class $Maps
{
    private $Maps() {
    }
    
    public static <K, V> HashMap<K, V> newHashMap() {
        return new HashMap<K, V>();
    }
    
    public static <K, V> LinkedHashMap<K, V> newLinkedHashMap() {
        return new LinkedHashMap<K, V>();
    }
    
    public static <K extends Comparable, V> TreeMap<K, V> newTreeMap() {
        return new TreeMap<K, V>();
    }
    
    public static <K, V> IdentityHashMap<K, V> newIdentityHashMap() {
        return new IdentityHashMap<K, V>();
    }
    
    public static <K, V> Map.Entry<K, V> immutableEntry(@$Nullable final K key, @$Nullable final V value) {
        return new $ImmutableEntry<K, V>(key, value);
    }
}
