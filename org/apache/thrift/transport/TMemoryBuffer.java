// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.thrift.transport;

import java.io.UnsupportedEncodingException;
import org.apache.thrift.TByteArrayOutputStream;

public class TMemoryBuffer extends TTransport
{
    private TByteArrayOutputStream arr_;
    private int pos_;
    
    public TMemoryBuffer(final int size) {
        this.arr_ = new TByteArrayOutputStream(size);
    }
    
    @Override
    public boolean isOpen() {
        return true;
    }
    
    @Override
    public void open() {
    }
    
    @Override
    public void close() {
    }
    
    @Override
    public int read(final byte[] buf, final int off, final int len) {
        final byte[] src = this.arr_.get();
        final int amtToRead = (len > this.arr_.len() - this.pos_) ? (this.arr_.len() - this.pos_) : len;
        if (amtToRead > 0) {
            System.arraycopy(src, this.pos_, buf, off, amtToRead);
            this.pos_ += amtToRead;
        }
        return amtToRead;
    }
    
    @Override
    public void write(final byte[] buf, final int off, final int len) {
        this.arr_.write(buf, off, len);
    }
    
    public String toString(final String enc) throws UnsupportedEncodingException {
        return this.arr_.toString(enc);
    }
    
    public String inspect() {
        final StringBuilder buf = new StringBuilder();
        final byte[] bytes = this.arr_.toByteArray();
        for (int i = 0; i < bytes.length; ++i) {
            buf.append((this.pos_ == i) ? "==>" : "").append(Integer.toHexString(bytes[i] & 0xFF)).append(" ");
        }
        return buf.toString();
    }
    
    public int length() {
        return this.arr_.size();
    }
    
    public byte[] getArray() {
        return this.arr_.get();
    }
}
