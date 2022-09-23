// 
// Decompiled by Procyon v0.5.36
// 

package parquet.hadoop.codec;

import org.apache.hadoop.conf.Configuration;
import parquet.Preconditions;
import java.io.IOException;
import org.xerial.snappy.Snappy;
import java.nio.ByteBuffer;
import org.apache.hadoop.io.compress.Compressor;

public class SnappyCompressor implements Compressor
{
    private ByteBuffer outputBuffer;
    private ByteBuffer inputBuffer;
    private long bytesRead;
    private long bytesWritten;
    private boolean finishCalled;
    
    public SnappyCompressor() {
        this.outputBuffer = ByteBuffer.allocateDirect(0);
        this.inputBuffer = ByteBuffer.allocateDirect(0);
        this.bytesRead = 0L;
        this.bytesWritten = 0L;
        this.finishCalled = false;
    }
    
    @Override
    public synchronized int compress(final byte[] buffer, final int off, final int len) throws IOException {
        SnappyUtil.validateBuffer(buffer, off, len);
        if (this.needsInput()) {
            return 0;
        }
        if (!this.outputBuffer.hasRemaining()) {
            final int maxOutputSize = Snappy.maxCompressedLength(this.inputBuffer.position());
            if (maxOutputSize > this.outputBuffer.capacity()) {
                this.outputBuffer = ByteBuffer.allocateDirect(maxOutputSize);
            }
            this.outputBuffer.clear();
            this.inputBuffer.limit(this.inputBuffer.position());
            this.inputBuffer.position(0);
            final int size = Snappy.compress(this.inputBuffer, this.outputBuffer);
            this.outputBuffer.limit(size);
            this.inputBuffer.limit(0);
            this.inputBuffer.rewind();
        }
        final int numBytes = Math.min(len, this.outputBuffer.remaining());
        this.outputBuffer.get(buffer, off, numBytes);
        this.bytesWritten += numBytes;
        return numBytes;
    }
    
    @Override
    public synchronized void setInput(final byte[] buffer, final int off, final int len) {
        SnappyUtil.validateBuffer(buffer, off, len);
        Preconditions.checkArgument(!this.outputBuffer.hasRemaining(), "Output buffer should be empty. Caller must call compress()");
        if (this.inputBuffer.capacity() - this.inputBuffer.position() < len) {
            final ByteBuffer tmp = ByteBuffer.allocateDirect(this.inputBuffer.position() + len);
            this.inputBuffer.rewind();
            tmp.put(this.inputBuffer);
            this.inputBuffer = tmp;
        }
        else {
            this.inputBuffer.limit(this.inputBuffer.position() + len);
        }
        this.inputBuffer.put(buffer, off, len);
        this.bytesRead += len;
    }
    
    @Override
    public void end() {
    }
    
    @Override
    public void finish() {
        this.finishCalled = true;
    }
    
    @Override
    public synchronized boolean finished() {
        return this.finishCalled && this.inputBuffer.position() == 0 && !this.outputBuffer.hasRemaining();
    }
    
    @Override
    public long getBytesRead() {
        return this.bytesRead;
    }
    
    @Override
    public long getBytesWritten() {
        return this.bytesWritten;
    }
    
    @Override
    public synchronized boolean needsInput() {
        return !this.finishCalled;
    }
    
    @Override
    public void reinit(final Configuration c) {
        this.reset();
    }
    
    @Override
    public synchronized void reset() {
        this.finishCalled = false;
        final long n = 0L;
        this.bytesWritten = n;
        this.bytesRead = n;
        this.inputBuffer.rewind();
        this.outputBuffer.rewind();
        this.inputBuffer.limit(0);
        this.outputBuffer.limit(0);
    }
    
    @Override
    public void setDictionary(final byte[] dictionary, final int off, final int len) {
    }
}
