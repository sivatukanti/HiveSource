// 
// Decompiled by Procyon v0.5.36
// 

package com.ctc.wstx.dtd;

import java.util.Iterator;
import java.util.TreeSet;
import com.ctc.wstx.util.PrefixedName;

public final class LargePrefixedNameSet extends PrefixedNameSet
{
    static final int MIN_HASH_AREA = 8;
    final boolean mNsAware;
    final PrefixedName[] mNames;
    final Bucket[] mBuckets;
    
    public LargePrefixedNameSet(final boolean nsAware, final PrefixedName[] names) {
        this.mNsAware = nsAware;
        final int len = names.length;
        int minSize;
        int tableSize;
        for (minSize = len + (len + 7 >> 3), tableSize = 8; tableSize < minSize; tableSize += tableSize) {}
        this.mNames = new PrefixedName[tableSize];
        Bucket[] buckets = null;
        final int mask = tableSize - 1;
        for (final PrefixedName nk : names) {
            int ix = nk.hashCode() & mask;
            if (this.mNames[ix] == null) {
                this.mNames[ix] = nk;
            }
            else {
                ix >>= 2;
                Bucket old;
                if (buckets == null) {
                    buckets = new Bucket[tableSize >> 2];
                    old = null;
                }
                else {
                    old = buckets[ix];
                }
                buckets[ix] = new Bucket(nk, old);
            }
        }
        this.mBuckets = buckets;
    }
    
    @Override
    public boolean hasMultiple() {
        return true;
    }
    
    @Override
    public boolean contains(final PrefixedName name) {
        final PrefixedName[] hashArea = this.mNames;
        final int index = name.hashCode() & hashArea.length - 1;
        PrefixedName res = hashArea[index];
        if (res != null && res.equals(name)) {
            return true;
        }
        final Bucket[] buckets = this.mBuckets;
        if (buckets != null) {
            for (Bucket bucket = buckets[index >> 2]; bucket != null; bucket = bucket.getNext()) {
                res = bucket.getName();
                if (res.equals(name)) {
                    return true;
                }
            }
        }
        return false;
    }
    
    @Override
    public void appendNames(final StringBuilder sb, final String sep) {
        final TreeSet<PrefixedName> ts = new TreeSet<PrefixedName>();
        for (int i = 0; i < this.mNames.length; ++i) {
            final PrefixedName name = this.mNames[i];
            if (name != null) {
                ts.add(name);
            }
        }
        if (this.mBuckets != null) {
            for (int i = 0; i < this.mNames.length >> 2; ++i) {
                for (Bucket b = this.mBuckets[i]; b != null; b = b.getNext()) {
                    ts.add(b.getName());
                }
            }
        }
        final Iterator<PrefixedName> it = ts.iterator();
        boolean first = true;
        while (it.hasNext()) {
            if (first) {
                first = false;
            }
            else {
                sb.append(sep);
            }
            sb.append(it.next().toString());
        }
    }
    
    private static final class Bucket
    {
        final PrefixedName mName;
        final Bucket mNext;
        
        public Bucket(final PrefixedName name, final Bucket next) {
            this.mName = name;
            this.mNext = next;
        }
        
        public PrefixedName getName() {
            return this.mName;
        }
        
        public Bucket getNext() {
            return this.mNext;
        }
    }
}
