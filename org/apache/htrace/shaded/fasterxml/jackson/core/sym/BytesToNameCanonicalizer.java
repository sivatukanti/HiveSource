// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.htrace.shaded.fasterxml.jackson.core.sym;

import java.util.Arrays;
import org.apache.htrace.shaded.fasterxml.jackson.core.util.InternCache;
import org.apache.htrace.shaded.fasterxml.jackson.core.JsonFactory;
import java.util.BitSet;
import java.util.concurrent.atomic.AtomicReference;

public final class BytesToNameCanonicalizer
{
    private static final int DEFAULT_T_SIZE = 64;
    private static final int MAX_T_SIZE = 65536;
    private static final int MAX_ENTRIES_FOR_REUSE = 6000;
    private static final int MAX_COLL_CHAIN_LENGTH = 100;
    static final int MIN_HASH_SIZE = 16;
    static final int INITIAL_COLLISION_LEN = 32;
    static final int LAST_VALID_BUCKET = 254;
    protected final BytesToNameCanonicalizer _parent;
    protected final AtomicReference<TableInfo> _tableInfo;
    private final int _seed;
    protected boolean _intern;
    protected final boolean _failOnDoS;
    protected int _count;
    protected int _longestCollisionList;
    protected int _hashMask;
    protected int[] _hash;
    protected Name[] _mainNames;
    protected Bucket[] _collList;
    protected int _collCount;
    protected int _collEnd;
    private transient boolean _needRehash;
    private boolean _hashShared;
    private boolean _namesShared;
    private boolean _collListShared;
    protected BitSet _overflows;
    private static final int MULT = 33;
    private static final int MULT2 = 65599;
    private static final int MULT3 = 31;
    
    private BytesToNameCanonicalizer(int sz, final boolean intern, final int seed, final boolean failOnDoS) {
        this._parent = null;
        this._seed = seed;
        this._intern = intern;
        this._failOnDoS = failOnDoS;
        if (sz < 16) {
            sz = 16;
        }
        else if ((sz & sz - 1) != 0x0) {
            int curr;
            for (curr = 16; curr < sz; curr += curr) {}
            sz = curr;
        }
        this._tableInfo = new AtomicReference<TableInfo>(this.initTableInfo(sz));
    }
    
    private BytesToNameCanonicalizer(final BytesToNameCanonicalizer parent, final boolean intern, final int seed, final boolean failOnDoS, final TableInfo state) {
        this._parent = parent;
        this._seed = seed;
        this._intern = intern;
        this._failOnDoS = failOnDoS;
        this._tableInfo = null;
        this._count = state.count;
        this._hashMask = state.mainHashMask;
        this._hash = state.mainHash;
        this._mainNames = state.mainNames;
        this._collList = state.collList;
        this._collCount = state.collCount;
        this._collEnd = state.collEnd;
        this._longestCollisionList = state.longestCollisionList;
        this._needRehash = false;
        this._hashShared = true;
        this._namesShared = true;
        this._collListShared = true;
    }
    
    private TableInfo initTableInfo(final int sz) {
        return new TableInfo(0, sz - 1, new int[sz], new Name[sz], null, 0, 0, 0);
    }
    
    public static BytesToNameCanonicalizer createRoot() {
        final long now = System.currentTimeMillis();
        final int seed = (int)now + (int)(now >>> 32) | 0x1;
        return createRoot(seed);
    }
    
    protected static BytesToNameCanonicalizer createRoot(final int seed) {
        return new BytesToNameCanonicalizer(64, true, seed, true);
    }
    
    public BytesToNameCanonicalizer makeChild(final int flags) {
        return new BytesToNameCanonicalizer(this, JsonFactory.Feature.INTERN_FIELD_NAMES.enabledIn(flags), this._seed, JsonFactory.Feature.FAIL_ON_SYMBOL_HASH_OVERFLOW.enabledIn(flags), this._tableInfo.get());
    }
    
    @Deprecated
    public BytesToNameCanonicalizer makeChild(final boolean canonicalize, final boolean intern) {
        return new BytesToNameCanonicalizer(this, intern, this._seed, true, this._tableInfo.get());
    }
    
    public void release() {
        if (this._parent != null && this.maybeDirty()) {
            this._parent.mergeChild(new TableInfo(this));
            this._hashShared = true;
            this._namesShared = true;
            this._collListShared = true;
        }
    }
    
    private void mergeChild(TableInfo childState) {
        final int childCount = childState.count;
        final TableInfo currState = this._tableInfo.get();
        if (childCount == currState.count) {
            return;
        }
        if (childCount > 6000) {
            childState = this.initTableInfo(64);
        }
        this._tableInfo.compareAndSet(currState, childState);
    }
    
    public int size() {
        if (this._tableInfo != null) {
            return this._tableInfo.get().count;
        }
        return this._count;
    }
    
    public int bucketCount() {
        return this._hash.length;
    }
    
    public boolean maybeDirty() {
        return !this._hashShared;
    }
    
    public int hashSeed() {
        return this._seed;
    }
    
    public int collisionCount() {
        return this._collCount;
    }
    
    public int maxCollisionLength() {
        return this._longestCollisionList;
    }
    
    public static Name getEmptyName() {
        return Name1.getEmptyName();
    }
    
    public Name findName(final int q1) {
        final int hash = this.calcHash(q1);
        final int ix = hash & this._hashMask;
        int val = this._hash[ix];
        if ((val >> 8 ^ hash) << 8 == 0) {
            final Name name = this._mainNames[ix];
            if (name == null) {
                return null;
            }
            if (name.equals(q1)) {
                return name;
            }
        }
        else if (val == 0) {
            return null;
        }
        val &= 0xFF;
        if (val > 0) {
            --val;
            final Bucket bucket = this._collList[val];
            if (bucket != null) {
                return bucket.find(hash, q1, 0);
            }
        }
        return null;
    }
    
    public Name findName(final int q1, final int q2) {
        final int hash = (q2 == 0) ? this.calcHash(q1) : this.calcHash(q1, q2);
        final int ix = hash & this._hashMask;
        int val = this._hash[ix];
        if ((val >> 8 ^ hash) << 8 == 0) {
            final Name name = this._mainNames[ix];
            if (name == null) {
                return null;
            }
            if (name.equals(q1, q2)) {
                return name;
            }
        }
        else if (val == 0) {
            return null;
        }
        val &= 0xFF;
        if (val > 0) {
            --val;
            final Bucket bucket = this._collList[val];
            if (bucket != null) {
                return bucket.find(hash, q1, q2);
            }
        }
        return null;
    }
    
    public Name findName(final int[] q, final int qlen) {
        if (qlen < 3) {
            return this.findName(q[0], (qlen < 2) ? 0 : q[1]);
        }
        final int hash = this.calcHash(q, qlen);
        final int ix = hash & this._hashMask;
        int val = this._hash[ix];
        if ((val >> 8 ^ hash) << 8 == 0) {
            final Name name = this._mainNames[ix];
            if (name == null || name.equals(q, qlen)) {
                return name;
            }
        }
        else if (val == 0) {
            return null;
        }
        val &= 0xFF;
        if (val > 0) {
            --val;
            final Bucket bucket = this._collList[val];
            if (bucket != null) {
                return bucket.find(hash, q, qlen);
            }
        }
        return null;
    }
    
    public Name addName(String name, final int q1, final int q2) {
        if (this._intern) {
            name = InternCache.instance.intern(name);
        }
        final int hash = (q2 == 0) ? this.calcHash(q1) : this.calcHash(q1, q2);
        final Name symbol = constructName(hash, name, q1, q2);
        this._addSymbol(hash, symbol);
        return symbol;
    }
    
    public Name addName(String name, final int[] q, final int qlen) {
        if (this._intern) {
            name = InternCache.instance.intern(name);
        }
        int hash;
        if (qlen < 3) {
            hash = ((qlen == 1) ? this.calcHash(q[0]) : this.calcHash(q[0], q[1]));
        }
        else {
            hash = this.calcHash(q, qlen);
        }
        final Name symbol = constructName(hash, name, q, qlen);
        this._addSymbol(hash, symbol);
        return symbol;
    }
    
    public int calcHash(final int q1) {
        int hash = q1 ^ this._seed;
        hash += hash >>> 15;
        hash ^= hash >>> 9;
        return hash;
    }
    
    public int calcHash(final int q1, final int q2) {
        int hash = q1;
        hash ^= hash >>> 15;
        hash += q2 * 33;
        hash ^= this._seed;
        hash += hash >>> 7;
        return hash;
    }
    
    public int calcHash(final int[] q, final int qlen) {
        if (qlen < 3) {
            throw new IllegalArgumentException();
        }
        int hash = q[0] ^ this._seed;
        hash += hash >>> 9;
        hash *= 33;
        hash += q[1];
        hash *= 65599;
        hash += hash >>> 15;
        hash ^= q[2];
        hash += hash >>> 17;
        for (int i = 3; i < qlen; ++i) {
            hash = (hash * 31 ^ q[i]);
            hash += hash >>> 3;
            hash ^= hash << 7;
        }
        hash += hash >>> 15;
        hash ^= hash << 9;
        return hash;
    }
    
    protected static int[] calcQuads(final byte[] wordBytes) {
        final int blen = wordBytes.length;
        final int[] result = new int[(blen + 3) / 4];
        for (int i = 0; i < blen; ++i) {
            int x = wordBytes[i] & 0xFF;
            if (++i < blen) {
                x = (x << 8 | (wordBytes[i] & 0xFF));
                if (++i < blen) {
                    x = (x << 8 | (wordBytes[i] & 0xFF));
                    if (++i < blen) {
                        x = (x << 8 | (wordBytes[i] & 0xFF));
                    }
                }
            }
            result[i >> 2] = x;
        }
        return result;
    }
    
    private void _addSymbol(final int hash, final Name symbol) {
        if (this._hashShared) {
            this.unshareMain();
        }
        if (this._needRehash) {
            this.rehash();
        }
        ++this._count;
        final int ix = hash & this._hashMask;
        if (this._mainNames[ix] == null) {
            this._hash[ix] = hash << 8;
            if (this._namesShared) {
                this.unshareNames();
            }
            this._mainNames[ix] = symbol;
        }
        else {
            if (this._collListShared) {
                this.unshareCollision();
            }
            ++this._collCount;
            final int entryValue = this._hash[ix];
            int bucket = entryValue & 0xFF;
            if (bucket == 0) {
                if (this._collEnd <= 254) {
                    bucket = this._collEnd;
                    ++this._collEnd;
                    if (bucket >= this._collList.length) {
                        this.expandCollision();
                    }
                }
                else {
                    bucket = this.findBestBucket();
                }
                this._hash[ix] = ((entryValue & 0xFFFFFF00) | bucket + 1);
            }
            else {
                --bucket;
            }
            final Bucket newB = new Bucket(symbol, this._collList[bucket]);
            final int collLen = newB.length;
            if (collLen > 100) {
                this._handleSpillOverflow(bucket, newB);
            }
            else {
                this._collList[bucket] = newB;
                this._longestCollisionList = Math.max(newB.length, this._longestCollisionList);
            }
        }
        final int hashSize = this._hash.length;
        if (this._count > hashSize >> 1) {
            final int hashQuarter = hashSize >> 2;
            if (this._count > hashSize - hashQuarter) {
                this._needRehash = true;
            }
            else if (this._collCount >= hashQuarter) {
                this._needRehash = true;
            }
        }
    }
    
    private void _handleSpillOverflow(final int bindex, final Bucket newBucket) {
        if (this._overflows == null) {
            (this._overflows = new BitSet()).set(bindex);
        }
        else if (this._overflows.get(bindex)) {
            if (this._failOnDoS) {
                this.reportTooManyCollisions(100);
            }
            this._intern = false;
        }
        else {
            this._overflows.set(bindex);
        }
        this._collList[bindex] = null;
        this._count -= newBucket.length;
        this._longestCollisionList = -1;
    }
    
    private void rehash() {
        this._needRehash = false;
        this._namesShared = false;
        final int[] oldMainHash = this._hash;
        final int len = oldMainHash.length;
        final int newLen = len + len;
        if (newLen > 65536) {
            this.nukeSymbols();
            return;
        }
        this._hash = new int[newLen];
        this._hashMask = newLen - 1;
        final Name[] oldNames = this._mainNames;
        this._mainNames = new Name[newLen];
        int symbolsSeen = 0;
        for (final Name symbol : oldNames) {
            if (symbol != null) {
                ++symbolsSeen;
                final int hash = symbol.hashCode();
                final int ix = hash & this._hashMask;
                this._mainNames[ix] = symbol;
                this._hash[ix] = hash << 8;
            }
        }
        final int oldEnd = this._collEnd;
        if (oldEnd == 0) {
            this._longestCollisionList = 0;
            return;
        }
        this._collCount = 0;
        this._collEnd = 0;
        this._collListShared = false;
        int maxColl = 0;
        final Bucket[] oldBuckets = this._collList;
        this._collList = new Bucket[oldBuckets.length];
        for (Bucket curr : oldBuckets) {
            while (curr != null) {
                ++symbolsSeen;
                final Name symbol2 = curr.name;
                final int hash2 = symbol2.hashCode();
                final int ix2 = hash2 & this._hashMask;
                final int val = this._hash[ix2];
                if (this._mainNames[ix2] == null) {
                    this._hash[ix2] = hash2 << 8;
                    this._mainNames[ix2] = symbol2;
                }
                else {
                    ++this._collCount;
                    int bucket = val & 0xFF;
                    if (bucket == 0) {
                        if (this._collEnd <= 254) {
                            bucket = this._collEnd;
                            ++this._collEnd;
                            if (bucket >= this._collList.length) {
                                this.expandCollision();
                            }
                        }
                        else {
                            bucket = this.findBestBucket();
                        }
                        this._hash[ix2] = ((val & 0xFFFFFF00) | bucket + 1);
                    }
                    else {
                        --bucket;
                    }
                    final Bucket newB = new Bucket(symbol2, this._collList[bucket]);
                    this._collList[bucket] = newB;
                    maxColl = Math.max(maxColl, newB.length);
                }
                curr = curr.next;
            }
        }
        this._longestCollisionList = maxColl;
        if (symbolsSeen != this._count) {
            throw new RuntimeException("Internal error: count after rehash " + symbolsSeen + "; should be " + this._count);
        }
    }
    
    private void nukeSymbols() {
        this._count = 0;
        this._longestCollisionList = 0;
        Arrays.fill(this._hash, 0);
        Arrays.fill(this._mainNames, null);
        Arrays.fill(this._collList, null);
        this._collCount = 0;
        this._collEnd = 0;
    }
    
    private int findBestBucket() {
        final Bucket[] buckets = this._collList;
        int bestCount = Integer.MAX_VALUE;
        int bestIx = -1;
        for (int i = 0, len = this._collEnd; i < len; ++i) {
            final int count = buckets[i].length;
            if (count < bestCount) {
                if (count == 1) {
                    return i;
                }
                bestCount = count;
                bestIx = i;
            }
        }
        return bestIx;
    }
    
    private void unshareMain() {
        final int[] old = this._hash;
        this._hash = Arrays.copyOf(old, old.length);
        this._hashShared = false;
    }
    
    private void unshareCollision() {
        final Bucket[] old = this._collList;
        if (old == null) {
            this._collList = new Bucket[32];
        }
        else {
            this._collList = Arrays.copyOf(old, old.length);
        }
        this._collListShared = false;
    }
    
    private void unshareNames() {
        final Name[] old = this._mainNames;
        this._mainNames = Arrays.copyOf(old, old.length);
        this._namesShared = false;
    }
    
    private void expandCollision() {
        final Bucket[] old = this._collList;
        this._collList = Arrays.copyOf(old, old.length * 2);
    }
    
    private static Name constructName(final int hash, final String name, final int q1, final int q2) {
        if (q2 == 0) {
            return new Name1(name, hash, q1);
        }
        return new Name2(name, hash, q1, q2);
    }
    
    private static Name constructName(final int hash, final String name, final int[] quads, final int qlen) {
        if (qlen < 4) {
            switch (qlen) {
                case 1: {
                    return new Name1(name, hash, quads[0]);
                }
                case 2: {
                    return new Name2(name, hash, quads[0], quads[1]);
                }
                case 3: {
                    return new Name3(name, hash, quads[0], quads[1], quads[2]);
                }
            }
        }
        return NameN.construct(name, hash, quads, qlen);
    }
    
    protected void reportTooManyCollisions(final int maxLen) {
        throw new IllegalStateException("Longest collision chain in symbol table (of size " + this._count + ") now exceeds maximum, " + maxLen + " -- suspect a DoS attack based on hash collisions");
    }
    
    private static final class TableInfo
    {
        public final int count;
        public final int mainHashMask;
        public final int[] mainHash;
        public final Name[] mainNames;
        public final Bucket[] collList;
        public final int collCount;
        public final int collEnd;
        public final int longestCollisionList;
        
        public TableInfo(final int count, final int mainHashMask, final int[] mainHash, final Name[] mainNames, final Bucket[] collList, final int collCount, final int collEnd, final int longestCollisionList) {
            this.count = count;
            this.mainHashMask = mainHashMask;
            this.mainHash = mainHash;
            this.mainNames = mainNames;
            this.collList = collList;
            this.collCount = collCount;
            this.collEnd = collEnd;
            this.longestCollisionList = longestCollisionList;
        }
        
        public TableInfo(final BytesToNameCanonicalizer src) {
            this.count = src._count;
            this.mainHashMask = src._hashMask;
            this.mainHash = src._hash;
            this.mainNames = src._mainNames;
            this.collList = src._collList;
            this.collCount = src._collCount;
            this.collEnd = src._collEnd;
            this.longestCollisionList = src._longestCollisionList;
        }
    }
    
    private static final class Bucket
    {
        protected final Name name;
        protected final Bucket next;
        private final int hash;
        private final int length;
        
        Bucket(final Name name, final Bucket next) {
            this.name = name;
            this.next = next;
            this.length = ((next == null) ? 1 : (next.length + 1));
            this.hash = name.hashCode();
        }
        
        public Name find(final int h, final int firstQuad, final int secondQuad) {
            if (this.hash == h && this.name.equals(firstQuad, secondQuad)) {
                return this.name;
            }
            for (Bucket curr = this.next; curr != null; curr = curr.next) {
                if (curr.hash == h) {
                    final Name currName = curr.name;
                    if (currName.equals(firstQuad, secondQuad)) {
                        return currName;
                    }
                }
            }
            return null;
        }
        
        public Name find(final int h, final int[] quads, final int qlen) {
            if (this.hash == h && this.name.equals(quads, qlen)) {
                return this.name;
            }
            for (Bucket curr = this.next; curr != null; curr = curr.next) {
                if (curr.hash == h) {
                    final Name currName = curr.name;
                    if (currName.equals(quads, qlen)) {
                        return currName;
                    }
                }
            }
            return null;
        }
    }
}
