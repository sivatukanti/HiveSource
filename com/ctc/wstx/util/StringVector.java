// 
// Decompiled by Procyon v0.5.36
// 

package com.ctc.wstx.util;

public final class StringVector
{
    private String[] mStrings;
    private int mSize;
    
    public StringVector(final int initialCount) {
        this.mStrings = new String[initialCount];
    }
    
    public int size() {
        return this.mSize;
    }
    
    public boolean isEmpty() {
        return this.mSize == 0;
    }
    
    public String getString(final int index) {
        if (index < 0 || index >= this.mSize) {
            throw new IllegalArgumentException("Index " + index + " out of valid range; current size: " + this.mSize + ".");
        }
        return this.mStrings[index];
    }
    
    public String getLastString() {
        if (this.mSize < 1) {
            throw new IllegalStateException("getLastString() called on empty StringVector.");
        }
        return this.mStrings[this.mSize - 1];
    }
    
    public String[] getInternalArray() {
        return this.mStrings;
    }
    
    public String[] asArray() {
        final String[] strs = new String[this.mSize];
        System.arraycopy(this.mStrings, 0, strs, 0, this.mSize);
        return strs;
    }
    
    public boolean containsInterned(final String value) {
        final String[] str = this.mStrings;
        for (int i = 0, len = this.mSize; i < len; ++i) {
            if (str[i] == value) {
                return true;
            }
        }
        return false;
    }
    
    public void addString(final String str) {
        if (this.mSize == this.mStrings.length) {
            final String[] old = this.mStrings;
            final int oldSize = old.length;
            System.arraycopy(old, 0, this.mStrings = new String[oldSize + (oldSize << 1)], 0, oldSize);
        }
        this.mStrings[this.mSize++] = str;
    }
    
    public void addStrings(final String str1, final String str2) {
        if (this.mSize + 2 > this.mStrings.length) {
            final String[] old = this.mStrings;
            final int oldSize = old.length;
            System.arraycopy(old, 0, this.mStrings = new String[oldSize + (oldSize << 1)], 0, oldSize);
        }
        this.mStrings[this.mSize] = str1;
        this.mStrings[this.mSize + 1] = str2;
        this.mSize += 2;
    }
    
    public void setString(final int index, final String str) {
        this.mStrings[index] = str;
    }
    
    public void clear(final boolean removeRefs) {
        if (removeRefs) {
            for (int i = 0, len = this.mSize; i < len; ++i) {
                this.mStrings[i] = null;
            }
        }
        this.mSize = 0;
    }
    
    public String removeLast() {
        final String[] mStrings = this.mStrings;
        final int mSize = this.mSize - 1;
        this.mSize = mSize;
        final String result = mStrings[mSize];
        this.mStrings[this.mSize] = null;
        return result;
    }
    
    public void removeLast(int count) {
        while (--count >= 0) {
            this.mStrings[--this.mSize] = null;
        }
    }
    
    public String findLastFromMap(final String key) {
        int index = this.mSize;
        do {
            index -= 2;
            if (index >= 0) {
                continue;
            }
            return null;
        } while (this.mStrings[index] != key);
        return this.mStrings[index + 1];
    }
    
    public String findLastNonInterned(final String key) {
        int index = this.mSize;
        while (true) {
            index -= 2;
            if (index < 0) {
                return null;
            }
            final String curr = this.mStrings[index];
            if (curr == key || (curr != null && curr.equals(key))) {
                return this.mStrings[index + 1];
            }
        }
    }
    
    public int findLastIndexNonInterned(final String key) {
        int index = this.mSize;
        while (true) {
            index -= 2;
            if (index < 0) {
                return -1;
            }
            final String curr = this.mStrings[index];
            if (curr == key || (curr != null && curr.equals(key))) {
                return index;
            }
        }
    }
    
    public String findLastByValueNonInterned(final String value) {
        for (int index = this.mSize - 1; index > 0; index -= 2) {
            final String currVal = this.mStrings[index];
            if (currVal == value || (currVal != null && currVal.equals(value))) {
                return this.mStrings[index - 1];
            }
        }
        return null;
    }
    
    public int findLastIndexByValueNonInterned(final String value) {
        for (int index = this.mSize - 1; index > 0; index -= 2) {
            final String currVal = this.mStrings[index];
            if (currVal == value || (currVal != null && currVal.equals(value))) {
                return index - 1;
            }
        }
        return -1;
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder(this.mSize * 16);
        sb.append("[(size = ");
        sb.append(this.mSize);
        sb.append(" ) ");
        for (int i = 0; i < this.mSize; ++i) {
            if (i > 0) {
                sb.append(", ");
            }
            sb.append('\"');
            sb.append(this.mStrings[i]);
            sb.append('\"');
            sb.append(" == ");
            sb.append(Integer.toHexString(System.identityHashCode(this.mStrings[i])));
        }
        sb.append(']');
        return sb.toString();
    }
}
