// 
// Decompiled by Procyon v0.5.36
// 

package com.google.protobuf;

import java.util.NoSuchElementException;
import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.TreeMap;
import java.util.SortedMap;
import java.util.Set;
import java.util.Iterator;
import java.util.Collections;
import java.util.Map;
import java.util.List;
import java.util.AbstractMap;

class SmallSortedMap<K extends Comparable<K>, V> extends AbstractMap<K, V>
{
    private final int maxArraySize;
    private List<Entry> entryList;
    private Map<K, V> overflowEntries;
    private boolean isImmutable;
    private volatile EntrySet lazyEntrySet;
    
    static <FieldDescriptorType extends FieldSet.FieldDescriptorLite<FieldDescriptorType>> SmallSortedMap<FieldDescriptorType, Object> newFieldMap(final int arraySize) {
        return new SmallSortedMap<FieldDescriptorType, Object>(arraySize) {
            @Override
            public void makeImmutable() {
                if (!this.isImmutable()) {
                    for (int i = 0; i < this.getNumArrayEntries(); ++i) {
                        final Map.Entry<FieldDescriptorType, Object> entry = this.getArrayEntryAt(i);
                        if (entry.getKey().isRepeated()) {
                            final List value = entry.getValue();
                            entry.setValue(Collections.unmodifiableList((List<?>)value));
                        }
                    }
                    final Iterator i$ = this.getOverflowEntries().iterator();
                    while (i$.hasNext()) {
                        final Map.Entry<FieldDescriptorType, Object> entry = i$.next();
                        if (entry.getKey().isRepeated()) {
                            final List value = entry.getValue();
                            entry.setValue(Collections.unmodifiableList((List<?>)value));
                        }
                    }
                }
                super.makeImmutable();
            }
        };
    }
    
    static <K extends Comparable<K>, V> SmallSortedMap<K, V> newInstanceForTest(final int arraySize) {
        return new SmallSortedMap<K, V>(arraySize);
    }
    
    private SmallSortedMap(final int arraySize) {
        this.maxArraySize = arraySize;
        this.entryList = Collections.emptyList();
        this.overflowEntries = Collections.emptyMap();
    }
    
    public void makeImmutable() {
        if (!this.isImmutable) {
            this.overflowEntries = (this.overflowEntries.isEmpty() ? Collections.emptyMap() : Collections.unmodifiableMap((Map<? extends K, ? extends V>)this.overflowEntries));
            this.isImmutable = true;
        }
    }
    
    public boolean isImmutable() {
        return this.isImmutable;
    }
    
    public int getNumArrayEntries() {
        return this.entryList.size();
    }
    
    public Map.Entry<K, V> getArrayEntryAt(final int index) {
        return this.entryList.get(index);
    }
    
    public int getNumOverflowEntries() {
        return this.overflowEntries.size();
    }
    
    public Iterable<Map.Entry<K, V>> getOverflowEntries() {
        return this.overflowEntries.isEmpty() ? EmptySet.iterable() : this.overflowEntries.entrySet();
    }
    
    @Override
    public int size() {
        return this.entryList.size() + this.overflowEntries.size();
    }
    
    @Override
    public boolean containsKey(final Object o) {
        final K key = (K)o;
        return this.binarySearchInArray(key) >= 0 || this.overflowEntries.containsKey(key);
    }
    
    @Override
    public V get(final Object o) {
        final K key = (K)o;
        final int index = this.binarySearchInArray(key);
        if (index >= 0) {
            return this.entryList.get(index).getValue();
        }
        return this.overflowEntries.get(key);
    }
    
    @Override
    public V put(final K key, final V value) {
        this.checkMutable();
        final int index = this.binarySearchInArray(key);
        if (index >= 0) {
            return this.entryList.get(index).setValue(value);
        }
        this.ensureEntryArrayMutable();
        final int insertionPoint = -(index + 1);
        if (insertionPoint >= this.maxArraySize) {
            return this.getOverflowEntriesMutable().put(key, value);
        }
        if (this.entryList.size() == this.maxArraySize) {
            final Entry lastEntryInArray = this.entryList.remove(this.maxArraySize - 1);
            this.getOverflowEntriesMutable().put(lastEntryInArray.getKey(), lastEntryInArray.getValue());
        }
        this.entryList.add(insertionPoint, new Entry(key, value));
        return null;
    }
    
    @Override
    public void clear() {
        this.checkMutable();
        if (!this.entryList.isEmpty()) {
            this.entryList.clear();
        }
        if (!this.overflowEntries.isEmpty()) {
            this.overflowEntries.clear();
        }
    }
    
    @Override
    public V remove(final Object o) {
        this.checkMutable();
        final K key = (K)o;
        final int index = this.binarySearchInArray(key);
        if (index >= 0) {
            return this.removeArrayEntryAt(index);
        }
        if (this.overflowEntries.isEmpty()) {
            return null;
        }
        return this.overflowEntries.remove(key);
    }
    
    private V removeArrayEntryAt(final int index) {
        this.checkMutable();
        final V removed = this.entryList.remove(index).getValue();
        if (!this.overflowEntries.isEmpty()) {
            final Iterator<Map.Entry<K, V>> iterator = this.getOverflowEntriesMutable().entrySet().iterator();
            this.entryList.add(new Entry(iterator.next()));
            iterator.remove();
        }
        return removed;
    }
    
    private int binarySearchInArray(final K key) {
        int left = 0;
        int right = this.entryList.size() - 1;
        if (right >= 0) {
            final int cmp = key.compareTo(this.entryList.get(right).getKey());
            if (cmp > 0) {
                return -(right + 2);
            }
            if (cmp == 0) {
                return right;
            }
        }
        while (left <= right) {
            final int mid = (left + right) / 2;
            final int cmp2 = key.compareTo(this.entryList.get(mid).getKey());
            if (cmp2 < 0) {
                right = mid - 1;
            }
            else {
                if (cmp2 <= 0) {
                    return mid;
                }
                left = mid + 1;
            }
        }
        return -(left + 1);
    }
    
    @Override
    public Set<Map.Entry<K, V>> entrySet() {
        if (this.lazyEntrySet == null) {
            this.lazyEntrySet = new EntrySet();
        }
        return this.lazyEntrySet;
    }
    
    private void checkMutable() {
        if (this.isImmutable) {
            throw new UnsupportedOperationException();
        }
    }
    
    private SortedMap<K, V> getOverflowEntriesMutable() {
        this.checkMutable();
        if (this.overflowEntries.isEmpty() && !(this.overflowEntries instanceof TreeMap)) {
            this.overflowEntries = new TreeMap<K, V>();
        }
        return (SortedMap<K, V>)(SortedMap)this.overflowEntries;
    }
    
    private void ensureEntryArrayMutable() {
        this.checkMutable();
        if (this.entryList.isEmpty() && !(this.entryList instanceof ArrayList)) {
            this.entryList = new ArrayList<Entry>(this.maxArraySize);
        }
    }
    
    private class Entry implements Map.Entry<K, V>, Comparable<Entry>
    {
        private final K key;
        private V value;
        
        Entry(final SmallSortedMap smallSortedMap, final Map.Entry<K, V> copy) {
            this(copy.getKey(), copy.getValue());
        }
        
        Entry(final K key, final V value) {
            this.key = key;
            this.value = value;
        }
        
        public K getKey() {
            return this.key;
        }
        
        public V getValue() {
            return this.value;
        }
        
        public int compareTo(final Entry other) {
            return this.getKey().compareTo(other.getKey());
        }
        
        public V setValue(final V newValue) {
            SmallSortedMap.this.checkMutable();
            final V oldValue = this.value;
            this.value = newValue;
            return oldValue;
        }
        
        @Override
        public boolean equals(final Object o) {
            if (o == this) {
                return true;
            }
            if (!(o instanceof Map.Entry)) {
                return false;
            }
            final Map.Entry<?, ?> other = (Map.Entry<?, ?>)o;
            return this.equals(this.key, other.getKey()) && this.equals(this.value, other.getValue());
        }
        
        @Override
        public int hashCode() {
            return ((this.key == null) ? 0 : this.key.hashCode()) ^ ((this.value == null) ? 0 : this.value.hashCode());
        }
        
        @Override
        public String toString() {
            return this.key + "=" + this.value;
        }
        
        private boolean equals(final Object o1, final Object o2) {
            return (o1 == null) ? (o2 == null) : o1.equals(o2);
        }
    }
    
    private class EntrySet extends AbstractSet<Map.Entry<K, V>>
    {
        @Override
        public Iterator<Map.Entry<K, V>> iterator() {
            return new EntryIterator();
        }
        
        @Override
        public int size() {
            return SmallSortedMap.this.size();
        }
        
        @Override
        public boolean contains(final Object o) {
            final Map.Entry<K, V> entry = (Map.Entry<K, V>)o;
            final V existing = SmallSortedMap.this.get(entry.getKey());
            final V value = entry.getValue();
            return existing == value || (existing != null && existing.equals(value));
        }
        
        @Override
        public boolean add(final Map.Entry<K, V> entry) {
            if (!this.contains(entry)) {
                SmallSortedMap.this.put(entry.getKey(), entry.getValue());
                return true;
            }
            return false;
        }
        
        @Override
        public boolean remove(final Object o) {
            final Map.Entry<K, V> entry = (Map.Entry<K, V>)o;
            if (this.contains(entry)) {
                SmallSortedMap.this.remove(entry.getKey());
                return true;
            }
            return false;
        }
        
        @Override
        public void clear() {
            SmallSortedMap.this.clear();
        }
    }
    
    private class EntryIterator implements Iterator<Map.Entry<K, V>>
    {
        private int pos;
        private boolean nextCalledBeforeRemove;
        private Iterator<Map.Entry<K, V>> lazyOverflowIterator;
        
        private EntryIterator() {
            this.pos = -1;
        }
        
        public boolean hasNext() {
            return this.pos + 1 < SmallSortedMap.this.entryList.size() || this.getOverflowIterator().hasNext();
        }
        
        public Map.Entry<K, V> next() {
            this.nextCalledBeforeRemove = true;
            if (++this.pos < SmallSortedMap.this.entryList.size()) {
                return SmallSortedMap.this.entryList.get(this.pos);
            }
            return this.getOverflowIterator().next();
        }
        
        public void remove() {
            if (!this.nextCalledBeforeRemove) {
                throw new IllegalStateException("remove() was called before next()");
            }
            this.nextCalledBeforeRemove = false;
            SmallSortedMap.this.checkMutable();
            if (this.pos < SmallSortedMap.this.entryList.size()) {
                SmallSortedMap.this.removeArrayEntryAt(this.pos--);
            }
            else {
                this.getOverflowIterator().remove();
            }
        }
        
        private Iterator<Map.Entry<K, V>> getOverflowIterator() {
            if (this.lazyOverflowIterator == null) {
                this.lazyOverflowIterator = SmallSortedMap.this.overflowEntries.entrySet().iterator();
            }
            return this.lazyOverflowIterator;
        }
    }
    
    private static class EmptySet
    {
        private static final Iterator<Object> ITERATOR;
        private static final Iterable<Object> ITERABLE;
        
        static <T> Iterable<T> iterable() {
            return (Iterable<T>)EmptySet.ITERABLE;
        }
        
        static {
            ITERATOR = new Iterator<Object>() {
                public boolean hasNext() {
                    return false;
                }
                
                public Object next() {
                    throw new NoSuchElementException();
                }
                
                public void remove() {
                    throw new UnsupportedOperationException();
                }
            };
            ITERABLE = new Iterable<Object>() {
                public Iterator<Object> iterator() {
                    return EmptySet.ITERATOR;
                }
            };
        }
    }
}
