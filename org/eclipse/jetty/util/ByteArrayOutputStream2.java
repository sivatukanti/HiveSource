// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.util;

import java.nio.charset.Charset;
import java.io.ByteArrayOutputStream;

public class ByteArrayOutputStream2 extends ByteArrayOutputStream
{
    public ByteArrayOutputStream2() {
    }
    
    public ByteArrayOutputStream2(final int size) {
        super(size);
    }
    
    public byte[] getBuf() {
        return this.buf;
    }
    
    public int getCount() {
        return this.count;
    }
    
    public void setCount(final int count) {
        this.count = count;
    }
    
    public void reset(final int minSize) {
        this.reset();
        if (this.buf.length < minSize) {
            this.buf = new byte[minSize];
        }
    }
    
    public void writeUnchecked(final int b) {
        this.buf[this.count++] = (byte)b;
    }
    
    @Override
    public String toString(final Charset charset) {
        return new String(this.buf, 0, this.count, charset);
    }
}
