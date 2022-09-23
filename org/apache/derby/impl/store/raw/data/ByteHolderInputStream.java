// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.store.raw.data;

import java.io.IOException;
import java.io.InputStream;

public class ByteHolderInputStream extends InputStream
{
    protected ByteHolder bh;
    
    public ByteHolderInputStream(final ByteHolder bh) {
        this.bh = bh;
    }
    
    public int read() throws IOException {
        return this.bh.read();
    }
    
    public int read(final byte[] array, final int n, final int n2) throws IOException {
        return this.bh.read(array, n, n2);
    }
    
    public long skip(final long n) throws IOException {
        return this.bh.skip(n);
    }
    
    public int available() throws IOException {
        return this.bh.available();
    }
    
    public void setByteHolder(final ByteHolder bh) {
        this.bh = bh;
    }
    
    public ByteHolder getByteHolder() {
        return this.bh;
    }
}
