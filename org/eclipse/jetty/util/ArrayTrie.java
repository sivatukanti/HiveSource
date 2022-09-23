// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.util;

import java.util.HashSet;
import java.util.Set;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;

public class ArrayTrie<V> extends AbstractTrie<V>
{
    private static final int ROW_SIZE = 32;
    private static final int[] __lookup;
    private final char[] _rowIndex;
    private final String[] _key;
    private final V[] _value;
    private char[][] _bigIndex;
    private char _rows;
    
    public ArrayTrie() {
        this(128);
    }
    
    public ArrayTrie(final int capacity) {
        super(true);
        this._value = (V[])new Object[capacity];
        this._rowIndex = new char[capacity * 32];
        this._key = new String[capacity];
    }
    
    @Override
    public void clear() {
        this._rows = '\0';
        Arrays.fill(this._value, null);
        Arrays.fill(this._rowIndex, '\0');
        Arrays.fill(this._key, null);
    }
    
    @Override
    public boolean put(final String s, final V v) {
        int t = 0;
        for (int limit = s.length(), k = 0; k < limit; ++k) {
            final char c = s.charAt(k);
            final int index = ArrayTrie.__lookup[c & '\u007f'];
            if (index >= 0) {
                final int idx = t * 32 + index;
                t = this._rowIndex[idx];
                if (t == 0) {
                    if (++this._rows >= this._value.length) {
                        return false;
                    }
                    final char[] rowIndex = this._rowIndex;
                    final int n = idx;
                    final char rows = this._rows;
                    rowIndex[n] = rows;
                    t = rows;
                }
            }
            else {
                if (c > '\u007f') {
                    throw new IllegalArgumentException("non ascii character");
                }
                if (this._bigIndex == null) {
                    this._bigIndex = new char[this._value.length][];
                }
                if (t >= this._bigIndex.length) {
                    return false;
                }
                char[] big = this._bigIndex[t];
                if (big == null) {
                    final char[][] bigIndex = this._bigIndex;
                    final int n2 = t;
                    final char[] array = new char[128];
                    bigIndex[n2] = array;
                    big = array;
                }
                t = big[c];
                if (t == 0) {
                    if (this._rows == this._value.length) {
                        return false;
                    }
                    final char[] array2 = big;
                    final char c2 = c;
                    final char rows2 = (char)(this._rows + '\u0001');
                    array2[c2] = (this._rows = rows2);
                    t = rows2;
                }
            }
        }
        if (t >= this._key.length) {
            this._rows = (char)this._key.length;
            return false;
        }
        this._key[t] = ((v == null) ? null : s);
        this._value[t] = v;
        return true;
    }
    
    @Override
    public V get(final String s, final int offset, final int len) {
        int t = 0;
        for (int i = 0; i < len; ++i) {
            final char c = s.charAt(offset + i);
            final int index = ArrayTrie.__lookup[c & '\u007f'];
            if (index >= 0) {
                final int idx = t * 32 + index;
                t = this._rowIndex[idx];
                if (t == 0) {
                    return null;
                }
            }
            else {
                final char[] big = (char[])((this._bigIndex == null) ? null : this._bigIndex[t]);
                if (big == null) {
                    return null;
                }
                t = big[c];
                if (t == 0) {
                    return null;
                }
            }
        }
        return this._value[t];
    }
    
    @Override
    public V get(final ByteBuffer b, final int offset, final int len) {
        int t = 0;
        for (int i = 0; i < len; ++i) {
            final byte c = b.get(offset + i);
            final int index = ArrayTrie.__lookup[c & 0x7F];
            if (index >= 0) {
                final int idx = t * 32 + index;
                t = this._rowIndex[idx];
                if (t == 0) {
                    return null;
                }
            }
            else {
                final char[] big = (char[])((this._bigIndex == null) ? null : this._bigIndex[t]);
                if (big == null) {
                    return null;
                }
                t = big[c];
                if (t == 0) {
                    return null;
                }
            }
        }
        return this._value[t];
    }
    
    @Override
    public V getBest(final byte[] b, final int offset, final int len) {
        return this.getBest(0, b, offset, len);
    }
    
    @Override
    public V getBest(final ByteBuffer b, final int offset, final int len) {
        if (b.hasArray()) {
            return this.getBest(0, b.array(), b.arrayOffset() + b.position() + offset, len);
        }
        return this.getBest(0, b, offset, len);
    }
    
    @Override
    public V getBest(final String s, final int offset, final int len) {
        return this.getBest(0, s, offset, len);
    }
    
    private V getBest(int t, final String s, final int offset, final int len) {
        int pos = offset;
        int i = 0;
        while (i < len) {
            final char c = s.charAt(pos++);
            final int index = ArrayTrie.__lookup[c & '\u007f'];
            if (index >= 0) {
                final int idx = t * 32 + index;
                final int nt = this._rowIndex[idx];
                if (nt == 0) {
                    break;
                }
                t = nt;
            }
            else {
                final char[] big = (char[])((this._bigIndex == null) ? null : this._bigIndex[t]);
                if (big == null) {
                    return null;
                }
                final int nt = big[c];
                if (nt == 0) {
                    break;
                }
                t = nt;
            }
            if (this._key[t] != null) {
                final V best = this.getBest(t, s, offset + i + 1, len - i - 1);
                if (best != null) {
                    return best;
                }
                return this._value[t];
            }
            else {
                ++i;
            }
        }
        return this._value[t];
    }
    
    private V getBest(int t, final byte[] b, final int offset, final int len) {
        int i = 0;
        while (i < len) {
            final byte c = b[offset + i];
            final int index = ArrayTrie.__lookup[c & 0x7F];
            if (index >= 0) {
                final int idx = t * 32 + index;
                final int nt = this._rowIndex[idx];
                if (nt == 0) {
                    break;
                }
                t = nt;
            }
            else {
                final char[] big = (char[])((this._bigIndex == null) ? null : this._bigIndex[t]);
                if (big == null) {
                    return null;
                }
                final int nt = big[c];
                if (nt == 0) {
                    break;
                }
                t = nt;
            }
            if (this._key[t] != null) {
                final V best = this.getBest(t, b, offset + i + 1, len - i - 1);
                if (best != null) {
                    return best;
                }
                break;
            }
            else {
                ++i;
            }
        }
        return this._value[t];
    }
    
    private V getBest(int t, final ByteBuffer b, final int offset, final int len) {
        int pos = b.position() + offset;
        int i = 0;
        while (i < len) {
            final byte c = b.get(pos++);
            final int index = ArrayTrie.__lookup[c & 0x7F];
            if (index >= 0) {
                final int idx = t * 32 + index;
                final int nt = this._rowIndex[idx];
                if (nt == 0) {
                    break;
                }
                t = nt;
            }
            else {
                final char[] big = (char[])((this._bigIndex == null) ? null : this._bigIndex[t]);
                if (big == null) {
                    return null;
                }
                final int nt = big[c];
                if (nt == 0) {
                    break;
                }
                t = nt;
            }
            if (this._key[t] != null) {
                final V best = this.getBest(t, b, offset + i + 1, len - i - 1);
                if (best != null) {
                    return best;
                }
                break;
            }
            else {
                ++i;
            }
        }
        return this._value[t];
    }
    
    @Override
    public String toString() {
        final StringBuilder buf = new StringBuilder();
        this.toString(buf, 0);
        if (buf.length() == 0) {
            return "{}";
        }
        buf.setCharAt(0, '{');
        buf.append('}');
        return buf.toString();
    }
    
    private void toString(final Appendable out, final int t) {
        if (this._value[t] != null) {
            try {
                out.append(',');
                out.append(this._key[t]);
                out.append('=');
                out.append(this._value[t].toString());
            }
            catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        for (int i = 0; i < 32; ++i) {
            final int idx = t * 32 + i;
            if (this._rowIndex[idx] != '\0') {
                this.toString(out, this._rowIndex[idx]);
            }
        }
        final char[] big = (char[])((this._bigIndex == null) ? null : this._bigIndex[t]);
        if (big != null) {
            for (final int j : big) {
                if (j != 0) {
                    this.toString(out, j);
                }
            }
        }
    }
    
    @Override
    public Set<String> keySet() {
        final Set<String> keys = new HashSet<String>();
        this.keySet(keys, 0);
        return keys;
    }
    
    private void keySet(final Set<String> set, final int t) {
        if (t < this._value.length && this._value[t] != null) {
            set.add(this._key[t]);
        }
        for (int i = 0; i < 32; ++i) {
            final int idx = t * 32 + i;
            if (idx < this._rowIndex.length && this._rowIndex[idx] != '\0') {
                this.keySet(set, this._rowIndex[idx]);
            }
        }
        final char[] big = (char[])((this._bigIndex == null || t >= this._bigIndex.length) ? null : this._bigIndex[t]);
        if (big != null) {
            for (final int j : big) {
                if (j != 0) {
                    this.keySet(set, j);
                }
            }
        }
    }
    
    @Override
    public boolean isFull() {
        return this._rows + '\u0001' >= this._key.length;
    }
    
    static {
        __lookup = new int[] { -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 31, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 26, -1, 27, 30, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 28, 29, -1, -1, -1, -1, -1, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, -1, -1, -1, -1, -1, -1, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, -1, -1, -1, -1, -1 };
    }
}
