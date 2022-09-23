// 
// Decompiled by Procyon v0.5.36
// 

package com.google.inject.internal.util;

import java.util.List;
import java.util.Collection;
import java.util.Set;
import java.util.Iterator;
import java.util.Map;
import java.io.Serializable;
import java.util.concurrent.ConcurrentMap;

public abstract class $ImmutableMap<K, V> implements ConcurrentMap<K, V>, Serializable
{
    private static final $ImmutableMap<?, ?> EMPTY_IMMUTABLE_MAP;
    
    public static <K, V> $ImmutableMap<K, V> of() {
        return ($ImmutableMap<K, V>)$ImmutableMap.EMPTY_IMMUTABLE_MAP;
    }
    
    public static <K, V> $ImmutableMap<K, V> of(final K k1, final V v1) {
        return new SingletonImmutableMap<K, V>((Object)$Preconditions.checkNotNull(k1), (Object)$Preconditions.checkNotNull(v1));
    }
    
    public static <K, V> $ImmutableMap<K, V> of(final K k1, final V v1, final K k2, final V v2) {
        return new RegularImmutableMap<K, V>(new Map.Entry[] { entryOf(k1, v1), entryOf(k2, v2) });
    }
    
    public static <K, V> $ImmutableMap<K, V> of(final K k1, final V v1, final K k2, final V v2, final K k3, final V v3) {
        return new RegularImmutableMap<K, V>(new Map.Entry[] { entryOf(k1, v1), entryOf(k2, v2), entryOf(k3, v3) });
    }
    
    public static <K, V> $ImmutableMap<K, V> of(final K k1, final V v1, final K k2, final V v2, final K k3, final V v3, final K k4, final V v4) {
        return new RegularImmutableMap<K, V>(new Map.Entry[] { entryOf(k1, v1), entryOf(k2, v2), entryOf(k3, v3), entryOf(k4, v4) });
    }
    
    public static <K, V> $ImmutableMap<K, V> of(final K k1, final V v1, final K k2, final V v2, final K k3, final V v3, final K k4, final V v4, final K k5, final V v5) {
        return new RegularImmutableMap<K, V>(new Map.Entry[] { entryOf(k1, v1), entryOf(k2, v2), entryOf(k3, v3), entryOf(k4, v4), entryOf(k5, v5) });
    }
    
    public static <K, V> Builder<K, V> builder() {
        return new Builder<K, V>();
    }
    
    private static <K, V> Map.Entry<K, V> entryOf(final K key, final V value) {
        return $Maps.immutableEntry((K)$Preconditions.checkNotNull((K)key), (V)$Preconditions.checkNotNull((V)value));
    }
    
    public static <K, V> $ImmutableMap<K, V> copyOf(final Map<? extends K, ? extends V> map) {
        if (map instanceof $ImmutableMap) {
            final $ImmutableMap<K, V> kvMap = ($ImmutableMap<K, V>)($ImmutableMap)map;
            return kvMap;
        }
        final int size = map.size();
        switch (size) {
            case 0: {
                return of();
            }
            case 1: {
                final Map.Entry<? extends K, ? extends V> loneEntry = $Iterables.getOnlyElement(map.entrySet());
                return of((K)loneEntry.getKey(), (V)loneEntry.getValue());
            }
            default: {
                final Map.Entry<?, ?>[] array = (Map.Entry<?, ?>[])new Map.Entry[size];
                int i = 0;
                for (final Map.Entry<? extends K, ? extends V> entry : map.entrySet()) {
                    array[i++] = entryOf(entry.getKey(), entry.getValue());
                }
                return new RegularImmutableMap<K, V>((Map.Entry[])array);
            }
        }
    }
    
    $ImmutableMap() {
    }
    
    public final V put(final K k, final V v) {
        throw new UnsupportedOperationException();
    }
    
    public final V remove(final Object o) {
        throw new UnsupportedOperationException();
    }
    
    public final V putIfAbsent(final K key, final V value) {
        throw new UnsupportedOperationException();
    }
    
    public final boolean remove(final Object key, final Object value) {
        throw new UnsupportedOperationException();
    }
    
    public final boolean replace(final K key, final V oldValue, final V newValue) {
        throw new UnsupportedOperationException();
    }
    
    public final V replace(final K key, final V value) {
        throw new UnsupportedOperationException();
    }
    
    public final void putAll(final Map<? extends K, ? extends V> map) {
        throw new UnsupportedOperationException();
    }
    
    public final void clear() {
        throw new UnsupportedOperationException();
    }
    
    public abstract boolean containsKey(@$Nullable final Object p0);
    
    public abstract boolean containsValue(@$Nullable final Object p0);
    
    public abstract V get(@$Nullable final Object p0);
    
    public abstract $ImmutableSet<Map.Entry<K, V>> entrySet();
    
    public abstract $ImmutableSet<K> keySet();
    
    public abstract $ImmutableCollection<V> values();
    
    @Override
    public boolean equals(@$Nullable final Object object) {
        if (object == this) {
            return true;
        }
        if (object instanceof Map) {
            final Map<?, ?> that = (Map<?, ?>)object;
            return this.entrySet().equals(that.entrySet());
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        return this.entrySet().hashCode();
    }
    
    @Override
    public String toString() {
        final StringBuilder result = new StringBuilder(this.size() * 16).append('{');
        final Iterator<Map.Entry<K, V>> entries = this.entrySet().iterator();
        result.append(entries.next());
        while (entries.hasNext()) {
            result.append(", ").append(entries.next());
        }
        return result.append('}').toString();
    }
    
    Object writeReplace() {
        return new SerializedForm(this);
    }
    
    static {
        EMPTY_IMMUTABLE_MAP = new EmptyImmutableMap();
    }
    
    public static class Builder<K, V>
    {
        final List<Map.Entry<K, V>> entries;
        
        public Builder() {
            this.entries = (List<Map.Entry<K, V>>)$Lists.newArrayList();
        }
        
        public Builder<K, V> put(final K key, final V value) {
            this.entries.add(entryOf(key, value));
            return this;
        }
        
        public Builder<K, V> putAll(final Map<? extends K, ? extends V> map) {
            for (final Map.Entry<? extends K, ? extends V> entry : map.entrySet()) {
                this.put(entry.getKey(), entry.getValue());
            }
            return this;
        }
        
        public $ImmutableMap<K, V> build() {
            return fromEntryList(this.entries);
        }
        
        private static <K, V> $ImmutableMap<K, V> fromEntryList(final List<Map.Entry<K, V>> entries) {
            final int size = entries.size();
            switch (size) {
                case 0: {
                    return $ImmutableMap.of();
                }
                case 1: {
                    return new SingletonImmutableMap<K, V>((Map.Entry)$Iterables.getOnlyElement(entries));
                }
                default: {
                    final Map.Entry<?, ?>[] entryArray = entries.toArray(new Map.Entry[entries.size()]);
                    return new RegularImmutableMap<K, V>((Map.Entry[])entryArray);
                }
            }
        }
    }
    
    private static final class EmptyImmutableMap extends $ImmutableMap<Object, Object>
    {
        @Override
        public Object get(final Object key) {
            return null;
        }
        
        public int size() {
            return 0;
        }
        
        public boolean isEmpty() {
            return true;
        }
        
        @Override
        public boolean containsKey(final Object key) {
            return false;
        }
        
        @Override
        public boolean containsValue(final Object value) {
            return false;
        }
        
        @Override
        public $ImmutableSet<Map.Entry<Object, Object>> entrySet() {
            return $ImmutableSet.of();
        }
        
        @Override
        public $ImmutableSet<Object> keySet() {
            return $ImmutableSet.of();
        }
        
        @Override
        public $ImmutableCollection<Object> values() {
            return $ImmutableCollection.EMPTY_IMMUTABLE_COLLECTION;
        }
        
        @Override
        public boolean equals(@$Nullable final Object object) {
            if (object instanceof Map) {
                final Map<?, ?> that = (Map<?, ?>)object;
                return that.isEmpty();
            }
            return false;
        }
        
        @Override
        public int hashCode() {
            return 0;
        }
        
        @Override
        public String toString() {
            return "{}";
        }
    }
    
    private static final class SingletonImmutableMap<K, V> extends $ImmutableMap<K, V>
    {
        private final transient K singleKey;
        private final transient V singleValue;
        private transient Map.Entry<K, V> entry;
        private transient $ImmutableSet<Map.Entry<K, V>> entrySet;
        private transient $ImmutableSet<K> keySet;
        private transient $ImmutableCollection<V> values;
        
        private SingletonImmutableMap(final K singleKey, final V singleValue) {
            this.singleKey = singleKey;
            this.singleValue = singleValue;
        }
        
        private SingletonImmutableMap(final Map.Entry<K, V> entry) {
            this.entry = entry;
            this.singleKey = entry.getKey();
            this.singleValue = entry.getValue();
        }
        
        private Map.Entry<K, V> entry() {
            final Map.Entry<K, V> e = this.entry;
            return (e == null) ? (this.entry = $Maps.immutableEntry(this.singleKey, this.singleValue)) : e;
        }
        
        @Override
        public V get(final Object key) {
            return this.singleKey.equals(key) ? this.singleValue : null;
        }
        
        public int size() {
            return 1;
        }
        
        public boolean isEmpty() {
            return false;
        }
        
        @Override
        public boolean containsKey(final Object key) {
            return this.singleKey.equals(key);
        }
        
        @Override
        public boolean containsValue(final Object value) {
            return this.singleValue.equals(value);
        }
        
        @Override
        public $ImmutableSet<Map.Entry<K, V>> entrySet() {
            final $ImmutableSet<Map.Entry<K, V>> es = this.entrySet;
            return (es == null) ? (this.entrySet = $ImmutableSet.of(this.entry())) : es;
        }
        
        @Override
        public $ImmutableSet<K> keySet() {
            final $ImmutableSet<K> ks = this.keySet;
            return (ks == null) ? (this.keySet = $ImmutableSet.of(this.singleKey)) : ks;
        }
        
        @Override
        public $ImmutableCollection<V> values() {
            final $ImmutableCollection<V> v = this.values;
            return (v == null) ? (this.values = new Values<V>(this.singleValue)) : v;
        }
        
        @Override
        public boolean equals(@$Nullable final Object object) {
            if (object == this) {
                return true;
            }
            if (!(object instanceof Map)) {
                return false;
            }
            final Map<?, ?> that = (Map<?, ?>)object;
            if (that.size() != 1) {
                return false;
            }
            final Map.Entry<?, ?> entry = that.entrySet().iterator().next();
            return this.singleKey.equals(entry.getKey()) && this.singleValue.equals(entry.getValue());
        }
        
        @Override
        public int hashCode() {
            return this.singleKey.hashCode() ^ this.singleValue.hashCode();
        }
        
        @Override
        public String toString() {
            return '{' + this.singleKey.toString() + '=' + this.singleValue.toString() + '}';
        }
        
        private static class Values<V> extends $ImmutableCollection<V>
        {
            final V singleValue;
            
            Values(final V singleValue) {
                this.singleValue = singleValue;
            }
            
            @Override
            public boolean contains(final Object object) {
                return this.singleValue.equals(object);
            }
            
            @Override
            public boolean isEmpty() {
                return false;
            }
            
            public int size() {
                return 1;
            }
            
            @Override
            public $UnmodifiableIterator<V> iterator() {
                return $Iterators.singletonIterator(this.singleValue);
            }
        }
    }
    
    private static final class RegularImmutableMap<K, V> extends $ImmutableMap<K, V>
    {
        private final transient Map.Entry<K, V>[] entries;
        private final transient Object[] table;
        private final transient int mask;
        private final transient int keySetHashCode;
        private transient $ImmutableSet<Map.Entry<K, V>> entrySet;
        private transient $ImmutableSet<K> keySet;
        private transient $ImmutableCollection<V> values;
        
        private RegularImmutableMap(final Map.Entry<?, ?>... entries) {
            final Map.Entry<K, V>[] tmp = (Map.Entry<K, V>[])entries;
            this.entries = tmp;
            final int tableSize = $Hashing.chooseTableSize(entries.length);
            this.table = new Object[tableSize * 2];
            this.mask = tableSize - 1;
            int keySetHashCodeMutable = 0;
            for (final Map.Entry<K, V> entry : this.entries) {
                final K key = entry.getKey();
                final int keyHashCode = key.hashCode();
                int i = $Hashing.smear(keyHashCode);
                while (true) {
                    final int index = (i & this.mask) * 2;
                    final Object existing = this.table[index];
                    if (existing == null) {
                        final V value = entry.getValue();
                        this.table[index] = key;
                        this.table[index + 1] = value;
                        keySetHashCodeMutable += keyHashCode;
                        break;
                    }
                    if (existing.equals(key)) {
                        throw new IllegalArgumentException("duplicate key: " + key);
                    }
                    ++i;
                }
            }
            this.keySetHashCode = keySetHashCodeMutable;
        }
        
        @Override
        public V get(final Object key) {
            if (key == null) {
                return null;
            }
            int i = $Hashing.smear(key.hashCode());
            while (true) {
                final int index = (i & this.mask) * 2;
                final Object candidate = this.table[index];
                if (candidate == null) {
                    return null;
                }
                if (candidate.equals(key)) {
                    final V value = (V)this.table[index + 1];
                    return value;
                }
                ++i;
            }
        }
        
        public int size() {
            return this.entries.length;
        }
        
        public boolean isEmpty() {
            return false;
        }
        
        @Override
        public boolean containsKey(final Object key) {
            return this.get(key) != null;
        }
        
        @Override
        public boolean containsValue(final Object value) {
            if (value == null) {
                return false;
            }
            for (final Map.Entry<K, V> entry : this.entries) {
                if (entry.getValue().equals(value)) {
                    return true;
                }
            }
            return false;
        }
        
        @Override
        public $ImmutableSet<Map.Entry<K, V>> entrySet() {
            final $ImmutableSet<Map.Entry<K, V>> es = this.entrySet;
            return (es == null) ? (this.entrySet = ($ImmutableSet<Map.Entry<K, V>>)new EntrySet((RegularImmutableMap<Object, Object>)this)) : es;
        }
        
        @Override
        public $ImmutableSet<K> keySet() {
            final $ImmutableSet<K> ks = this.keySet;
            return (ks == null) ? (this.keySet = ($ImmutableSet<K>)new KeySet((RegularImmutableMap<Object, Object>)this)) : ks;
        }
        
        @Override
        public $ImmutableCollection<V> values() {
            final $ImmutableCollection<V> v = this.values;
            return (v == null) ? (this.values = new Values<V>(this)) : v;
        }
        
        @Override
        public String toString() {
            final StringBuilder result = new StringBuilder(this.size() * 16).append('{').append(this.entries[0]);
            for (int e = 1; e < this.entries.length; ++e) {
                result.append(", ").append(this.entries[e].toString());
            }
            return result.append('}').toString();
        }
        
        private static class EntrySet<K, V> extends ArrayImmutableSet<Map.Entry<K, V>>
        {
            final RegularImmutableMap<K, V> map;
            
            EntrySet(final RegularImmutableMap<K, V> map) {
                super(((RegularImmutableMap<Object, Object>)map).entries);
                this.map = map;
            }
            
            @Override
            public boolean contains(final Object target) {
                if (target instanceof Map.Entry) {
                    final Map.Entry<?, ?> entry = (Map.Entry<?, ?>)target;
                    final V mappedValue = this.map.get(entry.getKey());
                    return mappedValue != null && mappedValue.equals(entry.getValue());
                }
                return false;
            }
        }
        
        private static class KeySet<K, V> extends TransformedImmutableSet<Map.Entry<K, V>, K>
        {
            final RegularImmutableMap<K, V> map;
            
            KeySet(final RegularImmutableMap<K, V> map) {
                super(((RegularImmutableMap<Object, Object>)map).entries, ((RegularImmutableMap<Object, Object>)map).keySetHashCode);
                this.map = map;
            }
            
            @Override
            K transform(final Map.Entry<K, V> element) {
                return element.getKey();
            }
            
            @Override
            public boolean contains(final Object target) {
                return this.map.containsKey(target);
            }
        }
        
        private static class Values<V> extends $ImmutableCollection<V>
        {
            final RegularImmutableMap<?, V> map;
            
            Values(final RegularImmutableMap<?, V> map) {
                this.map = map;
            }
            
            public int size() {
                return ((RegularImmutableMap<Object, Object>)this.map).entries.length;
            }
            
            @Override
            public boolean isEmpty() {
                return false;
            }
            
            @Override
            public $UnmodifiableIterator<V> iterator() {
                final Iterator<V> iterator = new $AbstractIterator<V>() {
                    int index = 0;
                    
                    @Override
                    protected V computeNext() {
                        return (this.index < ((RegularImmutableMap<Object, Object>)Values.this.map).entries.length) ? ((RegularImmutableMap<Object, Object>)Values.this.map).entries[this.index++].getValue() : this.endOfData();
                    }
                };
                return $Iterators.unmodifiableIterator(iterator);
            }
            
            @Override
            public boolean contains(final Object target) {
                return this.map.containsValue(target);
            }
        }
    }
    
    private static class SerializedForm implements Serializable
    {
        final Object[] keys;
        final Object[] values;
        private static final long serialVersionUID = 0L;
        
        SerializedForm(final $ImmutableMap<?, ?> map) {
            this.keys = new Object[map.size()];
            this.values = new Object[map.size()];
            int i = 0;
            for (final Map.Entry<?, ?> entry : map.entrySet()) {
                this.keys[i] = entry.getKey();
                this.values[i] = entry.getValue();
                ++i;
            }
        }
        
        Object readResolve() {
            final Builder<Object, Object> builder = new Builder<Object, Object>();
            for (int i = 0; i < this.keys.length; ++i) {
                builder.put(this.keys[i], this.values[i]);
            }
            return builder.build();
        }
    }
}
