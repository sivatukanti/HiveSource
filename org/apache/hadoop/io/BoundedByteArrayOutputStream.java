// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.io;

import java.io.IOException;
import java.io.EOFException;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;
import java.io.OutputStream;

@InterfaceAudience.LimitedPrivate({ "HDFS", "MapReduce" })
@InterfaceStability.Unstable
public class BoundedByteArrayOutputStream extends OutputStream
{
    private byte[] buffer;
    private int startOffset;
    private int limit;
    private int currentPointer;
    
    public BoundedByteArrayOutputStream(final int capacity) {
        this(capacity, capacity);
    }
    
    public BoundedByteArrayOutputStream(final int capacity, final int limit) {
        this(new byte[capacity], 0, limit);
    }
    
    protected BoundedByteArrayOutputStream(final byte[] buf, final int offset, final int limit) {
        this.resetBuffer(buf, offset, limit);
    }
    
    protected void resetBuffer(final byte[] buf, final int offset, final int limit) {
        final int capacity = buf.length - offset;
        if (capacity < limit || (capacity | limit) < 0) {
            throw new IllegalArgumentException("Invalid capacity/limit");
        }
        this.buffer = buf;
        this.startOffset = offset;
        this.currentPointer = offset;
        this.limit = offset + limit;
    }
    
    @Override
    public void write(final int b) throws IOException {
        if (this.currentPointer >= this.limit) {
            throw new EOFException("Reaching the limit of the buffer.");
        }
        this.buffer[this.currentPointer++] = (byte)b;
    }
    
    @Override
    public void write(final byte[] b, final int off, final int len) throws IOException {
        if (off < 0 || off > b.length || len < 0 || off + len > b.length || off + len < 0) {
            throw new IndexOutOfBoundsException();
        }
        if (len == 0) {
            return;
        }
        if (this.currentPointer + len > this.limit) {
            throw new EOFException("Reach the limit of the buffer");
        }
        System.arraycopy(b, off, this.buffer, this.currentPointer, len);
        this.currentPointer += len;
    }
    
    public void reset(final int newlim) {
        if (newlim > this.buffer.length - this.startOffset) {
            throw new IndexOutOfBoundsException("Limit exceeds buffer size");
        }
        this.limit = newlim;
        this.currentPointer = this.startOffset;
    }
    
    public void reset() {
        this.limit = this.buffer.length - this.startOffset;
        this.currentPointer = this.startOffset;
    }
    
    public int getLimit() {
        return this.limit;
    }
    
    public byte[] getBuffer() {
        return this.buffer;
    }
    
    public int size() {
        return this.currentPointer - this.startOffset;
    }
    
    public int available() {
        return this.limit - this.currentPointer;
    }
}
