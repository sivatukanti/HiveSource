// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.thrift.transport;

import org.apache.thrift.TByteArrayOutputStream;

public class TFramedTransport extends TTransport
{
    protected static final int DEFAULT_MAX_LENGTH = 16384000;
    private int maxLength_;
    private TTransport transport_;
    private final TByteArrayOutputStream writeBuffer_;
    private TMemoryInputTransport readBuffer_;
    private final byte[] i32buf;
    
    public TFramedTransport(final TTransport transport, final int maxLength) {
        this.transport_ = null;
        this.writeBuffer_ = new TByteArrayOutputStream(1024);
        this.readBuffer_ = new TMemoryInputTransport(new byte[0]);
        this.i32buf = new byte[4];
        this.transport_ = transport;
        this.maxLength_ = maxLength;
    }
    
    public TFramedTransport(final TTransport transport) {
        this.transport_ = null;
        this.writeBuffer_ = new TByteArrayOutputStream(1024);
        this.readBuffer_ = new TMemoryInputTransport(new byte[0]);
        this.i32buf = new byte[4];
        this.transport_ = transport;
        this.maxLength_ = 16384000;
    }
    
    @Override
    public void open() throws TTransportException {
        this.transport_.open();
    }
    
    @Override
    public boolean isOpen() {
        return this.transport_.isOpen();
    }
    
    @Override
    public void close() {
        this.transport_.close();
    }
    
    @Override
    public int read(final byte[] buf, final int off, final int len) throws TTransportException {
        if (this.readBuffer_ != null) {
            final int got = this.readBuffer_.read(buf, off, len);
            if (got > 0) {
                return got;
            }
        }
        this.readFrame();
        return this.readBuffer_.read(buf, off, len);
    }
    
    @Override
    public byte[] getBuffer() {
        return this.readBuffer_.getBuffer();
    }
    
    @Override
    public int getBufferPosition() {
        return this.readBuffer_.getBufferPosition();
    }
    
    @Override
    public int getBytesRemainingInBuffer() {
        return this.readBuffer_.getBytesRemainingInBuffer();
    }
    
    @Override
    public void consumeBuffer(final int len) {
        this.readBuffer_.consumeBuffer(len);
    }
    
    private void readFrame() throws TTransportException {
        this.transport_.readAll(this.i32buf, 0, 4);
        final int size = decodeFrameSize(this.i32buf);
        if (size < 0) {
            throw new TTransportException("Read a negative frame size (" + size + ")!");
        }
        if (size > this.maxLength_) {
            throw new TTransportException("Frame size (" + size + ") larger than max length (" + this.maxLength_ + ")!");
        }
        final byte[] buff = new byte[size];
        this.transport_.readAll(buff, 0, size);
        this.readBuffer_.reset(buff);
    }
    
    @Override
    public void write(final byte[] buf, final int off, final int len) throws TTransportException {
        this.writeBuffer_.write(buf, off, len);
    }
    
    @Override
    public void flush() throws TTransportException {
        final byte[] buf = this.writeBuffer_.get();
        final int len = this.writeBuffer_.len();
        this.writeBuffer_.reset();
        encodeFrameSize(len, this.i32buf);
        this.transport_.write(this.i32buf, 0, 4);
        this.transport_.write(buf, 0, len);
        this.transport_.flush();
    }
    
    public static final void encodeFrameSize(final int frameSize, final byte[] buf) {
        buf[0] = (byte)(0xFF & frameSize >> 24);
        buf[1] = (byte)(0xFF & frameSize >> 16);
        buf[2] = (byte)(0xFF & frameSize >> 8);
        buf[3] = (byte)(0xFF & frameSize);
    }
    
    public static final int decodeFrameSize(final byte[] buf) {
        return (buf[0] & 0xFF) << 24 | (buf[1] & 0xFF) << 16 | (buf[2] & 0xFF) << 8 | (buf[3] & 0xFF);
    }
    
    public static class Factory extends TTransportFactory
    {
        private int maxLength_;
        
        public Factory() {
            this.maxLength_ = 16384000;
        }
        
        public Factory(final int maxLength) {
            this.maxLength_ = maxLength;
        }
        
        @Override
        public TTransport getTransport(final TTransport base) {
            return new TFramedTransport(base, this.maxLength_);
        }
    }
}
