// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.serde2;

import java.io.IOException;
import org.apache.hadoop.hive.common.io.NonSyncByteArrayOutputStream;
import org.apache.hadoop.hive.common.io.NonSyncByteArrayInputStream;

public class ByteStream
{
    public static class Input extends NonSyncByteArrayInputStream
    {
        public byte[] getData() {
            return this.buf;
        }
        
        public int getCount() {
            return this.count;
        }
        
        public void reset(final byte[] argBuf, final int argCount) {
            this.buf = argBuf;
            final int n = 0;
            this.pos = n;
            this.mark = n;
            this.count = argCount;
        }
        
        public Input() {
            super(new byte[1]);
        }
        
        public Input(final byte[] buf) {
            super(buf);
        }
        
        public Input(final byte[] buf, final int offset, final int length) {
            super(buf, offset, length);
        }
    }
    
    public static final class Output extends NonSyncByteArrayOutputStream implements RandomAccessOutput
    {
        @Override
        public byte[] getData() {
            return this.buf;
        }
        
        public Output() {
        }
        
        public Output(final int size) {
            super(size);
        }
        
        @Override
        public void writeInt(final long offset, final int value) {
            int offset2 = (int)offset;
            this.getData()[offset2++] = (byte)(value >> 24);
            this.getData()[offset2++] = (byte)(value >> 16);
            this.getData()[offset2++] = (byte)(value >> 8);
            this.getData()[offset2] = (byte)value;
        }
        
        @Override
        public void writeByte(final long offset, final byte value) {
            this.getData()[(int)offset] = value;
        }
        
        @Override
        public void reserve(final int byteCount) {
            for (int i = 0; i < byteCount; ++i) {
                this.write(0);
            }
        }
        
        public boolean arraysEquals(final Output output) {
            if (this.count != output.count) {
                return false;
            }
            for (int i = 0; i < this.count; ++i) {
                if (this.buf[i] != output.buf[i]) {
                    return false;
                }
            }
            return true;
        }
    }
    
    public interface RandomAccessOutput
    {
        void writeByte(final long p0, final byte p1);
        
        void writeInt(final long p0, final int p1);
        
        void reserve(final int p0);
        
        void write(final int p0);
        
        void write(final byte[] p0) throws IOException;
        
        void write(final byte[] p0, final int p1, final int p2);
        
        int getLength();
    }
}
