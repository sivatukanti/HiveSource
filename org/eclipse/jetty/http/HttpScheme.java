// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.http;

import org.eclipse.jetty.util.ArrayTrie;
import org.eclipse.jetty.util.BufferUtil;
import java.nio.ByteBuffer;
import org.eclipse.jetty.util.Trie;

public enum HttpScheme
{
    HTTP("http"), 
    HTTPS("https"), 
    WS("ws"), 
    WSS("wss");
    
    public static final Trie<HttpScheme> CACHE;
    private final String _string;
    private final ByteBuffer _buffer;
    
    private HttpScheme(final String s) {
        this._string = s;
        this._buffer = BufferUtil.toBuffer(s);
    }
    
    public ByteBuffer asByteBuffer() {
        return this._buffer.asReadOnlyBuffer();
    }
    
    public boolean is(final String s) {
        return s != null && this._string.equalsIgnoreCase(s);
    }
    
    public String asString() {
        return this._string;
    }
    
    @Override
    public String toString() {
        return this._string;
    }
    
    static {
        CACHE = new ArrayTrie<HttpScheme>();
        for (final HttpScheme version : values()) {
            HttpScheme.CACHE.put(version.asString(), version);
        }
    }
}
