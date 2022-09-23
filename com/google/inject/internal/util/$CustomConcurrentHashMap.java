// 
// Decompiled by Procyon v0.5.36
// 

package com.google.inject.internal.util;

import java.lang.reflect.Field;
import java.util.AbstractCollection;
import java.util.AbstractSet;
import java.util.NoSuchElementException;
import java.util.concurrent.atomic.AtomicReferenceArray;
import java.util.concurrent.locks.ReentrantLock;
import java.io.ObjectInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Iterator;
import java.lang.reflect.Array;
import java.util.Map;
import java.util.Collection;
import java.util.Set;
import java.io.Serializable;
import java.util.AbstractMap;
import java.util.concurrent.ConcurrentMap;

final class $CustomConcurrentHashMap
{
    private $CustomConcurrentHashMap() {
    }
    
    private static int rehash(int h) {
        h += (h << 15 ^ 0xFFFFCD7D);
        h ^= h >>> 10;
        h += h << 3;
        h ^= h >>> 6;
        h += (h << 2) + (h << 14);
        return h ^ h >>> 16;
    }
    
    static final class Builder
    {
        float loadFactor;
        int initialCapacity;
        int concurrencyLevel;
        
        Builder() {
            this.loadFactor = 0.75f;
            this.initialCapacity = 16;
            this.concurrencyLevel = 16;
        }
        
        public Builder loadFactor(final float loadFactor) {
            if (loadFactor <= 0.0f) {
                throw new IllegalArgumentException();
            }
            this.loadFactor = loadFactor;
            return this;
        }
        
        public Builder initialCapacity(final int initialCapacity) {
            if (initialCapacity < 0) {
                throw new IllegalArgumentException();
            }
            this.initialCapacity = initialCapacity;
            return this;
        }
        
        public Builder concurrencyLevel(final int concurrencyLevel) {
            if (concurrencyLevel <= 0) {
                throw new IllegalArgumentException();
            }
            this.concurrencyLevel = concurrencyLevel;
            return this;
        }
        
        public <K, V, E> ConcurrentMap<K, V> buildMap(final Strategy<K, V, E> strategy) {
            if (strategy == null) {
                throw new NullPointerException("strategy");
            }
            return new Impl<K, V, Object>(strategy, this);
        }
        
        public <K, V, E> ConcurrentMap<K, V> buildComputingMap(final ComputingStrategy<K, V, E> strategy, final $Function<? super K, ? extends V> computer) {
            if (strategy == null) {
                throw new NullPointerException("strategy");
            }
            if (computer == null) {
                throw new NullPointerException("computer");
            }
            return new ComputingImpl<K, V, Object>(strategy, this, computer);
        }
    }
    
    static class Impl<K, V, E> extends AbstractMap<K, V> implements ConcurrentMap<K, V>, Serializable
    {
        static final int MAXIMUM_CAPACITY = 1073741824;
        static final int MAX_SEGMENTS = 65536;
        static final int RETRIES_BEFORE_LOCK = 2;
        final Strategy<K, V, E> strategy;
        final int segmentMask;
        final int segmentShift;
        final Segment[] segments;
        final float loadFactor;
        Set<K> keySet;
        Collection<V> values;
        Set<Map.Entry<K, V>> entrySet;
        private static final long serialVersionUID = 0L;
        
        Impl(final Strategy<K, V, E> strategy, final Builder builder) {
            this.loadFactor = builder.loadFactor;
            int concurrencyLevel = builder.concurrencyLevel;
            int initialCapacity = builder.initialCapacity;
            if (concurrencyLevel > 65536) {
                concurrencyLevel = 65536;
            }
            int segmentShift = 0;
            int segmentCount;
            for (segmentCount = 1; segmentCount < concurrencyLevel; segmentCount <<= 1) {
                ++segmentShift;
            }
            this.segmentShift = 32 - segmentShift;
            this.segmentMask = segmentCount - 1;
            this.segments = this.newSegmentArray(segmentCount);
            if (initialCapacity > 1073741824) {
                initialCapacity = 1073741824;
            }
            int segmentCapacity = initialCapacity / segmentCount;
            if (segmentCapacity * segmentCount < initialCapacity) {
                ++segmentCapacity;
            }
            int segmentSize;
            for (segmentSize = 1; segmentSize < segmentCapacity; segmentSize <<= 1) {}
            for (int i = 0; i < this.segments.length; ++i) {
                this.segments[i] = new Segment(segmentSize);
            }
            (this.strategy = strategy).setInternals(new InternalsImpl());
        }
        
        int hash(final Object key) {
            final int h = this.strategy.hashKey(key);
            return rehash(h);
        }
        
        Segment[] newSegmentArray(final int ssize) {
            return (Segment[])Array.newInstance(Segment.class, ssize);
        }
        
        Segment segmentFor(final int hash) {
            return this.segments[hash >>> this.segmentShift & this.segmentMask];
        }
        
        @Override
        public boolean isEmpty() {
            final Segment[] segments = this.segments;
            final int[] mc = new int[segments.length];
            int mcsum = 0;
            for (int i = 0; i < segments.length; ++i) {
                if (segments[i].count != 0) {
                    return false;
                }
                final int n = mcsum;
                final int[] array = mc;
                final int n2 = i;
                final int modCount = segments[i].modCount;
                array[n2] = modCount;
                mcsum = n + modCount;
            }
            if (mcsum != 0) {
                for (int i = 0; i < segments.length; ++i) {
                    if (segments[i].count != 0 || mc[i] != segments[i].modCount) {
                        return false;
                    }
                }
            }
            return true;
        }
        
        @Override
        public int size() {
            final Segment[] segments = this.segments;
            long sum = 0L;
            long check = 0L;
            final int[] mc = new int[segments.length];
            for (int k = 0; k < 2; ++k) {
                check = 0L;
                sum = 0L;
                int mcsum = 0;
                for (int i = 0; i < segments.length; ++i) {
                    sum += segments[i].count;
                    final int n = mcsum;
                    final int[] array = mc;
                    final int n2 = i;
                    final int modCount = segments[i].modCount;
                    array[n2] = modCount;
                    mcsum = n + modCount;
                }
                if (mcsum != 0) {
                    for (int i = 0; i < segments.length; ++i) {
                        check += segments[i].count;
                        if (mc[i] != segments[i].modCount) {
                            check = -1L;
                            break;
                        }
                    }
                }
                if (check == sum) {
                    break;
                }
            }
            if (check != sum) {
                sum = 0L;
                for (final Segment segment : segments) {
                    segment.lock();
                }
                for (final Segment segment : segments) {
                    sum += segment.count;
                }
                for (final Segment segment : segments) {
                    segment.unlock();
                }
            }
            if (sum > 2147483647L) {
                return Integer.MAX_VALUE;
            }
            return (int)sum;
        }
        
        @Override
        public V get(final Object key) {
            if (key == null) {
                throw new NullPointerException("key");
            }
            final int hash = this.hash(key);
            return this.segmentFor(hash).get(key, hash);
        }
        
        @Override
        public boolean containsKey(final Object key) {
            if (key == null) {
                throw new NullPointerException("key");
            }
            final int hash = this.hash(key);
            return this.segmentFor(hash).containsKey(key, hash);
        }
        
        @Override
        public boolean containsValue(final Object value) {
            if (value == null) {
                throw new NullPointerException("value");
            }
            final Segment[] segments = this.segments;
            final int[] mc = new int[segments.length];
            for (int k = 0; k < 2; ++k) {
                int mcsum = 0;
                for (int i = 0; i < segments.length; ++i) {
                    final int c = segments[i].count;
                    final int n = mcsum;
                    final int[] array = mc;
                    final int n2 = i;
                    final int modCount = segments[i].modCount;
                    array[n2] = modCount;
                    mcsum = n + modCount;
                    if (segments[i].containsValue(value)) {
                        return true;
                    }
                }
                boolean cleanSweep = true;
                if (mcsum != 0) {
                    for (int j = 0; j < segments.length; ++j) {
                        final int c2 = segments[j].count;
                        if (mc[j] != segments[j].modCount) {
                            cleanSweep = false;
                            break;
                        }
                    }
                }
                if (cleanSweep) {
                    return false;
                }
            }
            for (final Segment segment : segments) {
                segment.lock();
            }
            boolean found = false;
            try {
                for (final Segment segment2 : segments) {
                    if (segment2.containsValue(value)) {
                        found = true;
                        break;
                    }
                }
            }
            finally {
                for (final Segment segment3 : segments) {
                    segment3.unlock();
                }
            }
            return found;
        }
        
        @Override
        public V put(final K key, final V value) {
            if (key == null) {
                throw new NullPointerException("key");
            }
            if (value == null) {
                throw new NullPointerException("value");
            }
            final int hash = this.hash(key);
            return this.segmentFor(hash).put(key, hash, value, false);
        }
        
        public V putIfAbsent(final K key, final V value) {
            if (key == null) {
                throw new NullPointerException("key");
            }
            if (value == null) {
                throw new NullPointerException("value");
            }
            final int hash = this.hash(key);
            return this.segmentFor(hash).put(key, hash, value, true);
        }
        
        @Override
        public void putAll(final Map<? extends K, ? extends V> m) {
            for (final Map.Entry<? extends K, ? extends V> e : m.entrySet()) {
                this.put(e.getKey(), e.getValue());
            }
        }
        
        @Override
        public V remove(final Object key) {
            if (key == null) {
                throw new NullPointerException("key");
            }
            final int hash = this.hash(key);
            return this.segmentFor(hash).remove(key, hash);
        }
        
        public boolean remove(final Object key, final Object value) {
            if (key == null) {
                throw new NullPointerException("key");
            }
            final int hash = this.hash(key);
            return this.segmentFor(hash).remove(key, hash, value);
        }
        
        public boolean replace(final K key, final V oldValue, final V newValue) {
            if (key == null) {
                throw new NullPointerException("key");
            }
            if (oldValue == null) {
                throw new NullPointerException("oldValue");
            }
            if (newValue == null) {
                throw new NullPointerException("newValue");
            }
            final int hash = this.hash(key);
            return this.segmentFor(hash).replace(key, hash, oldValue, newValue);
        }
        
        public V replace(final K key, final V value) {
            if (key == null) {
                throw new NullPointerException("key");
            }
            if (value == null) {
                throw new NullPointerException("value");
            }
            final int hash = this.hash(key);
            return this.segmentFor(hash).replace(key, hash, value);
        }
        
        @Override
        public void clear() {
            for (final Segment segment : this.segments) {
                segment.clear();
            }
        }
        
        @Override
        public Set<K> keySet() {
            final Set<K> ks = this.keySet;
            return (ks != null) ? ks : (this.keySet = new KeySet());
        }
        
        @Override
        public Collection<V> values() {
            final Collection<V> vs = this.values;
            return (vs != null) ? vs : (this.values = new Values());
        }
        
        @Override
        public Set<Map.Entry<K, V>> entrySet() {
            final Set<Map.Entry<K, V>> es = this.entrySet;
            return (es != null) ? es : (this.entrySet = new EntrySet());
        }
        
        private void writeObject(final ObjectOutputStream out) throws IOException {
            out.writeInt(this.size());
            out.writeFloat(this.loadFactor);
            out.writeInt(this.segments.length);
            out.writeObject(this.strategy);
            for (final Map.Entry<K, V> entry : this.entrySet()) {
                out.writeObject(entry.getKey());
                out.writeObject(entry.getValue());
            }
            out.writeObject(null);
        }
        
        private void readObject(final ObjectInputStream in) throws IOException, ClassNotFoundException {
            try {
                int initialCapacity = in.readInt();
                final float loadFactor = in.readFloat();
                int concurrencyLevel = in.readInt();
                final Strategy<K, V, E> strategy = (Strategy<K, V, E>)in.readObject();
                Fields.loadFactor.set(this, loadFactor);
                if (concurrencyLevel > 65536) {
                    concurrencyLevel = 65536;
                }
                int segmentShift = 0;
                int segmentCount;
                for (segmentCount = 1; segmentCount < concurrencyLevel; segmentCount <<= 1) {
                    ++segmentShift;
                }
                Fields.segmentShift.set(this, 32 - segmentShift);
                Fields.segmentMask.set(this, segmentCount - 1);
                Fields.segments.set(this, this.newSegmentArray(segmentCount));
                if (initialCapacity > 1073741824) {
                    initialCapacity = 1073741824;
                }
                int segmentCapacity = initialCapacity / segmentCount;
                if (segmentCapacity * segmentCount < initialCapacity) {
                    ++segmentCapacity;
                }
                int segmentSize;
                for (segmentSize = 1; segmentSize < segmentCapacity; segmentSize <<= 1) {}
                for (int i = 0; i < this.segments.length; ++i) {
                    this.segments[i] = new Segment(segmentSize);
                }
                Fields.strategy.set(this, strategy);
                while (true) {
                    final K key = (K)in.readObject();
                    if (key == null) {
                        break;
                    }
                    final V value = (V)in.readObject();
                    this.put(key, value);
                }
            }
            catch (IllegalAccessException e) {
                throw new AssertionError((Object)e);
            }
        }
        
        class InternalsImpl implements Internals<K, V, E>, Serializable
        {
            static final long serialVersionUID = 0L;
            
            public E getEntry(final K key) {
                if (key == null) {
                    throw new NullPointerException("key");
                }
                final int hash = Impl.this.hash(key);
                return Impl.this.segmentFor(hash).getEntry(key, hash);
            }
            
            public boolean removeEntry(final E entry, final V value) {
                if (entry == null) {
                    throw new NullPointerException("entry");
                }
                final int hash = Impl.this.strategy.getHash(entry);
                return Impl.this.segmentFor(hash).removeEntry(entry, hash, value);
            }
            
            public boolean removeEntry(final E entry) {
                if (entry == null) {
                    throw new NullPointerException("entry");
                }
                final int hash = Impl.this.strategy.getHash(entry);
                return Impl.this.segmentFor(hash).removeEntry(entry, hash);
            }
        }
        
        final class Segment extends ReentrantLock
        {
            volatile int count;
            int modCount;
            int threshold;
            volatile AtomicReferenceArray<E> table;
            
            Segment(final int initialCapacity) {
                this.setTable(this.newEntryArray(initialCapacity));
            }
            
            AtomicReferenceArray<E> newEntryArray(final int size) {
                return new AtomicReferenceArray<E>(size);
            }
            
            void setTable(final AtomicReferenceArray<E> newTable) {
                this.threshold = (int)(newTable.length() * Impl.this.loadFactor);
                this.table = newTable;
            }
            
            E getFirst(final int hash) {
                final AtomicReferenceArray<E> table = this.table;
                return table.get(hash & table.length() - 1);
            }
            
            public E getEntry(final Object key, final int hash) {
                final Strategy<K, V, E> s = Impl.this.strategy;
                if (this.count != 0) {
                    for (E e = this.getFirst(hash); e != null; e = s.getNext(e)) {
                        if (s.getHash(e) == hash) {
                            final K entryKey = s.getKey(e);
                            if (entryKey != null) {
                                if (s.equalKeys(entryKey, key)) {
                                    return e;
                                }
                            }
                        }
                    }
                }
                return null;
            }
            
            V get(final Object key, final int hash) {
                final E entry = this.getEntry(key, hash);
                if (entry == null) {
                    return null;
                }
                return Impl.this.strategy.getValue(entry);
            }
            
            boolean containsKey(final Object key, final int hash) {
                final Strategy<K, V, E> s = Impl.this.strategy;
                if (this.count != 0) {
                    for (E e = this.getFirst(hash); e != null; e = s.getNext(e)) {
                        if (s.getHash(e) == hash) {
                            final K entryKey = s.getKey(e);
                            if (entryKey != null) {
                                if (s.equalKeys(entryKey, key)) {
                                    return s.getValue(e) != null;
                                }
                            }
                        }
                    }
                }
                return false;
            }
            
            boolean containsValue(final Object value) {
                final Strategy<K, V, E> s = Impl.this.strategy;
                if (this.count != 0) {
                    final AtomicReferenceArray<E> table = this.table;
                    for (int length = table.length(), i = 0; i < length; ++i) {
                        for (E e = table.get(i); e != null; e = s.getNext(e)) {
                            final V entryValue = s.getValue(e);
                            if (entryValue != null) {
                                if (s.equalValues(entryValue, value)) {
                                    return true;
                                }
                            }
                        }
                    }
                }
                return false;
            }
            
            boolean replace(final K key, final int hash, final V oldValue, final V newValue) {
                final Strategy<K, V, E> s = Impl.this.strategy;
                this.lock();
                try {
                    for (E e = this.getFirst(hash); e != null; e = s.getNext(e)) {
                        final K entryKey = s.getKey(e);
                        if (s.getHash(e) == hash && entryKey != null && s.equalKeys(key, entryKey)) {
                            final V entryValue = s.getValue(e);
                            if (entryValue == null) {
                                return false;
                            }
                            if (s.equalValues(entryValue, oldValue)) {
                                s.setValue(e, newValue);
                                return true;
                            }
                        }
                    }
                    return false;
                }
                finally {
                    this.unlock();
                }
            }
            
            V replace(final K key, final int hash, final V newValue) {
                final Strategy<K, V, E> s = Impl.this.strategy;
                this.lock();
                try {
                    E e = this.getFirst(hash);
                    while (e != null) {
                        final K entryKey = s.getKey(e);
                        if (s.getHash(e) == hash && entryKey != null && s.equalKeys(key, entryKey)) {
                            final V entryValue = s.getValue(e);
                            if (entryValue == null) {
                                return null;
                            }
                            s.setValue(e, newValue);
                            return entryValue;
                        }
                        else {
                            e = s.getNext(e);
                        }
                    }
                    return null;
                }
                finally {
                    this.unlock();
                }
            }
            
            V put(final K key, final int hash, final V value, final boolean onlyIfAbsent) {
                final Strategy<K, V, E> s = Impl.this.strategy;
                this.lock();
                try {
                    int count = this.count;
                    if (count++ > this.threshold) {
                        this.expand();
                    }
                    final AtomicReferenceArray<E> table = this.table;
                    final int index = hash & table.length() - 1;
                    E e;
                    final E first = e = table.get(index);
                    while (e != null) {
                        final K entryKey = s.getKey(e);
                        if (s.getHash(e) == hash && entryKey != null && s.equalKeys(key, entryKey)) {
                            final V entryValue = s.getValue(e);
                            if (onlyIfAbsent && entryValue != null) {
                                return entryValue;
                            }
                            s.setValue(e, value);
                            return entryValue;
                        }
                        else {
                            e = s.getNext(e);
                        }
                    }
                    ++this.modCount;
                    final E newEntry = s.newEntry(key, hash, first);
                    s.setValue(newEntry, value);
                    table.set(index, newEntry);
                    this.count = count;
                    return null;
                }
                finally {
                    this.unlock();
                }
            }
            
            void expand() {
                final AtomicReferenceArray<E> oldTable = this.table;
                final int oldCapacity = oldTable.length();
                if (oldCapacity >= 1073741824) {
                    return;
                }
                final Strategy<K, V, E> s = Impl.this.strategy;
                final AtomicReferenceArray<E> newTable = this.newEntryArray(oldCapacity << 1);
                this.threshold = (int)(newTable.length() * Impl.this.loadFactor);
                final int newMask = newTable.length() - 1;
                for (int oldIndex = 0; oldIndex < oldCapacity; ++oldIndex) {
                    final E head = oldTable.get(oldIndex);
                    if (head != null) {
                        final E next = s.getNext(head);
                        final int headIndex = s.getHash(head) & newMask;
                        if (next == null) {
                            newTable.set(headIndex, head);
                        }
                        else {
                            E tail = head;
                            int tailIndex = headIndex;
                            for (E last = next; last != null; last = s.getNext(last)) {
                                final int newIndex = s.getHash(last) & newMask;
                                if (newIndex != tailIndex) {
                                    tailIndex = newIndex;
                                    tail = last;
                                }
                            }
                            newTable.set(tailIndex, tail);
                            for (E e = head; e != tail; e = s.getNext(e)) {
                                final K key = s.getKey(e);
                                if (key != null) {
                                    final int newIndex2 = s.getHash(e) & newMask;
                                    final E newNext = newTable.get(newIndex2);
                                    newTable.set(newIndex2, s.copyEntry(key, e, newNext));
                                }
                            }
                        }
                    }
                }
                this.table = newTable;
            }
            
            V remove(final Object key, final int hash) {
                final Strategy<K, V, E> s = Impl.this.strategy;
                this.lock();
                try {
                    final int count = this.count - 1;
                    final AtomicReferenceArray<E> table = this.table;
                    final int index = hash & table.length() - 1;
                    E e;
                    for (E first = e = table.get(index); e != null; e = s.getNext(e)) {
                        final K entryKey = s.getKey(e);
                        if (s.getHash(e) == hash && entryKey != null && s.equalKeys(entryKey, key)) {
                            final V entryValue = Impl.this.strategy.getValue(e);
                            ++this.modCount;
                            E newFirst = s.getNext(e);
                            for (E p = first; p != e; p = s.getNext(p)) {
                                final K pKey = s.getKey(p);
                                if (pKey != null) {
                                    newFirst = s.copyEntry(pKey, p, newFirst);
                                }
                            }
                            table.set(index, newFirst);
                            this.count = count;
                            return entryValue;
                        }
                    }
                    return null;
                }
                finally {
                    this.unlock();
                }
            }
            
            boolean remove(final Object key, final int hash, final Object value) {
                final Strategy<K, V, E> s = Impl.this.strategy;
                this.lock();
                try {
                    final int count = this.count - 1;
                    final AtomicReferenceArray<E> table = this.table;
                    final int index = hash & table.length() - 1;
                    E e;
                    final E first = e = table.get(index);
                    while (e != null) {
                        final K entryKey = s.getKey(e);
                        if (s.getHash(e) == hash && entryKey != null && s.equalKeys(entryKey, key)) {
                            final V entryValue = Impl.this.strategy.getValue(e);
                            if (value == entryValue || (value != null && entryValue != null && s.equalValues(entryValue, value))) {
                                ++this.modCount;
                                E newFirst = s.getNext(e);
                                for (E p = first; p != e; p = s.getNext(p)) {
                                    final K pKey = s.getKey(p);
                                    if (pKey != null) {
                                        newFirst = s.copyEntry(pKey, p, newFirst);
                                    }
                                }
                                table.set(index, newFirst);
                                this.count = count;
                                return true;
                            }
                            return false;
                        }
                        else {
                            e = s.getNext(e);
                        }
                    }
                    return false;
                }
                finally {
                    this.unlock();
                }
            }
            
            public boolean removeEntry(final E entry, final int hash, final V value) {
                final Strategy<K, V, E> s = Impl.this.strategy;
                this.lock();
                try {
                    final int count = this.count - 1;
                    final AtomicReferenceArray<E> table = this.table;
                    final int index = hash & table.length() - 1;
                    E e;
                    final E first = e = table.get(index);
                    while (e != null) {
                        if (s.getHash(e) == hash && entry.equals(e)) {
                            final V entryValue = s.getValue(e);
                            if (entryValue == value || (value != null && s.equalValues(entryValue, value))) {
                                ++this.modCount;
                                E newFirst = s.getNext(e);
                                for (E p = first; p != e; p = s.getNext(p)) {
                                    final K pKey = s.getKey(p);
                                    if (pKey != null) {
                                        newFirst = s.copyEntry(pKey, p, newFirst);
                                    }
                                }
                                table.set(index, newFirst);
                                this.count = count;
                                return true;
                            }
                            return false;
                        }
                        else {
                            e = s.getNext(e);
                        }
                    }
                    return false;
                }
                finally {
                    this.unlock();
                }
            }
            
            public boolean removeEntry(final E entry, final int hash) {
                final Strategy<K, V, E> s = Impl.this.strategy;
                this.lock();
                try {
                    final int count = this.count - 1;
                    final AtomicReferenceArray<E> table = this.table;
                    final int index = hash & table.length() - 1;
                    E e;
                    for (E first = e = table.get(index); e != null; e = s.getNext(e)) {
                        if (s.getHash(e) == hash && entry.equals(e)) {
                            ++this.modCount;
                            E newFirst = s.getNext(e);
                            for (E p = first; p != e; p = s.getNext(p)) {
                                final K pKey = s.getKey(p);
                                if (pKey != null) {
                                    newFirst = s.copyEntry(pKey, p, newFirst);
                                }
                            }
                            table.set(index, newFirst);
                            this.count = count;
                            return true;
                        }
                    }
                    return false;
                }
                finally {
                    this.unlock();
                }
            }
            
            void clear() {
                if (this.count != 0) {
                    this.lock();
                    try {
                        final AtomicReferenceArray<E> table = this.table;
                        for (int i = 0; i < table.length(); ++i) {
                            table.set(i, null);
                        }
                        ++this.modCount;
                        this.count = 0;
                    }
                    finally {
                        this.unlock();
                    }
                }
            }
        }
        
        abstract class HashIterator
        {
            int nextSegmentIndex;
            int nextTableIndex;
            AtomicReferenceArray<E> currentTable;
            E nextEntry;
            WriteThroughEntry nextExternal;
            WriteThroughEntry lastReturned;
            
            HashIterator() {
                this.nextSegmentIndex = Impl.this.segments.length - 1;
                this.nextTableIndex = -1;
                this.advance();
            }
            
            public boolean hasMoreElements() {
                return this.hasNext();
            }
            
            final void advance() {
                this.nextExternal = null;
                if (this.nextInChain()) {
                    return;
                }
                if (this.nextInTable()) {
                    return;
                }
                while (this.nextSegmentIndex >= 0) {
                    final Segment seg = Impl.this.segments[this.nextSegmentIndex--];
                    if (seg.count != 0) {
                        this.currentTable = seg.table;
                        this.nextTableIndex = this.currentTable.length() - 1;
                        if (this.nextInTable()) {
                            return;
                        }
                        continue;
                    }
                }
            }
            
            boolean nextInChain() {
                final Strategy<K, V, E> s = Impl.this.strategy;
                if (this.nextEntry != null) {
                    this.nextEntry = s.getNext(this.nextEntry);
                    while (this.nextEntry != null) {
                        if (this.advanceTo(this.nextEntry)) {
                            return true;
                        }
                        this.nextEntry = s.getNext(this.nextEntry);
                    }
                }
                return false;
            }
            
            boolean nextInTable() {
                while (this.nextTableIndex >= 0) {
                    final E value = this.currentTable.get(this.nextTableIndex--);
                    this.nextEntry = value;
                    if (value != null && (this.advanceTo(this.nextEntry) || this.nextInChain())) {
                        return true;
                    }
                }
                return false;
            }
            
            boolean advanceTo(final E entry) {
                final Strategy<K, V, E> s = Impl.this.strategy;
                final K key = s.getKey(entry);
                final V value = s.getValue(entry);
                if (key != null && value != null) {
                    this.nextExternal = new WriteThroughEntry(key, value);
                    return true;
                }
                return false;
            }
            
            public boolean hasNext() {
                return this.nextExternal != null;
            }
            
            WriteThroughEntry nextEntry() {
                if (this.nextExternal == null) {
                    throw new NoSuchElementException();
                }
                this.lastReturned = this.nextExternal;
                this.advance();
                return this.lastReturned;
            }
            
            public void remove() {
                if (this.lastReturned == null) {
                    throw new IllegalStateException();
                }
                Impl.this.remove(this.lastReturned.getKey());
                this.lastReturned = null;
            }
        }
        
        final class KeyIterator extends HashIterator implements Iterator<K>
        {
            public K next() {
                return super.nextEntry().getKey();
            }
        }
        
        final class ValueIterator extends HashIterator implements Iterator<V>
        {
            public V next() {
                return super.nextEntry().getValue();
            }
        }
        
        final class WriteThroughEntry extends $AbstractMapEntry<K, V>
        {
            final K key;
            V value;
            
            WriteThroughEntry(final K key, final V value) {
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
            
            @Override
            public V setValue(final V value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                final V oldValue = Impl.this.put(this.getKey(), value);
                this.value = value;
                return oldValue;
            }
        }
        
        final class EntryIterator extends HashIterator implements Iterator<Map.Entry<K, V>>
        {
            public Map.Entry<K, V> next() {
                return this.nextEntry();
            }
        }
        
        final class KeySet extends AbstractSet<K>
        {
            @Override
            public Iterator<K> iterator() {
                return new KeyIterator();
            }
            
            @Override
            public int size() {
                return Impl.this.size();
            }
            
            @Override
            public boolean isEmpty() {
                return Impl.this.isEmpty();
            }
            
            @Override
            public boolean contains(final Object o) {
                return Impl.this.containsKey(o);
            }
            
            @Override
            public boolean remove(final Object o) {
                return Impl.this.remove(o) != null;
            }
            
            @Override
            public void clear() {
                Impl.this.clear();
            }
        }
        
        final class Values extends AbstractCollection<V>
        {
            @Override
            public Iterator<V> iterator() {
                return new ValueIterator();
            }
            
            @Override
            public int size() {
                return Impl.this.size();
            }
            
            @Override
            public boolean isEmpty() {
                return Impl.this.isEmpty();
            }
            
            @Override
            public boolean contains(final Object o) {
                return Impl.this.containsValue(o);
            }
            
            @Override
            public void clear() {
                Impl.this.clear();
            }
        }
        
        final class EntrySet extends AbstractSet<Map.Entry<K, V>>
        {
            @Override
            public Iterator<Map.Entry<K, V>> iterator() {
                return new EntryIterator();
            }
            
            @Override
            public boolean contains(final Object o) {
                if (!(o instanceof Map.Entry)) {
                    return false;
                }
                final Map.Entry<?, ?> e = (Map.Entry<?, ?>)o;
                final Object key = e.getKey();
                if (key == null) {
                    return false;
                }
                final V v = Impl.this.get(key);
                return v != null && Impl.this.strategy.equalValues(v, e.getValue());
            }
            
            @Override
            public boolean remove(final Object o) {
                if (!(o instanceof Map.Entry)) {
                    return false;
                }
                final Map.Entry<?, ?> e = (Map.Entry<?, ?>)o;
                final Object key = e.getKey();
                return key != null && Impl.this.remove(key, e.getValue());
            }
            
            @Override
            public int size() {
                return Impl.this.size();
            }
            
            @Override
            public boolean isEmpty() {
                return Impl.this.isEmpty();
            }
            
            @Override
            public void clear() {
                Impl.this.clear();
            }
        }
        
        static class Fields
        {
            static final Field loadFactor;
            static final Field segmentShift;
            static final Field segmentMask;
            static final Field segments;
            static final Field strategy;
            
            static Field findField(final String name) {
                try {
                    final Field f = Impl.class.getDeclaredField(name);
                    f.setAccessible(true);
                    return f;
                }
                catch (NoSuchFieldException e) {
                    throw new AssertionError((Object)e);
                }
            }
            
            static {
                loadFactor = findField("loadFactor");
                segmentShift = findField("segmentShift");
                segmentMask = findField("segmentMask");
                segments = findField("segments");
                strategy = findField("strategy");
            }
        }
    }
    
    static class ComputingImpl<K, V, E> extends Impl<K, V, E>
    {
        static final long serialVersionUID = 0L;
        final ComputingStrategy<K, V, E> computingStrategy;
        final $Function<? super K, ? extends V> computer;
        
        ComputingImpl(final ComputingStrategy<K, V, E> strategy, final Builder builder, final $Function<? super K, ? extends V> computer) {
            super(strategy, builder);
            this.computingStrategy = strategy;
            this.computer = computer;
        }
        
        @Override
        public V get(final Object k) {
            final K key = (K)k;
            if (key == null) {
                throw new NullPointerException("key");
            }
            final int hash = this.hash(key);
            final Segment segment = this.segmentFor(hash);
            while (true) {
                E entry = segment.getEntry(key, hash);
                if (entry == null) {
                    boolean created = false;
                    segment.lock();
                    try {
                        entry = segment.getEntry(key, hash);
                        if (entry == null) {
                            created = true;
                            int count = segment.count;
                            if (count++ > segment.threshold) {
                                segment.expand();
                            }
                            final AtomicReferenceArray<E> table = (AtomicReferenceArray<E>)segment.table;
                            final int index = hash & table.length() - 1;
                            final E first = table.get(index);
                            final Segment segment2 = segment;
                            ++segment2.modCount;
                            entry = this.computingStrategy.newEntry(key, hash, first);
                            table.set(index, entry);
                            segment.count = count;
                        }
                    }
                    finally {
                        segment.unlock();
                    }
                    if (created) {
                        boolean success = false;
                        try {
                            final V value = this.computingStrategy.compute(key, entry, this.computer);
                            if (value == null) {
                                throw new NullPointerException("compute() returned null unexpectedly");
                            }
                            success = true;
                            return value;
                        }
                        finally {
                            if (!success) {
                                segment.removeEntry(entry, hash);
                            }
                        }
                    }
                }
                boolean interrupted = false;
                while (true) {
                    try {
                        final V value2 = this.computingStrategy.waitForValue(entry);
                        if (value2 != null) {
                            return value2;
                        }
                        segment.removeEntry(entry, hash);
                    }
                    catch (InterruptedException e) {
                        interrupted = true;
                        continue;
                    }
                    finally {
                        if (interrupted) {
                            Thread.currentThread().interrupt();
                        }
                    }
                    break;
                }
            }
        }
    }
    
    static class SimpleStrategy<K, V> implements Strategy<K, V, SimpleInternalEntry<K, V>>
    {
        public SimpleInternalEntry<K, V> newEntry(final K key, final int hash, final SimpleInternalEntry<K, V> next) {
            return new SimpleInternalEntry<K, V>(key, hash, null, next);
        }
        
        public SimpleInternalEntry<K, V> copyEntry(final K key, final SimpleInternalEntry<K, V> original, final SimpleInternalEntry<K, V> next) {
            return new SimpleInternalEntry<K, V>(key, original.hash, original.value, next);
        }
        
        public void setValue(final SimpleInternalEntry<K, V> entry, final V value) {
            entry.value = value;
        }
        
        public V getValue(final SimpleInternalEntry<K, V> entry) {
            return entry.value;
        }
        
        public boolean equalKeys(final K a, final Object b) {
            return a.equals(b);
        }
        
        public boolean equalValues(final V a, final Object b) {
            return a.equals(b);
        }
        
        public int hashKey(final Object key) {
            return key.hashCode();
        }
        
        public K getKey(final SimpleInternalEntry<K, V> entry) {
            return entry.key;
        }
        
        public SimpleInternalEntry<K, V> getNext(final SimpleInternalEntry<K, V> entry) {
            return entry.next;
        }
        
        public int getHash(final SimpleInternalEntry<K, V> entry) {
            return entry.hash;
        }
        
        public void setInternals(final Internals<K, V, SimpleInternalEntry<K, V>> internals) {
        }
    }
    
    static class SimpleInternalEntry<K, V>
    {
        final K key;
        final int hash;
        final SimpleInternalEntry<K, V> next;
        volatile V value;
        
        SimpleInternalEntry(final K key, final int hash, @$Nullable final V value, final SimpleInternalEntry<K, V> next) {
            this.key = key;
            this.hash = hash;
            this.value = value;
            this.next = next;
        }
    }
    
    public interface Internals<K, V, E>
    {
        E getEntry(final K p0);
        
        boolean removeEntry(final E p0, @$Nullable final V p1);
        
        boolean removeEntry(final E p0);
    }
    
    public interface Strategy<K, V, E>
    {
        E newEntry(final K p0, final int p1, final E p2);
        
        E copyEntry(final K p0, final E p1, final E p2);
        
        void setValue(final E p0, final V p1);
        
        V getValue(final E p0);
        
        boolean equalKeys(final K p0, final Object p1);
        
        boolean equalValues(final V p0, final Object p1);
        
        int hashKey(final Object p0);
        
        K getKey(final E p0);
        
        E getNext(final E p0);
        
        int getHash(final E p0);
        
        void setInternals(final Internals<K, V, E> p0);
    }
    
    public interface ComputingStrategy<K, V, E> extends Strategy<K, V, E>
    {
        V compute(final K p0, final E p1, final $Function<? super K, ? extends V> p2);
        
        V waitForValue(final E p0) throws InterruptedException;
    }
}
