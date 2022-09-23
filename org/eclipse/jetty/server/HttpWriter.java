// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.server;

import java.io.IOException;
import org.eclipse.jetty.util.ByteArrayOutputStream2;
import java.io.Writer;

public abstract class HttpWriter extends Writer
{
    public static final int MAX_OUTPUT_CHARS = 512;
    final HttpOutput _out;
    final ByteArrayOutputStream2 _bytes;
    final char[] _chars;
    
    public HttpWriter(final HttpOutput out) {
        this._out = out;
        this._chars = new char[512];
        this._bytes = new ByteArrayOutputStream2(512);
    }
    
    @Override
    public void close() throws IOException {
        this._out.close();
    }
    
    @Override
    public void flush() throws IOException {
        this._out.flush();
    }
    
    @Override
    public void write(final String s, int offset, int length) throws IOException {
        while (length > 512) {
            this.write(s, offset, 512);
            offset += 512;
            length -= 512;
        }
        s.getChars(offset, offset + length, this._chars, 0);
        this.write(this._chars, 0, length);
    }
    
    @Override
    public void write(final char[] s, final int offset, final int length) throws IOException {
        throw new AbstractMethodError();
    }
}
