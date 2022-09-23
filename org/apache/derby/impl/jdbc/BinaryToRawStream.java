// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.jdbc;

import java.io.IOException;
import java.io.EOFException;
import java.io.InputStream;
import java.io.FilterInputStream;

class BinaryToRawStream extends FilterInputStream
{
    private int length;
    private Object parent;
    
    BinaryToRawStream(final InputStream in, final Object parent) throws IOException {
        super(in);
        this.parent = parent;
        final int read = in.read();
        if (read == -1) {
            throw new EOFException();
        }
        if ((read & 0x80) != 0x0) {
            if (read == 192) {
                final int read2 = in.read();
                final int read3 = in.read();
                final int read4 = in.read();
                final int read5 = in.read();
                if (read2 == -1 || read3 == -1 || read4 == -1 || read5 == -1) {
                    throw new EOFException();
                }
                this.length = ((read2 & 0xFF) << 24 | (read3 & 0xFF) << 16 | (read4 & 0xFF) << 8 | (read5 & 0xFF));
            }
            else if (read == 160) {
                final int read6 = in.read();
                final int read7 = in.read();
                if (read6 == -1 || read7 == -1) {
                    throw new EOFException();
                }
                this.length = ((read6 & 0xFF) << 8) + (read7 & 0xFF);
            }
            else {
                this.length = (read & 0x1F);
            }
        }
        else {
            final int read8 = in.read();
            final int read9 = in.read();
            final int read10 = in.read();
            if (read8 == -1 || read9 == -1 || read10 == -1) {
                throw new EOFException();
            }
            final int n = (read & 0xFF) << 24 | (read8 & 0xFF) << 16 | (read9 & 0xFF) << 8 | (read10 & 0xFF);
            this.length = n / 8;
            if (n % 8 != 0) {
                ++this.length;
            }
            if (this.length == 0) {
                this.length = -1;
            }
        }
    }
    
    int getLength() {
        return this.length;
    }
}
