// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.htrace.shaded.fasterxml.jackson.core.sym;

import java.util.Arrays;
import org.apache.htrace.shaded.fasterxml.jackson.core.util.InternCache;
import org.apache.htrace.shaded.fasterxml.jackson.core.JsonFactory;
import java.util.BitSet;

public final class CharsToNameCanonicalizer
{
    public static final int HASH_MULT = 33;
    protected static final int DEFAULT_T_SIZE = 64;
    protected static final int MAX_T_SIZE = 65536;
    static final int MAX_ENTRIES_FOR_REUSE = 12000;
    static final int MAX_COLL_CHAIN_LENGTH = 100;
    static final CharsToNameCanonicalizer sBootstrapSymbolTable;
    protected CharsToNameCanonicalizer _parent;
    private final int _hashSeed;
    protected final int _flags;
    protected boolean _canonicalize;
    protected String[] _symbols;
    protected Bucket[] _buckets;
    protected int _size;
    protected int _sizeThreshold;
    protected int _indexMask;
    protected int _longestCollisionList;
    protected boolean _dirty;
    protected BitSet _overflows;
    
    public static CharsToNameCanonicalizer createRoot() {
        final long now = System.currentTimeMillis();
        final int seed = (int)now + (int)(now >>> 32) | 0x1;
        return createRoot(seed);
    }
    
    protected static CharsToNameCanonicalizer createRoot(final int hashSeed) {
        return CharsToNameCanonicalizer.sBootstrapSymbolTable.makeOrphan(hashSeed);
    }
    
    private CharsToNameCanonicalizer() {
        this._canonicalize = true;
        this._flags = -1;
        this._dirty = true;
        this._hashSeed = 0;
        this._longestCollisionList = 0;
        this.initTables(64);
    }
    
    private void initTables(final int initialSize) {
        this._symbols = new String[initialSize];
        this._buckets = new Bucket[initialSize >> 1];
        this._indexMask = initialSize - 1;
        this._size = 0;
        this._longestCollisionList = 0;
        this._sizeThreshold = _thresholdSize(initialSize);
    }
    
    private static int _thresholdSize(final int hashAreaSize) {
        return hashAreaSize - (hashAreaSize >> 2);
    }
    
    private CharsToNameCanonicalizer(final CharsToNameCanonicalizer parent, final int flags, final String[] symbols, final Bucket[] buckets, final int size, final int hashSeed, final int longestColl) {
        this._parent = parent;
        this._flags = flags;
        this._canonicalize = JsonFactory.Feature.CANONICALIZE_FIELD_NAMES.enabledIn(flags);
        this._symbols = symbols;
        this._buckets = buckets;
        this._size = size;
        this._hashSeed = hashSeed;
        final int arrayLen = symbols.length;
        this._sizeThreshold = _thresholdSize(arrayLen);
        this._indexMask = arrayLen - 1;
        this._longestCollisionList = longestColl;
        this._dirty = false;
    }
    
    public CharsToNameCanonicalizer makeChild(final int flags) {
        final String[] symbols;
        final Bucket[] buckets;
        final int size;
        final int hashSeed;
        final int longestCollisionList;
        synchronized (this) {
            symbols = this._symbols;
            buckets = this._buckets;
            size = this._size;
            hashSeed = this._hashSeed;
            longestCollisionList = this._longestCollisionList;
        }
        return new CharsToNameCanonicalizer(this, flags, symbols, buckets, size, hashSeed, longestCollisionList);
    }
    
    private CharsToNameCanonicalizer makeOrphan(final int seed) {
        return new CharsToNameCanonicalizer(null, -1, this._symbols, this._buckets, this._size, seed, this._longestCollisionList);
    }
    
    private void mergeChild(final CharsToNameCanonicalizer child) {
        if (child.size() > 12000) {
            synchronized (this) {
                this.initTables(256);
                this._dirty = false;
            }
        }
        else {
            if (child.size() <= this.size()) {
                return;
            }
            synchronized (this) {
                this._symbols = child._symbols;
                this._buckets = child._buckets;
                this._size = child._size;
                this._sizeThreshold = child._sizeThreshold;
                this._indexMask = child._indexMask;
                this._longestCollisionList = child._longestCollisionList;
                this._dirty = false;
            }
        }
    }
    
    public void release() {
        if (!this.maybeDirty()) {
            return;
        }
        if (this._parent != null && this._canonicalize) {
            this._parent.mergeChild(this);
            this._dirty = false;
        }
    }
    
    public int size() {
        return this._size;
    }
    
    public int bucketCount() {
        return this._symbols.length;
    }
    
    public boolean maybeDirty() {
        return this._dirty;
    }
    
    public int hashSeed() {
        return this._hashSeed;
    }
    
    public int collisionCount() {
        int count = 0;
        for (final Bucket bucket : this._buckets) {
            if (bucket != null) {
                count += bucket.length;
            }
        }
        return count;
    }
    
    public int maxCollisionLength() {
        return this._longestCollisionList;
    }
    
    public String findSymbol(final char[] buffer, final int start, final int len, final int h) {
        if (len < 1) {
            return "";
        }
        if (!this._canonicalize) {
            return new String(buffer, start, len);
        }
        final int index = this._hashToIndex(h);
        String sym = this._symbols[index];
        if (sym != null) {
            if (sym.length() == len) {
                int i = 0;
                while (sym.charAt(i) == buffer[start + i]) {
                    if (++i == len) {
                        return sym;
                    }
                }
            }
            final Bucket b = this._buckets[index >> 1];
            if (b != null) {
                sym = b.has(buffer, start, len);
                if (sym != null) {
                    return sym;
                }
                sym = this._findSymbol2(buffer, start, len, b.next);
                if (sym != null) {
                    return sym;
                }
            }
        }
        return this._addSymbol(buffer, start, len, h, index);
    }
    
    private String _findSymbol2(final char[] buffer, final int start, final int len, Bucket b) {
        while (b != null) {
            final String sym = b.has(buffer, start, len);
            if (sym != null) {
                return sym;
            }
            b = b.next;
        }
        return null;
    }
    
    private String _addSymbol(final char[] buffer, final int start, final int len, final int h, int index) {
        if (!this._dirty) {
            this.copyArrays();
            this._dirty = true;
        }
        else if (this._size >= this._sizeThreshold) {
            this.rehash();
            index = this._hashToIndex(this.calcHash(buffer, start, len));
        }
        String newSymbol = new String(buffer, start, len);
        if (JsonFactory.Feature.INTERN_FIELD_NAMES.enabledIn(this._flags)) {
            newSymbol = InternCache.instance.intern(newSymbol);
        }
        ++this._size;
        if (this._symbols[index] == null) {
            this._symbols[index] = newSymbol;
        }
        else {
            final int bix = index >> 1;
            final Bucket newB = new Bucket(newSymbol, this._buckets[bix]);
            final int collLen = newB.length;
            if (collLen > 100) {
                this._handleSpillOverflow(bix, newB);
            }
            else {
                this._buckets[bix] = newB;
                this._longestCollisionList = Math.max(collLen, this._longestCollisionList);
            }
        }
        return newSymbol;
    }
    
    private void _handleSpillOverflow(final int bindex, final Bucket newBucket) {
        if (this._overflows == null) {
            (this._overflows = new BitSet()).set(bindex);
        }
        else if (this._overflows.get(bindex)) {
            if (JsonFactory.Feature.FAIL_ON_SYMBOL_HASH_OVERFLOW.enabledIn(this._flags)) {
                this.reportTooManyCollisions(100);
            }
            this._canonicalize = false;
        }
        else {
            this._overflows.set(bindex);
        }
        this._symbols[bindex + bindex] = newBucket.symbol;
        this._buckets[bindex] = null;
        this._size -= newBucket.length;
        this._longestCollisionList = -1;
    }
    
    public int _hashToIndex(int rawHash) {
        rawHash += rawHash >>> 15;
        return rawHash & this._indexMask;
    }
    
    public int calcHash(final char[] buffer, final int start, final int len) {
        int hash = this._hashSeed;
        for (int i = start, end = start + len; i < end; ++i) {
            hash = hash * 33 + buffer[i];
        }
        return (hash == 0) ? 1 : hash;
    }
    
    public int calcHash(final String key) {
        final int len = key.length();
        int hash = this._hashSeed;
        for (int i = 0; i < len; ++i) {
            hash = hash * 33 + key.charAt(i);
        }
        return (hash == 0) ? 1 : hash;
    }
    
    private void copyArrays() {
        final String[] oldSyms = this._symbols;
        this._symbols = Arrays.copyOf(oldSyms, oldSyms.length);
        final Bucket[] oldBuckets = this._buckets;
        this._buckets = Arrays.copyOf(oldBuckets, oldBuckets.length);
    }
    
    private void rehash() {
        int size = this._symbols.length;
        final int newSize = size + size;
        if (newSize > 65536) {
            this._size = 0;
            this._canonicalize = false;
            this._symbols = new String[64];
            this._buckets = new Bucket[32];
            this._indexMask = 63;
            this._dirty = true;
            return;
        }
        final String[] oldSyms = this._symbols;
        final Bucket[] oldBuckets = this._buckets;
        this._symbols = new String[newSize];
        this._buckets = new Bucket[newSize >> 1];
        this._indexMask = newSize - 1;
        this._sizeThreshold = _thresholdSize(newSize);
        int count = 0;
        int maxColl = 0;
        for (final String symbol : oldSyms) {
            if (symbol != null) {
                ++count;
                final int index = this._hashToIndex(this.calcHash(symbol));
                if (this._symbols[index] == null) {
                    this._symbols[index] = symbol;
                }
                else {
                    final int bix = index >> 1;
                    final Bucket newB = new Bucket(symbol, this._buckets[bix]);
                    this._buckets[bix] = newB;
                    maxColl = Math.max(maxColl, newB.length);
                }
            }
        }
        size >>= 1;
        for (Bucket b : oldBuckets) {
            while (b != null) {
                ++count;
                final String symbol2 = b.symbol;
                final int index2 = this._hashToIndex(this.calcHash(symbol2));
                if (this._symbols[index2] == null) {
                    this._symbols[index2] = symbol2;
                }
                else {
                    final int bix2 = index2 >> 1;
                    final Bucket newB2 = new Bucket(symbol2, this._buckets[bix2]);
                    this._buckets[bix2] = newB2;
                    maxColl = Math.max(maxColl, newB2.length);
                }
                b = b.next;
            }
        }
        this._longestCollisionList = maxColl;
        this._overflows = null;
        if (count != this._size) {
            throw new Error("Internal error on SymbolTable.rehash(): had " + this._size + " entries; now have " + count + ".");
        }
    }
    
    protected void reportTooManyCollisions(final int maxLen) {
        throw new IllegalStateException("Longest collision chain in symbol table (of size " + this._size + ") now exceeds maximum, " + maxLen + " -- suspect a DoS attack based on hash collisions");
    }
    
    static {
        sBootstrapSymbolTable = new CharsToNameCanonicalizer();
    }
    
    static final class Bucket
    {
        private final String symbol;
        private final Bucket next;
        private final int length;
        
        public Bucket(final String s, final Bucket n) {
            this.symbol = s;
            this.next = n;
            this.length = ((n == null) ? 1 : (n.length + 1));
        }
        
        public String has(final char[] buf, final int start, final int len) {
            if (this.symbol.length() != len) {
                return null;
            }
            int i = 0;
            while (this.symbol.charAt(i) == buf[start + i]) {
                if (++i >= len) {
                    return this.symbol;
                }
            }
            return null;
        }
    }
}
