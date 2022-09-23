// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.avro.io;

import java.nio.channels.Channels;
import java.nio.channels.WritableByteChannel;
import java.nio.ByteBuffer;
import java.io.IOException;
import org.apache.avro.AvroRuntimeException;
import java.io.OutputStream;

public class BufferedBinaryEncoder extends BinaryEncoder
{
    private byte[] buf;
    private int pos;
    private ByteSink sink;
    private int bulkLimit;
    
    BufferedBinaryEncoder(final OutputStream out, final int bufferSize) {
        this.configure(out, bufferSize);
    }
    
    BufferedBinaryEncoder configure(final OutputStream out, final int bufferSize) {
        if (null == out) {
            throw new NullPointerException("OutputStream cannot be null!");
        }
        if (null != this.sink && this.pos > 0) {
            try {
                this.flushBuffer();
            }
            catch (IOException e) {
                throw new AvroRuntimeException("Failure flushing old output", e);
            }
        }
        this.sink = new OutputStreamSink(out);
        this.pos = 0;
        if (null == this.buf || this.buf.length != bufferSize) {
            this.buf = new byte[bufferSize];
        }
        this.bulkLimit = this.buf.length >>> 1;
        if (this.bulkLimit > 512) {
            this.bulkLimit = 512;
        }
        return this;
    }
    
    @Override
    public void flush() throws IOException {
        this.flushBuffer();
        this.sink.innerFlush();
    }
    
    private void flushBuffer() throws IOException {
        if (this.pos > 0) {
            this.sink.innerWrite(this.buf, 0, this.pos);
            this.pos = 0;
        }
    }
    
    private void ensureBounds(final int num) throws IOException {
        final int remaining = this.buf.length - this.pos;
        if (remaining < num) {
            this.flushBuffer();
        }
    }
    
    @Override
    public void writeBoolean(final boolean b) throws IOException {
        if (this.buf.length == this.pos) {
            this.flushBuffer();
        }
        this.pos += BinaryData.encodeBoolean(b, this.buf, this.pos);
    }
    
    @Override
    public void writeInt(final int n) throws IOException {
        this.ensureBounds(5);
        this.pos += BinaryData.encodeInt(n, this.buf, this.pos);
    }
    
    @Override
    public void writeLong(final long n) throws IOException {
        this.ensureBounds(10);
        this.pos += BinaryData.encodeLong(n, this.buf, this.pos);
    }
    
    @Override
    public void writeFloat(final float f) throws IOException {
        this.ensureBounds(4);
        this.pos += BinaryData.encodeFloat(f, this.buf, this.pos);
    }
    
    @Override
    public void writeDouble(final double d) throws IOException {
        this.ensureBounds(8);
        this.pos += BinaryData.encodeDouble(d, this.buf, this.pos);
    }
    
    @Override
    public void writeFixed(final byte[] bytes, final int start, final int len) throws IOException {
        if (len > this.bulkLimit) {
            this.flushBuffer();
            this.sink.innerWrite(bytes, start, len);
            return;
        }
        this.ensureBounds(len);
        System.arraycopy(bytes, start, this.buf, this.pos, len);
        this.pos += len;
    }
    
    @Override
    public void writeFixed(final ByteBuffer bytes) throws IOException {
        if (!bytes.hasArray() && bytes.remaining() > this.bulkLimit) {
            this.flushBuffer();
            this.sink.innerWrite(bytes);
        }
        else {
            super.writeFixed(bytes);
        }
    }
    
    @Override
    protected void writeZero() throws IOException {
        this.writeByte(0);
    }
    
    private void writeByte(final int b) throws IOException {
        if (this.pos == this.buf.length) {
            this.flushBuffer();
        }
        this.buf[this.pos++] = (byte)(b & 0xFF);
    }
    
    @Override
    public int bytesBuffered() {
        return this.pos;
    }
    
    private abstract static class ByteSink
    {
        protected ByteSink() {
        }
        
        protected abstract void innerWrite(final byte[] p0, final int p1, final int p2) throws IOException;
        
        protected abstract void innerWrite(final ByteBuffer p0) throws IOException;
        
        protected abstract void innerFlush() throws IOException;
    }
    
    static class OutputStreamSink extends ByteSink
    {
        private final OutputStream out;
        private final WritableByteChannel channel;
        
        private OutputStreamSink(final OutputStream out) {
            this.out = out;
            this.channel = Channels.newChannel(out);
        }
        
        @Override
        protected void innerWrite(final byte[] bytes, final int off, final int len) throws IOException {
            this.out.write(bytes, off, len);
        }
        
        @Override
        protected void innerFlush() throws IOException {
            this.out.flush();
        }
        
        @Override
        protected void innerWrite(final ByteBuffer buff) throws IOException {
            this.channel.write(buff);
        }
    }
}
