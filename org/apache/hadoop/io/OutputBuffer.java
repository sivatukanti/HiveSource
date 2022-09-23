// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.io;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;
import java.io.FilterOutputStream;

@InterfaceAudience.LimitedPrivate({ "HDFS", "MapReduce" })
@InterfaceStability.Unstable
public class OutputBuffer extends FilterOutputStream
{
    private Buffer buffer;
    
    public OutputBuffer() {
        this(new Buffer());
    }
    
    private OutputBuffer(final Buffer buffer) {
        super(buffer);
        this.buffer = buffer;
    }
    
    public byte[] getData() {
        return this.buffer.getData();
    }
    
    public int getLength() {
        return this.buffer.getLength();
    }
    
    public OutputBuffer reset() {
        this.buffer.reset();
        return this;
    }
    
    public void write(final InputStream in, final int length) throws IOException {
        this.buffer.write(in, length);
    }
    
    private static class Buffer extends ByteArrayOutputStream
    {
        public byte[] getData() {
            return this.buf;
        }
        
        public int getLength() {
            return this.count;
        }
        
        @Override
        public void reset() {
            this.count = 0;
        }
        
        public void write(final InputStream in, final int len) throws IOException {
            final int newcount = this.count + len;
            if (newcount > this.buf.length) {
                final byte[] newbuf = new byte[Math.max(this.buf.length << 1, newcount)];
                System.arraycopy(this.buf, 0, newbuf, 0, this.count);
                this.buf = newbuf;
            }
            IOUtils.readFully(in, this.buf, this.count, len);
            this.count = newcount;
        }
    }
}
