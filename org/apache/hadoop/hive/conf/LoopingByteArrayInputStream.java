// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.conf;

import java.io.IOException;
import java.io.ByteArrayInputStream;
import java.io.InputStream;

public class LoopingByteArrayInputStream extends InputStream
{
    private final byte[] buf;
    private final ThreadLocal<ByteArrayInputStream> threadLocalByteArrayInputStream;
    
    public LoopingByteArrayInputStream(final byte[] buf) {
        this.threadLocalByteArrayInputStream = new ThreadLocal<ByteArrayInputStream>() {
            @Override
            protected synchronized ByteArrayInputStream initialValue() {
                return null;
            }
        };
        this.buf = buf;
    }
    
    private ByteArrayInputStream getByteArrayInputStream() {
        ByteArrayInputStream bais = this.threadLocalByteArrayInputStream.get();
        if (bais == null) {
            bais = new ByteArrayInputStream(this.buf);
            this.threadLocalByteArrayInputStream.set(bais);
        }
        return bais;
    }
    
    @Override
    public synchronized int available() {
        return this.getByteArrayInputStream().available();
    }
    
    @Override
    public void mark(final int arg0) {
        this.getByteArrayInputStream().mark(arg0);
    }
    
    @Override
    public boolean markSupported() {
        return this.getByteArrayInputStream().markSupported();
    }
    
    @Override
    public synchronized int read() {
        return this.getByteArrayInputStream().read();
    }
    
    @Override
    public synchronized int read(final byte[] arg0, final int arg1, final int arg2) {
        return this.getByteArrayInputStream().read(arg0, arg1, arg2);
    }
    
    @Override
    public synchronized void reset() {
        this.getByteArrayInputStream().reset();
    }
    
    @Override
    public synchronized long skip(final long arg0) {
        return this.getByteArrayInputStream().skip(arg0);
    }
    
    @Override
    public int read(final byte[] arg0) throws IOException {
        return this.getByteArrayInputStream().read(arg0);
    }
    
    @Override
    public void close() throws IOException {
        this.getByteArrayInputStream().reset();
        this.getByteArrayInputStream().close();
    }
}
