// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.thrift.transport;

public class AutoExpandingBufferReadTransport extends TTransport
{
    private final AutoExpandingBuffer buf;
    private int pos;
    private int limit;
    
    public AutoExpandingBufferReadTransport(final int initialCapacity, final double overgrowthCoefficient) {
        this.pos = 0;
        this.limit = 0;
        this.buf = new AutoExpandingBuffer(initialCapacity, overgrowthCoefficient);
    }
    
    public void fill(final TTransport inTrans, final int length) throws TTransportException {
        this.buf.resizeIfNecessary(length);
        inTrans.readAll(this.buf.array(), 0, length);
        this.pos = 0;
        this.limit = length;
    }
    
    @Override
    public void close() {
    }
    
    @Override
    public boolean isOpen() {
        return true;
    }
    
    @Override
    public void open() throws TTransportException {
    }
    
    @Override
    public final int read(final byte[] target, final int off, final int len) throws TTransportException {
        final int amtToRead = Math.min(len, this.getBytesRemainingInBuffer());
        System.arraycopy(this.buf.array(), this.pos, target, off, amtToRead);
        this.consumeBuffer(amtToRead);
        return amtToRead;
    }
    
    @Override
    public void write(final byte[] buf, final int off, final int len) throws TTransportException {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public final void consumeBuffer(final int len) {
        this.pos += len;
    }
    
    @Override
    public final byte[] getBuffer() {
        return this.buf.array();
    }
    
    @Override
    public final int getBufferPosition() {
        return this.pos;
    }
    
    @Override
    public final int getBytesRemainingInBuffer() {
        return this.limit - this.pos;
    }
}
