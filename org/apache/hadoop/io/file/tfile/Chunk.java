// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.io.file.tfile;

import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.OutputStream;
import java.io.DataInput;
import java.io.IOException;
import java.io.DataInputStream;
import java.io.InputStream;

final class Chunk
{
    private Chunk() {
    }
    
    public static class ChunkDecoder extends InputStream
    {
        private DataInputStream in;
        private boolean lastChunk;
        private int remain;
        private boolean closed;
        
        public ChunkDecoder() {
            this.in = null;
            this.remain = 0;
            this.lastChunk = true;
            this.closed = true;
        }
        
        public void reset(final DataInputStream downStream) {
            this.in = downStream;
            this.lastChunk = false;
            this.remain = 0;
            this.closed = false;
        }
        
        public ChunkDecoder(final DataInputStream in) {
            this.in = null;
            this.remain = 0;
            this.in = in;
            this.lastChunk = false;
            this.closed = false;
        }
        
        public boolean isLastChunk() throws IOException {
            this.checkEOF();
            return this.lastChunk;
        }
        
        public int getRemain() throws IOException {
            this.checkEOF();
            return this.remain;
        }
        
        private void readLength() throws IOException {
            this.remain = Utils.readVInt(this.in);
            if (this.remain >= 0) {
                this.lastChunk = true;
            }
            else {
                this.remain = -this.remain;
            }
        }
        
        private boolean checkEOF() throws IOException {
            if (this.isClosed()) {
                return true;
            }
            while (this.remain <= 0) {
                if (this.lastChunk) {
                    return true;
                }
                this.readLength();
            }
            return false;
        }
        
        @Override
        public int available() {
            return this.remain;
        }
        
        @Override
        public int read() throws IOException {
            if (this.checkEOF()) {
                return -1;
            }
            final int ret = this.in.read();
            if (ret < 0) {
                throw new IOException("Corrupted chunk encoding stream");
            }
            --this.remain;
            return ret;
        }
        
        @Override
        public int read(final byte[] b) throws IOException {
            return this.read(b, 0, b.length);
        }
        
        @Override
        public int read(final byte[] b, final int off, final int len) throws IOException {
            if ((off | len | off + len | b.length - (off + len)) < 0) {
                throw new IndexOutOfBoundsException();
            }
            if (this.checkEOF()) {
                return -1;
            }
            final int n = Math.min(this.remain, len);
            final int ret = this.in.read(b, off, n);
            if (ret < 0) {
                throw new IOException("Corrupted chunk encoding stream");
            }
            this.remain -= ret;
            return ret;
        }
        
        @Override
        public long skip(final long n) throws IOException {
            if (!this.checkEOF()) {
                final long ret = this.in.skip(Math.min(this.remain, n));
                this.remain -= (int)ret;
                return ret;
            }
            return 0L;
        }
        
        @Override
        public boolean markSupported() {
            return false;
        }
        
        public boolean isClosed() {
            return this.closed;
        }
        
        @Override
        public void close() throws IOException {
            if (!this.closed) {
                try {
                    while (!this.checkEOF()) {
                        this.skip(2147483647L);
                    }
                }
                finally {
                    this.closed = true;
                }
            }
        }
    }
    
    public static class ChunkEncoder extends OutputStream
    {
        private DataOutputStream out;
        private byte[] buf;
        private int count;
        
        public ChunkEncoder(final DataOutputStream out, final byte[] buf) {
            this.out = out;
            this.buf = buf;
            this.count = 0;
        }
        
        private void writeChunk(final byte[] chunk, final int offset, final int len, final boolean last) throws IOException {
            if (last) {
                Utils.writeVInt(this.out, len);
                if (len > 0) {
                    this.out.write(chunk, offset, len);
                }
            }
            else if (len > 0) {
                Utils.writeVInt(this.out, -len);
                this.out.write(chunk, offset, len);
            }
        }
        
        private void writeBufData(final byte[] data, final int offset, final int len) throws IOException {
            if (this.count + len > 0) {
                Utils.writeVInt(this.out, -(this.count + len));
                this.out.write(this.buf, 0, this.count);
                this.count = 0;
                this.out.write(data, offset, len);
            }
        }
        
        private void flushBuffer() throws IOException {
            if (this.count > 0) {
                this.writeChunk(this.buf, 0, this.count, false);
                this.count = 0;
            }
        }
        
        @Override
        public void write(final int b) throws IOException {
            if (this.count >= this.buf.length) {
                this.flushBuffer();
            }
            this.buf[this.count++] = (byte)b;
        }
        
        @Override
        public void write(final byte[] b) throws IOException {
            this.write(b, 0, b.length);
        }
        
        @Override
        public void write(final byte[] b, final int off, final int len) throws IOException {
            if (len + this.count >= this.buf.length) {
                this.writeBufData(b, off, len);
                return;
            }
            System.arraycopy(b, off, this.buf, this.count, len);
            this.count += len;
        }
        
        @Override
        public void flush() throws IOException {
            this.flushBuffer();
            this.out.flush();
        }
        
        @Override
        public void close() throws IOException {
            if (this.buf != null) {
                try {
                    this.writeChunk(this.buf, 0, this.count, true);
                }
                finally {
                    this.buf = null;
                    this.out = null;
                }
            }
        }
    }
    
    public static class SingleChunkEncoder extends OutputStream
    {
        private final DataOutputStream out;
        private int remain;
        private boolean closed;
        
        public SingleChunkEncoder(final DataOutputStream out, final int size) throws IOException {
            this.closed = false;
            Utils.writeVInt(this.out = out, this.remain = size);
        }
        
        @Override
        public void write(final int b) throws IOException {
            if (this.remain > 0) {
                this.out.write(b);
                --this.remain;
                return;
            }
            throw new IOException("Writing more bytes than advertised size.");
        }
        
        @Override
        public void write(final byte[] b) throws IOException {
            this.write(b, 0, b.length);
        }
        
        @Override
        public void write(final byte[] b, final int off, final int len) throws IOException {
            if (this.remain >= len) {
                this.out.write(b, off, len);
                this.remain -= len;
                return;
            }
            throw new IOException("Writing more bytes than advertised size.");
        }
        
        @Override
        public void flush() throws IOException {
            this.out.flush();
        }
        
        @Override
        public void close() throws IOException {
            if (this.closed) {
                return;
            }
            try {
                if (this.remain > 0) {
                    throw new IOException("Writing less bytes than advertised size.");
                }
            }
            finally {
                this.closed = true;
            }
        }
    }
}
