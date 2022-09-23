// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.ipc;

import java.util.Arrays;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import org.apache.hadoop.classification.InterfaceAudience;
import java.io.DataOutputStream;

@InterfaceAudience.Private
public class ResponseBuffer extends DataOutputStream
{
    public ResponseBuffer() {
        this(1024);
    }
    
    public ResponseBuffer(final int capacity) {
        super(new FramedBuffer(capacity));
    }
    
    private FramedBuffer getFramedBuffer() {
        final FramedBuffer buf = (FramedBuffer)this.out;
        buf.setSize(this.written);
        return buf;
    }
    
    public void writeTo(final OutputStream out) throws IOException {
        this.getFramedBuffer().writeTo(out);
    }
    
    byte[] toByteArray() {
        return this.getFramedBuffer().toByteArray();
    }
    
    int capacity() {
        return ((FramedBuffer)this.out).capacity();
    }
    
    void setCapacity(final int capacity) {
        ((FramedBuffer)this.out).setCapacity(capacity);
    }
    
    void ensureCapacity(final int capacity) {
        if (((FramedBuffer)this.out).capacity() < capacity) {
            ((FramedBuffer)this.out).setCapacity(capacity);
        }
    }
    
    ResponseBuffer reset() {
        this.written = 0;
        ((FramedBuffer)this.out).reset();
        return this;
    }
    
    private static class FramedBuffer extends ByteArrayOutputStream
    {
        private static final int FRAMING_BYTES = 4;
        
        FramedBuffer(final int capacity) {
            super(capacity + 4);
            this.reset();
        }
        
        @Override
        public int size() {
            return this.count - 4;
        }
        
        void setSize(final int size) {
            this.buf[0] = (byte)(size >>> 24 & 0xFF);
            this.buf[1] = (byte)(size >>> 16 & 0xFF);
            this.buf[2] = (byte)(size >>> 8 & 0xFF);
            this.buf[3] = (byte)(size >>> 0 & 0xFF);
        }
        
        int capacity() {
            return this.buf.length - 4;
        }
        
        void setCapacity(final int capacity) {
            this.buf = Arrays.copyOf(this.buf, capacity + 4);
        }
        
        @Override
        public void reset() {
            this.count = 4;
            this.setSize(0);
        }
    }
}
