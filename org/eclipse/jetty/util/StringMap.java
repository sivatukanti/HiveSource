// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.util;

import java.io.ObjectInput;
import java.io.IOException;
import java.util.HashMap;
import java.io.ObjectOutput;
import java.util.Map;
import java.util.Collections;
import java.util.Set;
import java.util.HashSet;
import java.io.Externalizable;
import java.util.AbstractMap;

public class StringMap extends AbstractMap implements Externalizable
{
    public static final boolean CASE_INSENSTIVE = true;
    protected static final int __HASH_WIDTH = 17;
    protected int _width;
    protected Node _root;
    protected boolean _ignoreCase;
    protected NullEntry _nullEntry;
    protected Object _nullValue;
    protected HashSet _entrySet;
    protected Set _umEntrySet;
    
    public StringMap() {
        this._width = 17;
        this._root = new Node();
        this._ignoreCase = false;
        this._nullEntry = null;
        this._nullValue = null;
        this._entrySet = new HashSet(3);
        this._umEntrySet = Collections.unmodifiableSet((Set<?>)this._entrySet);
    }
    
    public StringMap(final boolean ignoreCase) {
        this();
        this._ignoreCase = ignoreCase;
    }
    
    public StringMap(final boolean ignoreCase, final int width) {
        this();
        this._ignoreCase = ignoreCase;
        this._width = width;
    }
    
    public void setIgnoreCase(final boolean ic) {
        if (this._root._children != null) {
            throw new IllegalStateException("Must be set before first put");
        }
        this._ignoreCase = ic;
    }
    
    public boolean isIgnoreCase() {
        return this._ignoreCase;
    }
    
    public void setWidth(final int width) {
        this._width = width;
    }
    
    public int getWidth() {
        return this._width;
    }
    
    @Override
    public Object put(final Object key, final Object value) {
        if (key == null) {
            return this.put(null, value);
        }
        return this.put(key.toString(), value);
    }
    
    public Object put(final String key, final Object value) {
        if (key == null) {
            final Object oldValue = this._nullValue;
            this._nullValue = value;
            if (this._nullEntry == null) {
                this._nullEntry = new NullEntry();
                this._entrySet.add(this._nullEntry);
            }
            return oldValue;
        }
        Node node = this._root;
        int ni = -1;
        Node prev = null;
        Node parent = null;
        int i = 0;
    Label_0065:
        while (i < key.length()) {
            final char c = key.charAt(i);
            if (ni == -1) {
                parent = node;
                prev = null;
                ni = 0;
                node = ((node._children == null) ? null : node._children[c % this._width]);
            }
            while (node != null) {
                if (node._char[ni] == c || (this._ignoreCase && node._ochar[ni] == c)) {
                    prev = null;
                    if (++ni == node._char.length) {
                        ni = -1;
                    }
                }
                else {
                    if (ni == 0) {
                        prev = node;
                        node = node._next;
                        continue;
                    }
                    node.split(this, ni);
                    --i;
                    ni = -1;
                }
                ++i;
                continue Label_0065;
            }
            node = new Node(this._ignoreCase, key, i);
            if (prev != null) {
                prev._next = node;
                break;
            }
            if (parent != null) {
                if (parent._children == null) {
                    parent._children = new Node[this._width];
                }
                parent._children[c % this._width] = node;
                final int oi = node._ochar[0] % this._width;
                if (node._ochar != null && node._char[0] % this._width != oi) {
                    if (parent._children[oi] == null) {
                        parent._children[oi] = node;
                    }
                    else {
                        Node n;
                        for (n = parent._children[oi]; n._next != null; n = n._next) {}
                        n._next = node;
                    }
                }
                break;
            }
            this._root = node;
            break;
        }
        if (node != null) {
            if (ni > 0) {
                node.split(this, ni);
            }
            final Object old = node._value;
            node._key = key;
            node._value = value;
            this._entrySet.add(node);
            return old;
        }
        return null;
    }
    
    @Override
    public Object get(final Object key) {
        if (key == null) {
            return this._nullValue;
        }
        if (key instanceof String) {
            return this.get((String)key);
        }
        return this.get(key.toString());
    }
    
    public Object get(final String key) {
        if (key == null) {
            return this._nullValue;
        }
        final Map.Entry entry = this.getEntry(key, 0, key.length());
        if (entry == null) {
            return null;
        }
        return entry.getValue();
    }
    
    public Map.Entry getEntry(final String key, final int offset, final int length) {
        if (key == null) {
            return this._nullEntry;
        }
        Node node = this._root;
        int ni = -1;
        int i = 0;
    Label_0021:
        while (i < length) {
            final char c = key.charAt(offset + i);
            if (ni == -1) {
                ni = 0;
                node = ((node._children == null) ? null : node._children[c % this._width]);
            }
            while (node != null) {
                if (node._char[ni] == c || (this._ignoreCase && node._ochar[ni] == c)) {
                    if (++ni == node._char.length) {
                        ni = -1;
                    }
                    ++i;
                    continue Label_0021;
                }
                if (ni > 0) {
                    return null;
                }
                node = node._next;
            }
            return null;
        }
        if (ni > 0) {
            return null;
        }
        if (node != null && node._key == null) {
            return null;
        }
        return node;
    }
    
    public Map.Entry getEntry(final char[] key, final int offset, final int length) {
        if (key == null) {
            return this._nullEntry;
        }
        Node node = this._root;
        int ni = -1;
        int i = 0;
    Label_0021:
        while (i < length) {
            final char c = key[offset + i];
            if (ni == -1) {
                ni = 0;
                node = ((node._children == null) ? null : node._children[c % this._width]);
            }
            while (node != null) {
                if (node._char[ni] == c || (this._ignoreCase && node._ochar[ni] == c)) {
                    if (++ni == node._char.length) {
                        ni = -1;
                    }
                    ++i;
                    continue Label_0021;
                }
                if (ni > 0) {
                    return null;
                }
                node = node._next;
            }
            return null;
        }
        if (ni > 0) {
            return null;
        }
        if (node != null && node._key == null) {
            return null;
        }
        return node;
    }
    
    public Map.Entry getBestEntry(final byte[] key, final int offset, final int maxLength) {
        if (key == null) {
            return this._nullEntry;
        }
        Node node = this._root;
        int ni = -1;
        int i = 0;
    Label_0021:
        while (i < maxLength) {
            final char c = (char)key[offset + i];
            if (ni == -1) {
                ni = 0;
                final Node child = (node._children == null) ? null : node._children[c % this._width];
                if (child == null && i > 0) {
                    return node;
                }
                node = child;
            }
            while (node != null) {
                if (node._char[ni] == c || (this._ignoreCase && node._ochar[ni] == c)) {
                    if (++ni == node._char.length) {
                        ni = -1;
                    }
                    ++i;
                    continue Label_0021;
                }
                if (ni > 0) {
                    return null;
                }
                node = node._next;
            }
            return null;
        }
        if (ni > 0) {
            return null;
        }
        if (node != null && node._key == null) {
            return null;
        }
        return node;
    }
    
    @Override
    public Object remove(final Object key) {
        if (key == null) {
            return this.remove(null);
        }
        return this.remove(key.toString());
    }
    
    public Object remove(final String key) {
        if (key == null) {
            final Object oldValue = this._nullValue;
            if (this._nullEntry != null) {
                this._entrySet.remove(this._nullEntry);
                this._nullEntry = null;
                this._nullValue = null;
            }
            return oldValue;
        }
        Node node = this._root;
        int ni = -1;
        int i = 0;
    Label_0050:
        while (i < key.length()) {
            final char c = key.charAt(i);
            if (ni == -1) {
                ni = 0;
                node = ((node._children == null) ? null : node._children[c % this._width]);
            }
            while (node != null) {
                if (node._char[ni] == c || (this._ignoreCase && node._ochar[ni] == c)) {
                    if (++ni == node._char.length) {
                        ni = -1;
                    }
                    ++i;
                    continue Label_0050;
                }
                if (ni > 0) {
                    return null;
                }
                node = node._next;
            }
            return null;
        }
        if (ni > 0) {
            return null;
        }
        if (node != null && node._key == null) {
            return null;
        }
        final Object old = node._value;
        this._entrySet.remove(node);
        node._value = null;
        node._key = null;
        return old;
    }
    
    @Override
    public Set entrySet() {
        return this._umEntrySet;
    }
    
    @Override
    public int size() {
        return this._entrySet.size();
    }
    
    @Override
    public boolean isEmpty() {
        return this._entrySet.isEmpty();
    }
    
    @Override
    public boolean containsKey(final Object key) {
        if (key == null) {
            return this._nullEntry != null;
        }
        return this.getEntry(key.toString(), 0, (key == null) ? 0 : key.toString().length()) != null;
    }
    
    @Override
    public void clear() {
        this._root = new Node();
        this._nullEntry = null;
        this._nullValue = null;
        this._entrySet.clear();
    }
    
    public void writeExternal(final ObjectOutput out) throws IOException {
        final HashMap map = new HashMap(this);
        out.writeBoolean(this._ignoreCase);
        out.writeObject(map);
    }
    
    public void readExternal(final ObjectInput in) throws IOException, ClassNotFoundException {
        final boolean ic = in.readBoolean();
        final HashMap map = (HashMap)in.readObject();
        this.setIgnoreCase(ic);
        this.putAll(map);
    }
    
    private static class Node implements Map.Entry
    {
        char[] _char;
        char[] _ochar;
        Node _next;
        Node[] _children;
        String _key;
        Object _value;
        
        Node() {
        }
        
        Node(final boolean ignoreCase, final String s, final int offset) {
            final int l = s.length() - offset;
            this._char = new char[l];
            this._ochar = new char[l];
            for (int i = 0; i < l; ++i) {
                final char c = s.charAt(offset + i);
                this._char[i] = c;
                if (ignoreCase) {
                    char o = c;
                    if (Character.isUpperCase(c)) {
                        o = Character.toLowerCase(c);
                    }
                    else if (Character.isLowerCase(c)) {
                        o = Character.toUpperCase(c);
                    }
                    this._ochar[i] = o;
                }
            }
        }
        
        Node split(final StringMap map, final int offset) {
            final Node split = new Node();
            final int sl = this._char.length - offset;
            char[] tmp = this._char;
            this._char = new char[offset];
            split._char = new char[sl];
            System.arraycopy(tmp, 0, this._char, 0, offset);
            System.arraycopy(tmp, offset, split._char, 0, sl);
            if (this._ochar != null) {
                tmp = this._ochar;
                this._ochar = new char[offset];
                split._ochar = new char[sl];
                System.arraycopy(tmp, 0, this._ochar, 0, offset);
                System.arraycopy(tmp, offset, split._ochar, 0, sl);
            }
            split._key = this._key;
            split._value = this._value;
            this._key = null;
            this._value = null;
            if (map._entrySet.remove(this)) {
                map._entrySet.add(split);
            }
            split._children = this._children;
            (this._children = new Node[map._width])[split._char[0] % map._width] = split;
            if (split._ochar != null && this._children[split._ochar[0] % map._width] != split) {
                this._children[split._ochar[0] % map._width] = split;
            }
            return split;
        }
        
        public Object getKey() {
            return this._key;
        }
        
        public Object getValue() {
            return this._value;
        }
        
        public Object setValue(final Object o) {
            final Object old = this._value;
            this._value = o;
            return old;
        }
        
        @Override
        public String toString() {
            final StringBuilder buf = new StringBuilder();
            this.toString(buf);
            return buf.toString();
        }
        
        private void toString(final StringBuilder buf) {
            buf.append("{[");
            if (this._char == null) {
                buf.append('-');
            }
            else {
                for (int i = 0; i < this._char.length; ++i) {
                    buf.append(this._char[i]);
                }
            }
            buf.append(':');
            buf.append(this._key);
            buf.append('=');
            buf.append(this._value);
            buf.append(']');
            if (this._children != null) {
                for (int i = 0; i < this._children.length; ++i) {
                    buf.append('|');
                    if (this._children[i] != null) {
                        this._children[i].toString(buf);
                    }
                    else {
                        buf.append("-");
                    }
                }
            }
            buf.append('}');
            if (this._next != null) {
                buf.append(",\n");
                this._next.toString(buf);
            }
        }
    }
    
    private class NullEntry implements Map.Entry
    {
        public Object getKey() {
            return null;
        }
        
        public Object getValue() {
            return StringMap.this._nullValue;
        }
        
        public Object setValue(final Object o) {
            final Object old = StringMap.this._nullValue;
            StringMap.this._nullValue = o;
            return old;
        }
        
        @Override
        public String toString() {
            return "[:null=" + StringMap.this._nullValue + "]";
        }
    }
}
