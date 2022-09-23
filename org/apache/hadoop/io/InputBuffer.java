// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.io;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;
import java.io.FilterInputStream;

@InterfaceAudience.LimitedPrivate({ "HDFS", "MapReduce" })
@InterfaceStability.Unstable
public class InputBuffer extends FilterInputStream
{
    private Buffer buffer;
    
    public InputBuffer() {
        this(new Buffer());
    }
    
    private InputBuffer(final Buffer buffer) {
        super(buffer);
        this.buffer = buffer;
    }
    
    public void reset(final byte[] input, final int length) {
        this.buffer.reset(input, 0, length);
    }
    
    public void reset(final byte[] input, final int start, final int length) {
        this.buffer.reset(input, start, length);
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
        
        public int getPosition() {
            return this.pos;
        }
        
        public int getLength() {
            return this.count;
        }
    }
}
