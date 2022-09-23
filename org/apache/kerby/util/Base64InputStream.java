// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.FilterInputStream;

public class Base64InputStream extends FilterInputStream
{
    private final boolean doEncode;
    private final Base64 base64;
    private final byte[] singleByte;
    
    public Base64InputStream(final InputStream in) {
        this(in, false);
    }
    
    public Base64InputStream(final InputStream in, final boolean doEncode) {
        super(in);
        this.singleByte = new byte[1];
        this.doEncode = doEncode;
        this.base64 = new Base64();
    }
    
    public Base64InputStream(final InputStream in, final boolean doEncode, final int lineLength, final byte[] lineSeparator) {
        super(in);
        this.singleByte = new byte[1];
        this.doEncode = doEncode;
        this.base64 = new Base64(lineLength, lineSeparator);
    }
    
    @Override
    public int read() throws IOException {
        int r;
        for (r = this.read(this.singleByte, 0, 1); r == 0; r = this.read(this.singleByte, 0, 1)) {}
        if (r > 0) {
            return (this.singleByte[0] < 0) ? (256 + this.singleByte[0]) : this.singleByte[0];
        }
        return -1;
    }
    
    @Override
    public int read(final byte[] b, final int offset, final int len) throws IOException {
        if (b == null) {
            throw new NullPointerException();
        }
        if (offset < 0 || len < 0) {
            throw new IndexOutOfBoundsException();
        }
        if (offset > b.length || offset + len > b.length) {
            throw new IndexOutOfBoundsException();
        }
        if (len == 0) {
            return 0;
        }
        if (!this.base64.hasData()) {
            final byte[] buf = new byte[this.doEncode ? 4096 : 8192];
            final int c = this.in.read(buf);
            if (c > 0 && b.length == len) {
                this.base64.setInitialBuffer(b, offset, len);
            }
            if (this.doEncode) {
                this.base64.encode(buf, 0, c);
            }
            else {
                this.base64.decode(buf, 0, c);
            }
        }
        return this.base64.readResults(b, offset, len);
    }
    
    @Override
    public boolean markSupported() {
        return false;
    }
}
