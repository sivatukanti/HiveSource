// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.http;

import org.eclipse.jetty.util.ArrayTrie;
import org.eclipse.jetty.util.BufferUtil;
import java.util.EnumSet;
import java.nio.ByteBuffer;
import org.eclipse.jetty.util.Trie;

public enum HttpHeaderValue
{
    CLOSE("close"), 
    CHUNKED("chunked"), 
    GZIP("gzip"), 
    IDENTITY("identity"), 
    KEEP_ALIVE("keep-alive"), 
    CONTINUE("100-continue"), 
    PROCESSING("102-processing"), 
    TE("TE"), 
    BYTES("bytes"), 
    NO_CACHE("no-cache"), 
    UPGRADE("Upgrade"), 
    UNKNOWN("::UNKNOWN::");
    
    public static final Trie<HttpHeaderValue> CACHE;
    private final String _string;
    private final ByteBuffer _buffer;
    private static EnumSet<HttpHeader> __known;
    
    private HttpHeaderValue(final String s) {
        this._string = s;
        this._buffer = BufferUtil.toBuffer(s);
    }
    
    public ByteBuffer toBuffer() {
        return this._buffer.asReadOnlyBuffer();
    }
    
    public boolean is(final String s) {
        return this._string.equalsIgnoreCase(s);
    }
    
    public String asString() {
        return this._string;
    }
    
    @Override
    public String toString() {
        return this._string;
    }
    
    public static boolean hasKnownValues(final HttpHeader header) {
        return header != null && HttpHeaderValue.__known.contains(header);
    }
    
    static {
        CACHE = new ArrayTrie<HttpHeaderValue>();
        for (final HttpHeaderValue value : values()) {
            if (value != HttpHeaderValue.UNKNOWN) {
                HttpHeaderValue.CACHE.put(value.toString(), value);
            }
        }
        HttpHeaderValue.__known = EnumSet.of(HttpHeader.CONNECTION, HttpHeader.TRANSFER_ENCODING, HttpHeader.CONTENT_ENCODING);
    }
}
