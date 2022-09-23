// 
// Decompiled by Procyon v0.5.36
// 

package parquet.it.unimi.dsi.fastutil.objects;

import parquet.it.unimi.dsi.fastutil.ints.IntListIterator;
import java.util.SortedSet;
import java.util.SortedMap;
import java.util.Set;
import java.util.Collection;
import java.io.ObjectInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Iterator;
import parquet.it.unimi.dsi.fastutil.ints.IntIterator;
import parquet.it.unimi.dsi.fastutil.ints.AbstractIntCollection;
import java.util.Comparator;
import parquet.it.unimi.dsi.fastutil.booleans.BooleanArrays;
import java.util.NoSuchElementException;
import java.util.Map;
import parquet.it.unimi.dsi.fastutil.HashCommon;
import parquet.it.unimi.dsi.fastutil.ints.IntCollection;
import parquet.it.unimi.dsi.fastutil.Hash;
import java.io.Serializable;

public class Object2IntLinkedOpenHashMap<K> extends AbstractObject2IntSortedMap<K> implements Serializable, Cloneable, Hash
{
    private static final long serialVersionUID = 0L;
    private static final boolean ASSERTS = false;
    protected transient K[] key;
    protected transient int[] value;
    protected transient boolean[] used;
    protected final float f;
    protected transient int n;
    protected transient int maxFill;
    protected transient int mask;
    protected int size;
    protected transient volatile Object2IntSortedMap.FastSortedEntrySet<K> entries;
    protected transient volatile ObjectSortedSet<K> keys;
    protected transient volatile IntCollection values;
    protected transient int first;
    protected transient int last;
    protected transient long[] link;
    
    public Object2IntLinkedOpenHashMap(final int expected, final float f) {
        this.first = -1;
        this.last = -1;
        if (f <= 0.0f || f > 1.0f) {
            throw new IllegalArgumentException("Load factor must be greater than 0 and smaller than or equal to 1");
        }
        if (expected < 0) {
            throw new IllegalArgumentException("The expected number of elements must be nonnegative");
        }
        this.f = f;
        this.n = HashCommon.arraySize(expected, f);
        this.mask = this.n - 1;
        this.maxFill = HashCommon.maxFill(this.n, f);
        this.key = (K[])new Object[this.n];
        this.value = new int[this.n];
        this.used = new boolean[this.n];
        this.link = new long[this.n];
    }
    
    public Object2IntLinkedOpenHashMap(final int expected) {
        this(expected, 0.75f);
    }
    
    public Object2IntLinkedOpenHashMap() {
        this(16, 0.75f);
    }
    
    public Object2IntLinkedOpenHashMap(final Map<? extends K, ? extends Integer> m, final float f) {
        this(m.size(), f);
        this.putAll(m);
    }
    
    public Object2IntLinkedOpenHashMap(final Map<? extends K, ? extends Integer> m) {
        this(m, 0.75f);
    }
    
    public Object2IntLinkedOpenHashMap(final Object2IntMap<K> m, final float f) {
        this(m.size(), f);
        this.putAll((Map<? extends K, ? extends Integer>)m);
    }
    
    public Object2IntLinkedOpenHashMap(final Object2IntMap<K> m) {
        this(m, 0.75f);
    }
    
    public Object2IntLinkedOpenHashMap(final K[] k, final int[] v, final float f) {
        this(k.length, f);
        if (k.length != v.length) {
            throw new IllegalArgumentException("The key array and the value array have different lengths (" + k.length + " and " + v.length + ")");
        }
        for (int i = 0; i < k.length; ++i) {
            this.put(k[i], v[i]);
        }
    }
    
    public Object2IntLinkedOpenHashMap(final K[] k, final int[] v) {
        this(k, v, 0.75f);
    }
    
    @Override
    public int put(final K k, final int v) {
        int pos = ((k == null) ? 142593372 : HashCommon.murmurHash3(k.hashCode() ^ this.mask)) & this.mask;
        while (this.used[pos]) {
            Label_0065: {
                if (this.key[pos] == null) {
                    if (k == null) {
                        break Label_0065;
                    }
                }
                else if (this.key[pos].equals(k)) {
                    break Label_0065;
                }
                pos = (pos + 1 & this.mask);
                continue;
            }
            final int oldValue = this.value[pos];
            this.value[pos] = v;
            return oldValue;
        }
        this.used[pos] = true;
        this.key[pos] = k;
        this.value[pos] = v;
        if (this.size == 0) {
            final int n = pos;
            this.last = n;
            this.first = n;
            this.link[pos] = -1L;
        }
        else {
            final long[] link = this.link;
            final int last = this.last;
            link[last] ^= ((this.link[this.last] ^ ((long)pos & 0xFFFFFFFFL)) & 0xFFFFFFFFL);
            this.link[pos] = (((long)this.last & 0xFFFFFFFFL) << 32 | 0xFFFFFFFFL);
            this.last = pos;
        }
        if (++this.size >= this.maxFill) {
            this.rehash(HashCommon.arraySize(this.size + 1, this.f));
        }
        return this.defRetValue;
    }
    
    @Override
    public Integer put(final K ok, final Integer ov) {
        final int v = ov;
        final K k = ok;
        int pos = ((k == null) ? 142593372 : HashCommon.murmurHash3(k.hashCode() ^ this.mask)) & this.mask;
        while (this.used[pos]) {
            Label_0081: {
                if (this.key[pos] == null) {
                    if (k == null) {
                        break Label_0081;
                    }
                }
                else if (this.key[pos].equals(k)) {
                    break Label_0081;
                }
                pos = (pos + 1 & this.mask);
                continue;
            }
            final Integer oldValue = this.value[pos];
            this.value[pos] = v;
            return oldValue;
        }
        this.used[pos] = true;
        this.key[pos] = k;
        this.value[pos] = v;
        if (this.size == 0) {
            final int n = pos;
            this.last = n;
            this.first = n;
            this.link[pos] = -1L;
        }
        else {
            final long[] link = this.link;
            final int last = this.last;
            link[last] ^= ((this.link[this.last] ^ ((long)pos & 0xFFFFFFFFL)) & 0xFFFFFFFFL);
            this.link[pos] = (((long)this.last & 0xFFFFFFFFL) << 32 | 0xFFFFFFFFL);
            this.last = pos;
        }
        if (++this.size >= this.maxFill) {
            this.rehash(HashCommon.arraySize(this.size + 1, this.f));
        }
        return null;
    }
    
    @Deprecated
    public int add(final K k, final int incr) {
        return this.addTo(k, incr);
    }
    
    public int addTo(final K k, final int incr) {
        int pos = ((k == null) ? 142593372 : HashCommon.murmurHash3(k.hashCode() ^ this.mask)) & this.mask;
        while (this.used[pos]) {
            Label_0065: {
                if (this.key[pos] == null) {
                    if (k == null) {
                        break Label_0065;
                    }
                }
                else if (this.key[pos].equals(k)) {
                    break Label_0065;
                }
                pos = (pos + 1 & this.mask);
                continue;
            }
            final int oldValue = this.value[pos];
            final int[] value = this.value;
            final int n = pos;
            value[n] += incr;
            return oldValue;
        }
        this.used[pos] = true;
        this.key[pos] = k;
        this.value[pos] = this.defRetValue + incr;
        if (this.size == 0) {
            final int n2 = pos;
            this.last = n2;
            this.first = n2;
            this.link[pos] = -1L;
        }
        else {
            final long[] link = this.link;
            final int last = this.last;
            link[last] ^= ((this.link[this.last] ^ ((long)pos & 0xFFFFFFFFL)) & 0xFFFFFFFFL);
            this.link[pos] = (((long)this.last & 0xFFFFFFFFL) << 32 | 0xFFFFFFFFL);
            this.last = pos;
        }
        if (++this.size >= this.maxFill) {
            this.rehash(HashCommon.arraySize(this.size + 1, this.f));
        }
        return this.defRetValue;
    }
    
    protected final int shiftKeys(int pos) {
        int last;
        while (true) {
            int slot;
            for (pos = ((last = pos) + 1 & this.mask); this.used[pos]; pos = (pos + 1 & this.mask)) {
                slot = (((this.key[pos] == null) ? 142593372 : HashCommon.murmurHash3(this.key[pos].hashCode() ^ this.mask)) & this.mask);
                if (last <= pos) {
                    if (last >= slot) {
                        break;
                    }
                    if (slot > pos) {
                        break;
                    }
                }
                else if (last >= slot && slot > pos) {
                    break;
                }
            }
            if (!this.used[pos]) {
                break;
            }
            this.key[last] = this.key[pos];
            this.value[last] = this.value[pos];
            this.fixPointers(pos, last);
        }
        this.used[last] = false;
        this.key[last] = null;
        return last;
    }
    
    @Override
    public int removeInt(final Object k) {
        int pos = ((k == null) ? 142593372 : HashCommon.murmurHash3(k.hashCode() ^ this.mask)) & this.mask;
        while (this.used[pos]) {
            Label_0065: {
                if (this.key[pos] == null) {
                    if (k == null) {
                        break Label_0065;
                    }
                }
                else if (this.key[pos].equals(k)) {
                    break Label_0065;
                }
                pos = (pos + 1 & this.mask);
                continue;
            }
            --this.size;
            this.fixPointers(pos);
            final int v = this.value[pos];
            this.shiftKeys(pos);
            return v;
        }
        return this.defRetValue;
    }
    
    @Override
    public Integer remove(final Object ok) {
        final K k = (K)ok;
        int pos = ((k == null) ? 142593372 : HashCommon.murmurHash3(k.hashCode() ^ this.mask)) & this.mask;
        while (this.used[pos]) {
            Label_0067: {
                if (this.key[pos] == null) {
                    if (k == null) {
                        break Label_0067;
                    }
                }
                else if (this.key[pos].equals(k)) {
                    break Label_0067;
                }
                pos = (pos + 1 & this.mask);
                continue;
            }
            --this.size;
            this.fixPointers(pos);
            final int v = this.value[pos];
            this.shiftKeys(pos);
            return v;
        }
        return null;
    }
    
    public int removeFirstInt() {
        if (this.size == 0) {
            throw new NoSuchElementException();
        }
        --this.size;
        final int pos = this.first;
        this.first = (int)this.link[pos];
        if (0 <= this.first) {
            final long[] link = this.link;
            final int first = this.first;
            link[first] |= 0xFFFFFFFF00000000L;
        }
        final int v = this.value[pos];
        this.shiftKeys(pos);
        return v;
    }
    
    public int removeLastInt() {
        if (this.size == 0) {
            throw new NoSuchElementException();
        }
        --this.size;
        final int pos = this.last;
        this.last = (int)(this.link[pos] >>> 32);
        if (0 <= this.last) {
            final long[] link = this.link;
            final int last = this.last;
            link[last] |= 0xFFFFFFFFL;
        }
        final int v = this.value[pos];
        this.shiftKeys(pos);
        return v;
    }
    
    private void moveIndexToFirst(final int i) {
        if (this.size == 1 || this.first == i) {
            return;
        }
        if (this.last == i) {
            this.last = (int)(this.link[i] >>> 32);
            final long[] link = this.link;
            final int last = this.last;
            link[last] |= 0xFFFFFFFFL;
        }
        else {
            final long linki = this.link[i];
            final int prev = (int)(linki >>> 32);
            final int next = (int)linki;
            final long[] link2 = this.link;
            final int n = prev;
            link2[n] ^= ((this.link[prev] ^ (linki & 0xFFFFFFFFL)) & 0xFFFFFFFFL);
            final long[] link3 = this.link;
            final int n2 = next;
            link3[n2] ^= ((this.link[next] ^ (linki & 0xFFFFFFFF00000000L)) & 0xFFFFFFFF00000000L);
        }
        final long[] link4 = this.link;
        final int first = this.first;
        link4[first] ^= ((this.link[this.first] ^ ((long)i & 0xFFFFFFFFL) << 32) & 0xFFFFFFFF00000000L);
        this.link[i] = (0xFFFFFFFF00000000L | ((long)this.first & 0xFFFFFFFFL));
        this.first = i;
    }
    
    private void moveIndexToLast(final int i) {
        if (this.size == 1 || this.last == i) {
            return;
        }
        if (this.first == i) {
            this.first = (int)this.link[i];
            final long[] link = this.link;
            final int first = this.first;
            link[first] |= 0xFFFFFFFF00000000L;
        }
        else {
            final long linki = this.link[i];
            final int prev = (int)(linki >>> 32);
            final int next = (int)linki;
            final long[] link2 = this.link;
            final int n = prev;
            link2[n] ^= ((this.link[prev] ^ (linki & 0xFFFFFFFFL)) & 0xFFFFFFFFL);
            final long[] link3 = this.link;
            final int n2 = next;
            link3[n2] ^= ((this.link[next] ^ (linki & 0xFFFFFFFF00000000L)) & 0xFFFFFFFF00000000L);
        }
        final long[] link4 = this.link;
        final int last = this.last;
        link4[last] ^= ((this.link[this.last] ^ ((long)i & 0xFFFFFFFFL)) & 0xFFFFFFFFL);
        this.link[i] = (((long)this.last & 0xFFFFFFFFL) << 32 | 0xFFFFFFFFL);
        this.last = i;
    }
    
    public int getAndMoveToFirst(final K k) {
        final K[] key = this.key;
        final boolean[] used = this.used;
        final int mask = this.mask;
        int pos = ((k == null) ? 142593372 : HashCommon.murmurHash3(k.hashCode() ^ mask)) & mask;
        while (used[pos]) {
            Label_0072: {
                if (k == null) {
                    if (key[pos] == null) {
                        break Label_0072;
                    }
                }
                else if (k.equals(key[pos])) {
                    break Label_0072;
                }
                pos = (pos + 1 & mask);
                continue;
            }
            this.moveIndexToFirst(pos);
            return this.value[pos];
        }
        return this.defRetValue;
    }
    
    public int getAndMoveToLast(final K k) {
        final K[] key = this.key;
        final boolean[] used = this.used;
        final int mask = this.mask;
        int pos = ((k == null) ? 142593372 : HashCommon.murmurHash3(k.hashCode() ^ mask)) & mask;
        while (used[pos]) {
            Label_0072: {
                if (k == null) {
                    if (key[pos] == null) {
                        break Label_0072;
                    }
                }
                else if (k.equals(key[pos])) {
                    break Label_0072;
                }
                pos = (pos + 1 & mask);
                continue;
            }
            this.moveIndexToLast(pos);
            return this.value[pos];
        }
        return this.defRetValue;
    }
    
    public int putAndMoveToFirst(final K k, final int v) {
        final K[] key = this.key;
        final boolean[] used = this.used;
        final int mask = this.mask;
        int pos = ((k == null) ? 142593372 : HashCommon.murmurHash3(k.hashCode() ^ mask)) & mask;
        while (used[pos]) {
            Label_0074: {
                if (k == null) {
                    if (key[pos] == null) {
                        break Label_0074;
                    }
                }
                else if (k.equals(key[pos])) {
                    break Label_0074;
                }
                pos = (pos + 1 & mask);
                continue;
            }
            final int oldValue = this.value[pos];
            this.value[pos] = v;
            this.moveIndexToFirst(pos);
            return oldValue;
        }
        used[pos] = true;
        key[pos] = k;
        this.value[pos] = v;
        if (this.size == 0) {
            final int n = pos;
            this.last = n;
            this.first = n;
            this.link[pos] = -1L;
        }
        else {
            final long[] link = this.link;
            final int first = this.first;
            link[first] ^= ((this.link[this.first] ^ ((long)pos & 0xFFFFFFFFL) << 32) & 0xFFFFFFFF00000000L);
            this.link[pos] = (0xFFFFFFFF00000000L | ((long)this.first & 0xFFFFFFFFL));
            this.first = pos;
        }
        if (++this.size >= this.maxFill) {
            this.rehash(HashCommon.arraySize(this.size, this.f));
        }
        return this.defRetValue;
    }
    
    public int putAndMoveToLast(final K k, final int v) {
        final K[] key = this.key;
        final boolean[] used = this.used;
        final int mask = this.mask;
        int pos = ((k == null) ? 142593372 : HashCommon.murmurHash3(k.hashCode() ^ mask)) & mask;
        while (used[pos]) {
            Label_0074: {
                if (k == null) {
                    if (key[pos] == null) {
                        break Label_0074;
                    }
                }
                else if (k.equals(key[pos])) {
                    break Label_0074;
                }
                pos = (pos + 1 & mask);
                continue;
            }
            final int oldValue = this.value[pos];
            this.value[pos] = v;
            this.moveIndexToLast(pos);
            return oldValue;
        }
        used[pos] = true;
        key[pos] = k;
        this.value[pos] = v;
        if (this.size == 0) {
            final int n = pos;
            this.last = n;
            this.first = n;
            this.link[pos] = -1L;
        }
        else {
            final long[] link = this.link;
            final int last = this.last;
            link[last] ^= ((this.link[this.last] ^ ((long)pos & 0xFFFFFFFFL)) & 0xFFFFFFFFL);
            this.link[pos] = (((long)this.last & 0xFFFFFFFFL) << 32 | 0xFFFFFFFFL);
            this.last = pos;
        }
        if (++this.size >= this.maxFill) {
            this.rehash(HashCommon.arraySize(this.size, this.f));
        }
        return this.defRetValue;
    }
    
    @Override
    public int getInt(final Object k) {
        for (int pos = ((k == null) ? 142593372 : HashCommon.murmurHash3(k.hashCode() ^ this.mask)) & this.mask; this.used[pos]; pos = (pos + 1 & this.mask)) {
            if (this.key[pos] == null) {
                if (k == null) {
                    return this.value[pos];
                }
            }
            else if (this.key[pos].equals(k)) {
                return this.value[pos];
            }
        }
        return this.defRetValue;
    }
    
    @Override
    public boolean containsKey(final Object k) {
        for (int pos = ((k == null) ? 142593372 : HashCommon.murmurHash3(k.hashCode() ^ this.mask)) & this.mask; this.used[pos]; pos = (pos + 1 & this.mask)) {
            if (this.key[pos] == null) {
                if (k == null) {
                    return true;
                }
            }
            else if (this.key[pos].equals(k)) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public boolean containsValue(final int v) {
        final int[] value = this.value;
        final boolean[] used = this.used;
        int i = this.n;
        while (i-- != 0) {
            if (used[i] && value[i] == v) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public void clear() {
        if (this.size == 0) {
            return;
        }
        this.size = 0;
        BooleanArrays.fill(this.used, false);
        ObjectArrays.fill(this.key, (K)null);
        final int n = -1;
        this.last = n;
        this.first = n;
    }
    
    @Override
    public int size() {
        return this.size;
    }
    
    @Override
    public boolean isEmpty() {
        return this.size == 0;
    }
    
    @Deprecated
    public void growthFactor(final int growthFactor) {
    }
    
    @Deprecated
    public int growthFactor() {
        return 16;
    }
    
    protected void fixPointers(final int i) {
        if (this.size == 0) {
            final int n = -1;
            this.last = n;
            this.first = n;
            return;
        }
        if (this.first == i) {
            this.first = (int)this.link[i];
            if (0 <= this.first) {
                final long[] link = this.link;
                final int first = this.first;
                link[first] |= 0xFFFFFFFF00000000L;
            }
            return;
        }
        if (this.last == i) {
            this.last = (int)(this.link[i] >>> 32);
            if (0 <= this.last) {
                final long[] link2 = this.link;
                final int last = this.last;
                link2[last] |= 0xFFFFFFFFL;
            }
            return;
        }
        final long linki = this.link[i];
        final int prev = (int)(linki >>> 32);
        final int next = (int)linki;
        final long[] link3 = this.link;
        final int n2 = prev;
        link3[n2] ^= ((this.link[prev] ^ (linki & 0xFFFFFFFFL)) & 0xFFFFFFFFL);
        final long[] link4 = this.link;
        final int n3 = next;
        link4[n3] ^= ((this.link[next] ^ (linki & 0xFFFFFFFF00000000L)) & 0xFFFFFFFF00000000L);
    }
    
    protected void fixPointers(final int s, final int d) {
        if (this.size == 1) {
            this.last = d;
            this.first = d;
            this.link[d] = -1L;
            return;
        }
        if (this.first == s) {
            this.first = d;
            final long[] link = this.link;
            final int n = (int)this.link[s];
            link[n] ^= ((this.link[(int)this.link[s]] ^ ((long)d & 0xFFFFFFFFL) << 32) & 0xFFFFFFFF00000000L);
            this.link[d] = this.link[s];
            return;
        }
        if (this.last == s) {
            this.last = d;
            final long[] link2 = this.link;
            final int n2 = (int)(this.link[s] >>> 32);
            link2[n2] ^= ((this.link[(int)(this.link[s] >>> 32)] ^ ((long)d & 0xFFFFFFFFL)) & 0xFFFFFFFFL);
            this.link[d] = this.link[s];
            return;
        }
        final long links = this.link[s];
        final int prev = (int)(links >>> 32);
        final int next = (int)links;
        final long[] link3 = this.link;
        final int n3 = prev;
        link3[n3] ^= ((this.link[prev] ^ ((long)d & 0xFFFFFFFFL)) & 0xFFFFFFFFL);
        final long[] link4 = this.link;
        final int n4 = next;
        link4[n4] ^= ((this.link[next] ^ ((long)d & 0xFFFFFFFFL) << 32) & 0xFFFFFFFF00000000L);
        this.link[d] = links;
    }
    
    @Override
    public K firstKey() {
        if (this.size == 0) {
            throw new NoSuchElementException();
        }
        return this.key[this.first];
    }
    
    @Override
    public K lastKey() {
        if (this.size == 0) {
            throw new NoSuchElementException();
        }
        return this.key[this.last];
    }
    
    @Override
    public Comparator<? super K> comparator() {
        return null;
    }
    
    @Override
    public Object2IntSortedMap<K> tailMap(final K from) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public Object2IntSortedMap<K> headMap(final K to) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public Object2IntSortedMap<K> subMap(final K from, final K to) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public Object2IntSortedMap.FastSortedEntrySet<K> object2IntEntrySet() {
        if (this.entries == null) {
            this.entries = new MapEntrySet();
        }
        return this.entries;
    }
    
    @Override
    public ObjectSortedSet<K> keySet() {
        if (this.keys == null) {
            this.keys = new KeySet();
        }
        return this.keys;
    }
    
    @Override
    public IntCollection values() {
        if (this.values == null) {
            this.values = new AbstractIntCollection() {
                @Override
                public IntIterator iterator() {
                    return new ValueIterator();
                }
                
                @Override
                public int size() {
                    return Object2IntLinkedOpenHashMap.this.size;
                }
                
                @Override
                public boolean contains(final int v) {
                    return Object2IntLinkedOpenHashMap.this.containsValue(v);
                }
                
                @Override
                public void clear() {
                    Object2IntLinkedOpenHashMap.this.clear();
                }
            };
        }
        return this.values;
    }
    
    @Deprecated
    public boolean rehash() {
        return true;
    }
    
    public boolean trim() {
        final int l = HashCommon.arraySize(this.size, this.f);
        if (l >= this.n) {
            return true;
        }
        try {
            this.rehash(l);
        }
        catch (OutOfMemoryError cantDoIt) {
            return false;
        }
        return true;
    }
    
    public boolean trim(final int n) {
        final int l = HashCommon.nextPowerOfTwo((int)Math.ceil(n / this.f));
        if (this.n <= l) {
            return true;
        }
        try {
            this.rehash(l);
        }
        catch (OutOfMemoryError cantDoIt) {
            return false;
        }
        return true;
    }
    
    protected void rehash(final int newN) {
        int i = this.first;
        int prev = -1;
        int newPrev = -1;
        final K[] key = this.key;
        final int[] value = this.value;
        final int mask = newN - 1;
        final K[] newKey = (K[])new Object[newN];
        final int[] newValue = new int[newN];
        final boolean[] newUsed = new boolean[newN];
        final long[] link = this.link;
        final long[] newLink = new long[newN];
        this.first = -1;
        int j = this.size;
        while (j-- != 0) {
            final K k = key[i];
            int pos;
            for (pos = (((k == null) ? 142593372 : HashCommon.murmurHash3(k.hashCode() ^ mask)) & mask); newUsed[pos]; pos = (pos + 1 & mask)) {}
            newUsed[pos] = true;
            newKey[pos] = k;
            newValue[pos] = value[i];
            if (prev != -1) {
                final long[] array = newLink;
                final int n = newPrev;
                array[n] ^= ((newLink[newPrev] ^ ((long)pos & 0xFFFFFFFFL)) & 0xFFFFFFFFL);
                final long[] array2 = newLink;
                final int n2 = pos;
                array2[n2] ^= ((newLink[pos] ^ ((long)newPrev & 0xFFFFFFFFL) << 32) & 0xFFFFFFFF00000000L);
                newPrev = pos;
            }
            else {
                final int first = pos;
                this.first = first;
                newPrev = first;
                newLink[pos] = -1L;
            }
            final int t = i;
            i = (int)link[i];
            prev = t;
        }
        this.n = newN;
        this.mask = mask;
        this.maxFill = HashCommon.maxFill(this.n, this.f);
        this.key = newKey;
        this.value = newValue;
        this.used = newUsed;
        this.link = newLink;
        if ((this.last = newPrev) != -1) {
            final long[] array3 = newLink;
            final int n3 = newPrev;
            array3[n3] |= 0xFFFFFFFFL;
        }
    }
    
    public Object2IntLinkedOpenHashMap<K> clone() {
        Object2IntLinkedOpenHashMap<K> c;
        try {
            c = (Object2IntLinkedOpenHashMap)super.clone();
        }
        catch (CloneNotSupportedException cantHappen) {
            throw new InternalError();
        }
        c.keys = null;
        c.values = null;
        c.entries = null;
        c.key = this.key.clone();
        c.value = this.value.clone();
        c.used = this.used.clone();
        c.link = this.link.clone();
        return c;
    }
    
    @Override
    public int hashCode() {
        int h = 0;
        int j = this.size;
        int i = 0;
        int t = 0;
        while (j-- != 0) {
            while (!this.used[i]) {
                ++i;
            }
            if (this != this.key[i]) {
                t = ((this.key[i] == null) ? 0 : this.key[i].hashCode());
            }
            t ^= this.value[i];
            h += t;
            ++i;
        }
        return h;
    }
    
    private void writeObject(final ObjectOutputStream s) throws IOException {
        final K[] key = this.key;
        final int[] value = this.value;
        final MapIterator i = new MapIterator();
        s.defaultWriteObject();
        int j = this.size;
        while (j-- != 0) {
            final int e = i.nextEntry();
            s.writeObject(key[e]);
            s.writeInt(value[e]);
        }
    }
    
    private void readObject(final ObjectInputStream s) throws IOException, ClassNotFoundException {
        s.defaultReadObject();
        this.n = HashCommon.arraySize(this.size, this.f);
        this.maxFill = HashCommon.maxFill(this.n, this.f);
        this.mask = this.n - 1;
        final Object[] key2 = new Object[this.n];
        this.key = (K[])key2;
        final K[] key = (K[])key2;
        final int[] value2 = new int[this.n];
        this.value = value2;
        final int[] value = value2;
        final boolean[] used2 = new boolean[this.n];
        this.used = used2;
        final boolean[] used = used2;
        final long[] link2 = new long[this.n];
        this.link = link2;
        final long[] link = link2;
        int prev = -1;
        final int n = -1;
        this.last = n;
        this.first = n;
        int i = this.size;
        int pos = 0;
        while (i-- != 0) {
            final K k = (K)s.readObject();
            final int v = s.readInt();
            for (pos = (((k == null) ? 142593372 : HashCommon.murmurHash3(k.hashCode() ^ this.mask)) & this.mask); used[pos]; pos = (pos + 1 & this.mask)) {}
            used[pos] = true;
            key[pos] = k;
            value[pos] = v;
            if (this.first != -1) {
                final long[] array = link;
                final int n2 = prev;
                array[n2] ^= ((link[prev] ^ ((long)pos & 0xFFFFFFFFL)) & 0xFFFFFFFFL);
                final long[] array2 = link;
                final int n3 = pos;
                array2[n3] ^= ((link[pos] ^ ((long)prev & 0xFFFFFFFFL) << 32) & 0xFFFFFFFF00000000L);
                prev = pos;
            }
            else {
                final int first = pos;
                this.first = first;
                prev = first;
                final long[] array3 = link;
                final int n4 = pos;
                array3[n4] |= 0xFFFFFFFF00000000L;
            }
        }
        if ((this.last = prev) != -1) {
            final long[] array4 = link;
            final int n5 = prev;
            array4[n5] |= 0xFFFFFFFFL;
        }
    }
    
    private void checkTable() {
    }
    
    private final class MapEntry implements Object2IntMap.Entry<K>, Map.Entry<K, Integer>
    {
        private int index;
        
        MapEntry(final int index) {
            this.index = index;
        }
        
        @Override
        public K getKey() {
            return Object2IntLinkedOpenHashMap.this.key[this.index];
        }
        
        @Override
        public Integer getValue() {
            return Object2IntLinkedOpenHashMap.this.value[this.index];
        }
        
        @Override
        public int getIntValue() {
            return Object2IntLinkedOpenHashMap.this.value[this.index];
        }
        
        @Override
        public int setValue(final int v) {
            final int oldValue = Object2IntLinkedOpenHashMap.this.value[this.index];
            Object2IntLinkedOpenHashMap.this.value[this.index] = v;
            return oldValue;
        }
        
        @Override
        public Integer setValue(final Integer v) {
            return this.setValue((int)v);
        }
        
        @Override
        public boolean equals(final Object o) {
            if (!(o instanceof Map.Entry)) {
                return false;
            }
            final Map.Entry<K, Integer> e = (Map.Entry<K, Integer>)o;
            if (Object2IntLinkedOpenHashMap.this.key[this.index] == null) {
                if (e.getKey() != null) {
                    return false;
                }
            }
            else if (!Object2IntLinkedOpenHashMap.this.key[this.index].equals(e.getKey())) {
                return false;
            }
            if (Object2IntLinkedOpenHashMap.this.value[this.index] == e.getValue()) {
                return true;
            }
            return false;
        }
        
        @Override
        public int hashCode() {
            return ((Object2IntLinkedOpenHashMap.this.key[this.index] == null) ? 0 : Object2IntLinkedOpenHashMap.this.key[this.index].hashCode()) ^ Object2IntLinkedOpenHashMap.this.value[this.index];
        }
        
        @Override
        public String toString() {
            return Object2IntLinkedOpenHashMap.this.key[this.index] + "=>" + Object2IntLinkedOpenHashMap.this.value[this.index];
        }
    }
    
    private class MapIterator
    {
        int prev;
        int next;
        int curr;
        int index;
        
        private MapIterator() {
            this.prev = -1;
            this.next = -1;
            this.curr = -1;
            this.index = -1;
            this.next = Object2IntLinkedOpenHashMap.this.first;
            this.index = 0;
        }
        
        private MapIterator(final K from) {
            this.prev = -1;
            this.next = -1;
            this.curr = -1;
            this.index = -1;
            Label_0083: {
                if (Object2IntLinkedOpenHashMap.this.key[Object2IntLinkedOpenHashMap.this.last] == null) {
                    if (from != null) {
                        break Label_0083;
                    }
                }
                else if (!Object2IntLinkedOpenHashMap.this.key[Object2IntLinkedOpenHashMap.this.last].equals(from)) {
                    break Label_0083;
                }
                this.prev = Object2IntLinkedOpenHashMap.this.last;
                this.index = Object2IntLinkedOpenHashMap.this.size;
                return;
            }
            int pos = ((from == null) ? 142593372 : HashCommon.murmurHash3(from.hashCode() ^ Object2IntLinkedOpenHashMap.this.mask)) & Object2IntLinkedOpenHashMap.this.mask;
            while (Object2IntLinkedOpenHashMap.this.used[pos]) {
                Label_0148: {
                    if (Object2IntLinkedOpenHashMap.this.key[pos] == null) {
                        if (from == null) {
                            break Label_0148;
                        }
                    }
                    else if (Object2IntLinkedOpenHashMap.this.key[pos].equals(from)) {
                        break Label_0148;
                    }
                    pos = (pos + 1 & Object2IntLinkedOpenHashMap.this.mask);
                    continue;
                }
                this.next = (int)Object2IntLinkedOpenHashMap.this.link[pos];
                this.prev = pos;
                return;
            }
            throw new NoSuchElementException("The key " + from + " does not belong to this map.");
        }
        
        public boolean hasNext() {
            return this.next != -1;
        }
        
        public boolean hasPrevious() {
            return this.prev != -1;
        }
        
        private final void ensureIndexKnown() {
            if (this.index >= 0) {
                return;
            }
            if (this.prev == -1) {
                this.index = 0;
                return;
            }
            if (this.next == -1) {
                this.index = Object2IntLinkedOpenHashMap.this.size;
                return;
            }
            int pos = Object2IntLinkedOpenHashMap.this.first;
            this.index = 1;
            while (pos != this.prev) {
                pos = (int)Object2IntLinkedOpenHashMap.this.link[pos];
                ++this.index;
            }
        }
        
        public int nextIndex() {
            this.ensureIndexKnown();
            return this.index;
        }
        
        public int previousIndex() {
            this.ensureIndexKnown();
            return this.index - 1;
        }
        
        public int nextEntry() {
            if (!this.hasNext()) {
                return Object2IntLinkedOpenHashMap.this.size();
            }
            this.curr = this.next;
            this.next = (int)Object2IntLinkedOpenHashMap.this.link[this.curr];
            this.prev = this.curr;
            if (this.index >= 0) {
                ++this.index;
            }
            return this.curr;
        }
        
        public int previousEntry() {
            if (!this.hasPrevious()) {
                return -1;
            }
            this.curr = this.prev;
            this.prev = (int)(Object2IntLinkedOpenHashMap.this.link[this.curr] >>> 32);
            this.next = this.curr;
            if (this.index >= 0) {
                --this.index;
            }
            return this.curr;
        }
        
        public void remove() {
            this.ensureIndexKnown();
            if (this.curr == -1) {
                throw new IllegalStateException();
            }
            if (this.curr == this.prev) {
                --this.index;
                this.prev = (int)(Object2IntLinkedOpenHashMap.this.link[this.curr] >>> 32);
            }
            else {
                this.next = (int)Object2IntLinkedOpenHashMap.this.link[this.curr];
            }
            final Object2IntLinkedOpenHashMap this$0 = Object2IntLinkedOpenHashMap.this;
            --this$0.size;
            if (this.prev == -1) {
                Object2IntLinkedOpenHashMap.this.first = this.next;
            }
            else {
                final long[] link = Object2IntLinkedOpenHashMap.this.link;
                final int prev = this.prev;
                link[prev] ^= ((Object2IntLinkedOpenHashMap.this.link[this.prev] ^ ((long)this.next & 0xFFFFFFFFL)) & 0xFFFFFFFFL);
            }
            if (this.next == -1) {
                Object2IntLinkedOpenHashMap.this.last = this.prev;
            }
            else {
                final long[] link2 = Object2IntLinkedOpenHashMap.this.link;
                final int next = this.next;
                link2[next] ^= ((Object2IntLinkedOpenHashMap.this.link[this.next] ^ ((long)this.prev & 0xFFFFFFFFL) << 32) & 0xFFFFFFFF00000000L);
            }
            int pos = this.curr;
            int last;
            while (true) {
                for (pos = ((last = pos) + 1 & Object2IntLinkedOpenHashMap.this.mask); Object2IntLinkedOpenHashMap.this.used[pos]; pos = (pos + 1 & Object2IntLinkedOpenHashMap.this.mask)) {
                    final int slot = ((Object2IntLinkedOpenHashMap.this.key[pos] == null) ? 142593372 : HashCommon.murmurHash3(Object2IntLinkedOpenHashMap.this.key[pos].hashCode() ^ Object2IntLinkedOpenHashMap.this.mask)) & Object2IntLinkedOpenHashMap.this.mask;
                    if (last <= pos) {
                        if (last >= slot) {
                            break;
                        }
                        if (slot > pos) {
                            break;
                        }
                    }
                    else if (last >= slot && slot > pos) {
                        break;
                    }
                }
                if (!Object2IntLinkedOpenHashMap.this.used[pos]) {
                    break;
                }
                Object2IntLinkedOpenHashMap.this.key[last] = Object2IntLinkedOpenHashMap.this.key[pos];
                Object2IntLinkedOpenHashMap.this.value[last] = Object2IntLinkedOpenHashMap.this.value[pos];
                if (this.next == pos) {
                    this.next = last;
                }
                if (this.prev == pos) {
                    this.prev = last;
                }
                Object2IntLinkedOpenHashMap.this.fixPointers(pos, last);
            }
            Object2IntLinkedOpenHashMap.this.used[last] = false;
            Object2IntLinkedOpenHashMap.this.key[last] = null;
            this.curr = -1;
        }
        
        public int skip(final int n) {
            int i = n;
            while (i-- != 0 && this.hasNext()) {
                this.nextEntry();
            }
            return n - i - 1;
        }
        
        public int back(final int n) {
            int i = n;
            while (i-- != 0 && this.hasPrevious()) {
                this.previousEntry();
            }
            return n - i - 1;
        }
    }
    
    private class EntryIterator extends MapIterator implements ObjectListIterator<Object2IntMap.Entry<K>>
    {
        private MapEntry entry;
        
        public EntryIterator() {
        }
        
        public EntryIterator(final K from) {
            super((Object)from);
        }
        
        @Override
        public MapEntry next() {
            return this.entry = new MapEntry(this.nextEntry());
        }
        
        @Override
        public MapEntry previous() {
            return this.entry = new MapEntry(this.previousEntry());
        }
        
        @Override
        public void remove() {
            super.remove();
            this.entry.index = -1;
        }
        
        @Override
        public void set(final Object2IntMap.Entry<K> ok) {
            throw new UnsupportedOperationException();
        }
        
        @Override
        public void add(final Object2IntMap.Entry<K> ok) {
            throw new UnsupportedOperationException();
        }
    }
    
    private class FastEntryIterator extends MapIterator implements ObjectListIterator<Object2IntMap.Entry<K>>
    {
        final BasicEntry<K> entry;
        
        public FastEntryIterator() {
            this.entry = new BasicEntry<K>(null, 0);
        }
        
        public FastEntryIterator(final K from) {
            super((Object)from);
            this.entry = new BasicEntry<K>(null, 0);
        }
        
        @Override
        public BasicEntry<K> next() {
            final int e = this.nextEntry();
            this.entry.key = Object2IntLinkedOpenHashMap.this.key[e];
            this.entry.value = Object2IntLinkedOpenHashMap.this.value[e];
            return this.entry;
        }
        
        @Override
        public BasicEntry<K> previous() {
            final int e = this.previousEntry();
            this.entry.key = Object2IntLinkedOpenHashMap.this.key[e];
            this.entry.value = Object2IntLinkedOpenHashMap.this.value[e];
            return this.entry;
        }
        
        @Override
        public void set(final Object2IntMap.Entry<K> ok) {
            throw new UnsupportedOperationException();
        }
        
        @Override
        public void add(final Object2IntMap.Entry<K> ok) {
            throw new UnsupportedOperationException();
        }
    }
    
    private final class MapEntrySet extends AbstractObjectSortedSet<Object2IntMap.Entry<K>> implements Object2IntSortedMap.FastSortedEntrySet<K>
    {
        @Override
        public ObjectBidirectionalIterator<Object2IntMap.Entry<K>> iterator() {
            return new EntryIterator();
        }
        
        @Override
        public Comparator<? super Object2IntMap.Entry<K>> comparator() {
            return null;
        }
        
        @Override
        public ObjectSortedSet<Object2IntMap.Entry<K>> subSet(final Object2IntMap.Entry<K> fromElement, final Object2IntMap.Entry<K> toElement) {
            throw new UnsupportedOperationException();
        }
        
        @Override
        public ObjectSortedSet<Object2IntMap.Entry<K>> headSet(final Object2IntMap.Entry<K> toElement) {
            throw new UnsupportedOperationException();
        }
        
        @Override
        public ObjectSortedSet<Object2IntMap.Entry<K>> tailSet(final Object2IntMap.Entry<K> fromElement) {
            throw new UnsupportedOperationException();
        }
        
        @Override
        public Object2IntMap.Entry<K> first() {
            if (Object2IntLinkedOpenHashMap.this.size == 0) {
                throw new NoSuchElementException();
            }
            return new MapEntry(Object2IntLinkedOpenHashMap.this.first);
        }
        
        @Override
        public Object2IntMap.Entry<K> last() {
            if (Object2IntLinkedOpenHashMap.this.size == 0) {
                throw new NoSuchElementException();
            }
            return new MapEntry(Object2IntLinkedOpenHashMap.this.last);
        }
        
        @Override
        public boolean contains(final Object o) {
            if (!(o instanceof Map.Entry)) {
                return false;
            }
            final Map.Entry<K, Integer> e = (Map.Entry<K, Integer>)o;
            final K k = e.getKey();
            for (int pos = ((k == null) ? 142593372 : HashCommon.murmurHash3(k.hashCode() ^ Object2IntLinkedOpenHashMap.this.mask)) & Object2IntLinkedOpenHashMap.this.mask; Object2IntLinkedOpenHashMap.this.used[pos]; pos = (pos + 1 & Object2IntLinkedOpenHashMap.this.mask)) {
                if (Object2IntLinkedOpenHashMap.this.key[pos] == null) {
                    if (k == null) {
                        return Object2IntLinkedOpenHashMap.this.value[pos] == e.getValue();
                    }
                }
                else if (Object2IntLinkedOpenHashMap.this.key[pos].equals(k)) {
                    return Object2IntLinkedOpenHashMap.this.value[pos] == e.getValue();
                }
            }
            return false;
        }
        
        @Override
        public boolean remove(final Object o) {
            if (!(o instanceof Map.Entry)) {
                return false;
            }
            final Map.Entry<K, Integer> e = (Map.Entry<K, Integer>)o;
            final K k = e.getKey();
            int pos = ((k == null) ? 142593372 : HashCommon.murmurHash3(k.hashCode() ^ Object2IntLinkedOpenHashMap.this.mask)) & Object2IntLinkedOpenHashMap.this.mask;
            while (Object2IntLinkedOpenHashMap.this.used[pos]) {
                Label_0105: {
                    if (Object2IntLinkedOpenHashMap.this.key[pos] == null) {
                        if (k == null) {
                            break Label_0105;
                        }
                    }
                    else if (Object2IntLinkedOpenHashMap.this.key[pos].equals(k)) {
                        break Label_0105;
                    }
                    pos = (pos + 1 & Object2IntLinkedOpenHashMap.this.mask);
                    continue;
                }
                Object2IntLinkedOpenHashMap.this.remove(e.getKey());
                return true;
            }
            return false;
        }
        
        @Override
        public int size() {
            return Object2IntLinkedOpenHashMap.this.size;
        }
        
        @Override
        public void clear() {
            Object2IntLinkedOpenHashMap.this.clear();
        }
        
        @Override
        public ObjectBidirectionalIterator<Object2IntMap.Entry<K>> iterator(final Object2IntMap.Entry<K> from) {
            return new EntryIterator(from.getKey());
        }
        
        @Override
        public ObjectBidirectionalIterator<Object2IntMap.Entry<K>> fastIterator() {
            return new FastEntryIterator();
        }
        
        @Override
        public ObjectBidirectionalIterator<Object2IntMap.Entry<K>> fastIterator(final Object2IntMap.Entry<K> from) {
            return new FastEntryIterator(from.getKey());
        }
    }
    
    private final class KeyIterator extends MapIterator implements ObjectListIterator<K>
    {
        public KeyIterator(final K k) {
            super((Object)k);
        }
        
        @Override
        public K previous() {
            return Object2IntLinkedOpenHashMap.this.key[this.previousEntry()];
        }
        
        @Override
        public void set(final K k) {
            throw new UnsupportedOperationException();
        }
        
        @Override
        public void add(final K k) {
            throw new UnsupportedOperationException();
        }
        
        public KeyIterator() {
        }
        
        @Override
        public K next() {
            return Object2IntLinkedOpenHashMap.this.key[this.nextEntry()];
        }
    }
    
    private final class KeySet extends AbstractObjectSortedSet<K>
    {
        @Override
        public ObjectListIterator<K> iterator(final K from) {
            return new KeyIterator(from);
        }
        
        @Override
        public ObjectListIterator<K> iterator() {
            return new KeyIterator();
        }
        
        @Override
        public int size() {
            return Object2IntLinkedOpenHashMap.this.size;
        }
        
        @Override
        public boolean contains(final Object k) {
            return Object2IntLinkedOpenHashMap.this.containsKey(k);
        }
        
        @Override
        public boolean remove(final Object k) {
            final int oldSize = Object2IntLinkedOpenHashMap.this.size;
            Object2IntLinkedOpenHashMap.this.remove(k);
            return Object2IntLinkedOpenHashMap.this.size != oldSize;
        }
        
        @Override
        public void clear() {
            Object2IntLinkedOpenHashMap.this.clear();
        }
        
        @Override
        public K first() {
            if (Object2IntLinkedOpenHashMap.this.size == 0) {
                throw new NoSuchElementException();
            }
            return Object2IntLinkedOpenHashMap.this.key[Object2IntLinkedOpenHashMap.this.first];
        }
        
        @Override
        public K last() {
            if (Object2IntLinkedOpenHashMap.this.size == 0) {
                throw new NoSuchElementException();
            }
            return Object2IntLinkedOpenHashMap.this.key[Object2IntLinkedOpenHashMap.this.last];
        }
        
        @Override
        public Comparator<? super K> comparator() {
            return null;
        }
        
        @Override
        public final ObjectSortedSet<K> tailSet(final K from) {
            throw new UnsupportedOperationException();
        }
        
        @Override
        public final ObjectSortedSet<K> headSet(final K to) {
            throw new UnsupportedOperationException();
        }
        
        @Override
        public final ObjectSortedSet<K> subSet(final K from, final K to) {
            throw new UnsupportedOperationException();
        }
    }
    
    private final class ValueIterator extends MapIterator implements IntListIterator
    {
        @Override
        public int previousInt() {
            return Object2IntLinkedOpenHashMap.this.value[this.previousEntry()];
        }
        
        @Override
        public Integer previous() {
            return Object2IntLinkedOpenHashMap.this.value[this.previousEntry()];
        }
        
        @Override
        public void set(final Integer ok) {
            throw new UnsupportedOperationException();
        }
        
        @Override
        public void add(final Integer ok) {
            throw new UnsupportedOperationException();
        }
        
        @Override
        public void set(final int v) {
            throw new UnsupportedOperationException();
        }
        
        @Override
        public void add(final int v) {
            throw new UnsupportedOperationException();
        }
        
        public ValueIterator() {
        }
        
        @Override
        public int nextInt() {
            return Object2IntLinkedOpenHashMap.this.value[this.nextEntry()];
        }
        
        @Override
        public Integer next() {
            return Object2IntLinkedOpenHashMap.this.value[this.nextEntry()];
        }
    }
}
