// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.jdbc;

import java.io.IOException;
import java.io.Writer;
import java.io.OutputStream;

final class ClobAsciiStream extends OutputStream
{
    private final Writer writer;
    private final char[] buffer;
    
    ClobAsciiStream(final Writer writer) {
        this.buffer = new char[1024];
        this.writer = writer;
    }
    
    public void write(final int n) throws IOException {
        this.writer.write(n & 0xFF);
    }
    
    public void write(final byte[] array, int n, int i) throws IOException {
        while (i > 0) {
            final int min = Math.min(i, this.buffer.length);
            for (int j = 0; j < min; ++j) {
                this.buffer[j] = (char)(array[n + j] & 0xFF);
            }
            this.writer.write(this.buffer, 0, min);
            n += min;
            i -= min;
        }
    }
}
