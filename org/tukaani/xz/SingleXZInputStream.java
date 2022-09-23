// 
// Decompiled by Procyon v0.5.36
// 

package org.tukaani.xz;

import org.tukaani.xz.common.DecoderUtil;
import java.io.DataInputStream;
import java.io.IOException;
import org.tukaani.xz.index.IndexHash;
import org.tukaani.xz.check.Check;
import org.tukaani.xz.common.StreamFlags;
import java.io.InputStream;

public class SingleXZInputStream extends InputStream
{
    private InputStream in;
    private int memoryLimit;
    private StreamFlags streamHeaderFlags;
    private Check check;
    private BlockInputStream blockDecoder;
    private IndexHash indexHash;
    private boolean endReached;
    private IOException exception;
    
    public SingleXZInputStream(final InputStream inputStream) throws IOException {
        this.blockDecoder = null;
        this.indexHash = new IndexHash();
        this.endReached = false;
        this.exception = null;
        this.initialize(inputStream, -1);
    }
    
    public SingleXZInputStream(final InputStream inputStream, final int n) throws IOException {
        this.blockDecoder = null;
        this.indexHash = new IndexHash();
        this.endReached = false;
        this.exception = null;
        this.initialize(inputStream, n);
    }
    
    SingleXZInputStream(final InputStream inputStream, final int n, final byte[] array) throws IOException {
        this.blockDecoder = null;
        this.indexHash = new IndexHash();
        this.endReached = false;
        this.exception = null;
        this.initialize(inputStream, n, array);
    }
    
    private void initialize(final InputStream in, final int n) throws IOException {
        final byte[] b = new byte[12];
        new DataInputStream(in).readFully(b);
        this.initialize(in, n, b);
    }
    
    private void initialize(final InputStream in, final int memoryLimit, final byte[] array) throws IOException {
        this.in = in;
        this.memoryLimit = memoryLimit;
        this.streamHeaderFlags = DecoderUtil.decodeStreamHeader(array);
        this.check = Check.getInstance(this.streamHeaderFlags.checkType);
    }
    
    public int getCheckType() {
        return this.streamHeaderFlags.checkType;
    }
    
    public String getCheckName() {
        return this.check.getName();
    }
    
    public int read() throws IOException {
        final byte[] array = { 0 };
        return (this.read(array, 0, 1) == -1) ? -1 : (array[0] & 0xFF);
    }
    
    public int read(final byte[] array, int n, int i) throws IOException {
        if (n < 0 || i < 0 || n + i < 0 || n + i > array.length) {
            throw new IndexOutOfBoundsException();
        }
        if (i == 0) {
            return 0;
        }
        if (this.in == null) {
            throw new XZIOException("Stream closed");
        }
        if (this.exception != null) {
            throw this.exception;
        }
        if (this.endReached) {
            return -1;
        }
        int n2 = 0;
        try {
            while (i > 0) {
                if (this.blockDecoder == null) {
                    try {
                        this.blockDecoder = new BlockInputStream(this.in, this.check, this.memoryLimit, -1L, -1L);
                    }
                    catch (IndexIndicatorException ex) {
                        this.indexHash.validate(this.in);
                        this.validateStreamFooter();
                        this.endReached = true;
                        return (n2 > 0) ? n2 : -1;
                    }
                }
                final int read = this.blockDecoder.read(array, n, i);
                if (read > 0) {
                    n2 += read;
                    n += read;
                    i -= read;
                }
                else {
                    if (read != -1) {
                        continue;
                    }
                    this.indexHash.add(this.blockDecoder.getUnpaddedSize(), this.blockDecoder.getUncompressedSize());
                    this.blockDecoder = null;
                }
            }
        }
        catch (IOException exception) {
            this.exception = exception;
            if (n2 == 0) {
                throw exception;
            }
        }
        return n2;
    }
    
    private void validateStreamFooter() throws IOException {
        final byte[] b = new byte[12];
        new DataInputStream(this.in).readFully(b);
        final StreamFlags decodeStreamFooter = DecoderUtil.decodeStreamFooter(b);
        if (!DecoderUtil.areStreamFlagsEqual(this.streamHeaderFlags, decodeStreamFooter) || this.indexHash.getIndexSize() != decodeStreamFooter.backwardSize) {
            throw new CorruptedInputException("XZ Stream Footer does not match Stream Header");
        }
    }
    
    public int available() throws IOException {
        if (this.in == null) {
            throw new XZIOException("Stream closed");
        }
        if (this.exception != null) {
            throw this.exception;
        }
        return (this.blockDecoder == null) ? 0 : this.blockDecoder.available();
    }
    
    public void close() throws IOException {
        if (this.in != null) {
            try {
                this.in.close();
            }
            finally {
                this.in = null;
            }
        }
    }
}
