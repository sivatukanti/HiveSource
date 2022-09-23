// 
// Decompiled by Procyon v0.5.36
// 

package org.tukaani.xz.index;

import java.io.IOException;
import java.io.EOFException;
import org.tukaani.xz.MemoryLimitException;
import org.tukaani.xz.UnsupportedOptionsException;
import org.tukaani.xz.common.DecoderUtil;
import java.util.zip.Checksum;
import java.io.InputStream;
import java.util.zip.CheckedInputStream;
import java.util.zip.CRC32;
import org.tukaani.xz.XZIOException;
import org.tukaani.xz.CorruptedInputException;
import org.tukaani.xz.common.StreamFlags;
import org.tukaani.xz.SeekableInputStream;

public class IndexDecoder extends IndexBase
{
    private final BlockInfo info;
    private final long streamPadding;
    private final int memoryUsage;
    private final long[] unpadded;
    private final long[] uncompressed;
    private long largestBlockSize;
    private int pos;
    
    public IndexDecoder(final SeekableInputStream in, final StreamFlags streamFlags, final long streamPadding, final int n) throws IOException {
        super(new CorruptedInputException("XZ Index is corrupt"));
        this.info = new BlockInfo();
        this.largestBlockSize = 0L;
        this.pos = -1;
        this.info.streamFlags = streamFlags;
        this.streamPadding = streamPadding;
        final long n2 = in.position() + streamFlags.backwardSize - 4L;
        final CRC32 cksum = new CRC32();
        final CheckedInputStream checkedInputStream = new CheckedInputStream(in, cksum);
        if (checkedInputStream.read() != 0) {
            throw new CorruptedInputException("XZ Index is corrupt");
        }
        try {
            final long decodeVLI = DecoderUtil.decodeVLI(checkedInputStream);
            if (decodeVLI >= streamFlags.backwardSize / 2L) {
                throw new CorruptedInputException("XZ Index is corrupt");
            }
            if (decodeVLI > 2147483647L) {
                throw new UnsupportedOptionsException("XZ Index has over 2147483647 Records");
            }
            this.memoryUsage = 1 + (int)((16L * decodeVLI + 1023L) / 1024L);
            if (n >= 0 && this.memoryUsage > n) {
                throw new MemoryLimitException(this.memoryUsage, n);
            }
            this.unpadded = new long[(int)decodeVLI];
            this.uncompressed = new long[(int)decodeVLI];
            int n3 = 0;
            for (int i = (int)decodeVLI; i > 0; --i) {
                final long decodeVLI2 = DecoderUtil.decodeVLI(checkedInputStream);
                final long decodeVLI3 = DecoderUtil.decodeVLI(checkedInputStream);
                if (in.position() > n2) {
                    throw new CorruptedInputException("XZ Index is corrupt");
                }
                this.unpadded[n3] = this.blocksSum + decodeVLI2;
                this.uncompressed[n3] = this.uncompressedSum + decodeVLI3;
                ++n3;
                super.add(decodeVLI2, decodeVLI3);
                assert n3 == this.recordCount;
                if (this.largestBlockSize < decodeVLI3) {
                    this.largestBlockSize = decodeVLI3;
                }
            }
        }
        catch (EOFException ex) {
            throw new CorruptedInputException("XZ Index is corrupt");
        }
        int indexPaddingSize = this.getIndexPaddingSize();
        if (in.position() + indexPaddingSize != n2) {
            throw new CorruptedInputException("XZ Index is corrupt");
        }
        while (indexPaddingSize-- > 0) {
            if (checkedInputStream.read() != 0) {
                throw new CorruptedInputException("XZ Index is corrupt");
            }
        }
        final long value = cksum.getValue();
        for (int j = 0; j < 4; ++j) {
            if ((value >>> j * 8 & 0xFFL) != in.read()) {
                throw new CorruptedInputException("XZ Index is corrupt");
            }
        }
    }
    
    public BlockInfo locate(final long n) {
        assert n < this.uncompressedSum;
        int i = 0;
        int n2 = this.unpadded.length - 1;
        while (i < n2) {
            final int n3 = i + (n2 - i) / 2;
            if (this.uncompressed[n3] <= n) {
                i = n3 + 1;
            }
            else {
                n2 = n3;
            }
        }
        this.pos = i;
        return this.getInfo();
    }
    
    public int getMemoryUsage() {
        return this.memoryUsage;
    }
    
    public long getStreamAndPaddingSize() {
        return this.getStreamSize() + this.streamPadding;
    }
    
    public long getUncompressedSize() {
        return this.uncompressedSum;
    }
    
    public long getLargestBlockSize() {
        return this.largestBlockSize;
    }
    
    public boolean hasNext() {
        return this.pos + 1 < this.recordCount;
    }
    
    public BlockInfo getNext() {
        ++this.pos;
        return this.getInfo();
    }
    
    private BlockInfo getInfo() {
        if (this.pos == 0) {
            this.info.compressedOffset = 0L;
            this.info.uncompressedOffset = 0L;
        }
        else {
            this.info.compressedOffset = (this.unpadded[this.pos - 1] + 3L & 0xFFFFFFFFFFFFFFFCL);
            this.info.uncompressedOffset = this.uncompressed[this.pos - 1];
        }
        this.info.unpaddedSize = this.unpadded[this.pos] - this.info.compressedOffset;
        this.info.uncompressedSize = this.uncompressed[this.pos] - this.info.uncompressedOffset;
        final BlockInfo info = this.info;
        info.compressedOffset += 12L;
        return this.info;
    }
}
