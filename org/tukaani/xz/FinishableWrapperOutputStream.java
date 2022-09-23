// 
// Decompiled by Procyon v0.5.36
// 

package org.tukaani.xz;

import java.io.IOException;
import java.io.OutputStream;

public class FinishableWrapperOutputStream extends FinishableOutputStream
{
    protected OutputStream out;
    
    public FinishableWrapperOutputStream(final OutputStream out) {
        this.out = out;
    }
    
    public void write(final int n) throws IOException {
        this.out.write(n);
    }
    
    public void write(final byte[] b) throws IOException {
        this.out.write(b);
    }
    
    public void write(final byte[] b, final int off, final int len) throws IOException {
        this.out.write(b, off, len);
    }
    
    public void flush() throws IOException {
        this.out.flush();
    }
    
    public void close() throws IOException {
        this.out.close();
    }
}
