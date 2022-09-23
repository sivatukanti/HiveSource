// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.io;

import java.nio.ByteBuffer;
import java.io.InputStream;
import java.io.DataInputStream;

public class DataInputByteBuffer extends DataInputStream
{
    private Buffer buffers;
    
    public DataInputByteBuffer() {
        this(new Buffer());
    }
    
    private DataInputByteBuffer(final Buffer buffers) {
        super(buffers);
        this.buffers = buffers;
    }
    
    public void reset(final ByteBuffer... input) {
        this.buffers.reset(input);
    }
    
    public ByteBuffer[] getData() {
        return this.buffers.getData();
    }
    
    public int getPosition() {
        return this.buffers.getPosition();
    }
    
    public int getLength() {
        return this.buffers.getLength();
    }
    
    private static class Buffer extends InputStream
    {
        private final byte[] scratch;
        ByteBuffer[] buffers;
        int bidx;
        int pos;
        int length;
        
        private Buffer() {
            this.scratch = new byte[1];
            this.buffers = new ByteBuffer[0];
        }
        
        @Override
        public int read() {
            if (-1 == this.read(this.scratch, 0, 1)) {
                return -1;
            }
            return this.scratch[0] & 0xFF;
        }
        
        @Override
        public int read(final byte[] b, int off, int len) {
            if (this.bidx >= this.buffers.length) {
                return -1;
            }
            int cur = 0;
            do {
                final int rem = Math.min(len, this.buffers[this.bidx].remaining());
                this.buffers[this.bidx].get(b, off, rem);
                cur += rem;
                off += rem;
                len -= rem;
            } while (len > 0 && ++this.bidx < this.buffers.length);
            this.pos += cur;
            return cur;
        }
        
        public void reset(final ByteBuffer[] buffers) {
            final int bidx = 0;
            this.length = bidx;
            this.pos = bidx;
            this.bidx = bidx;
            this.buffers = buffers;
            for (final ByteBuffer b : buffers) {
                this.length += b.remaining();
            }
        }
        
        public int getPosition() {
            return this.pos;
        }
        
        public int getLength() {
            return this.length;
        }
        
        public ByteBuffer[] getData() {
            return this.buffers;
        }
    }
}
