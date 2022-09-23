// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.util.internal;

import java.util.AbstractCollection;
import java.util.AbstractSet;
import java.util.NoSuchElementException;
import java.util.Arrays;
import java.util.concurrent.locks.ReentrantLock;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;
import java.util.AbstractMap;

public final class ConcurrentIdentityHashMap<K, V> extends AbstractMap<K, V> implements ConcurrentMap<K, V>
{
    static final int DEFAULT_INITIAL_CAPACITY = 16;
    static final float DEFAULT_LOAD_FACTOR = 0.75f;
    static final int DEFAULT_CONCURRENCY_LEVEL = 16;
    static final int MAXIMUM_CAPACITY = 1073741824;
    static final int MAX_SEGMENTS = 65536;
    static final int RETRIES_BEFORE_LOCK = 2;
    final int segmentMask;
    final int segmentShift;
    final Segment<K, V>[] segments;
    Set<K> keySet;
    Set<Map.Entry<K, V>> entrySet;
    Collection<V> values;
    
    private static int hash(int h) {
        h += (h << 15 ^ 0xFFFFCD7D);
        h ^= h >>> 10;
        h += h << 3;
        h ^= h >>> 6;
        h += (h << 2) + (h << 14);
        return h ^ h >>> 16;
    }
    
    Segment<K, V> segmentFor(final int hash) {
        return this.segments[hash >>> this.segmentShift & this.segmentMask];
    }
    
    private static int hashOf(final Object key) {
        return hash(System.identityHashCode(key));
    }
    
    public ConcurrentIdentityHashMap(int initialCapacity, final float loadFactor, int concurrencyLevel) {
        if (loadFactor <= 0.0f || initialCapacity < 0 || concurrencyLevel <= 0) {
            throw new IllegalArgumentException();
        }
        if (concurrencyLevel > 65536) {
            concurrencyLevel = 65536;
        }
        int sshift = 0;
        int ssize;
        for (ssize = 1; ssize < concurrencyLevel; ssize <<= 1) {
            ++sshift;
        }
        this.segmentShift = 32 - sshift;
        this.segmentMask = ssize - 1;
        this.segments = Segment.newArray(ssize);
        if (initialCapacity > 1073741824) {
            initialCapacity = 1073741824;
        }
        int c = initialCapacity / ssize;
        if (c * ssize < initialCapacity) {
            ++c;
        }
        int cap;
        for (cap = 1; cap < c; cap <<= 1) {}
        for (int i = 0; i < this.segments.length; ++i) {
            this.segments[i] = new Segment<K, V>(cap, loadFactor);
        }
    }
    
    public ConcurrentIdentityHashMap(final int initialCapacity, final float loadFactor) {
        this(initialCapacity, loadFactor, 16);
    }
    
    public ConcurrentIdentityHashMap(final int initialCapacity) {
        this(initialCapacity, 0.75f, 16);
    }
    
    public ConcurrentIdentityHashMap() {
        this(16, 0.75f, 16);
    }
    
    public ConcurrentIdentityHashMap(final Map<? extends K, ? extends V> m) {
        this(Math.max((int)(m.size() / 0.75f) + 1, 16), 0.75f, 16);
        this.putAll(m);
    }
    
    @Override
    public boolean isEmpty() {
        final Segment<K, V>[] segments = this.segments;
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
        final Segment<K, V>[] segments = this.segments;
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
            for (final Segment<K, V> segment : segments) {
                segment.lock();
            }
            for (final Segment<K, V> segment : segments) {
                sum += segment.count;
            }
            for (final Segment<K, V> segment : segments) {
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
        final int hash = hashOf(key);
        return this.segmentFor(hash).get(key, hash);
    }
    
    @Override
    public boolean containsKey(final Object key) {
        final int hash = hashOf(key);
        return this.segmentFor(hash).containsKey(key, hash);
    }
    
    @Override
    public boolean containsValue(final Object value) {
        if (value == null) {
            throw new NullPointerException();
        }
        final Segment<K, V>[] segments = this.segments;
        final int[] mc = new int[segments.length];
        for (int k = 0; k < 2; ++k) {
            int mcsum = 0;
            for (int i = 0; i < segments.length; ++i) {
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
        for (final Segment<K, V> segment : segments) {
            segment.lock();
        }
        boolean found = false;
        try {
            for (final Segment<K, V> segment2 : segments) {
                if (segment2.containsValue(value)) {
                    found = true;
                    break;
                }
            }
        }
        finally {
            for (final Segment<K, V> segment3 : segments) {
                segment3.unlock();
            }
        }
        return found;
    }
    
    public boolean contains(final Object value) {
        return this.containsValue(value);
    }
    
    @Override
    public V put(final K key, final V value) {
        if (value == null) {
            throw new NullPointerException();
        }
        final int hash = hashOf(key);
        return this.segmentFor(hash).put(key, hash, value, false);
    }
    
    public V putIfAbsent(final K key, final V value) {
        if (value == null) {
            throw new NullPointerException();
        }
        final int hash = hashOf(key);
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
        final int hash = hashOf(key);
        return this.segmentFor(hash).remove(key, hash, null, false);
    }
    
    public boolean remove(final Object key, final Object value) {
        final int hash = hashOf(key);
        return value != null && this.segmentFor(hash).remove(key, hash, value, false) != null;
    }
    
    public boolean replace(final K key, final V oldValue, final V newValue) {
        if (oldValue == null || newValue == null) {
            throw new NullPointerException();
        }
        final int hash = hashOf(key);
        return this.segmentFor(hash).replace(key, hash, oldValue, newValue);
    }
    
    public V replace(final K key, final V value) {
        if (value == null) {
            throw new NullPointerException();
        }
        final int hash = hashOf(key);
        return this.segmentFor(hash).replace(key, hash, value);
    }
    
    @Override
    public void clear() {
        for (final Segment<K, V> segment : this.segments) {
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
    
    public Enumeration<K> keys() {
        return new KeyIterator();
    }
    
    public Enumeration<V> elements() {
        return new ValueIterator();
    }
    
    static final class HashEntry<K, V>
    {
        final Object key;
        final int hash;
        volatile Object value;
        final HashEntry<K, V> next;
        
        HashEntry(final K key, final int hash, final HashEntry<K, V> next, final V value) {
            this.hash = hash;
            this.next = next;
            this.key = key;
            this.value = value;
        }
        
        K key() {
            return (K)this.key;
        }
        
        V value() {
            return (V)this.value;
        }
        
        void setValue(final V value) {
            this.value = value;
        }
        
        static <K, V> HashEntry<K, V>[] newArray(final int i) {
            return (HashEntry<K, V>[])new HashEntry[i];
        }
    }
    
    static final class Segment<K, V> extends ReentrantLock
    {
        private static final long serialVersionUID = 5207829234977119743L;
        transient volatile int count;
        int modCount;
        int threshold;
        transient volatile HashEntry<K, V>[] table;
        final float loadFactor;
        
        Segment(final int initialCapacity, final float lf) {
            this.loadFactor = lf;
            this.setTable(HashEntry.newArray(initialCapacity));
        }
        
        static <K, V> Segment<K, V>[] newArray(final int i) {
            return (Segment<K, V>[])new Segment[i];
        }
        
        private static boolean keyEq(final Object src, final Object dest) {
            return src == dest;
        }
        
        void setTable(final HashEntry<K, V>[] newTable) {
            this.threshold = (int)(newTable.length * this.loadFactor);
            this.table = newTable;
        }
        
        HashEntry<K, V> getFirst(final int hash) {
            final HashEntry<K, V>[] tab = this.table;
            return tab[hash & tab.length - 1];
        }
        
        HashEntry<K, V> newHashEntry(final K key, final int hash, final HashEntry<K, V> next, final V value) {
            return new HashEntry<K, V>(key, hash, next, value);
        }
        
        V readValueUnderLock(final HashEntry<K, V> e) {
            this.lock();
            try {
                return e.value();
            }
            finally {
                this.unlock();
            }
        }
        
        V get(final Object key, final int hash) {
            if (this.count != 0) {
                final HashEntry<K, V>[] tab = this.table;
                HashEntry<K, V> e = tab[hash & tab.length - 1];
                if (tab != this.table) {
                    return this.get(key, hash);
                }
                while (e != null) {
                    if (e.hash == hash && keyEq(key, e.key())) {
                        final V opaque = e.value();
                        if (opaque != null) {
                            return opaque;
                        }
                        return this.readValueUnderLock(e);
                    }
                    else {
                        e = e.next;
                    }
                }
            }
            return null;
        }
        
        boolean containsKey(final Object key, final int hash) {
            if (this.count != 0) {
                final HashEntry<K, V>[] tab = this.table;
                HashEntry<K, V> e = tab[hash & tab.length - 1];
                if (tab != this.table) {
                    return this.containsKey(key, hash);
                }
                while (e != null) {
                    if (e.hash == hash && keyEq(key, e.key())) {
                        return true;
                    }
                    e = e.next;
                }
            }
            return false;
        }
        
        boolean containsValue(final Object value) {
            if (this.count != 0) {
                final HashEntry[] arr$;
                final HashEntry<K, V>[] tab = (HashEntry<K, V>[])(arr$ = this.table);
                for (HashEntry<K, V> e : arr$) {
                    while (e != null) {
                        final V opaque = e.value();
                        V v;
                        if (opaque == null) {
                            v = this.readValueUnderLock(e);
                        }
                        else {
                            v = opaque;
                        }
                        if (value.equals(v)) {
                            return true;
                        }
                        e = e.next;
                    }
                }
                if (this.table != tab) {
                    return this.containsValue(value);
                }
            }
            return false;
        }
        
        boolean replace(final K key, final int hash, final V oldValue, final V newValue) {
            this.lock();
            try {
                HashEntry<K, V> e;
                for (e = this.getFirst(hash); e != null && (e.hash != hash || !keyEq(key, e.key())); e = e.next) {}
                boolean replaced = false;
                if (e != null && oldValue.equals(e.value())) {
                    replaced = true;
                    e.setValue(newValue);
                }
                return replaced;
            }
            finally {
                this.unlock();
            }
        }
        
        V replace(final K key, final int hash, final V newValue) {
            this.lock();
            try {
                HashEntry<K, V> e;
                for (e = this.getFirst(hash); e != null && (e.hash != hash || !keyEq(key, e.key())); e = e.next) {}
                V oldValue = null;
                if (e != null) {
                    oldValue = e.value();
                    e.setValue(newValue);
                }
                return oldValue;
            }
            finally {
                this.unlock();
            }
        }
        
        V put(final K key, final int hash, final V value, final boolean onlyIfAbsent) {
            this.lock();
            try {
                int c = this.count;
                if (c++ > this.threshold) {
                    final int reduced = this.rehash();
                    if (reduced > 0) {
                        this.count = (c -= reduced) - 1;
                    }
                }
                final HashEntry<K, V>[] tab = this.table;
                final int index = hash & tab.length - 1;
                HashEntry<K, V> e;
                HashEntry<K, V> first;
                for (first = (e = tab[index]); e != null && (e.hash != hash || !keyEq(key, e.key())); e = e.next) {}
                V oldValue;
                if (e != null) {
                    oldValue = e.value();
                    if (!onlyIfAbsent) {
                        e.setValue(value);
                    }
                }
                else {
                    oldValue = null;
                    ++this.modCount;
                    tab[index] = this.newHashEntry(key, hash, first, value);
                    this.count = c;
                }
                return oldValue;
            }
            finally {
                this.unlock();
            }
        }
        
        int rehash() {
            final HashEntry<K, V>[] oldTable = this.table;
            final int oldCapacity = oldTable.length;
            if (oldCapacity >= 1073741824) {
                return 0;
            }
            final HashEntry<K, V>[] newTable = HashEntry.newArray(oldCapacity << 1);
            this.threshold = (int)(newTable.length * this.loadFactor);
            final int sizeMask = newTable.length - 1;
            int reduce = 0;
            for (final HashEntry<K, V> e : oldTable) {
                if (e != null) {
                    final HashEntry<K, V> next = e.next;
                    final int idx = e.hash & sizeMask;
                    if (next == null) {
                        newTable[idx] = e;
                    }
                    else {
                        HashEntry<K, V> lastRun = e;
                        int lastIdx = idx;
                        for (HashEntry<K, V> last = next; last != null; last = last.next) {
                            final int k = last.hash & sizeMask;
                            if (k != lastIdx) {
                                lastIdx = k;
                                lastRun = last;
                            }
                        }
                        newTable[lastIdx] = lastRun;
                        for (HashEntry<K, V> p = e; p != lastRun; p = p.next) {
                            final K key = p.key();
                            if (key == null) {
                                ++reduce;
                            }
                            else {
                                final int i = p.hash & sizeMask;
                                final HashEntry<K, V> n = newTable[i];
                                newTable[i] = this.newHashEntry(key, p.hash, n, p.value());
                            }
                        }
                    }
                }
            }
            this.table = newTable;
            Arrays.fill(oldTable, null);
            return reduce;
        }
        
        V remove(final Object key, final int hash, final Object value, final boolean refRemove) {
            this.lock();
            try {
                int c = this.count - 1;
                final HashEntry<K, V>[] tab = this.table;
                final int index = hash & tab.length - 1;
                HashEntry<K, V> e;
                HashEntry<K, V> first;
                for (first = (e = tab[index]); e != null && key != e.key && (refRemove || hash != e.hash || !keyEq(key, e.key())); e = e.next) {}
                V oldValue = null;
                if (e != null) {
                    final V v = e.value();
                    if (value == null || value.equals(v)) {
                        oldValue = v;
                        ++this.modCount;
                        HashEntry<K, V> newFirst = e.next;
                        for (HashEntry<K, V> p = first; p != e; p = p.next) {
                            final K pKey = p.key();
                            if (pKey == null) {
                                --c;
                            }
                            else {
                                newFirst = this.newHashEntry(pKey, p.hash, newFirst, p.value());
                            }
                        }
                        tab[index] = newFirst;
                        this.count = c;
                    }
                }
                return oldValue;
            }
            finally {
                this.unlock();
            }
        }
        
        void clear() {
            if (this.count != 0) {
                this.lock();
                try {
                    final HashEntry<K, V>[] tab = this.table;
                    for (int i = 0; i < tab.length; ++i) {
                        tab[i] = null;
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
        HashEntry<K, V>[] currentTable;
        HashEntry<K, V> nextEntry;
        HashEntry<K, V> lastReturned;
        K currentKey;
        
        HashIterator() {
            this.nextSegmentIndex = ConcurrentIdentityHashMap.this.segments.length - 1;
            this.nextTableIndex = -1;
            this.advance();
        }
        
        public void rewind() {
            this.nextSegmentIndex = ConcurrentIdentityHashMap.this.segments.length - 1;
            this.nextTableIndex = -1;
            this.currentTable = null;
            this.nextEntry = null;
            this.lastReturned = null;
            this.currentKey = null;
            this.advance();
        }
        
        public boolean hasMoreElements() {
            return this.hasNext();
        }
        
        final void advance() {
            if (this.nextEntry != null && (this.nextEntry = this.nextEntry.next) != null) {
                return;
            }
            while (this.nextTableIndex >= 0) {
                if ((this.nextEntry = this.currentTable[this.nextTableIndex--]) != null) {
                    return;
                }
            }
            while (this.nextSegmentIndex >= 0) {
                final Segment<K, V> seg = ConcurrentIdentityHashMap.this.segments[this.nextSegmentIndex--];
                if (seg.count != 0) {
                    this.currentTable = seg.table;
                    for (int j = this.currentTable.length - 1; j >= 0; --j) {
                        if ((this.nextEntry = this.currentTable[j]) != null) {
                            this.nextTableIndex = j - 1;
                            return;
                        }
                    }
                }
            }
        }
        
        public boolean hasNext() {
            while (this.nextEntry != null) {
                if (this.nextEntry.key() != null) {
                    return true;
                }
                this.advance();
            }
            return false;
        }
        
        HashEntry<K, V> nextEntry() {
            while (this.nextEntry != null) {
                this.lastReturned = this.nextEntry;
                this.currentKey = this.lastReturned.key();
                this.advance();
                if (this.currentKey != null) {
                    return this.lastReturned;
                }
            }
            throw new NoSuchElementException();
        }
        
        public void remove() {
            if (this.lastReturned == null) {
                throw new IllegalStateException();
            }
            ConcurrentIdentityHashMap.this.remove(this.currentKey);
            this.lastReturned = null;
        }
    }
    
    final class KeyIterator extends HashIterator implements ReusableIterator<K>, Enumeration<K>
    {
        public K next() {
            return this.nextEntry().key();
        }
        
        public K nextElement() {
            return this.nextEntry().key();
        }
    }
    
    final class ValueIterator extends HashIterator implements ReusableIterator<V>, Enumeration<V>
    {
        public V next() {
            return this.nextEntry().value();
        }
        
        public V nextElement() {
            return this.nextEntry().value();
        }
    }
    
    static class SimpleEntry<K, V> implements Map.Entry<K, V>
    {
        private final K key;
        private V value;
        
        public SimpleEntry(final K key, final V value) {
            this.key = key;
            this.value = value;
        }
        
        public SimpleEntry(final Map.Entry<? extends K, ? extends V> entry) {
            this.key = (K)entry.getKey();
            this.value = (V)entry.getValue();
        }
        
        public K getKey() {
            return this.key;
        }
        
        public V getValue() {
            return this.value;
        }
        
        public V setValue(final V value) {
            final V oldValue = this.value;
            this.value = value;
            return oldValue;
        }
        
        @Override
        public boolean equals(final Object o) {
            if (!(o instanceof Map.Entry)) {
                return false;
            }
            final Map.Entry e = (Map.Entry)o;
            return eq(this.key, e.getKey()) && eq(this.value, e.getValue());
        }
        
        @Override
        public int hashCode() {
            return ((this.key == null) ? 0 : this.key.hashCode()) ^ ((this.value == null) ? 0 : this.value.hashCode());
        }
        
        @Override
        public String toString() {
            return this.key + "=" + this.value;
        }
        
        private static boolean eq(final Object o1, final Object o2) {
            return (o1 == null) ? (o2 == null) : o1.equals(o2);
        }
    }
    
    final class WriteThroughEntry extends SimpleEntry<K, V>
    {
        WriteThroughEntry(final K k, final V v) {
            super(k, v);
        }
        
        @Override
        public V setValue(final V value) {
            if (value == null) {
                throw new NullPointerException();
            }
            final V v = super.setValue(value);
            ConcurrentIdentityHashMap.this.put(((SimpleEntry<K, V>)this).getKey(), value);
            return v;
        }
    }
    
    final class EntryIterator extends HashIterator implements ReusableIterator<Map.Entry<K, V>>
    {
        public Map.Entry<K, V> next() {
            final HashEntry<K, V> e = this.nextEntry();
            return new WriteThroughEntry(e.key(), e.value());
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
            return ConcurrentIdentityHashMap.this.size();
        }
        
        @Override
        public boolean isEmpty() {
            return ConcurrentIdentityHashMap.this.isEmpty();
        }
        
        @Override
        public boolean contains(final Object o) {
            return ConcurrentIdentityHashMap.this.containsKey(o);
        }
        
        @Override
        public boolean remove(final Object o) {
            return ConcurrentIdentityHashMap.this.remove(o) != null;
        }
        
        @Override
        public void clear() {
            ConcurrentIdentityHashMap.this.clear();
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
            return ConcurrentIdentityHashMap.this.size();
        }
        
        @Override
        public boolean isEmpty() {
            return ConcurrentIdentityHashMap.this.isEmpty();
        }
        
        @Override
        public boolean contains(final Object o) {
            return ConcurrentIdentityHashMap.this.containsValue(o);
        }
        
        @Override
        public void clear() {
            ConcurrentIdentityHashMap.this.clear();
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
            final V v = ConcurrentIdentityHashMap.this.get(e.getKey());
            return v != null && v.equals(e.getValue());
        }
        
        @Override
        public boolean remove(final Object o) {
            if (!(o instanceof Map.Entry)) {
                return false;
            }
            final Map.Entry<?, ?> e = (Map.Entry<?, ?>)o;
            return ConcurrentIdentityHashMap.this.remove(e.getKey(), e.getValue());
        }
        
        @Override
        public int size() {
            return ConcurrentIdentityHashMap.this.size();
        }
        
        @Override
        public boolean isEmpty() {
            return ConcurrentIdentityHashMap.this.isEmpty();
        }
        
        @Override
        public void clear() {
            ConcurrentIdentityHashMap.this.clear();
        }
    }
}
