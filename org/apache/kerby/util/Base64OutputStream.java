// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.util;

import java.io.IOException;
import java.io.OutputStream;
import java.io.FilterOutputStream;

public class Base64OutputStream extends FilterOutputStream
{
    private final boolean doEncode;
    private final Base64 base64;
    private final byte[] singleByte;
    
    public Base64OutputStream(final OutputStream out) {
        this(out, true);
    }
    
    public Base64OutputStream(final OutputStream out, final boolean doEncode) {
        super(out);
        this.singleByte = new byte[1];
        this.doEncode = doEncode;
        this.base64 = new Base64();
    }
    
    public Base64OutputStream(final OutputStream out, final boolean doEncode, final int lineLength, final byte[] lineSeparator) {
        super(out);
        this.singleByte = new byte[1];
        this.doEncode = doEncode;
        this.base64 = new Base64(lineLength, lineSeparator);
    }
    
    @Override
    public void write(final int i) throws IOException {
        this.singleByte[0] = (byte)i;
        this.write(this.singleByte, 0, 1);
    }
    
    @Override
    public void write(final byte[] b, final int offset, final int len) throws IOException {
        if (b == null) {
            throw new NullPointerException();
        }
        if (offset < 0 || len < 0) {
            throw new IndexOutOfBoundsException();
        }
        if (offset > b.length || offset + len > b.length) {
            throw new IndexOutOfBoundsException();
        }
        if (len > 0) {
            if (this.doEncode) {
                this.base64.encode(b, offset, len);
            }
            else {
                this.base64.decode(b, offset, len);
            }
            this.flush(false);
        }
    }
    
    private void flush(final boolean propogate) throws IOException {
        final int avail = this.base64.avail();
        if (avail > 0) {
            final byte[] buf = new byte[avail];
            final int c = this.base64.readResults(buf, 0, avail);
            if (c > 0) {
                this.out.write(buf, 0, c);
            }
        }
        if (propogate) {
            this.out.flush();
        }
    }
    
    @Override
    public void flush() throws IOException {
        this.flush(true);
    }
    
    @Override
    public void close() throws IOException {
        if (this.doEncode) {
            this.base64.encode(this.singleByte, 0, -1);
        }
        else {
            this.base64.decode(this.singleByte, 0, -1);
        }
        this.flush();
        this.out.close();
    }
}
