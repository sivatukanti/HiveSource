// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.io.compress;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Public
@InterfaceStability.Evolving
public class BlockDecompressorStream extends DecompressorStream
{
    private int originalBlockSize;
    private int noUncompressedBytes;
    
    public BlockDecompressorStream(final InputStream in, final Decompressor decompressor, final int bufferSize) throws IOException {
        super(in, decompressor, bufferSize);
        this.originalBlockSize = 0;
        this.noUncompressedBytes = 0;
    }
    
    public BlockDecompressorStream(final InputStream in, final Decompressor decompressor) throws IOException {
        super(in, decompressor);
        this.originalBlockSize = 0;
        this.noUncompressedBytes = 0;
    }
    
    protected BlockDecompressorStream(final InputStream in) throws IOException {
        super(in);
        this.originalBlockSize = 0;
        this.noUncompressedBytes = 0;
    }
    
    @Override
    protected int decompress(final byte[] b, final int off, final int len) throws IOException {
        if (this.noUncompressedBytes == this.originalBlockSize) {
            try {
                this.originalBlockSize = this.rawReadInt();
            }
            catch (IOException ioe) {
                return -1;
            }
            this.noUncompressedBytes = 0;
            if (this.originalBlockSize == 0) {
                this.eof = true;
                return -1;
            }
        }
        int n = 0;
        while ((n = this.decompressor.decompress(b, off, len)) == 0) {
            if ((this.decompressor.finished() || this.decompressor.needsDictionary()) && this.noUncompressedBytes >= this.originalBlockSize) {
                this.eof = true;
                return -1;
            }
            if (!this.decompressor.needsInput()) {
                continue;
            }
            int m;
            try {
                m = this.getCompressedData();
            }
            catch (EOFException e) {
                this.eof = true;
                return -1;
            }
            this.decompressor.setInput(this.buffer, 0, m);
        }
        this.noUncompressedBytes += n;
        return n;
    }
    
    @Override
    protected int getCompressedData() throws IOException {
        this.checkStream();
        final int len = this.rawReadInt();
        if (len > this.buffer.length) {
            this.buffer = new byte[len];
        }
        int n = 0;
        final int off = 0;
        while (n < len) {
            final int count = this.in.read(this.buffer, off + n, len - n);
            if (count < 0) {
                throw new EOFException("Unexpected end of block in input stream");
            }
            n += count;
        }
        return len;
    }
    
    @Override
    public void resetState() throws IOException {
        this.originalBlockSize = 0;
        this.noUncompressedBytes = 0;
        super.resetState();
    }
    
    private int rawReadInt() throws IOException {
        final int b1 = this.in.read();
        final int b2 = this.in.read();
        final int b3 = this.in.read();
        final int b4 = this.in.read();
        if ((b1 | b2 | b3 | b4) < 0) {
            throw new EOFException();
        }
        return (b1 << 24) + (b2 << 16) + (b3 << 8) + (b4 << 0);
    }
}
