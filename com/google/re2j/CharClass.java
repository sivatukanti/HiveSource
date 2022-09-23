// 
// Decompiled by Procyon v0.5.36
// 

package com.google.re2j;

class CharClass
{
    private int[] r;
    private int len;
    
    CharClass(final int[] r) {
        this.r = r;
        this.len = r.length;
    }
    
    CharClass() {
        this.r = Utils.EMPTY_INTS;
        this.len = 0;
    }
    
    private void ensureCapacity(int newLen) {
        if (this.r.length < newLen) {
            if (newLen < this.len * 2) {
                newLen = this.len * 2;
            }
            final int[] r2 = new int[newLen];
            System.arraycopy(this.r, 0, r2, 0, this.len);
            this.r = r2;
        }
    }
    
    int[] toArray() {
        if (this.len == this.r.length) {
            return this.r;
        }
        final int[] r2 = new int[this.len];
        System.arraycopy(this.r, 0, r2, 0, this.len);
        return r2;
    }
    
    CharClass cleanClass() {
        if (this.len < 4) {
            return this;
        }
        qsortIntPair(this.r, 0, this.len - 2);
        int w = 2;
        for (int i = 2; i < this.len; i += 2) {
            final int lo = this.r[i];
            final int hi = this.r[i + 1];
            if (lo <= this.r[w - 1] + 1) {
                if (hi > this.r[w - 1]) {
                    this.r[w - 1] = hi;
                }
            }
            else {
                this.r[w] = lo;
                this.r[w + 1] = hi;
                w += 2;
            }
        }
        this.len = w;
        return this;
    }
    
    CharClass appendLiteral(final int x, final int flags) {
        return ((flags & 0x1) != 0x0) ? this.appendFoldedRange(x, x) : this.appendRange(x, x);
    }
    
    CharClass appendRange(final int lo, final int hi) {
        if (this.len > 0) {
            for (int i = 2; i <= 4; i += 2) {
                if (this.len >= i) {
                    final int rlo = this.r[this.len - i];
                    final int rhi = this.r[this.len - i + 1];
                    if (lo <= rhi + 1 && rlo <= hi + 1) {
                        if (lo < rlo) {
                            this.r[this.len - i] = lo;
                        }
                        if (hi > rhi) {
                            this.r[this.len - i + 1] = hi;
                        }
                        return this;
                    }
                }
            }
        }
        this.ensureCapacity(this.len + 2);
        this.r[this.len++] = lo;
        this.r[this.len++] = hi;
        return this;
    }
    
    CharClass appendFoldedRange(int lo, int hi) {
        if (lo <= 65 && hi >= 66639) {
            return this.appendRange(lo, hi);
        }
        if (hi < 65 || lo > 66639) {
            return this.appendRange(lo, hi);
        }
        if (lo < 65) {
            this.appendRange(lo, 64);
            lo = 65;
        }
        if (hi > 66639) {
            this.appendRange(66640, hi);
            hi = 66639;
        }
        for (int c = lo; c <= hi; ++c) {
            this.appendRange(c, c);
            for (int f = Unicode.simpleFold(c); f != c; f = Unicode.simpleFold(f)) {
                this.appendRange(f, f);
            }
        }
        return this;
    }
    
    CharClass appendClass(final int[] x) {
        for (int i = 0; i < x.length; i += 2) {
            this.appendRange(x[i], x[i + 1]);
        }
        return this;
    }
    
    CharClass appendFoldedClass(final int[] x) {
        for (int i = 0; i < x.length; i += 2) {
            this.appendFoldedRange(x[i], x[i + 1]);
        }
        return this;
    }
    
    CharClass appendNegatedClass(final int[] x) {
        int nextLo = 0;
        for (int i = 0; i < x.length; i += 2) {
            final int lo = x[i];
            final int hi = x[i + 1];
            if (nextLo <= lo - 1) {
                this.appendRange(nextLo, lo - 1);
            }
            nextLo = hi + 1;
        }
        if (nextLo <= 1114111) {
            this.appendRange(nextLo, 1114111);
        }
        return this;
    }
    
    CharClass appendTable(final int[][] table) {
        for (final int[] triple : table) {
            final int lo = triple[0];
            final int hi = triple[1];
            final int stride = triple[2];
            if (stride == 1) {
                this.appendRange(lo, hi);
            }
            else {
                for (int c = lo; c <= hi; c += stride) {
                    this.appendRange(c, c);
                }
            }
        }
        return this;
    }
    
    CharClass appendNegatedTable(final int[][] table) {
        int nextLo = 0;
        for (final int[] triple : table) {
            final int lo = triple[0];
            final int hi = triple[1];
            final int stride = triple[2];
            if (stride == 1) {
                if (nextLo <= lo - 1) {
                    this.appendRange(nextLo, lo - 1);
                }
                nextLo = hi + 1;
            }
            else {
                for (int c = lo; c <= hi; c += stride) {
                    if (nextLo <= c - 1) {
                        this.appendRange(nextLo, c - 1);
                    }
                    nextLo = c + 1;
                }
            }
        }
        if (nextLo <= 1114111) {
            this.appendRange(nextLo, 1114111);
        }
        return this;
    }
    
    CharClass appendTableWithSign(final int[][] table, final int sign) {
        return (sign < 0) ? this.appendNegatedTable(table) : this.appendTable(table);
    }
    
    CharClass negateClass() {
        int nextLo = 0;
        int w = 0;
        for (int i = 0; i < this.len; i += 2) {
            final int lo = this.r[i];
            final int hi = this.r[i + 1];
            if (nextLo <= lo - 1) {
                this.r[w] = nextLo;
                this.r[w + 1] = lo - 1;
                w += 2;
            }
            nextLo = hi + 1;
        }
        this.len = w;
        if (nextLo <= 1114111) {
            this.ensureCapacity(this.len + 2);
            this.r[this.len++] = nextLo;
            this.r[this.len++] = 1114111;
        }
        return this;
    }
    
    CharClass appendClassWithSign(final int[] x, final int sign) {
        return (sign < 0) ? this.appendNegatedClass(x) : this.appendClass(x);
    }
    
    CharClass appendGroup(final CharGroup g, final boolean foldCase) {
        int[] cls = g.cls;
        if (foldCase) {
            cls = new CharClass().appendFoldedClass(cls).cleanClass().toArray();
        }
        return this.appendClassWithSign(cls, g.sign);
    }
    
    private static int cmp(final int[] array, final int i, final int pivotFrom, final int pivotTo) {
        final int cmp = array[i] - pivotFrom;
        return (cmp != 0) ? cmp : (pivotTo - array[i + 1]);
    }
    
    private static void qsortIntPair(final int[] array, final int left, final int right) {
        final int pivotIndex = (left + right) / 2 & 0xFFFFFFFE;
        final int pivotFrom = array[pivotIndex];
        final int pivotTo = array[pivotIndex + 1];
        int i;
        int j;
        for (i = left, j = right; i <= j; i += 2, j -= 2) {
            while (i < right && cmp(array, i, pivotFrom, pivotTo) < 0) {
                i += 2;
            }
            while (j > left && cmp(array, j, pivotFrom, pivotTo) > 0) {
                j -= 2;
            }
            if (i <= j) {
                if (i != j) {
                    int temp = array[i];
                    array[i] = array[j];
                    array[j] = temp;
                    temp = array[i + 1];
                    array[i + 1] = array[j + 1];
                    array[j + 1] = temp;
                }
            }
        }
        if (left < j) {
            qsortIntPair(array, left, j);
        }
        if (i < right) {
            qsortIntPair(array, i, right);
        }
    }
    
    static String charClassToString(final int[] r, final int len) {
        final StringBuilder b = new StringBuilder();
        b.append('[');
        for (int i = 0; i < len; i += 2) {
            if (i > 0) {
                b.append(' ');
            }
            final int lo = r[i];
            final int hi = r[i + 1];
            if (lo == hi) {
                b.append("0x");
                b.append(Integer.toHexString(lo));
            }
            else {
                b.append("0x");
                b.append(Integer.toHexString(lo));
                b.append("-0x");
                b.append(Integer.toHexString(hi));
            }
        }
        b.append(']');
        return b.toString();
    }
    
    @Override
    public String toString() {
        return charClassToString(this.r, this.len);
    }
}
