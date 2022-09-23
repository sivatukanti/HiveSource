// 
// Decompiled by Procyon v0.5.36
// 

package parquet.org.apache.thrift.transport;

public class TFastFramedTransport extends TTransport
{
    public static final int DEFAULT_BUF_CAPACITY = 1024;
    public static final int DEFAULT_MAX_LENGTH = Integer.MAX_VALUE;
    private final TTransport underlying;
    private final AutoExpandingBufferWriteTransport writeBuffer;
    private final AutoExpandingBufferReadTransport readBuffer;
    private final byte[] i32buf;
    private final int maxLength;
    
    public TFastFramedTransport(final TTransport underlying) {
        this(underlying, 1024, Integer.MAX_VALUE);
    }
    
    public TFastFramedTransport(final TTransport underlying, final int initialBufferCapacity) {
        this(underlying, initialBufferCapacity, Integer.MAX_VALUE);
    }
    
    public TFastFramedTransport(final TTransport underlying, final int initialBufferCapacity, final int maxLength) {
        this.i32buf = new byte[4];
        this.underlying = underlying;
        this.maxLength = maxLength;
        this.writeBuffer = new AutoExpandingBufferWriteTransport(initialBufferCapacity, 1.5);
        this.readBuffer = new AutoExpandingBufferReadTransport(initialBufferCapacity, 1.5);
    }
    
    @Override
    public void close() {
        this.underlying.close();
    }
    
    @Override
    public boolean isOpen() {
        return this.underlying.isOpen();
    }
    
    @Override
    public void open() throws TTransportException {
        this.underlying.open();
    }
    
    @Override
    public int read(final byte[] buf, final int off, final int len) throws TTransportException {
        final int got = this.readBuffer.read(buf, off, len);
        if (got > 0) {
            return got;
        }
        this.readFrame();
        return this.readBuffer.read(buf, off, len);
    }
    
    private void readFrame() throws TTransportException {
        this.underlying.readAll(this.i32buf, 0, 4);
        final int size = TFramedTransport.decodeFrameSize(this.i32buf);
        if (size < 0) {
            throw new TTransportException("Read a negative frame size (" + size + ")!");
        }
        if (size > this.maxLength) {
            throw new TTransportException("Frame size (" + size + ") larger than max length (" + this.maxLength + ")!");
        }
        this.readBuffer.fill(this.underlying, size);
    }
    
    @Override
    public void write(final byte[] buf, final int off, final int len) throws TTransportException {
        this.writeBuffer.write(buf, off, len);
    }
    
    @Override
    public void consumeBuffer(final int len) {
        this.readBuffer.consumeBuffer(len);
    }
    
    @Override
    public void flush() throws TTransportException {
        final int length = this.writeBuffer.getPos();
        TFramedTransport.encodeFrameSize(length, this.i32buf);
        this.underlying.write(this.i32buf, 0, 4);
        this.underlying.write(this.writeBuffer.getBuf().array(), 0, length);
        this.writeBuffer.reset();
        this.underlying.flush();
    }
    
    @Override
    public byte[] getBuffer() {
        return this.readBuffer.getBuffer();
    }
    
    @Override
    public int getBufferPosition() {
        return this.readBuffer.getBufferPosition();
    }
    
    @Override
    public int getBytesRemainingInBuffer() {
        return this.readBuffer.getBytesRemainingInBuffer();
    }
    
    public static class Factory extends TTransportFactory
    {
        private final int initialCapacity;
        private final int maxLength;
        
        public Factory() {
            this(1024, Integer.MAX_VALUE);
        }
        
        public Factory(final int initialCapacity) {
            this(initialCapacity, Integer.MAX_VALUE);
        }
        
        public Factory(final int initialCapacity, final int maxLength) {
            this.initialCapacity = initialCapacity;
            this.maxLength = maxLength;
        }
        
        @Override
        public TTransport getTransport(final TTransport trans) {
            return new TFastFramedTransport(trans, this.initialCapacity, this.maxLength);
        }
    }
}
