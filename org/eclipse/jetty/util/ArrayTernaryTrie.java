// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.util;

import java.util.HashSet;
import java.util.Set;
import java.nio.ByteBuffer;
import java.util.Arrays;

public class ArrayTernaryTrie<V> extends AbstractTrie<V>
{
    private static int LO;
    private static int EQ;
    private static int HI;
    private static final int ROW_SIZE = 4;
    private final char[] _tree;
    private final String[] _key;
    private final V[] _value;
    private char _rows;
    
    public ArrayTernaryTrie() {
        this(128);
    }
    
    public ArrayTernaryTrie(final boolean insensitive) {
        this(insensitive, 128);
    }
    
    public ArrayTernaryTrie(final int capacity) {
        this(true, capacity);
    }
    
    public ArrayTernaryTrie(final boolean insensitive, final int capacity) {
        super(insensitive);
        this._value = (V[])new Object[capacity];
        this._tree = new char[capacity * 4];
        this._key = new String[capacity];
    }
    
    public ArrayTernaryTrie(final ArrayTernaryTrie<V> trie, final double factor) {
        super(trie.isCaseInsensitive());
        final int capacity = (int)(trie._value.length * factor);
        this._rows = trie._rows;
        this._value = Arrays.copyOf(trie._value, capacity);
        this._tree = Arrays.copyOf(trie._tree, capacity * 4);
        this._key = Arrays.copyOf(trie._key, capacity);
    }
    
    @Override
    public void clear() {
        this._rows = '\0';
        Arrays.fill(this._value, null);
        Arrays.fill(this._tree, '\0');
        Arrays.fill(this._key, null);
    }
    
    @Override
    public boolean put(final String s, final V v) {
        int t = 0;
        final int limit = s.length();
        int last = 0;
        for (int k = 0; k < limit; ++k) {
            char c = s.charAt(k);
            if (this.isCaseInsensitive() && c < '\u0080') {
                c = StringUtil.lowercases[c];
            }
            int diff;
            do {
                final int row = 4 * t;
                if (t == this._rows) {
                    ++this._rows;
                    if (this._rows >= this._key.length) {
                        --this._rows;
                        return false;
                    }
                    this._tree[row] = c;
                }
                final char n = this._tree[row];
                diff = n - c;
                if (diff == 0) {
                    t = this._tree[last = row + ArrayTernaryTrie.EQ];
                }
                else if (diff < 0) {
                    t = this._tree[last = row + ArrayTernaryTrie.LO];
                }
                else {
                    t = this._tree[last = row + ArrayTernaryTrie.HI];
                }
                if (t == 0) {
                    t = this._rows;
                    this._tree[last] = (char)t;
                }
            } while (diff != 0);
        }
        if (t == this._rows) {
            ++this._rows;
            if (this._rows >= this._key.length) {
                --this._rows;
                return false;
            }
        }
        this._key[t] = ((v == null) ? null : s);
        this._value[t] = v;
        return true;
    }
    
    @Override
    public V get(final String s, final int offset, final int len) {
        int t = 0;
        int i = 0;
        while (i < len) {
            char c = s.charAt(offset + i++);
            if (this.isCaseInsensitive() && c < '\u0080') {
                c = StringUtil.lowercases[c];
            }
            while (true) {
                final int row = 4 * t;
                final char n = this._tree[row];
                final int diff = n - c;
                if (diff == 0) {
                    t = this._tree[row + ArrayTernaryTrie.EQ];
                    if (t == 0) {
                        return null;
                    }
                    break;
                }
                else {
                    t = this._tree[row + hilo(diff)];
                    if (t == 0) {
                        return null;
                    }
                    continue;
                }
            }
        }
        return this._value[t];
    }
    
    @Override
    public V get(final ByteBuffer b, int offset, final int len) {
        int t = 0;
        offset += b.position();
        int i = 0;
        while (i < len) {
            byte c = (byte)(b.get(offset + i++) & 0x7F);
            if (this.isCaseInsensitive()) {
                c = (byte)StringUtil.lowercases[c];
            }
            while (true) {
                final int row = 4 * t;
                final char n = this._tree[row];
                final int diff = n - c;
                if (diff == 0) {
                    t = this._tree[row + ArrayTernaryTrie.EQ];
                    if (t == 0) {
                        return null;
                    }
                    break;
                }
                else {
                    t = this._tree[row + hilo(diff)];
                    if (t == 0) {
                        return null;
                    }
                    continue;
                }
            }
        }
        return this._value[t];
    }
    
    @Override
    public V getBest(final String s) {
        return this.getBest(0, s, 0, s.length());
    }
    
    @Override
    public V getBest(final String s, final int offset, final int length) {
        return this.getBest(0, s, offset, length);
    }
    
    private V getBest(int t, final String s, int offset, int len) {
        int node = t;
        final int end = offset + len;
    Label_0157:
        while (offset < end) {
            char c = s.charAt(offset++);
            --len;
            if (this.isCaseInsensitive() && c < '\u0080') {
                c = StringUtil.lowercases[c];
            }
            while (true) {
                final int row = 4 * t;
                final char n = this._tree[row];
                final int diff = n - c;
                if (diff == 0) {
                    t = this._tree[row + ArrayTernaryTrie.EQ];
                    if (t == 0) {
                        break Label_0157;
                    }
                    if (this._key[t] != null) {
                        node = t;
                        final V better = this.getBest(t, s, offset, len);
                        if (better != null) {
                            return better;
                        }
                    }
                    break;
                }
                else {
                    t = this._tree[row + hilo(diff)];
                    if (t == 0) {
                        break Label_0157;
                    }
                    continue;
                }
            }
        }
        return this._value[node];
    }
    
    @Override
    public V getBest(final ByteBuffer b, final int offset, final int len) {
        if (b.hasArray()) {
            return this.getBest(0, b.array(), b.arrayOffset() + b.position() + offset, len);
        }
        return this.getBest(0, b, offset, len);
    }
    
    private V getBest(int t, final byte[] b, int offset, int len) {
        int node = t;
        final int end = offset + len;
    Label_0152:
        while (offset < end) {
            byte c = (byte)(b[offset++] & 0x7F);
            --len;
            if (this.isCaseInsensitive()) {
                c = (byte)StringUtil.lowercases[c];
            }
            while (true) {
                final int row = 4 * t;
                final char n = this._tree[row];
                final int diff = n - c;
                if (diff == 0) {
                    t = this._tree[row + ArrayTernaryTrie.EQ];
                    if (t == 0) {
                        break Label_0152;
                    }
                    if (this._key[t] != null) {
                        node = t;
                        final V better = this.getBest(t, b, offset, len);
                        if (better != null) {
                            return better;
                        }
                    }
                    break;
                }
                else {
                    t = this._tree[row + hilo(diff)];
                    if (t == 0) {
                        break Label_0152;
                    }
                    continue;
                }
            }
        }
        return this._value[node];
    }
    
    private V getBest(int t, final ByteBuffer b, final int offset, final int len) {
        int node = t;
        final int o = offset + b.position();
    Label_0171:
        for (int i = 0; i < len; ++i) {
            byte c = (byte)(b.get(o + i) & 0x7F);
            if (this.isCaseInsensitive()) {
                c = (byte)StringUtil.lowercases[c];
            }
            while (true) {
                final int row = 4 * t;
                final char n = this._tree[row];
                final int diff = n - c;
                if (diff == 0) {
                    t = this._tree[row + ArrayTernaryTrie.EQ];
                    if (t == 0) {
                        break Label_0171;
                    }
                    if (this._key[t] != null) {
                        node = t;
                        final V best = this.getBest(t, b, offset + i + 1, len - i - 1);
                        if (best != null) {
                            return best;
                        }
                    }
                    break;
                }
                else {
                    t = this._tree[row + hilo(diff)];
                    if (t == 0) {
                        break Label_0171;
                    }
                    continue;
                }
            }
        }
        return this._value[node];
    }
    
    @Override
    public String toString() {
        final StringBuilder buf = new StringBuilder();
        for (int r = 0; r <= this._rows; ++r) {
            if (this._key[r] != null && this._value[r] != null) {
                buf.append(',');
                buf.append(this._key[r]);
                buf.append('=');
                buf.append(this._value[r].toString());
            }
        }
        if (buf.length() == 0) {
            return "{}";
        }
        buf.setCharAt(0, '{');
        buf.append('}');
        return buf.toString();
    }
    
    @Override
    public Set<String> keySet() {
        final Set<String> keys = new HashSet<String>();
        for (int r = 0; r <= this._rows; ++r) {
            if (this._key[r] != null && this._value[r] != null) {
                keys.add(this._key[r]);
            }
        }
        return keys;
    }
    
    @Override
    public boolean isFull() {
        return this._rows + '\u0001' == this._key.length;
    }
    
    public static int hilo(final int diff) {
        return 1 + (diff | Integer.MAX_VALUE) / 1073741823;
    }
    
    public void dump() {
        for (int r = 0; r < this._rows; ++r) {
            final char c = this._tree[r * 4 + 0];
            System.err.printf("%4d [%s,%d,%d,%d] '%s':%s%n", r, (c < ' ' || c > '\u007f') ? ("" + (int)c) : ("'" + c + "'"), (int)this._tree[r * 4 + ArrayTernaryTrie.LO], (int)this._tree[r * 4 + ArrayTernaryTrie.EQ], (int)this._tree[r * 4 + ArrayTernaryTrie.HI], this._key[r], this._value[r]);
        }
    }
    
    static {
        ArrayTernaryTrie.LO = 1;
        ArrayTernaryTrie.EQ = 2;
        ArrayTernaryTrie.HI = 3;
    }
}
