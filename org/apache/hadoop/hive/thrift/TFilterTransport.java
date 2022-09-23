// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.thrift;

import org.apache.thrift.transport.TTransportException;
import org.apache.thrift.transport.TTransport;

public class TFilterTransport extends TTransport
{
    protected final TTransport wrapped;
    
    public TFilterTransport(final TTransport wrapped) {
        this.wrapped = wrapped;
    }
    
    @Override
    public void open() throws TTransportException {
        this.wrapped.open();
    }
    
    @Override
    public boolean isOpen() {
        return this.wrapped.isOpen();
    }
    
    @Override
    public boolean peek() {
        return this.wrapped.peek();
    }
    
    @Override
    public void close() {
        this.wrapped.close();
    }
    
    @Override
    public int read(final byte[] buf, final int off, final int len) throws TTransportException {
        return this.wrapped.read(buf, off, len);
    }
    
    @Override
    public int readAll(final byte[] buf, final int off, final int len) throws TTransportException {
        return this.wrapped.readAll(buf, off, len);
    }
    
    @Override
    public void write(final byte[] buf) throws TTransportException {
        this.wrapped.write(buf);
    }
    
    @Override
    public void write(final byte[] buf, final int off, final int len) throws TTransportException {
        this.wrapped.write(buf, off, len);
    }
    
    @Override
    public void flush() throws TTransportException {
        this.wrapped.flush();
    }
    
    @Override
    public byte[] getBuffer() {
        return this.wrapped.getBuffer();
    }
    
    @Override
    public int getBufferPosition() {
        return this.wrapped.getBufferPosition();
    }
    
    @Override
    public int getBytesRemainingInBuffer() {
        return this.wrapped.getBytesRemainingInBuffer();
    }
    
    @Override
    public void consumeBuffer(final int len) {
        this.wrapped.consumeBuffer(len);
    }
}
