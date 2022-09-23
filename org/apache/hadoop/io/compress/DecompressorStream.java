// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.io.compress;

import java.io.EOFException;
import java.io.IOException;
import com.google.common.annotations.VisibleForTesting;
import java.io.InputStream;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Public
@InterfaceStability.Evolving
public class DecompressorStream extends CompressionInputStream
{
    private static final int MAX_INPUT_BUFFER_SIZE = 512;
    private static final int MAX_SKIP_BUFFER_SIZE = 2048;
    private byte[] skipBytes;
    private byte[] oneByte;
    protected Decompressor decompressor;
    protected byte[] buffer;
    protected boolean eof;
    protected boolean closed;
    private int lastBytesSent;
    
    @VisibleForTesting
    DecompressorStream(final InputStream in, final Decompressor decompressor, final int bufferSize, final int skipBufferSize) throws IOException {
        super(in);
        this.oneByte = new byte[1];
        this.decompressor = null;
        this.eof = false;
        this.closed = false;
        this.lastBytesSent = 0;
        if (decompressor == null) {
            throw new NullPointerException();
        }
        if (bufferSize <= 0) {
            throw new IllegalArgumentException("Illegal bufferSize");
        }
        this.decompressor = decompressor;
        this.buffer = new byte[bufferSize];
        this.skipBytes = new byte[skipBufferSize];
    }
    
    public DecompressorStream(final InputStream in, final Decompressor decompressor, final int bufferSize) throws IOException {
        this(in, decompressor, bufferSize, 2048);
    }
    
    public DecompressorStream(final InputStream in, final Decompressor decompressor) throws IOException {
        this(in, decompressor, 512);
    }
    
    protected DecompressorStream(final InputStream in) throws IOException {
        super(in);
        this.oneByte = new byte[1];
        this.decompressor = null;
        this.eof = false;
        this.closed = false;
        this.lastBytesSent = 0;
    }
    
    @Override
    public int read() throws IOException {
        this.checkStream();
        return (this.read(this.oneByte, 0, this.oneByte.length) == -1) ? -1 : (this.oneByte[0] & 0xFF);
    }
    
    @Override
    public int read(final byte[] b, final int off, final int len) throws IOException {
        this.checkStream();
        if ((off | len | off + len | b.length - (off + len)) < 0) {
            throw new IndexOutOfBoundsException();
        }
        if (len == 0) {
            return 0;
        }
        return this.decompress(b, off, len);
    }
    
    protected int decompress(final byte[] b, final int off, final int len) throws IOException {
        int n;
        while ((n = this.decompressor.decompress(b, off, len)) == 0) {
            if (this.decompressor.needsDictionary()) {
                this.eof = true;
                return -1;
            }
            if (this.decompressor.finished()) {
                final int nRemaining = this.decompressor.getRemaining();
                if (nRemaining == 0) {
                    final int m = this.getCompressedData();
                    if (m == -1) {
                        this.eof = true;
                        return -1;
                    }
                    this.decompressor.reset();
                    this.decompressor.setInput(this.buffer, 0, m);
                    this.lastBytesSent = m;
                }
                else {
                    this.decompressor.reset();
                    final int leftoverOffset = this.lastBytesSent - nRemaining;
                    assert leftoverOffset >= 0;
                    this.decompressor.setInput(this.buffer, leftoverOffset, nRemaining);
                }
            }
            else {
                if (!this.decompressor.needsInput()) {
                    continue;
                }
                final int i = this.getCompressedData();
                if (i == -1) {
                    throw new EOFException("Unexpected end of input stream");
                }
                this.decompressor.setInput(this.buffer, 0, i);
                this.lastBytesSent = i;
            }
        }
        return n;
    }
    
    protected int getCompressedData() throws IOException {
        this.checkStream();
        return this.in.read(this.buffer, 0, this.buffer.length);
    }
    
    protected void checkStream() throws IOException {
        if (this.closed) {
            throw new IOException("Stream closed");
        }
    }
    
    @Override
    public void resetState() throws IOException {
        this.decompressor.reset();
    }
    
    @Override
    public long skip(final long n) throws IOException {
        if (n < 0L) {
            throw new IllegalArgumentException("negative skip length");
        }
        this.checkStream();
        int skipped;
        int len;
        for (skipped = 0; skipped < n; skipped += len) {
            len = Math.min((int)n - skipped, this.skipBytes.length);
            len = this.read(this.skipBytes, 0, len);
            if (len == -1) {
                this.eof = true;
                break;
            }
        }
        return skipped;
    }
    
    @Override
    public int available() throws IOException {
        this.checkStream();
        return this.eof ? 0 : 1;
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
    public boolean markSupported() {
        return false;
    }
    
    @Override
    public synchronized void mark(final int readlimit) {
    }
    
    @Override
    public synchronized void reset() throws IOException {
        throw new IOException("mark/reset not supported");
    }
}
