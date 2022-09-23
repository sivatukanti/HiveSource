// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.fs;

import java.util.zip.Checksum;
import org.apache.htrace.core.TraceScope;
import java.io.IOException;
import org.apache.hadoop.util.DataChecksum;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;
import java.io.OutputStream;

@InterfaceAudience.LimitedPrivate({ "HDFS" })
@InterfaceStability.Unstable
public abstract class FSOutputSummer extends OutputStream
{
    private final DataChecksum sum;
    private byte[] buf;
    private byte[] checksum;
    private int count;
    private static final int BUFFER_NUM_CHUNKS = 9;
    
    protected FSOutputSummer(final DataChecksum sum) {
        this.sum = sum;
        this.buf = new byte[sum.getBytesPerChecksum() * 9];
        this.checksum = new byte[this.getChecksumSize() * 9];
        this.count = 0;
    }
    
    protected abstract void writeChunk(final byte[] p0, final int p1, final int p2, final byte[] p3, final int p4, final int p5) throws IOException;
    
    protected abstract void checkClosed() throws IOException;
    
    @Override
    public synchronized void write(final int b) throws IOException {
        this.buf[this.count++] = (byte)b;
        if (this.count == this.buf.length) {
            this.flushBuffer();
        }
    }
    
    @Override
    public synchronized void write(final byte[] b, final int off, final int len) throws IOException {
        this.checkClosed();
        if (off < 0 || len < 0 || off > b.length - len) {
            throw new ArrayIndexOutOfBoundsException();
        }
        for (int n = 0; n < len; n += this.write1(b, off + n, len - n)) {}
    }
    
    private int write1(final byte[] b, final int off, final int len) throws IOException {
        if (this.count == 0 && len >= this.buf.length) {
            final int length = this.buf.length;
            this.writeChecksumChunks(b, off, length);
            return length;
        }
        int bytesToCopy = this.buf.length - this.count;
        bytesToCopy = ((len < bytesToCopy) ? len : bytesToCopy);
        System.arraycopy(b, off, this.buf, this.count, bytesToCopy);
        this.count += bytesToCopy;
        if (this.count == this.buf.length) {
            this.flushBuffer();
        }
        return bytesToCopy;
    }
    
    protected synchronized void flushBuffer() throws IOException {
        this.flushBuffer(false, true);
    }
    
    protected synchronized int flushBuffer(final boolean keep, final boolean flushPartial) throws IOException {
        final int bufLen = this.count;
        final int partialLen = bufLen % this.sum.getBytesPerChecksum();
        final int lenToFlush = flushPartial ? bufLen : (bufLen - partialLen);
        if (lenToFlush != 0) {
            this.writeChecksumChunks(this.buf, 0, lenToFlush);
            if (!flushPartial || keep) {
                this.count = partialLen;
                System.arraycopy(this.buf, bufLen - this.count, this.buf, 0, this.count);
            }
            else {
                this.count = 0;
            }
        }
        return this.count - (bufLen - lenToFlush);
    }
    
    @Override
    public void flush() throws IOException {
        this.flushBuffer(false, false);
    }
    
    protected synchronized int getBufferedDataSize() {
        return this.count;
    }
    
    protected int getChecksumSize() {
        return this.sum.getChecksumSize();
    }
    
    protected DataChecksum getDataChecksum() {
        return this.sum;
    }
    
    protected TraceScope createWriteTraceScope() {
        return null;
    }
    
    private void writeChecksumChunks(final byte[] b, final int off, final int len) throws IOException {
        this.sum.calculateChunkedSums(b, off, len, this.checksum, 0);
        final TraceScope scope = this.createWriteTraceScope();
        try {
            for (int i = 0; i < len; i += this.sum.getBytesPerChecksum()) {
                final int chunkLen = Math.min(this.sum.getBytesPerChecksum(), len - i);
                final int ckOffset = i / this.sum.getBytesPerChecksum() * this.getChecksumSize();
                this.writeChunk(b, off + i, chunkLen, this.checksum, ckOffset, this.getChecksumSize());
            }
        }
        finally {
            if (scope != null) {
                scope.close();
            }
        }
    }
    
    public static byte[] convertToByteStream(final Checksum sum, final int checksumSize) {
        return int2byte((int)sum.getValue(), new byte[checksumSize]);
    }
    
    static byte[] int2byte(final int integer, final byte[] bytes) {
        if (bytes.length != 0) {
            bytes[0] = (byte)(integer >>> 24 & 0xFF);
            bytes[1] = (byte)(integer >>> 16 & 0xFF);
            bytes[2] = (byte)(integer >>> 8 & 0xFF);
            bytes[3] = (byte)(integer >>> 0 & 0xFF);
            return bytes;
        }
        return bytes;
    }
    
    protected synchronized void setChecksumBufSize(final int size) {
        this.buf = new byte[size];
        this.checksum = new byte[this.sum.getChecksumSize(size)];
        this.count = 0;
    }
    
    protected synchronized void resetChecksumBufSize() {
        this.setChecksumBufSize(this.sum.getBytesPerChecksum() * 9);
    }
}
