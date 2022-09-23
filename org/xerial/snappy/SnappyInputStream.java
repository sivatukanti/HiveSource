// 
// Decompiled by Procyon v0.5.36
// 

package org.xerial.snappy;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

public class SnappyInputStream extends InputStream
{
    private boolean finishedReading;
    protected final InputStream in;
    private byte[] compressed;
    private byte[] uncompressed;
    private int uncompressedCursor;
    private int uncompressedLimit;
    private byte[] chunkSizeBuf;
    
    public SnappyInputStream(final InputStream input) throws IOException {
        this.finishedReading = false;
        this.uncompressedCursor = 0;
        this.uncompressedLimit = 0;
        this.chunkSizeBuf = new byte[4];
        this.in = input;
        this.readHeader();
    }
    
    @Override
    public void close() throws IOException {
        this.compressed = null;
        this.uncompressed = null;
        if (this.in != null) {
            this.in.close();
        }
    }
    
    protected void readHeader() throws IOException {
        byte[] header;
        int readBytes;
        int ret;
        for (header = new byte[SnappyCodec.headerSize()], readBytes = 0; readBytes < header.length; readBytes += ret) {
            ret = this.in.read(header, readBytes, header.length - readBytes);
            if (ret == -1) {
                break;
            }
        }
        if (readBytes < header.length || header[0] != SnappyCodec.MAGIC_HEADER[0]) {
            this.readFully(header, readBytes);
            return;
        }
        final SnappyCodec codec = SnappyCodec.readHeader(new ByteArrayInputStream(header));
        if (!codec.isValidMagicHeader()) {
            this.readFully(header, readBytes);
            return;
        }
        if (codec.version < 1) {
            throw new IOException(String.format("compressed with imcompatible codec version %d. At least version %d is required", codec.version, 1));
        }
    }
    
    protected void readFully(final byte[] fragment, final int fragmentLength) throws IOException {
        System.arraycopy(fragment, 0, this.compressed = new byte[Math.max(8192, fragmentLength)], 0, fragmentLength);
        int cursor = fragmentLength;
        int readBytes = 0;
        while ((readBytes = this.in.read(this.compressed, cursor, this.compressed.length - cursor)) != -1) {
            cursor += readBytes;
            if (cursor >= this.compressed.length) {
                final byte[] newBuf = new byte[this.compressed.length * 2];
                System.arraycopy(this.compressed, 0, newBuf, 0, this.compressed.length);
                this.compressed = newBuf;
            }
        }
        this.finishedReading = true;
        final int uncompressedLength = Snappy.uncompressedLength(this.compressed, 0, cursor);
        this.uncompressed = new byte[uncompressedLength];
        Snappy.uncompress(this.compressed, 0, cursor, this.uncompressed, 0);
        this.uncompressedCursor = 0;
        this.uncompressedLimit = uncompressedLength;
    }
    
    @Override
    public int read(final byte[] b, final int off, final int len) throws IOException {
        return this.rawRead(b, off, len);
    }
    
    public int rawRead(final Object array, final int byteOffset, final int byteLength) throws IOException {
        int writtenBytes = 0;
        while (writtenBytes < byteLength) {
            if (this.uncompressedCursor >= this.uncompressedLimit) {
                if (this.hasNextChunk()) {
                    continue;
                }
                return (writtenBytes == 0) ? -1 : writtenBytes;
            }
            else {
                final int bytesToWrite = Math.min(this.uncompressedLimit - this.uncompressedCursor, byteLength - writtenBytes);
                Snappy.arrayCopy(this.uncompressed, this.uncompressedCursor, bytesToWrite, array, byteOffset + writtenBytes);
                writtenBytes += bytesToWrite;
                this.uncompressedCursor += bytesToWrite;
            }
        }
        return writtenBytes;
    }
    
    public int read(final long[] d, final int off, final int len) throws IOException {
        return this.rawRead(d, off * 8, len * 8);
    }
    
    public int read(final long[] d) throws IOException {
        return this.read(d, 0, d.length);
    }
    
    public int read(final double[] d, final int off, final int len) throws IOException {
        return this.rawRead(d, off * 8, len * 8);
    }
    
    public int read(final double[] d) throws IOException {
        return this.read(d, 0, d.length);
    }
    
    public int read(final int[] d) throws IOException {
        return this.read(d, 0, d.length);
    }
    
    public int read(final int[] d, final int off, final int len) throws IOException {
        return this.rawRead(d, off * 4, len * 4);
    }
    
    public int read(final float[] d, final int off, final int len) throws IOException {
        return this.rawRead(d, off * 4, len * 4);
    }
    
    public int read(final float[] d) throws IOException {
        return this.read(d, 0, d.length);
    }
    
    public int read(final short[] d, final int off, final int len) throws IOException {
        return this.rawRead(d, off * 2, len * 2);
    }
    
    public int read(final short[] d) throws IOException {
        return this.read(d, 0, d.length);
    }
    
    protected boolean hasNextChunk() throws IOException {
        if (this.finishedReading) {
            return false;
        }
        this.uncompressedCursor = 0;
        this.uncompressedLimit = 0;
        int ret;
        for (int readBytes = 0; readBytes < 4; readBytes += ret) {
            ret = this.in.read(this.chunkSizeBuf, readBytes, 4 - readBytes);
            if (ret == -1) {
                this.finishedReading = true;
                return false;
            }
        }
        final int chunkSize = SnappyOutputStream.readInt(this.chunkSizeBuf, 0);
        if (this.compressed == null || chunkSize > this.compressed.length) {
            this.compressed = new byte[chunkSize];
        }
        int readBytes;
        int ret2;
        for (readBytes = 0; readBytes < chunkSize; readBytes += ret2) {
            ret2 = this.in.read(this.compressed, readBytes, chunkSize - readBytes);
            if (ret2 == -1) {
                break;
            }
        }
        if (readBytes < chunkSize) {
            throw new IOException("failed to read chunk");
        }
        try {
            final int uncompressedLength = Snappy.uncompressedLength(this.compressed, 0, chunkSize);
            if (this.uncompressed == null || uncompressedLength > this.uncompressed.length) {
                this.uncompressed = new byte[uncompressedLength];
            }
            final int actualUncompressedLength = Snappy.uncompress(this.compressed, 0, chunkSize, this.uncompressed, 0);
            if (uncompressedLength != actualUncompressedLength) {
                throw new IOException("invalid uncompressed byte size");
            }
            this.uncompressedLimit = actualUncompressedLength;
        }
        catch (IOException e) {
            throw new IOException("failed to uncompress the chunk: " + e.getMessage());
        }
        return true;
    }
    
    @Override
    public int read() throws IOException {
        if (this.uncompressedCursor < this.uncompressedLimit) {
            return this.uncompressed[this.uncompressedCursor++] & 0xFF;
        }
        if (this.hasNextChunk()) {
            return this.read();
        }
        return -1;
    }
    
    @Override
    public int available() throws IOException {
        if (this.uncompressedCursor < this.uncompressedLimit) {
            return this.uncompressedLimit - this.uncompressedCursor;
        }
        if (this.hasNextChunk()) {
            return this.uncompressedLimit - this.uncompressedCursor;
        }
        return 0;
    }
}
