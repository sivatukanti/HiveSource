// 
// Decompiled by Procyon v0.5.36
// 

package parquet.org.apache.thrift.transport;

public final class TMemoryInputTransport extends TTransport
{
    private byte[] buf_;
    private int pos_;
    private int endPos_;
    
    public TMemoryInputTransport() {
    }
    
    public TMemoryInputTransport(final byte[] buf) {
        this.reset(buf);
    }
    
    public TMemoryInputTransport(final byte[] buf, final int offset, final int length) {
        this.reset(buf, offset, length);
    }
    
    public void reset(final byte[] buf) {
        this.reset(buf, 0, buf.length);
    }
    
    public void reset(final byte[] buf, final int offset, final int length) {
        this.buf_ = buf;
        this.pos_ = offset;
        this.endPos_ = offset + length;
    }
    
    public void clear() {
        this.buf_ = null;
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
        final int bytesRemaining = this.getBytesRemainingInBuffer();
        final int amtToRead = (len > bytesRemaining) ? bytesRemaining : len;
        if (amtToRead > 0) {
            System.arraycopy(this.buf_, this.pos_, buf, off, amtToRead);
            this.consumeBuffer(amtToRead);
        }
        return amtToRead;
    }
    
    @Override
    public void write(final byte[] buf, final int off, final int len) throws TTransportException {
        throw new UnsupportedOperationException("No writing allowed!");
    }
    
    @Override
    public byte[] getBuffer() {
        return this.buf_;
    }
    
    @Override
    public int getBufferPosition() {
        return this.pos_;
    }
    
    @Override
    public int getBytesRemainingInBuffer() {
        return this.endPos_ - this.pos_;
    }
    
    @Override
    public void consumeBuffer(final int len) {
        this.pos_ += len;
    }
}
