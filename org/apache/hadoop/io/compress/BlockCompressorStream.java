// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.io.compress;

import java.io.IOException;
import java.io.OutputStream;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Public
@InterfaceStability.Evolving
public class BlockCompressorStream extends CompressorStream
{
    private final int MAX_INPUT_SIZE;
    
    public BlockCompressorStream(final OutputStream out, final Compressor compressor, final int bufferSize, final int compressionOverhead) {
        super(out, compressor, bufferSize);
        this.MAX_INPUT_SIZE = bufferSize - compressionOverhead;
    }
    
    public BlockCompressorStream(final OutputStream out, final Compressor compressor) {
        this(out, compressor, 512, 18);
    }
    
    @Override
    public void write(final byte[] b, int off, int len) throws IOException {
        if (this.compressor.finished()) {
            throw new IOException("write beyond end of stream");
        }
        if (b == null) {
            throw new NullPointerException();
        }
        if (off < 0 || off > b.length || len < 0 || off + len > b.length) {
            throw new IndexOutOfBoundsException();
        }
        if (len == 0) {
            return;
        }
        final long limlen = this.compressor.getBytesRead();
        if (len + limlen > this.MAX_INPUT_SIZE && limlen > 0L) {
            this.finish();
            this.compressor.reset();
        }
        if (len > this.MAX_INPUT_SIZE) {
            this.rawWriteInt(len);
            do {
                final int bufLen = Math.min(len, this.MAX_INPUT_SIZE);
                this.compressor.setInput(b, off, bufLen);
                this.compressor.finish();
                while (!this.compressor.finished()) {
                    this.compress();
                }
                this.compressor.reset();
                off += bufLen;
                len -= bufLen;
            } while (len > 0);
            return;
        }
        this.compressor.setInput(b, off, len);
        if (!this.compressor.needsInput()) {
            this.rawWriteInt((int)this.compressor.getBytesRead());
            do {
                this.compress();
            } while (!this.compressor.needsInput());
        }
    }
    
    @Override
    public void finish() throws IOException {
        if (!this.compressor.finished()) {
            this.rawWriteInt((int)this.compressor.getBytesRead());
            this.compressor.finish();
            while (!this.compressor.finished()) {
                this.compress();
            }
        }
    }
    
    @Override
    protected void compress() throws IOException {
        final int len = this.compressor.compress(this.buffer, 0, this.buffer.length);
        if (len > 0) {
            this.rawWriteInt(len);
            this.out.write(this.buffer, 0, len);
        }
    }
    
    private void rawWriteInt(final int v) throws IOException {
        this.out.write(v >>> 24 & 0xFF);
        this.out.write(v >>> 16 & 0xFF);
        this.out.write(v >>> 8 & 0xFF);
        this.out.write(v >>> 0 & 0xFF);
    }
}
