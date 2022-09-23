// 
// Decompiled by Procyon v0.5.36
// 

package parquet.org.apache.thrift.transport;

import org.apache.commons.lang.NotImplementedException;

public final class AutoExpandingBufferWriteTransport extends TTransport
{
    private final AutoExpandingBuffer buf;
    private int pos;
    
    public AutoExpandingBufferWriteTransport(final int initialCapacity, final double growthCoefficient) {
        this.buf = new AutoExpandingBuffer(initialCapacity, growthCoefficient);
        this.pos = 0;
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
    public int read(final byte[] buf, final int off, final int len) throws TTransportException {
        throw new NotImplementedException();
    }
    
    @Override
    public void write(final byte[] toWrite, final int off, final int len) throws TTransportException {
        this.buf.resizeIfNecessary(this.pos + len);
        System.arraycopy(toWrite, off, this.buf.array(), this.pos, len);
        this.pos += len;
    }
    
    public AutoExpandingBuffer getBuf() {
        return this.buf;
    }
    
    public int getPos() {
        return this.pos;
    }
    
    public void reset() {
        this.pos = 0;
    }
}
