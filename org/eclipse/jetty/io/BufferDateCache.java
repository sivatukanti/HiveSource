// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.io;

import java.util.Locale;
import java.text.DateFormatSymbols;
import org.eclipse.jetty.util.DateCache;

public class BufferDateCache extends DateCache
{
    Buffer _buffer;
    String _last;
    
    public BufferDateCache() {
    }
    
    public BufferDateCache(final String format, final DateFormatSymbols s) {
        super(format, s);
    }
    
    public BufferDateCache(final String format, final Locale l) {
        super(format, l);
    }
    
    public BufferDateCache(final String format) {
        super(format);
    }
    
    public synchronized Buffer formatBuffer(final long date) {
        final String d = super.format(date);
        if (d == this._last) {
            return this._buffer;
        }
        this._last = d;
        return this._buffer = new ByteArrayBuffer(d);
    }
}
