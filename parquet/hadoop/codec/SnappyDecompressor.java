// 
// Decompiled by Procyon v0.5.36
// 

package parquet.hadoop.codec;

import java.io.IOException;
import org.xerial.snappy.Snappy;
import parquet.Preconditions;
import java.nio.ByteBuffer;
import org.apache.hadoop.io.compress.Decompressor;

public class SnappyDecompressor implements Decompressor
{
    private ByteBuffer outputBuffer;
    private ByteBuffer inputBuffer;
    private boolean finished;
    
    public SnappyDecompressor() {
        this.outputBuffer = ByteBuffer.allocateDirect(0);
        this.inputBuffer = ByteBuffer.allocateDirect(0);
    }
    
    @Override
    public synchronized int decompress(final byte[] buffer, final int off, final int len) throws IOException {
        SnappyUtil.validateBuffer(buffer, off, len);
        if (this.inputBuffer.position() == 0 && !this.outputBuffer.hasRemaining()) {
            return 0;
        }
        if (!this.outputBuffer.hasRemaining()) {
            this.inputBuffer.rewind();
            Preconditions.checkArgument(this.inputBuffer.position() == 0, "Invalid position of 0.");
            Preconditions.checkArgument(this.outputBuffer.position() == 0, "Invalid position of 0.");
            final int decompressedSize = Snappy.uncompressedLength(this.inputBuffer);
            if (decompressedSize > this.outputBuffer.capacity()) {
                this.outputBuffer = ByteBuffer.allocateDirect(decompressedSize);
            }
            this.outputBuffer.clear();
            final int size = Snappy.uncompress(this.inputBuffer, this.outputBuffer);
            this.outputBuffer.limit(size);
            this.inputBuffer.clear();
            this.inputBuffer.limit(0);
            this.finished = true;
        }
        final int numBytes = Math.min(len, this.outputBuffer.remaining());
        this.outputBuffer.get(buffer, off, numBytes);
        return numBytes;
    }
    
    @Override
    public synchronized void setInput(final byte[] buffer, final int off, final int len) {
        SnappyUtil.validateBuffer(buffer, off, len);
        if (this.inputBuffer.capacity() - this.inputBuffer.position() < len) {
            final ByteBuffer newBuffer = ByteBuffer.allocateDirect(this.inputBuffer.position() + len);
            this.inputBuffer.rewind();
            newBuffer.put(this.inputBuffer);
            this.inputBuffer = newBuffer;
        }
        else {
            this.inputBuffer.limit(this.inputBuffer.position() + len);
        }
        this.inputBuffer.put(buffer, off, len);
    }
    
    @Override
    public void end() {
    }
    
    @Override
    public synchronized boolean finished() {
        return this.finished && !this.outputBuffer.hasRemaining();
    }
    
    @Override
    public int getRemaining() {
        return 0;
    }
    
    @Override
    public synchronized boolean needsInput() {
        return !this.inputBuffer.hasRemaining() && !this.outputBuffer.hasRemaining();
    }
    
    @Override
    public synchronized void reset() {
        this.finished = false;
        this.inputBuffer.rewind();
        this.outputBuffer.rewind();
        this.inputBuffer.limit(0);
        this.outputBuffer.limit(0);
    }
    
    @Override
    public boolean needsDictionary() {
        return false;
    }
    
    @Override
    public void setDictionary(final byte[] b, final int off, final int len) {
    }
}
