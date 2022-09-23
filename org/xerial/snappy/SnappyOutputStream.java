// 
// Decompiled by Procyon v0.5.36
// 

package org.xerial.snappy;

import java.io.IOException;
import java.io.OutputStream;

public class SnappyOutputStream extends OutputStream
{
    static final int DEFAULT_BLOCK_SIZE = 32768;
    protected final OutputStream out;
    private final int blockSize;
    private int cursor;
    protected byte[] uncompressed;
    protected byte[] compressed;
    
    public SnappyOutputStream(final OutputStream out) throws IOException {
        this(out, 32768);
    }
    
    public SnappyOutputStream(final OutputStream out, final int blockSize) throws IOException {
        this.cursor = 0;
        this.out = out;
        this.blockSize = blockSize;
        this.uncompressed = new byte[blockSize];
        this.compressed = new byte[Snappy.maxCompressedLength(blockSize)];
        this.writeHeader();
    }
    
    protected void writeHeader() throws IOException {
        SnappyCodec.currentHeader().writeHeader(this.out);
    }
    
    @Override
    public void write(final byte[] b, final int off, final int len) throws IOException {
        this.rawWrite(b, off, len);
    }
    
    public void write(final long[] d, final int off, final int len) throws IOException {
        this.rawWrite(d, off * 8, len * 8);
    }
    
    public void write(final double[] f, final int off, final int len) throws IOException {
        this.rawWrite(f, off * 8, len * 8);
    }
    
    public void write(final float[] f, final int off, final int len) throws IOException {
        this.rawWrite(f, off * 4, len * 4);
    }
    
    public void write(final int[] f, final int off, final int len) throws IOException {
        this.rawWrite(f, off * 4, len * 4);
    }
    
    public void write(final short[] f, final int off, final int len) throws IOException {
        this.rawWrite(f, off * 2, len * 2);
    }
    
    public void write(final long[] d) throws IOException {
        this.write(d, 0, d.length);
    }
    
    public void write(final double[] f) throws IOException {
        this.write(f, 0, f.length);
    }
    
    public void write(final float[] f) throws IOException {
        this.write(f, 0, f.length);
    }
    
    public void write(final int[] f) throws IOException {
        this.write(f, 0, f.length);
    }
    
    public void write(final short[] f) throws IOException {
        this.write(f, 0, f.length);
    }
    
    public void rawWrite(final Object array, final int byteOffset, final int byteLength) throws IOException {
        int readBytes = 0;
        while (readBytes < byteLength) {
            final int copyLen = Math.min(this.uncompressed.length - this.cursor, byteLength - readBytes);
            Snappy.arrayCopy(array, byteOffset + readBytes, copyLen, this.uncompressed, this.cursor);
            readBytes += copyLen;
            this.cursor += copyLen;
            if (this.cursor >= this.uncompressed.length) {
                this.dump();
            }
        }
    }
    
    @Override
    public void write(final int b) throws IOException {
        if (this.cursor >= this.uncompressed.length) {
            this.dump();
        }
        this.uncompressed[this.cursor++] = (byte)b;
    }
    
    @Override
    public void flush() throws IOException {
        this.dump();
        this.out.flush();
    }
    
    static void writeInt(final OutputStream out, final int value) throws IOException {
        out.write(value >> 24 & 0xFF);
        out.write(value >> 16 & 0xFF);
        out.write(value >> 8 & 0xFF);
        out.write(value >> 0 & 0xFF);
    }
    
    static int readInt(final byte[] buffer, final int pos) {
        final int b1 = (buffer[pos] & 0xFF) << 24;
        final int b2 = (buffer[pos + 1] & 0xFF) << 16;
        final int b3 = (buffer[pos + 2] & 0xFF) << 8;
        final int b4 = buffer[pos + 3] & 0xFF;
        return b1 | b2 | b3 | b4;
    }
    
    protected void dump() throws IOException {
        if (this.cursor <= 0) {
            return;
        }
        final int compressedSize = Snappy.compress(this.uncompressed, 0, this.cursor, this.compressed, 0);
        writeInt(this.out, compressedSize);
        this.out.write(this.compressed, 0, compressedSize);
        this.cursor = 0;
    }
    
    @Override
    public void close() throws IOException {
        this.flush();
        super.close();
        this.out.close();
    }
}
