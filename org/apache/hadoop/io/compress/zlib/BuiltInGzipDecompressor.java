// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.io.compress.zlib;

import java.util.zip.DataFormatException;
import java.io.IOException;
import org.apache.hadoop.util.DataChecksum;
import java.util.zip.Checksum;
import java.util.zip.Inflater;
import org.apache.hadoop.io.compress.DoNotPool;
import org.apache.hadoop.io.compress.Decompressor;

@DoNotPool
public class BuiltInGzipDecompressor implements Decompressor
{
    private static final int GZIP_MAGIC_ID = 35615;
    private static final int GZIP_DEFLATE_METHOD = 8;
    private static final int GZIP_FLAGBIT_HEADER_CRC = 2;
    private static final int GZIP_FLAGBIT_EXTRA_FIELD = 4;
    private static final int GZIP_FLAGBIT_FILENAME = 8;
    private static final int GZIP_FLAGBIT_COMMENT = 16;
    private static final int GZIP_FLAGBITS_RESERVED = 224;
    private Inflater inflater;
    private byte[] userBuf;
    private int userBufOff;
    private int userBufLen;
    private byte[] localBuf;
    private int localBufOff;
    private int headerBytesRead;
    private int trailerBytesRead;
    private int numExtraFieldBytesRemaining;
    private Checksum crc;
    private boolean hasExtraField;
    private boolean hasFilename;
    private boolean hasComment;
    private boolean hasHeaderCRC;
    private GzipStateLabel state;
    
    public BuiltInGzipDecompressor() {
        this.inflater = new Inflater(true);
        this.userBuf = null;
        this.userBufOff = 0;
        this.userBufLen = 0;
        this.localBuf = new byte[256];
        this.localBufOff = 0;
        this.headerBytesRead = 0;
        this.trailerBytesRead = 0;
        this.numExtraFieldBytesRemaining = -1;
        this.crc = DataChecksum.newCrc32();
        this.hasExtraField = false;
        this.hasFilename = false;
        this.hasComment = false;
        this.hasHeaderCRC = false;
        this.state = GzipStateLabel.HEADER_BASIC;
        this.crc.reset();
    }
    
    @Override
    public synchronized boolean needsInput() {
        if (this.state == GzipStateLabel.DEFLATE_STREAM) {
            return this.inflater.needsInput();
        }
        return this.state != GzipStateLabel.FINISHED;
    }
    
    @Override
    public synchronized void setInput(final byte[] b, final int off, final int len) {
        if (b == null) {
            throw new NullPointerException();
        }
        if (off < 0 || len < 0 || off > b.length - len) {
            throw new ArrayIndexOutOfBoundsException();
        }
        this.userBuf = b;
        this.userBufOff = off;
        this.userBufLen = len;
    }
    
    @Override
    public synchronized int decompress(final byte[] b, final int off, final int len) throws IOException {
        int numAvailBytes = 0;
        if (this.state != GzipStateLabel.DEFLATE_STREAM) {
            this.executeHeaderState();
            if (this.userBufLen <= 0) {
                return numAvailBytes;
            }
        }
        if (this.state == GzipStateLabel.DEFLATE_STREAM) {
            if (this.userBufLen > 0) {
                this.inflater.setInput(this.userBuf, this.userBufOff, this.userBufLen);
                this.userBufOff += this.userBufLen;
                this.userBufLen = 0;
            }
            try {
                numAvailBytes = this.inflater.inflate(b, off, len);
            }
            catch (DataFormatException dfe) {
                throw new IOException(dfe.getMessage());
            }
            this.crc.update(b, off, numAvailBytes);
            if (!this.inflater.finished()) {
                return numAvailBytes;
            }
            this.state = GzipStateLabel.TRAILER_CRC;
            final int bytesRemaining = this.inflater.getRemaining();
            assert bytesRemaining >= 0 : "logic error: Inflater finished; byte-count is inconsistent";
            this.userBufOff -= bytesRemaining;
            this.userBufLen = bytesRemaining;
        }
        this.executeTrailerState();
        return numAvailBytes;
    }
    
    private void executeHeaderState() throws IOException {
        if (this.userBufLen <= 0) {
            return;
        }
        if (this.state == GzipStateLabel.HEADER_BASIC) {
            final int n = Math.min(this.userBufLen, 10 - this.localBufOff);
            this.checkAndCopyBytesToLocal(n);
            if (this.localBufOff >= 10) {
                this.processBasicHeader();
                this.localBufOff = 0;
                this.state = GzipStateLabel.HEADER_EXTRA_FIELD;
            }
        }
        if (this.userBufLen <= 0) {
            return;
        }
        if (this.state == GzipStateLabel.HEADER_EXTRA_FIELD) {
            if (this.hasExtraField) {
                if (this.numExtraFieldBytesRemaining < 0) {
                    final int n = Math.min(this.userBufLen, 2 - this.localBufOff);
                    this.checkAndCopyBytesToLocal(n);
                    if (this.localBufOff >= 2) {
                        this.numExtraFieldBytesRemaining = this.readUShortLE(this.localBuf, 0);
                        this.localBufOff = 0;
                    }
                }
                if (this.numExtraFieldBytesRemaining > 0 && this.userBufLen > 0) {
                    final int n = Math.min(this.userBufLen, this.numExtraFieldBytesRemaining);
                    this.checkAndSkipBytes(n);
                    this.numExtraFieldBytesRemaining -= n;
                }
                if (this.numExtraFieldBytesRemaining == 0) {
                    this.state = GzipStateLabel.HEADER_FILENAME;
                }
            }
            else {
                this.state = GzipStateLabel.HEADER_FILENAME;
            }
        }
        if (this.userBufLen <= 0) {
            return;
        }
        if (this.state == GzipStateLabel.HEADER_FILENAME) {
            if (this.hasFilename) {
                final boolean doneWithFilename = this.checkAndSkipBytesUntilNull();
                if (!doneWithFilename) {
                    return;
                }
            }
            this.state = GzipStateLabel.HEADER_COMMENT;
        }
        if (this.userBufLen <= 0) {
            return;
        }
        if (this.state == GzipStateLabel.HEADER_COMMENT) {
            if (this.hasComment) {
                final boolean doneWithComment = this.checkAndSkipBytesUntilNull();
                if (!doneWithComment) {
                    return;
                }
            }
            this.state = GzipStateLabel.HEADER_CRC;
        }
        if (this.userBufLen <= 0) {
            return;
        }
        if (this.state == GzipStateLabel.HEADER_CRC) {
            if (this.hasHeaderCRC) {
                assert this.localBufOff < 2;
                final int n = Math.min(this.userBufLen, 2 - this.localBufOff);
                this.copyBytesToLocal(n);
                if (this.localBufOff >= 2) {
                    final long headerCRC = this.readUShortLE(this.localBuf, 0);
                    if (headerCRC != (this.crc.getValue() & 0xFFFFL)) {
                        throw new IOException("gzip header CRC failure");
                    }
                    this.localBufOff = 0;
                    this.crc.reset();
                    this.state = GzipStateLabel.DEFLATE_STREAM;
                }
            }
            else {
                this.crc.reset();
                this.state = GzipStateLabel.DEFLATE_STREAM;
            }
        }
    }
    
    private void executeTrailerState() throws IOException {
        if (this.userBufLen <= 0) {
            return;
        }
        if (this.state == GzipStateLabel.TRAILER_CRC) {
            assert this.localBufOff < 4;
            final int n = Math.min(this.userBufLen, 4 - this.localBufOff);
            this.copyBytesToLocal(n);
            if (this.localBufOff >= 4) {
                final long streamCRC = this.readUIntLE(this.localBuf, 0);
                if (streamCRC != this.crc.getValue()) {
                    throw new IOException("gzip stream CRC failure");
                }
                this.localBufOff = 0;
                this.crc.reset();
                this.state = GzipStateLabel.TRAILER_SIZE;
            }
        }
        if (this.userBufLen <= 0) {
            return;
        }
        if (this.state == GzipStateLabel.TRAILER_SIZE) {
            assert this.localBufOff < 4;
            final int n = Math.min(this.userBufLen, 4 - this.localBufOff);
            this.copyBytesToLocal(n);
            if (this.localBufOff >= 4) {
                final long inputSize = this.readUIntLE(this.localBuf, 0);
                if (inputSize != (this.inflater.getBytesWritten() & 0xFFFFFFFFL)) {
                    throw new IOException("stored gzip size doesn't match decompressed size");
                }
                this.localBufOff = 0;
                this.state = GzipStateLabel.FINISHED;
            }
        }
        if (this.state == GzipStateLabel.FINISHED) {
            return;
        }
    }
    
    public synchronized long getBytesRead() {
        return this.headerBytesRead + this.inflater.getBytesRead() + this.trailerBytesRead;
    }
    
    @Override
    public synchronized int getRemaining() {
        return this.userBufLen;
    }
    
    @Override
    public synchronized boolean needsDictionary() {
        return this.inflater.needsDictionary();
    }
    
    @Override
    public synchronized void setDictionary(final byte[] b, final int off, final int len) {
        this.inflater.setDictionary(b, off, len);
    }
    
    @Override
    public synchronized boolean finished() {
        return this.state == GzipStateLabel.FINISHED;
    }
    
    @Override
    public synchronized void reset() {
        this.inflater.reset();
        this.state = GzipStateLabel.HEADER_BASIC;
        this.crc.reset();
        final int n = 0;
        this.userBufLen = n;
        this.userBufOff = n;
        this.localBufOff = 0;
        this.headerBytesRead = 0;
        this.trailerBytesRead = 0;
        this.numExtraFieldBytesRemaining = -1;
        this.hasExtraField = false;
        this.hasFilename = false;
        this.hasComment = false;
        this.hasHeaderCRC = false;
    }
    
    @Override
    public synchronized void end() {
        this.inflater.end();
    }
    
    private void processBasicHeader() throws IOException {
        if (this.readUShortLE(this.localBuf, 0) != 35615) {
            throw new IOException("not a gzip file");
        }
        if (this.readUByte(this.localBuf, 2) != 8) {
            throw new IOException("gzip data not compressed with deflate method");
        }
        final int flg = this.readUByte(this.localBuf, 3);
        if ((flg & 0xE0) != 0x0) {
            throw new IOException("unknown gzip format (reserved flagbits set)");
        }
        this.hasExtraField = ((flg & 0x4) != 0x0);
        this.hasFilename = ((flg & 0x8) != 0x0);
        this.hasComment = ((flg & 0x10) != 0x0);
        this.hasHeaderCRC = ((flg & 0x2) != 0x0);
    }
    
    private void checkAndCopyBytesToLocal(final int len) {
        System.arraycopy(this.userBuf, this.userBufOff, this.localBuf, this.localBufOff, len);
        this.localBufOff += len;
        this.crc.update(this.userBuf, this.userBufOff, len);
        this.userBufOff += len;
        this.userBufLen -= len;
        this.headerBytesRead += len;
    }
    
    private void checkAndSkipBytes(final int len) {
        this.crc.update(this.userBuf, this.userBufOff, len);
        this.userBufOff += len;
        this.userBufLen -= len;
        this.headerBytesRead += len;
    }
    
    private boolean checkAndSkipBytesUntilNull() {
        boolean hitNull = false;
        if (this.userBufLen > 0) {
            do {
                hitNull = (this.userBuf[this.userBufOff] == 0);
                this.crc.update(this.userBuf[this.userBufOff]);
                ++this.userBufOff;
                --this.userBufLen;
                ++this.headerBytesRead;
            } while (this.userBufLen > 0 && !hitNull);
        }
        return hitNull;
    }
    
    private void copyBytesToLocal(final int len) {
        System.arraycopy(this.userBuf, this.userBufOff, this.localBuf, this.localBufOff, len);
        this.localBufOff += len;
        this.userBufOff += len;
        this.userBufLen -= len;
        if (this.state == GzipStateLabel.TRAILER_CRC || this.state == GzipStateLabel.TRAILER_SIZE) {
            this.trailerBytesRead += len;
        }
        else {
            this.headerBytesRead += len;
        }
    }
    
    private int readUByte(final byte[] b, final int off) {
        return b[off] & 0xFF;
    }
    
    private int readUShortLE(final byte[] b, final int off) {
        return ((b[off + 1] & 0xFF) << 8 | (b[off] & 0xFF)) & 0xFFFF;
    }
    
    private long readUIntLE(final byte[] b, final int off) {
        return ((long)(b[off + 3] & 0xFF) << 24 | (long)(b[off + 2] & 0xFF) << 16 | (long)(b[off + 1] & 0xFF) << 8 | (long)(b[off] & 0xFF)) & 0xFFFFFFFFL;
    }
    
    private enum GzipStateLabel
    {
        HEADER_BASIC, 
        HEADER_EXTRA_FIELD, 
        HEADER_FILENAME, 
        HEADER_COMMENT, 
        HEADER_CRC, 
        DEFLATE_STREAM, 
        TRAILER_CRC, 
        TRAILER_SIZE, 
        FINISHED;
    }
}
