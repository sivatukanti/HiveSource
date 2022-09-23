// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.io.vfmem;

import java.io.InputStream;

class BlockedByteArrayInputStream extends InputStream
{
    private BlockedByteArray src;
    private long pos;
    
    public BlockedByteArrayInputStream(final BlockedByteArray src, final long pos) {
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
    
    public int read() {
        final int read = this.src.read(this.pos);
        if (read != -1) {
            ++this.pos;
        }
        return read;
    }
    
    public int read(final byte[] array, final int n, final int n2) {
        final int read = this.src.read(this.pos, array, n, n2);
        if (read != -1) {
            this.pos += read;
        }
        return read;
    }
    
    public void close() {
        this.src = null;
    }
}
