// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.util;

import java.util.HashSet;
import java.util.Set;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;

public class TreeTrie<V> extends AbstractTrie<V>
{
    private static final int[] __lookup;
    private static final int INDEX = 32;
    private final TreeTrie<V>[] _nextIndex;
    private final List<TreeTrie<V>> _nextOther;
    private final char _c;
    private String _key;
    private V _value;
    
    public TreeTrie() {
        super(true);
        this._nextOther = new ArrayList<TreeTrie<V>>();
        this._nextIndex = (TreeTrie<V>[])new TreeTrie[32];
        this._c = '\0';
    }
    
    private TreeTrie(final char c) {
        super(true);
        this._nextOther = new ArrayList<TreeTrie<V>>();
        this._nextIndex = (TreeTrie<V>[])new TreeTrie[32];
        this._c = c;
    }
    
    @Override
    public void clear() {
        Arrays.fill(this._nextIndex, null);
        this._nextOther.clear();
        this._key = null;
        this._value = null;
    }
    
    @Override
    public boolean put(final String s, final V v) {
        TreeTrie<V> t = this;
        for (int limit = s.length(), k = 0; k < limit; ++k) {
            final char c = s.charAt(k);
            final int index = (c >= '\0' && c < '\u007f') ? TreeTrie.__lookup[c] : -1;
            if (index >= 0) {
                if (t._nextIndex[index] == null) {
                    t._nextIndex[index] = new TreeTrie<V>(c);
                }
                t = t._nextIndex[index];
            }
            else {
                TreeTrie<V> n = null;
                int i = t._nextOther.size();
                while (i-- > 0) {
                    n = t._nextOther.get(i);
                    if (n._c == c) {
                        break;
                    }
                    n = null;
                }
                if (n == null) {
                    n = new TreeTrie<V>(c);
                    t._nextOther.add(n);
                }
                t = n;
            }
        }
        t._key = ((v == null) ? null : s);
        t._value = v;
        return true;
    }
    
    @Override
    public V get(final String s, final int offset, final int len) {
        TreeTrie<V> t = this;
        for (int i = 0; i < len; ++i) {
            final char c = s.charAt(offset + i);
            final int index = (c >= '\0' && c < '\u007f') ? TreeTrie.__lookup[c] : -1;
            if (index >= 0) {
                if (t._nextIndex[index] == null) {
                    return null;
                }
                t = t._nextIndex[index];
            }
            else {
                TreeTrie<V> n = null;
                int j = t._nextOther.size();
                while (j-- > 0) {
                    n = t._nextOther.get(j);
                    if (n._c == c) {
                        break;
                    }
                    n = null;
                }
                if (n == null) {
                    return null;
                }
                t = n;
            }
        }
        return t._value;
    }
    
    @Override
    public V get(final ByteBuffer b, final int offset, final int len) {
        TreeTrie<V> t = this;
        for (int i = 0; i < len; ++i) {
            final byte c = b.get(offset + i);
            final int index = (c >= 0 && c < 127) ? TreeTrie.__lookup[c] : -1;
            if (index >= 0) {
                if (t._nextIndex[index] == null) {
                    return null;
                }
                t = t._nextIndex[index];
            }
            else {
                TreeTrie<V> n = null;
                int j = t._nextOther.size();
                while (j-- > 0) {
                    n = t._nextOther.get(j);
                    if (n._c == c) {
                        break;
                    }
                    n = null;
                }
                if (n == null) {
                    return null;
                }
                t = n;
            }
        }
        return t._value;
    }
    
    @Override
    public V getBest(final byte[] b, final int offset, final int len) {
        TreeTrie<V> t = this;
        int i = 0;
        while (i < len) {
            final byte c = b[offset + i];
            final int index = (c >= 0 && c < 127) ? TreeTrie.__lookup[c] : -1;
            if (index >= 0) {
                if (t._nextIndex[index] == null) {
                    break;
                }
                t = t._nextIndex[index];
            }
            else {
                TreeTrie<V> n = null;
                int j = t._nextOther.size();
                while (j-- > 0) {
                    n = t._nextOther.get(j);
                    if (n._c == c) {
                        break;
                    }
                    n = null;
                }
                if (n == null) {
                    break;
                }
                t = n;
            }
            if (t._key != null) {
                final V best = t.getBest(b, offset + i + 1, len - i - 1);
                if (best != null) {
                    return best;
                }
                break;
            }
            else {
                ++i;
            }
        }
        return t._value;
    }
    
    @Override
    public V getBest(final String s, final int offset, final int len) {
        TreeTrie<V> t = this;
        int i = 0;
        while (i < len) {
            final byte c = (byte)('\u00ff' & s.charAt(offset + i));
            final int index = (c >= 0 && c < 127) ? TreeTrie.__lookup[c] : -1;
            if (index >= 0) {
                if (t._nextIndex[index] == null) {
                    break;
                }
                t = t._nextIndex[index];
            }
            else {
                TreeTrie<V> n = null;
                int j = t._nextOther.size();
                while (j-- > 0) {
                    n = t._nextOther.get(j);
                    if (n._c == c) {
                        break;
                    }
                    n = null;
                }
                if (n == null) {
                    break;
                }
                t = n;
            }
            if (t._key != null) {
                final V best = t.getBest(s, offset + i + 1, len - i - 1);
                if (best != null) {
                    return best;
                }
                break;
            }
            else {
                ++i;
            }
        }
        return t._value;
    }
    
    @Override
    public V getBest(final ByteBuffer b, final int offset, final int len) {
        if (b.hasArray()) {
            return this.getBest(b.array(), b.arrayOffset() + b.position() + offset, len);
        }
        return this.getBestByteBuffer(b, offset, len);
    }
    
    private V getBestByteBuffer(final ByteBuffer b, final int offset, final int len) {
        TreeTrie<V> t = this;
        int pos = b.position() + offset;
        int i = 0;
        while (i < len) {
            final byte c = b.get(pos++);
            final int index = (c >= 0 && c < 127) ? TreeTrie.__lookup[c] : -1;
            if (index >= 0) {
                if (t._nextIndex[index] == null) {
                    break;
                }
                t = t._nextIndex[index];
            }
            else {
                TreeTrie<V> n = null;
                int j = t._nextOther.size();
                while (j-- > 0) {
                    n = t._nextOther.get(j);
                    if (n._c == c) {
                        break;
                    }
                    n = null;
                }
                if (n == null) {
                    break;
                }
                t = n;
            }
            if (t._key != null) {
                final V best = t.getBest(b, offset + i + 1, len - i - 1);
                if (best != null) {
                    return best;
                }
                break;
            }
            else {
                ++i;
            }
        }
        return t._value;
    }
    
    @Override
    public String toString() {
        final StringBuilder buf = new StringBuilder();
        toString(buf, (TreeTrie<Object>)this);
        if (buf.length() == 0) {
            return "{}";
        }
        buf.setCharAt(0, '{');
        buf.append('}');
        return buf.toString();
    }
    
    private static <V> void toString(final Appendable out, final TreeTrie<V> t) {
        if (t != null) {
            if (t._value != null) {
                try {
                    out.append(',');
                    out.append(t._key);
                    out.append('=');
                    out.append(t._value.toString());
                }
                catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            for (int i = 0; i < 32; ++i) {
                if (t._nextIndex[i] != null) {
                    toString(out, (TreeTrie<Object>)t._nextIndex[i]);
                }
            }
            int i = t._nextOther.size();
            while (i-- > 0) {
                toString(out, (TreeTrie<Object>)t._nextOther.get(i));
            }
        }
    }
    
    @Override
    public Set<String> keySet() {
        final Set<String> keys = new HashSet<String>();
        keySet(keys, (TreeTrie<Object>)this);
        return keys;
    }
    
    private static <V> void keySet(final Set<String> set, final TreeTrie<V> t) {
        if (t != null) {
            if (t._key != null) {
                set.add(t._key);
            }
            for (int i = 0; i < 32; ++i) {
                if (t._nextIndex[i] != null) {
                    keySet(set, (TreeTrie<Object>)t._nextIndex[i]);
                }
            }
            int i = t._nextOther.size();
            while (i-- > 0) {
                keySet(set, (TreeTrie<Object>)t._nextOther.get(i));
            }
        }
    }
    
    @Override
    public boolean isFull() {
        return false;
    }
    
    static {
        __lookup = new int[] { -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 31, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 26, -1, 27, 30, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 28, 29, -1, -1, -1, -1, -1, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, -1, -1, -1, -1, -1, -1, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, -1, -1, -1, -1, -1 };
    }
}
