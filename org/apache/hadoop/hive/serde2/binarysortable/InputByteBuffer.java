// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.serde2.binarysortable;

import java.io.EOFException;
import java.io.IOException;

public class InputByteBuffer
{
    byte[] data;
    int start;
    int end;
    
    public void reset(final byte[] data, final int start, final int end) {
        this.data = data;
        this.start = start;
        this.end = end;
    }
    
    public final byte read() throws IOException {
        return this.read(false);
    }
    
    public final byte read(final boolean invert) throws IOException {
        if (this.start >= this.end) {
            throw new EOFException();
        }
        if (invert) {
            return (byte)(0xFF ^ this.data[this.start++]);
        }
        return this.data[this.start++];
    }
    
    public final int tell() {
        return this.start;
    }
    
    public final void seek(final int position) {
        this.start = position;
    }
    
    public final int getEnd() {
        return this.end;
    }
    
    public final boolean isEof() {
        return this.start >= this.end;
    }
    
    public final byte[] getData() {
        return this.data;
    }
    
    public String dumpHex() {
        final StringBuilder sb = new StringBuilder();
        for (int i = this.start; i < this.end; ++i) {
            final byte b = this.data[i];
            final int v = (b < 0) ? (256 + b) : b;
            sb.append(String.format("x%02x", v));
        }
        return sb.toString();
    }
}
