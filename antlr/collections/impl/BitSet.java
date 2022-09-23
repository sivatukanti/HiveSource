// 
// Decompiled by Procyon v0.5.36
// 

package antlr.collections.impl;

import antlr.CharFormatter;

public class BitSet implements Cloneable
{
    protected static final int BITS = 64;
    protected static final int NIBBLE = 4;
    protected static final int LOG_BITS = 6;
    protected static final int MOD_MASK = 63;
    protected long[] bits;
    
    public BitSet() {
        this(64);
    }
    
    public BitSet(final long[] bits) {
        this.bits = bits;
    }
    
    public BitSet(final int n) {
        this.bits = new long[(n - 1 >> 6) + 1];
    }
    
    public void add(final int n) {
        final int wordNumber = wordNumber(n);
        if (wordNumber >= this.bits.length) {
            this.growToInclude(n);
        }
        final long[] bits = this.bits;
        final int n2 = wordNumber;
        bits[n2] |= bitMask(n);
    }
    
    public BitSet and(final BitSet set) {
        final BitSet set2 = (BitSet)this.clone();
        set2.andInPlace(set);
        return set2;
    }
    
    public void andInPlace(final BitSet set) {
        final int min = Math.min(this.bits.length, set.bits.length);
        for (int i = min - 1; i >= 0; --i) {
            final long[] bits = this.bits;
            final int n = i;
            bits[n] &= set.bits[i];
        }
        for (int j = min; j < this.bits.length; ++j) {
            this.bits[j] = 0L;
        }
    }
    
    private static final long bitMask(final int n) {
        return 1L << (n & 0x3F);
    }
    
    public void clear() {
        for (int i = this.bits.length - 1; i >= 0; --i) {
            this.bits[i] = 0L;
        }
    }
    
    public void clear(final int n) {
        final int wordNumber = wordNumber(n);
        if (wordNumber >= this.bits.length) {
            this.growToInclude(n);
        }
        final long[] bits = this.bits;
        final int n2 = wordNumber;
        bits[n2] &= ~bitMask(n);
    }
    
    public Object clone() {
        BitSet set;
        try {
            set = (BitSet)super.clone();
            set.bits = new long[this.bits.length];
            System.arraycopy(this.bits, 0, set.bits, 0, this.bits.length);
        }
        catch (CloneNotSupportedException ex) {
            throw new InternalError();
        }
        return set;
    }
    
    public int degree() {
        int n = 0;
        for (int i = this.bits.length - 1; i >= 0; --i) {
            final long n2 = this.bits[i];
            if (n2 != 0L) {
                for (int j = 63; j >= 0; --j) {
                    if ((n2 & 1L << j) != 0x0L) {
                        ++n;
                    }
                }
            }
        }
        return n;
    }
    
    public boolean equals(final Object o) {
        if (o != null && o instanceof BitSet) {
            final BitSet set = (BitSet)o;
            int min;
            final int n = min = Math.min(this.bits.length, set.bits.length);
            while (min-- > 0) {
                if (this.bits[min] != set.bits[min]) {
                    return false;
                }
            }
            if (this.bits.length > n) {
                int length = this.bits.length;
                while (length-- > n) {
                    if (this.bits[length] != 0L) {
                        return false;
                    }
                }
            }
            else if (set.bits.length > n) {
                int length2 = set.bits.length;
                while (length2-- > n) {
                    if (set.bits[length2] != 0L) {
                        return false;
                    }
                }
            }
            return true;
        }
        return false;
    }
    
    public static Vector getRanges(final int[] array) {
        if (array.length == 0) {
            return null;
        }
        final int n = array[0];
        final int n2 = array[array.length - 1];
        if (array.length <= 2) {
            return null;
        }
        final Vector vector = new Vector(5);
        for (int i = 0; i < array.length - 2; ++i) {
            int n3 = array.length - 1;
            for (int j = i + 1; j < array.length; ++j) {
                if (array[j] != array[j - 1] + 1) {
                    n3 = j - 1;
                    break;
                }
            }
            if (n3 - i > 2) {
                vector.appendElement(new IntRange(array[i], array[n3]));
            }
        }
        return vector;
    }
    
    public void growToInclude(final int n) {
        final long[] bits = new long[Math.max(this.bits.length << 1, this.numWordsToHold(n))];
        System.arraycopy(this.bits, 0, bits, 0, this.bits.length);
        this.bits = bits;
    }
    
    public boolean member(final int n) {
        final int wordNumber = wordNumber(n);
        return wordNumber < this.bits.length && (this.bits[wordNumber] & bitMask(n)) != 0x0L;
    }
    
    public boolean nil() {
        for (int i = this.bits.length - 1; i >= 0; --i) {
            if (this.bits[i] != 0L) {
                return false;
            }
        }
        return true;
    }
    
    public BitSet not() {
        final BitSet set = (BitSet)this.clone();
        set.notInPlace();
        return set;
    }
    
    public void notInPlace() {
        for (int i = this.bits.length - 1; i >= 0; --i) {
            this.bits[i] ^= -1L;
        }
    }
    
    public void notInPlace(final int n) {
        this.notInPlace(0, n);
    }
    
    public void notInPlace(final int n, final int n2) {
        this.growToInclude(n2);
        for (int i = n; i <= n2; ++i) {
            final int wordNumber = wordNumber(i);
            final long[] bits = this.bits;
            final int n3 = wordNumber;
            bits[n3] ^= bitMask(i);
        }
    }
    
    private final int numWordsToHold(final int n) {
        return (n >> 6) + 1;
    }
    
    public static BitSet of(final int n) {
        final BitSet set = new BitSet(n + 1);
        set.add(n);
        return set;
    }
    
    public BitSet or(final BitSet set) {
        final BitSet set2 = (BitSet)this.clone();
        set2.orInPlace(set);
        return set2;
    }
    
    public void orInPlace(final BitSet set) {
        if (set.bits.length > this.bits.length) {
            this.setSize(set.bits.length);
        }
        for (int i = Math.min(this.bits.length, set.bits.length) - 1; i >= 0; --i) {
            final long[] bits = this.bits;
            final int n = i;
            bits[n] |= set.bits[i];
        }
    }
    
    public void remove(final int n) {
        final int wordNumber = wordNumber(n);
        if (wordNumber >= this.bits.length) {
            this.growToInclude(n);
        }
        final long[] bits = this.bits;
        final int n2 = wordNumber;
        bits[n2] &= ~bitMask(n);
    }
    
    private void setSize(final int a) {
        final long[] bits = new long[a];
        System.arraycopy(this.bits, 0, bits, 0, Math.min(a, this.bits.length));
        this.bits = bits;
    }
    
    public int size() {
        return this.bits.length << 6;
    }
    
    public int lengthInLongWords() {
        return this.bits.length;
    }
    
    public boolean subset(final BitSet set) {
        return set != null && set instanceof BitSet && this.and(set).equals(this);
    }
    
    public void subtractInPlace(final BitSet set) {
        if (set == null) {
            return;
        }
        for (int n = 0; n < this.bits.length && n < set.bits.length; ++n) {
            final long[] bits = this.bits;
            final int n2 = n;
            bits[n2] &= ~set.bits[n];
        }
    }
    
    public int[] toArray() {
        final int[] array = new int[this.degree()];
        int n = 0;
        for (int i = 0; i < this.bits.length << 6; ++i) {
            if (this.member(i)) {
                array[n++] = i;
            }
        }
        return array;
    }
    
    public long[] toPackedArray() {
        return this.bits;
    }
    
    public String toString() {
        return this.toString(",");
    }
    
    public String toString(final String str) {
        String s = "";
        for (int i = 0; i < this.bits.length << 6; ++i) {
            if (this.member(i)) {
                if (s.length() > 0) {
                    s += str;
                }
                s += i;
            }
        }
        return s;
    }
    
    public String toString(final String str, final CharFormatter charFormatter) {
        String s = "";
        for (int i = 0; i < this.bits.length << 6; ++i) {
            if (this.member(i)) {
                if (s.length() > 0) {
                    s += str;
                }
                s += charFormatter.literalChar(i);
            }
        }
        return s;
    }
    
    public String toString(final String str, final Vector vector) {
        if (vector == null) {
            return this.toString(str);
        }
        String s = "";
        for (int i = 0; i < this.bits.length << 6; ++i) {
            if (this.member(i)) {
                if (s.length() > 0) {
                    s += str;
                }
                if (i >= vector.size()) {
                    s = s + "<bad element " + i + ">";
                }
                else if (vector.elementAt(i) == null) {
                    s = s + "<" + i + ">";
                }
                else {
                    s += (String)vector.elementAt(i);
                }
            }
        }
        return s;
    }
    
    public String toStringOfHalfWords() {
        String s = new String();
        for (int i = 0; i < this.bits.length; ++i) {
            if (i != 0) {
                s += ", ";
            }
            s = s + (this.bits[i] & 0xFFFFFFFFL) + "UL" + ", " + (this.bits[i] >>> 32 & 0xFFFFFFFFL) + "UL";
        }
        return s;
    }
    
    public String toStringOfWords() {
        String s = new String();
        for (int i = 0; i < this.bits.length; ++i) {
            if (i != 0) {
                s += ", ";
            }
            s = s + this.bits[i] + "L";
        }
        return s;
    }
    
    public String toStringWithRanges(final String str, final CharFormatter charFormatter) {
        String str2 = "";
        final int[] array = this.toArray();
        if (array.length == 0) {
            return "";
        }
        for (int i = 0; i < array.length; ++i) {
            int n = 0;
            for (int n2 = i + 1; n2 < array.length && array[n2] == array[n2 - 1] + 1; ++n2) {
                n = n2;
            }
            if (str2.length() > 0) {
                str2 += str;
            }
            if (n - i >= 2) {
                str2 = str2 + charFormatter.literalChar(array[i]) + ".." + charFormatter.literalChar(array[n]);
                i = n;
            }
            else {
                str2 += charFormatter.literalChar(array[i]);
            }
        }
        return str2;
    }
    
    private static final int wordNumber(final int n) {
        return n >> 6;
    }
}
