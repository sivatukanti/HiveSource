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
public class CompressorStream extends CompressionOutputStream
{
    protected Compressor compressor;
    protected byte[] buffer;
    protected boolean closed;
    private byte[] oneByte;
    
    public CompressorStream(final OutputStream out, final Compressor compressor, final int bufferSize) {
        super(out);
        this.closed = false;
        this.oneByte = new byte[1];
        if (out == null || compressor == null) {
            throw new NullPointerException();
        }
        if (bufferSize <= 0) {
            throw new IllegalArgumentException("Illegal bufferSize");
        }
        this.compressor = compressor;
        this.buffer = new byte[bufferSize];
    }
    
    public CompressorStream(final OutputStream out, final Compressor compressor) {
        this(out, compressor, 512);
    }
    
    protected CompressorStream(final OutputStream out) {
        super(out);
        this.closed = false;
        this.oneByte = new byte[1];
    }
    
    @Override
    public void write(final byte[] b, final int off, final int len) throws IOException {
        if (this.compressor.finished()) {
            throw new IOException("write beyond end of stream");
        }
        if ((off | len | off + len | b.length - (off + len)) < 0) {
            throw new IndexOutOfBoundsException();
        }
        if (len == 0) {
            return;
        }
        this.compressor.setInput(b, off, len);
        while (!this.compressor.needsInput()) {
            this.compress();
        }
    }
    
    protected void compress() throws IOException {
        final int len = this.compressor.compress(this.buffer, 0, this.buffer.length);
        if (len > 0) {
            this.out.write(this.buffer, 0, len);
        }
    }
    
    @Override
    public void finish() throws IOException {
        if (!this.compressor.finished()) {
            this.compressor.finish();
            while (!this.compressor.finished()) {
                this.compress();
            }
        }
    }
    
    @Override
    public void resetState() throws IOException {
        this.compressor.reset();
    }
    
    @Override
    public void close() throws IOException {
        if (!this.closed) {
            try {
                super.close();
            }
            finally {
                this.closed = true;
            }
        }
    }
    
    @Override
    public void write(final int b) throws IOException {
        this.oneByte[0] = (byte)(b & 0xFF);
        this.write(this.oneByte, 0, this.oneByte.length);
    }
}
