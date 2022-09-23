// 
// Decompiled by Procyon v0.5.36
// 

package com.ctc.wstx.util;

public class SymbolTable
{
    protected static final int DEFAULT_TABLE_SIZE = 128;
    protected static final float DEFAULT_FILL_FACTOR = 0.75f;
    protected static final String EMPTY_STRING = "";
    protected boolean mInternStrings;
    protected String[] mSymbols;
    protected Bucket[] mBuckets;
    protected int mSize;
    protected int mSizeThreshold;
    protected int mIndexMask;
    protected int mThisVersion;
    protected boolean mDirty;
    
    public SymbolTable() {
        this(true);
    }
    
    public SymbolTable(final boolean internStrings) {
        this(internStrings, 128);
    }
    
    public SymbolTable(final boolean internStrings, final int initialSize) {
        this(internStrings, initialSize, 0.75f);
    }
    
    public SymbolTable(final boolean internStrings, int initialSize, final float fillFactor) {
        this.mInternStrings = internStrings;
        this.mThisVersion = 1;
        this.mDirty = true;
        if (initialSize < 1) {
            throw new IllegalArgumentException("Can not use negative/zero initial size: " + initialSize);
        }
        int currSize;
        for (currSize = 4; currSize < initialSize; currSize += currSize) {}
        initialSize = currSize;
        this.mSymbols = new String[initialSize];
        this.mBuckets = new Bucket[initialSize >> 1];
        this.mIndexMask = initialSize - 1;
        this.mSize = 0;
        if (fillFactor < 0.01f) {
            throw new IllegalArgumentException("Fill factor can not be lower than 0.01.");
        }
        if (fillFactor > 10.0f) {
            throw new IllegalArgumentException("Fill factor can not be higher than 10.0.");
        }
        this.mSizeThreshold = (int)(initialSize * fillFactor + 0.5);
    }
    
    private SymbolTable(final boolean internStrings, final String[] symbols, final Bucket[] buckets, final int size, final int sizeThreshold, final int indexMask, final int version) {
        this.mInternStrings = internStrings;
        this.mSymbols = symbols;
        this.mBuckets = buckets;
        this.mSize = size;
        this.mSizeThreshold = sizeThreshold;
        this.mIndexMask = indexMask;
        this.mThisVersion = version;
        this.mDirty = false;
    }
    
    public SymbolTable makeChild() {
        final boolean internStrings;
        final String[] symbols;
        final Bucket[] buckets;
        final int size;
        final int sizeThreshold;
        final int indexMask;
        final int version;
        synchronized (this) {
            internStrings = this.mInternStrings;
            symbols = this.mSymbols;
            buckets = this.mBuckets;
            size = this.mSize;
            sizeThreshold = this.mSizeThreshold;
            indexMask = this.mIndexMask;
            version = this.mThisVersion + 1;
        }
        return new SymbolTable(internStrings, symbols, buckets, size, sizeThreshold, indexMask, version);
    }
    
    public synchronized void mergeChild(final SymbolTable child) {
        if (child.size() <= this.size()) {
            return;
        }
        this.mSymbols = child.mSymbols;
        this.mBuckets = child.mBuckets;
        this.mSize = child.mSize;
        this.mSizeThreshold = child.mSizeThreshold;
        this.mIndexMask = child.mIndexMask;
        ++this.mThisVersion;
        this.mDirty = false;
        child.mDirty = false;
    }
    
    public void setInternStrings(final boolean state) {
        this.mInternStrings = state;
    }
    
    public int size() {
        return this.mSize;
    }
    
    public int version() {
        return this.mThisVersion;
    }
    
    public boolean isDirty() {
        return this.mDirty;
    }
    
    public boolean isDirectChildOf(final SymbolTable t) {
        return this.mThisVersion == t.mThisVersion + 1;
    }
    
    public String findSymbol(final char[] buffer, final int start, final int len, int hash) {
        if (len < 1) {
            return "";
        }
        hash &= this.mIndexMask;
        String sym = this.mSymbols[hash];
        if (sym != null) {
            Label_0080: {
                if (sym.length() == len) {
                    int i = 0;
                    while (true) {
                        while (sym.charAt(i) == buffer[start + i]) {
                            if (++i >= len) {
                                if (i == len) {
                                    return sym;
                                }
                                break Label_0080;
                            }
                        }
                        continue;
                    }
                }
            }
            final Bucket b = this.mBuckets[hash >> 1];
            if (b != null) {
                sym = b.find(buffer, start, len);
                if (sym != null) {
                    return sym;
                }
            }
        }
        if (this.mSize >= this.mSizeThreshold) {
            this.rehash();
            hash = (calcHash(buffer, start, len) & this.mIndexMask);
        }
        else if (!this.mDirty) {
            this.copyArrays();
            this.mDirty = true;
        }
        ++this.mSize;
        String newSymbol = new String(buffer, start, len);
        if (this.mInternStrings) {
            newSymbol = newSymbol.intern();
        }
        if (this.mSymbols[hash] == null) {
            this.mSymbols[hash] = newSymbol;
        }
        else {
            final int bix = hash >> 1;
            this.mBuckets[bix] = new Bucket(newSymbol, this.mBuckets[bix]);
        }
        return newSymbol;
    }
    
    public String findSymbolIfExists(final char[] buffer, final int start, final int len, int hash) {
        if (len < 1) {
            return "";
        }
        hash &= this.mIndexMask;
        String sym = this.mSymbols[hash];
        if (sym != null) {
            Label_0080: {
                if (sym.length() == len) {
                    int i = 0;
                    while (true) {
                        while (sym.charAt(i) == buffer[start + i]) {
                            if (++i >= len) {
                                if (i == len) {
                                    return sym;
                                }
                                break Label_0080;
                            }
                        }
                        continue;
                    }
                }
            }
            final Bucket b = this.mBuckets[hash >> 1];
            if (b != null) {
                sym = b.find(buffer, start, len);
                if (sym != null) {
                    return sym;
                }
            }
        }
        return null;
    }
    
    public String findSymbol(String str) {
        final int len = str.length();
        if (len < 1) {
            return "";
        }
        int index = calcHash(str) & this.mIndexMask;
        String sym = this.mSymbols[index];
        if (sym != null) {
            if (sym.length() == len) {
                int i;
                for (i = 0; i < len && sym.charAt(i) == str.charAt(i); ++i) {}
                if (i == len) {
                    return sym;
                }
            }
            final Bucket b = this.mBuckets[index >> 1];
            if (b != null) {
                sym = b.find(str);
                if (sym != null) {
                    return sym;
                }
            }
        }
        if (this.mSize >= this.mSizeThreshold) {
            this.rehash();
            index = (calcHash(str) & this.mIndexMask);
        }
        else if (!this.mDirty) {
            this.copyArrays();
            this.mDirty = true;
        }
        ++this.mSize;
        if (this.mInternStrings) {
            str = str.intern();
        }
        if (this.mSymbols[index] == null) {
            this.mSymbols[index] = str;
        }
        else {
            final int bix = index >> 1;
            this.mBuckets[bix] = new Bucket(str, this.mBuckets[bix]);
        }
        return str;
    }
    
    public static int calcHash(final char[] buffer, final int start, final int len) {
        int hash = buffer[start];
        for (int i = 1; i < len; ++i) {
            hash = hash * 31 + buffer[start + i];
        }
        return hash;
    }
    
    public static int calcHash(final String key) {
        int hash = key.charAt(0);
        for (int i = 1, len = key.length(); i < len; ++i) {
            hash = hash * 31 + key.charAt(i);
        }
        return hash;
    }
    
    private void copyArrays() {
        final String[] oldSyms = this.mSymbols;
        int size = oldSyms.length;
        System.arraycopy(oldSyms, 0, this.mSymbols = new String[size], 0, size);
        final Bucket[] oldBuckets = this.mBuckets;
        size = oldBuckets.length;
        System.arraycopy(oldBuckets, 0, this.mBuckets = new Bucket[size], 0, size);
    }
    
    private void rehash() {
        int size = this.mSymbols.length;
        final int newSize = size + size;
        final String[] oldSyms = this.mSymbols;
        final Bucket[] oldBuckets = this.mBuckets;
        this.mSymbols = new String[newSize];
        this.mBuckets = new Bucket[newSize >> 1];
        this.mIndexMask = newSize - 1;
        this.mSizeThreshold += this.mSizeThreshold;
        int count = 0;
        for (final String symbol : oldSyms) {
            if (symbol != null) {
                ++count;
                final int index = calcHash(symbol) & this.mIndexMask;
                if (this.mSymbols[index] == null) {
                    this.mSymbols[index] = symbol;
                }
                else {
                    final int bix = index >> 1;
                    this.mBuckets[bix] = new Bucket(symbol, this.mBuckets[bix]);
                }
            }
        }
        size >>= 1;
        for (Bucket b : oldBuckets) {
            while (b != null) {
                ++count;
                final String symbol2 = b.getSymbol();
                final int index2 = calcHash(symbol2) & this.mIndexMask;
                if (this.mSymbols[index2] == null) {
                    this.mSymbols[index2] = symbol2;
                }
                else {
                    final int bix2 = index2 >> 1;
                    this.mBuckets[bix2] = new Bucket(symbol2, this.mBuckets[bix2]);
                }
                b = b.getNext();
            }
        }
        if (count != this.mSize) {
            throw new IllegalStateException("Internal error on SymbolTable.rehash(): had " + this.mSize + " entries; now have " + count + ".");
        }
    }
    
    public double calcAvgSeek() {
        int count = 0;
        for (int i = 0, len = this.mSymbols.length; i < len; ++i) {
            if (this.mSymbols[i] != null) {
                ++count;
            }
        }
        for (int i = 0, len = this.mBuckets.length; i < len; ++i) {
            Bucket b = this.mBuckets[i];
            int cost = 2;
            while (b != null) {
                count += cost;
                ++cost;
                b = b.getNext();
            }
        }
        return count / (double)this.mSize;
    }
    
    static final class Bucket
    {
        private final String mSymbol;
        private final Bucket mNext;
        
        public Bucket(final String symbol, final Bucket next) {
            this.mSymbol = symbol;
            this.mNext = next;
        }
        
        public String getSymbol() {
            return this.mSymbol;
        }
        
        public Bucket getNext() {
            return this.mNext;
        }
        
        public String find(final char[] buf, final int start, final int len) {
            String sym = this.mSymbol;
            Bucket b = this.mNext;
            while (true) {
                Label_0061: {
                    if (sym.length() == len) {
                        int i = 0;
                        while (true) {
                            while (sym.charAt(i) == buf[start + i]) {
                                if (++i >= len) {
                                    if (i == len) {
                                        return sym;
                                    }
                                    break Label_0061;
                                }
                            }
                            continue;
                        }
                    }
                }
                if (b == null) {
                    return null;
                }
                sym = b.getSymbol();
                b = b.getNext();
            }
        }
        
        public String find(final String str) {
            String sym = this.mSymbol;
            for (Bucket b = this.mNext; !sym.equals(str); sym = b.getSymbol(), b = b.getNext()) {
                if (b == null) {
                    return null;
                }
            }
            return sym;
        }
    }
}
