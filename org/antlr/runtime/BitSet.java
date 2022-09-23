// 
// Decompiled by Procyon v0.5.36
// 

package org.antlr.runtime;

import java.util.List;

public class BitSet implements Cloneable
{
    protected static final int BITS = 64;
    protected static final int LOG_BITS = 6;
    protected static final int MOD_MASK = 63;
    protected long[] bits;
    
    public BitSet() {
        this(64);
    }
    
    public BitSet(final long[] bits_) {
        this.bits = bits_;
    }
    
    public BitSet(final List items) {
        this();
        for (int i = 0; i < items.size(); ++i) {
            final Integer v = items.get(i);
            this.add(v);
        }
    }
    
    public BitSet(final int nbits) {
        this.bits = new long[(nbits - 1 >> 6) + 1];
    }
    
    public static BitSet of(final int el) {
        final BitSet s = new BitSet(el + 1);
        s.add(el);
        return s;
    }
    
    public static BitSet of(final int a, final int b) {
        final BitSet s = new BitSet(Math.max(a, b) + 1);
        s.add(a);
        s.add(b);
        return s;
    }
    
    public static BitSet of(final int a, final int b, final int c) {
        final BitSet s = new BitSet();
        s.add(a);
        s.add(b);
        s.add(c);
        return s;
    }
    
    public static BitSet of(final int a, final int b, final int c, final int d) {
        final BitSet s = new BitSet();
        s.add(a);
        s.add(b);
        s.add(c);
        s.add(d);
        return s;
    }
    
    public BitSet or(final BitSet a) {
        if (a == null) {
            return this;
        }
        final BitSet s = (BitSet)this.clone();
        s.orInPlace(a);
        return s;
    }
    
    public void add(final int el) {
        final int n = wordNumber(el);
        if (n >= this.bits.length) {
            this.growToInclude(el);
        }
        final long[] bits = this.bits;
        final int n2 = n;
        bits[n2] |= bitMask(el);
    }
    
    public void growToInclude(final int bit) {
        final int newSize = Math.max(this.bits.length << 1, this.numWordsToHold(bit));
        final long[] newbits = new long[newSize];
        System.arraycopy(this.bits, 0, newbits, 0, this.bits.length);
        this.bits = newbits;
    }
    
    public void orInPlace(final BitSet a) {
        if (a == null) {
            return;
        }
        if (a.bits.length > this.bits.length) {
            this.setSize(a.bits.length);
        }
        final int min = Math.min(this.bits.length, a.bits.length);
        for (int i = min - 1; i >= 0; --i) {
            final long[] bits = this.bits;
            final int n = i;
            bits[n] |= a.bits[i];
        }
    }
    
    private void setSize(final int nwords) {
        final long[] newbits = new long[nwords];
        final int n = Math.min(nwords, this.bits.length);
        System.arraycopy(this.bits, 0, newbits, 0, n);
        this.bits = newbits;
    }
    
    private static final long bitMask(final int bitNumber) {
        final int bitPosition = bitNumber & 0x3F;
        return 1L << bitPosition;
    }
    
    public Object clone() {
        BitSet s;
        try {
            s = (BitSet)super.clone();
            s.bits = new long[this.bits.length];
            System.arraycopy(this.bits, 0, s.bits, 0, this.bits.length);
        }
        catch (CloneNotSupportedException e) {
            throw new InternalError();
        }
        return s;
    }
    
    public int size() {
        int deg = 0;
        for (int i = this.bits.length - 1; i >= 0; --i) {
            final long word = this.bits[i];
            if (word != 0L) {
                for (int bit = 63; bit >= 0; --bit) {
                    if ((word & 1L << bit) != 0x0L) {
                        ++deg;
                    }
                }
            }
        }
        return deg;
    }
    
    public boolean equals(final Object other) {
        if (other == null || !(other instanceof BitSet)) {
            return false;
        }
        final BitSet otherSet = (BitSet)other;
        final int n = Math.min(this.bits.length, otherSet.bits.length);
        for (int i = 0; i < n; ++i) {
            if (this.bits[i] != otherSet.bits[i]) {
                return false;
            }
        }
        if (this.bits.length > n) {
            for (int i = n + 1; i < this.bits.length; ++i) {
                if (this.bits[i] != 0L) {
                    return false;
                }
            }
        }
        else if (otherSet.bits.length > n) {
            for (int i = n + 1; i < otherSet.bits.length; ++i) {
                if (otherSet.bits[i] != 0L) {
                    return false;
                }
            }
        }
        return true;
    }
    
    public boolean member(final int el) {
        if (el < 0) {
            return false;
        }
        final int n = wordNumber(el);
        return n < this.bits.length && (this.bits[n] & bitMask(el)) != 0x0L;
    }
    
    public void remove(final int el) {
        final int n = wordNumber(el);
        if (n < this.bits.length) {
            final long[] bits = this.bits;
            final int n2 = n;
            bits[n2] &= ~bitMask(el);
        }
    }
    
    public boolean isNil() {
        for (int i = this.bits.length - 1; i >= 0; --i) {
            if (this.bits[i] != 0L) {
                return false;
            }
        }
        return true;
    }
    
    private final int numWordsToHold(final int el) {
        return (el >> 6) + 1;
    }
    
    public int numBits() {
        return this.bits.length << 6;
    }
    
    public int lengthInLongWords() {
        return this.bits.length;
    }
    
    public int[] toArray() {
        final int[] elems = new int[this.size()];
        int en = 0;
        for (int i = 0; i < this.bits.length << 6; ++i) {
            if (this.member(i)) {
                elems[en++] = i;
            }
        }
        return elems;
    }
    
    public long[] toPackedArray() {
        return this.bits;
    }
    
    private static final int wordNumber(final int bit) {
        return bit >> 6;
    }
    
    public String toString() {
        return this.toString(null);
    }
    
    public String toString(final String[] tokenNames) {
        final StringBuffer buf = new StringBuffer();
        final String separator = ",";
        boolean havePrintedAnElement = false;
        buf.append('{');
        for (int i = 0; i < this.bits.length << 6; ++i) {
            if (this.member(i)) {
                if (i > 0 && havePrintedAnElement) {
                    buf.append(separator);
                }
                if (tokenNames != null) {
                    buf.append(tokenNames[i]);
                }
                else {
                    buf.append(i);
                }
                havePrintedAnElement = true;
            }
        }
        buf.append('}');
        return buf.toString();
    }
}
