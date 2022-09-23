// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.store.raw.data;

import java.io.IOException;

public abstract class BufferedByteHolderInputStream extends ByteHolderInputStream
{
    public BufferedByteHolderInputStream(final ByteHolder byteHolder) {
        super(byteHolder);
    }
    
    public abstract void fillByteHolder() throws IOException;
    
    public int read() throws IOException {
        this.fillByteHolder();
        return super.read();
    }
    
    public int read(final byte[] array, final int n, final int n2) throws IOException {
        this.fillByteHolder();
        return super.read(array, n, n2);
    }
    
    public long skip(final long n) throws IOException {
        long n2;
        long skip;
        for (n2 = 0L; n2 < n; n2 += skip) {
            this.fillByteHolder();
            skip = super.skip(n - n2);
            if (skip <= 0L) {
                break;
            }
        }
        return n2;
    }
    
    public int available() throws IOException {
        this.fillByteHolder();
        return super.available();
    }
}
