// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.thrift;

import java.io.ByteArrayOutputStream;

public class TByteArrayOutputStream extends ByteArrayOutputStream
{
    private final int initialSize;
    
    public TByteArrayOutputStream(final int size) {
        super(size);
        this.initialSize = size;
    }
    
    public TByteArrayOutputStream() {
        this(32);
    }
    
    public byte[] get() {
        return this.buf;
    }
    
    @Override
    public void reset() {
        super.reset();
        if (this.buf.length > this.initialSize) {
            this.buf = new byte[this.initialSize];
        }
    }
    
    public int len() {
        return this.count;
    }
}
