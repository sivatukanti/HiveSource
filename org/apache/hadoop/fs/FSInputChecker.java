// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.fs;

import org.slf4j.LoggerFactory;
import java.nio.ByteBuffer;
import java.io.InputStream;
import java.io.EOFException;
import org.apache.hadoop.util.StringUtils;
import java.io.IOException;
import java.nio.IntBuffer;
import java.util.zip.Checksum;
import org.slf4j.Logger;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.LimitedPrivate({ "HDFS" })
@InterfaceStability.Unstable
public abstract class FSInputChecker extends FSInputStream
{
    public static final Logger LOG;
    protected Path file;
    private Checksum sum;
    private boolean verifyChecksum;
    private int maxChunkSize;
    private byte[] buf;
    private byte[] checksum;
    private IntBuffer checksumInts;
    private int pos;
    private int count;
    private int numOfRetries;
    private long chunkPos;
    private static final int CHUNKS_PER_READ = 32;
    protected static final int CHECKSUM_SIZE = 4;
    
    protected FSInputChecker(final Path file, final int numOfRetries) {
        this.verifyChecksum = true;
        this.chunkPos = 0L;
        this.file = file;
        this.numOfRetries = numOfRetries;
    }
    
    protected FSInputChecker(final Path file, final int numOfRetries, final boolean verifyChecksum, final Checksum sum, final int chunkSize, final int checksumSize) {
        this(file, numOfRetries);
        this.set(verifyChecksum, sum, chunkSize, checksumSize);
    }
    
    protected abstract int readChunk(final long p0, final byte[] p1, final int p2, final int p3, final byte[] p4) throws IOException;
    
    protected abstract long getChunkPosition(final long p0);
    
    protected synchronized boolean needChecksum() {
        return this.verifyChecksum && this.sum != null;
    }
    
    @Override
    public synchronized int read() throws IOException {
        if (this.pos >= this.count) {
            this.fill();
            if (this.pos >= this.count) {
                return -1;
            }
        }
        return this.buf[this.pos++] & 0xFF;
    }
    
    @Override
    public synchronized int read(final byte[] b, final int off, final int len) throws IOException {
        if ((off | len | off + len | b.length - (off + len)) < 0) {
            throw new IndexOutOfBoundsException();
        }
        if (len == 0) {
            return 0;
        }
        int n = 0;
        while (true) {
            final int nread = this.read1(b, off + n, len - n);
            if (nread <= 0) {
                return (n == 0) ? nread : n;
            }
            n += nread;
            if (n >= len) {
                return n;
            }
        }
    }
    
    private void fill() throws IOException {
        assert this.pos >= this.count;
        this.count = this.readChecksumChunk(this.buf, 0, this.maxChunkSize);
        if (this.count < 0) {
            this.count = 0;
        }
    }
    
    protected final synchronized int readAndDiscard(final int len) throws IOException {
        int total;
        int rd;
        for (total = 0; total < len; total += rd) {
            if (this.pos >= this.count) {
                this.count = this.readChecksumChunk(this.buf, 0, this.maxChunkSize);
                if (this.count <= 0) {
                    break;
                }
            }
            rd = Math.min(this.count - this.pos, len - total);
            this.pos += rd;
        }
        return total;
    }
    
    private int read1(final byte[] b, final int off, final int len) throws IOException {
        int avail = this.count - this.pos;
        if (avail <= 0) {
            if (len >= this.maxChunkSize) {
                final int nread = this.readChecksumChunk(b, off, len);
                return nread;
            }
            this.fill();
            if (this.count <= 0) {
                return -1;
            }
            avail = this.count;
        }
        final int cnt = (avail < len) ? avail : len;
        System.arraycopy(this.buf, this.pos, b, off, cnt);
        this.pos += cnt;
        return cnt;
    }
    
    private int readChecksumChunk(final byte[] b, final int off, final int len) throws IOException {
        final int n = 0;
        this.pos = n;
        this.count = n;
        int read = 0;
        boolean retry = true;
        int retriesLeft = this.numOfRetries;
        do {
            --retriesLeft;
            try {
                read = this.readChunk(this.chunkPos, b, off, len, this.checksum);
                if (read > 0) {
                    if (this.needChecksum()) {
                        this.verifySums(b, off, read);
                    }
                    this.chunkPos += read;
                }
                retry = false;
            }
            catch (ChecksumException ce) {
                FSInputChecker.LOG.info("Found checksum error: b[" + off + ", " + (off + read) + "]=" + StringUtils.byteToHexString(b, off, off + read), ce);
                if (retriesLeft == 0) {
                    throw ce;
                }
                if (!this.seekToNewSource(this.chunkPos)) {
                    throw ce;
                }
                this.seek(this.chunkPos);
            }
        } while (retry);
        return read;
    }
    
    private void verifySums(final byte[] b, final int off, final int read) throws ChecksumException {
        int leftToVerify = read;
        int verifyOff = 0;
        this.checksumInts.rewind();
        this.checksumInts.limit((read - 1) / this.maxChunkSize + 1);
        while (leftToVerify > 0) {
            this.sum.update(b, off + verifyOff, Math.min(leftToVerify, this.maxChunkSize));
            final int expected = this.checksumInts.get();
            final int calculated = (int)this.sum.getValue();
            this.sum.reset();
            if (expected != calculated) {
                final long errPos = this.chunkPos + verifyOff;
                throw new ChecksumException("Checksum error: " + this.file + " at " + errPos + " exp: " + expected + " got: " + calculated, errPos);
            }
            leftToVerify -= this.maxChunkSize;
            verifyOff += this.maxChunkSize;
        }
    }
    
    @Deprecated
    public static long checksum2long(final byte[] checksum) {
        long crc = 0L;
        for (int i = 0; i < checksum.length; ++i) {
            crc |= (0xFFL & (long)checksum[i]) << (checksum.length - i - 1) * 8;
        }
        return crc;
    }
    
    @Override
    public synchronized long getPos() throws IOException {
        return this.chunkPos - Math.max(0L, this.count - this.pos);
    }
    
    @Override
    public synchronized int available() throws IOException {
        return Math.max(0, this.count - this.pos);
    }
    
    @Override
    public synchronized long skip(final long n) throws IOException {
        if (n <= 0L) {
            return 0L;
        }
        this.seek(this.getPos() + n);
        return n;
    }
    
    @Override
    public synchronized void seek(final long pos) throws IOException {
        if (pos < 0L) {
            throw new EOFException("Cannot seek to a negative offset");
        }
        final long start = this.chunkPos - this.count;
        if (pos >= start && pos < this.chunkPos) {
            this.pos = (int)(pos - start);
            return;
        }
        this.resetState();
        this.chunkPos = this.getChunkPosition(pos);
        final int delta = (int)(pos - this.chunkPos);
        if (delta > 0) {
            readFully(this, new byte[delta], 0, delta);
        }
    }
    
    protected static int readFully(final InputStream stm, final byte[] buf, final int offset, final int len) throws IOException {
        int n = 0;
        while (true) {
            final int nread = stm.read(buf, offset + n, len - n);
            if (nread <= 0) {
                return (n == 0) ? nread : n;
            }
            n += nread;
            if (n >= len) {
                return n;
            }
        }
    }
    
    protected final synchronized void set(final boolean verifyChecksum, final Checksum sum, final int maxChunkSize, final int checksumSize) {
        assert checksumSize == 4;
        this.maxChunkSize = maxChunkSize;
        this.verifyChecksum = verifyChecksum;
        this.sum = sum;
        this.buf = new byte[maxChunkSize];
        this.checksum = new byte[32 * checksumSize];
        this.checksumInts = ByteBuffer.wrap(this.checksum).asIntBuffer();
        this.count = 0;
        this.pos = 0;
    }
    
    @Override
    public final boolean markSupported() {
        return false;
    }
    
    @Override
    public final void mark(final int readlimit) {
    }
    
    @Override
    public final void reset() throws IOException {
        throw new IOException("mark/reset not supported");
    }
    
    private void resetState() {
        this.count = 0;
        this.pos = 0;
        if (this.sum != null) {
            this.sum.reset();
        }
    }
    
    static {
        LOG = LoggerFactory.getLogger(FSInputChecker.class);
    }
}
