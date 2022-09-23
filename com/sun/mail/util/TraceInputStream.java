// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.mail.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.FilterInputStream;

public class TraceInputStream extends FilterInputStream
{
    private boolean trace;
    private boolean quote;
    private OutputStream traceOut;
    
    public TraceInputStream(final InputStream in, final OutputStream traceOut) {
        super(in);
        this.trace = false;
        this.quote = false;
        this.traceOut = traceOut;
    }
    
    public void setTrace(final boolean trace) {
        this.trace = trace;
    }
    
    public void setQuote(final boolean quote) {
        this.quote = quote;
    }
    
    public int read() throws IOException {
        final int b = this.in.read();
        if (this.trace && b != -1) {
            if (this.quote) {
                this.writeByte(b);
            }
            else {
                this.traceOut.write(b);
            }
        }
        return b;
    }
    
    public int read(final byte[] b, final int off, final int len) throws IOException {
        final int count = this.in.read(b, off, len);
        if (this.trace && count != -1) {
            if (this.quote) {
                for (int i = 0; i < count; ++i) {
                    this.writeByte(b[off + i]);
                }
            }
            else {
                this.traceOut.write(b, off, count);
            }
        }
        return count;
    }
    
    private final void writeByte(int b) throws IOException {
        b &= 0xFF;
        if (b > 127) {
            this.traceOut.write(77);
            this.traceOut.write(45);
            b &= 0x7F;
        }
        if (b == 13) {
            this.traceOut.write(92);
            this.traceOut.write(114);
        }
        else if (b == 10) {
            this.traceOut.write(92);
            this.traceOut.write(110);
            this.traceOut.write(10);
        }
        else if (b == 9) {
            this.traceOut.write(92);
            this.traceOut.write(116);
        }
        else if (b < 32) {
            this.traceOut.write(94);
            this.traceOut.write(64 + b);
        }
        else {
            this.traceOut.write(b);
        }
    }
}
