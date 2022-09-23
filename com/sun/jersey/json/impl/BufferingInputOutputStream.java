// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.json.impl;

import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;
import java.io.OutputStream;

public class BufferingInputOutputStream extends OutputStream
{
    private final Queue<byte[]> buffers;
    
    public BufferingInputOutputStream() {
        this.buffers = new LinkedList<byte[]>();
    }
    
    @Override
    public void write(final int b) throws IOException {
        final byte[] buffer = { (byte)b };
        this.buffers.add(buffer);
    }
    
    @Override
    public void write(final byte[] b) throws IOException {
        this.buffers.add(b);
    }
    
    @Override
    public void write(final byte[] b, final int off, final int len) throws IOException {
        if (len > 0) {
            final byte[] buffer = new byte[len];
            System.arraycopy(b, off, buffer, 0, len);
            this.buffers.add(buffer);
        }
    }
    
    public byte[] nextBytes() {
        return this.buffers.poll();
    }
    
    public int available() {
        if (this.buffers.isEmpty()) {
            return 0;
        }
        return this.buffers.peek().length;
    }
}
