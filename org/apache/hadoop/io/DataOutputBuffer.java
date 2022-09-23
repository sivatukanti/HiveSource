// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.io;

import java.io.ByteArrayOutputStream;
import com.google.common.base.Preconditions;
import java.io.IOException;
import java.io.DataInput;
import java.io.OutputStream;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;
import java.io.DataOutputStream;

@InterfaceAudience.LimitedPrivate({ "HDFS", "MapReduce" })
@InterfaceStability.Unstable
public class DataOutputBuffer extends DataOutputStream
{
    private Buffer buffer;
    
    public DataOutputBuffer() {
        this(new Buffer());
    }
    
    public DataOutputBuffer(final int size) {
        this(new Buffer(size));
    }
    
    private DataOutputBuffer(final Buffer buffer) {
        super(buffer);
        this.buffer = buffer;
    }
    
    public byte[] getData() {
        return this.buffer.getData();
    }
    
    public int getLength() {
        return this.buffer.getLength();
    }
    
    public DataOutputBuffer reset() {
        this.written = 0;
        this.buffer.reset();
        return this;
    }
    
    public void write(final DataInput in, final int length) throws IOException {
        this.buffer.write(in, length);
    }
    
    public void writeTo(final OutputStream out) throws IOException {
        this.buffer.writeTo(out);
    }
    
    public void writeInt(final int v, final int offset) throws IOException {
        Preconditions.checkState(offset + 4 <= this.buffer.getLength());
        final byte[] b = { (byte)(v >>> 24 & 0xFF), (byte)(v >>> 16 & 0xFF), (byte)(v >>> 8 & 0xFF), (byte)(v >>> 0 & 0xFF) };
        final int oldCount = this.buffer.setCount(offset);
        this.buffer.write(b);
        this.buffer.setCount(oldCount);
    }
    
    private static class Buffer extends ByteArrayOutputStream
    {
        public byte[] getData() {
            return this.buf;
        }
        
        public int getLength() {
            return this.count;
        }
        
        public Buffer() {
        }
        
        public Buffer(final int size) {
            super(size);
        }
        
        public void write(final DataInput in, final int len) throws IOException {
            final int newcount = this.count + len;
            if (newcount > this.buf.length) {
                final byte[] newbuf = new byte[Math.max(this.buf.length << 1, newcount)];
                System.arraycopy(this.buf, 0, newbuf, 0, this.count);
                this.buf = newbuf;
            }
            in.readFully(this.buf, this.count, len);
            this.count = newcount;
        }
        
        private int setCount(final int newCount) {
            Preconditions.checkArgument(newCount >= 0 && newCount <= this.buf.length);
            final int oldCount = this.count;
            this.count = newCount;
            return oldCount;
        }
    }
}
