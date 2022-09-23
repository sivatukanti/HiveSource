// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.util;

import java.nio.charset.StandardCharsets;
import java.nio.ByteBuffer;

public abstract class AbstractTrie<V> implements Trie<V>
{
    final boolean _caseInsensitive;
    
    protected AbstractTrie(final boolean insensitive) {
        this._caseInsensitive = insensitive;
    }
    
    @Override
    public boolean put(final V v) {
        return this.put(v.toString(), v);
    }
    
    @Override
    public V remove(final String s) {
        final V o = this.get(s);
        this.put(s, null);
        return o;
    }
    
    @Override
    public V get(final String s) {
        return this.get(s, 0, s.length());
    }
    
    @Override
    public V get(final ByteBuffer b) {
        return this.get(b, 0, b.remaining());
    }
    
    @Override
    public V getBest(final String s) {
        return this.getBest(s, 0, s.length());
    }
    
    @Override
    public V getBest(final byte[] b, final int offset, final int len) {
        return this.getBest(new String(b, offset, len, StandardCharsets.ISO_8859_1));
    }
    
    @Override
    public boolean isCaseInsensitive() {
        return this._caseInsensitive;
    }
}
