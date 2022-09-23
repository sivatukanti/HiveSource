// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.jdbc;

import java.io.IOException;
import java.io.Reader;
import java.io.InputStream;

public final class ReaderToAscii extends InputStream
{
    private final Reader data;
    private char[] conv;
    private boolean closed;
    
    public ReaderToAscii(final Reader data) {
        this.data = data;
        if (!(data instanceof UTF8Reader)) {
            this.conv = new char[256];
        }
    }
    
    public int read() throws IOException {
        if (this.closed) {
            throw new IOException();
        }
        final int read = this.data.read();
        if (read == -1) {
            return -1;
        }
        if (read <= 255) {
            return read & 0xFF;
        }
        return 63;
    }
    
    public int read(final byte[] array, int n, int n2) throws IOException {
        if (this.closed) {
            throw new IOException();
        }
        if (this.data instanceof UTF8Reader) {
            return ((UTF8Reader)this.data).readAsciiInto(array, n, n2);
        }
        if (n2 > this.conv.length) {
            n2 = this.conv.length;
        }
        n2 = this.data.read(this.conv, 0, n2);
        if (n2 == -1) {
            return -1;
        }
        for (int i = 0; i < n2; ++i) {
            final char c = this.conv[i];
            byte b;
            if (c <= '\u00ff') {
                b = (byte)c;
            }
            else {
                b = 63;
            }
            array[n++] = b;
        }
        return n2;
    }
    
    public long skip(final long n) throws IOException {
        if (this.closed) {
            throw new IOException();
        }
        return this.data.skip(n);
    }
    
    public void close() throws IOException {
        if (!this.closed) {
            this.closed = true;
            this.data.close();
        }
    }
}
