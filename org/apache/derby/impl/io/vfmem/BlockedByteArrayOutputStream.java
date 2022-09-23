// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.io.vfmem;

import java.io.OutputStream;

public class BlockedByteArrayOutputStream extends OutputStream
{
    private BlockedByteArray src;
    private long pos;
    
    public BlockedByteArrayOutputStream(final BlockedByteArray src, final long pos) {
        if (src == null) {
            throw new IllegalArgumentException("BlockedByteArray cannot be null");
        }
        this.src = src;
        this.pos = pos;
    }
    
    void setPosition(final long pos) {
        this.pos = pos;
    }
    
    long getPosition() {
        return this.pos;
    }
    
    public void write(final int n) {
        this.pos += this.src.writeByte(this.pos, (byte)n);
    }
    
    public void write(final byte[] array, final int n, final int n2) {
        this.pos += this.src.writeBytes(this.pos, array, n, n2);
    }
    
    public void close() {
        this.src = null;
    }
}
