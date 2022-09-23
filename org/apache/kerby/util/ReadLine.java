// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.util;

import java.io.IOException;
import java.io.InputStream;

public class ReadLine
{
    final InputStream in;
    final byte[] bytes;
    int pos;
    int avail;
    
    public ReadLine(final InputStream in) {
        this.bytes = new byte[8192];
        this.pos = 0;
        this.avail = 0;
        this.in = in;
    }
    
    public String next() throws IOException {
        return this.next(1);
    }
    
    public String next(int lines) throws IOException {
        if (lines < 1) {
            lines = 1;
        }
        final StringBuilder buf = new StringBuilder(128 * lines);
        if (this.avail <= 0 || this.pos >= this.avail) {
            this.pos = 0;
            this.avail = this.in.read(this.bytes);
        }
        while (this.avail >= 0) {
            while (this.pos < this.avail) {
                final char c = (char)this.bytes[this.pos++];
                switch (c) {
                    case '\n':
                    case '\r': {
                        if (--lines < 1 && buf.length() > 0) {
                            return buf.toString();
                        }
                        continue;
                    }
                    default: {
                        buf.append(c);
                        continue;
                    }
                }
            }
            this.pos = 0;
            this.avail = this.in.read(this.bytes);
        }
        return (buf.length() > 0) ? buf.toString() : null;
    }
    
    public byte[] nextAsBytes() throws IOException {
        return this.nextAsBytes(1);
    }
    
    public byte[] nextAsBytes(int lines) throws IOException {
        if (lines < 1) {
            lines = 1;
        }
        byte[] buf = new byte[8192];
        int bufPos = 0;
        if (this.avail <= 0 || this.pos >= this.avail) {
            this.pos = 0;
            this.avail = this.in.read(this.bytes);
        }
        while (this.avail >= 0) {
            while (this.pos < this.avail) {
                final byte b = this.bytes[this.pos++];
                switch (b) {
                    case 10:
                    case 13: {
                        if (--lines == 0 && bufPos > 0) {
                            return buf;
                        }
                        continue;
                    }
                    default: {
                        if (bufPos >= buf.length) {
                            final byte[] moreBuff = new byte[buf.length * 2];
                            System.arraycopy(buf, 0, moreBuff, 0, buf.length);
                            buf = moreBuff;
                        }
                        buf[bufPos++] = b;
                        continue;
                    }
                }
            }
            this.pos = 0;
            this.avail = this.in.read(this.bytes);
        }
        return (byte[])((bufPos > 0) ? buf : null);
    }
}
