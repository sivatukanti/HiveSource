// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.io;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;
import java.io.DataInputStream;

@InterfaceAudience.LimitedPrivate({ "HDFS", "MapReduce" })
@InterfaceStability.Unstable
public class DataInputBuffer extends DataInputStream
{
    private Buffer buffer;
    
    public DataInputBuffer() {
        this(new Buffer());
    }
    
    private DataInputBuffer(final Buffer buffer) {
        super(buffer);
        this.buffer = buffer;
    }
    
    public void reset(final byte[] input, final int length) {
        this.buffer.reset(input, 0, length);
    }
    
    public void reset(final byte[] input, final int start, final int length) {
        this.buffer.reset(input, start, length);
    }
    
    public byte[] getData() {
        return this.buffer.getData();
    }
    
    public int getPosition() {
        return this.buffer.getPosition();
    }
    
    public int getLength() {
        return this.buffer.getLength();
    }
    
    private static class Buffer extends ByteArrayInputStream
    {
        public Buffer() {
            super(new byte[0]);
        }
        
        public void reset(final byte[] input, final int start, final int length) {
            this.buf = input;
            this.count = start + length;
            this.mark = start;
            this.pos = start;
        }
        
        public byte[] getData() {
            return this.buf;
        }
        
        public int getPosition() {
            return this.pos;
        }
        
        public int getLength() {
            return this.count;
        }
        
        @Override
        public int read() {
            return (this.pos < this.count) ? (this.buf[this.pos++] & 0xFF) : -1;
        }
        
        @Override
        public int read(final byte[] b, final int off, int len) {
            if (b == null) {
                throw new NullPointerException();
            }
            if (off < 0 || len < 0 || len > b.length - off) {
                throw new IndexOutOfBoundsException();
            }
            if (this.pos >= this.count) {
                return -1;
            }
            if (this.pos + len > this.count) {
                len = this.count - this.pos;
            }
            if (len <= 0) {
                return 0;
            }
            System.arraycopy(this.buf, this.pos, b, off, len);
            this.pos += len;
            return len;
        }
        
        @Override
        public long skip(long n) {
            if (this.pos + n > this.count) {
                n = this.count - this.pos;
            }
            if (n < 0L) {
                return 0L;
            }
            this.pos += (int)n;
            return n;
        }
        
        @Override
        public int available() {
            return this.count - this.pos;
        }
    }
}
