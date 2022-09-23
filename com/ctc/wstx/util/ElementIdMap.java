// 
// Decompiled by Procyon v0.5.36
// 

package com.ctc.wstx.util;

import javax.xml.stream.Location;

public final class ElementIdMap
{
    protected static final int DEFAULT_SIZE = 128;
    protected static final int MIN_SIZE = 16;
    protected static final int FILL_PCT = 80;
    protected ElementId[] mTable;
    protected int mSize;
    protected int mSizeThreshold;
    protected int mIndexMask;
    protected ElementId mHead;
    protected ElementId mTail;
    
    public ElementIdMap() {
        this(128);
    }
    
    public ElementIdMap(final int initialSize) {
        int actual;
        for (actual = 16; actual < initialSize; actual += actual) {}
        this.mTable = new ElementId[actual];
        this.mIndexMask = actual - 1;
        this.mSize = 0;
        this.mSizeThreshold = actual * 80 / 100;
        final ElementId elementId = null;
        this.mTail = elementId;
        this.mHead = elementId;
    }
    
    public ElementId getFirstUndefined() {
        return this.mHead;
    }
    
    public ElementId addReferenced(final char[] buffer, final int start, final int len, final int hash, final Location loc, final PrefixedName elemName, final PrefixedName attrName) {
        int index = hash & this.mIndexMask;
        for (ElementId id = this.mTable[index]; id != null; id = id.nextColliding()) {
            if (id.idMatches(buffer, start, len)) {
                return id;
            }
        }
        if (this.mSize >= this.mSizeThreshold) {
            this.rehash();
            index = (hash & this.mIndexMask);
        }
        ++this.mSize;
        final String idStr = new String(buffer, start, len);
        ElementId id = new ElementId(idStr, loc, false, elemName, attrName);
        id.setNextColliding(this.mTable[index]);
        this.mTable[index] = id;
        if (this.mHead == null) {
            final ElementId elementId = id;
            this.mTail = elementId;
            this.mHead = elementId;
        }
        else {
            this.mTail.linkUndefined(id);
            this.mTail = id;
        }
        return id;
    }
    
    public ElementId addReferenced(final String idStr, final Location loc, final PrefixedName elemName, final PrefixedName attrName) {
        final int hash = calcHash(idStr);
        int index = hash & this.mIndexMask;
        for (ElementId id = this.mTable[index]; id != null; id = id.nextColliding()) {
            if (id.idMatches(idStr)) {
                return id;
            }
        }
        if (this.mSize >= this.mSizeThreshold) {
            this.rehash();
            index = (hash & this.mIndexMask);
        }
        ++this.mSize;
        ElementId id = new ElementId(idStr, loc, false, elemName, attrName);
        id.setNextColliding(this.mTable[index]);
        this.mTable[index] = id;
        if (this.mHead == null) {
            final ElementId elementId = id;
            this.mTail = elementId;
            this.mHead = elementId;
        }
        else {
            this.mTail.linkUndefined(id);
            this.mTail = id;
        }
        return id;
    }
    
    public ElementId addDefined(final char[] buffer, final int start, final int len, final int hash, final Location loc, final PrefixedName elemName, final PrefixedName attrName) {
        int index = hash & this.mIndexMask;
        ElementId id;
        for (id = this.mTable[index]; id != null && !id.idMatches(buffer, start, len); id = id.nextColliding()) {}
        if (id == null) {
            if (this.mSize >= this.mSizeThreshold) {
                this.rehash();
                index = (hash & this.mIndexMask);
            }
            ++this.mSize;
            final String idStr = new String(buffer, start, len);
            id = new ElementId(idStr, loc, true, elemName, attrName);
            id.setNextColliding(this.mTable[index]);
            this.mTable[index] = id;
        }
        else if (!id.isDefined()) {
            id.markDefined(loc);
            if (id == this.mHead) {
                do {
                    this.mHead = this.mHead.nextUndefined();
                } while (this.mHead != null && this.mHead.isDefined());
                if (this.mHead == null) {
                    this.mTail = null;
                }
            }
        }
        return id;
    }
    
    public ElementId addDefined(final String idStr, final Location loc, final PrefixedName elemName, final PrefixedName attrName) {
        final int hash = calcHash(idStr);
        int index = hash & this.mIndexMask;
        ElementId id;
        for (id = this.mTable[index]; id != null && !id.idMatches(idStr); id = id.nextColliding()) {}
        if (id == null) {
            if (this.mSize >= this.mSizeThreshold) {
                this.rehash();
                index = (hash & this.mIndexMask);
            }
            ++this.mSize;
            id = new ElementId(idStr, loc, true, elemName, attrName);
            id.setNextColliding(this.mTable[index]);
            this.mTable[index] = id;
        }
        else if (!id.isDefined()) {
            id.markDefined(loc);
            if (id == this.mHead) {
                do {
                    this.mHead = this.mHead.nextUndefined();
                } while (this.mHead != null && this.mHead.isDefined());
                if (this.mHead == null) {
                    this.mTail = null;
                }
            }
        }
        return id;
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
    
    private void rehash() {
        final int size = this.mTable.length;
        final int newSize = size << 2;
        final ElementId[] oldSyms = this.mTable;
        this.mTable = new ElementId[newSize];
        this.mIndexMask = newSize - 1;
        this.mSizeThreshold <<= 2;
        int count = 0;
        for (ElementId id : oldSyms) {
            while (id != null) {
                ++count;
                final int index = calcHash(id.getId()) & this.mIndexMask;
                final ElementId nextIn = id.nextColliding();
                id.setNextColliding(this.mTable[index]);
                this.mTable[index] = id;
                id = nextIn;
            }
        }
        if (count != this.mSize) {
            ExceptionUtil.throwInternal("on rehash(): had " + this.mSize + " entries; now have " + count + ".");
        }
    }
}
