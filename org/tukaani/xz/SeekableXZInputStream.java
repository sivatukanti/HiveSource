// 
// Decompiled by Procyon v0.5.36
// 

package org.tukaani.xz;

import org.tukaani.xz.index.BlockInfo;
import java.io.EOFException;
import org.tukaani.xz.common.StreamFlags;
import org.tukaani.xz.common.DecoderUtil;
import java.util.Arrays;
import java.io.InputStream;
import java.io.DataInputStream;
import java.io.IOException;
import org.tukaani.xz.check.Check;
import org.tukaani.xz.index.IndexDecoder;
import java.util.ArrayList;

public class SeekableXZInputStream extends SeekableInputStream
{
    private SeekableInputStream in;
    private final int memoryLimit;
    private int indexMemoryUsage;
    private final ArrayList streams;
    private IndexDecoder index;
    private int checkTypes;
    private Check check;
    private BlockInputStream blockDecoder;
    private long uncompressedSize;
    private long largestBlockSize;
    private long curPos;
    private long seekPos;
    private boolean seekNeeded;
    private boolean endReached;
    private IOException exception;
    static final /* synthetic */ boolean $assertionsDisabled;
    
    public SeekableXZInputStream(final SeekableInputStream seekableInputStream) throws IOException {
        this(seekableInputStream, -1);
    }
    
    public SeekableXZInputStream(final SeekableInputStream seekableInputStream, int memoryLimit) throws IOException {
        this.indexMemoryUsage = 0;
        this.streams = new ArrayList();
        this.checkTypes = 0;
        this.blockDecoder = null;
        this.uncompressedSize = 0L;
        this.largestBlockSize = 0L;
        this.curPos = 0L;
        this.seekNeeded = false;
        this.endReached = false;
        this.exception = null;
        this.in = seekableInputStream;
        final DataInputStream dataInputStream = new DataInputStream(seekableInputStream);
        seekableInputStream.seek(0L);
        final byte[] array = new byte[XZ.HEADER_MAGIC.length];
        dataInputStream.readFully(array);
        if (!Arrays.equals(array, XZ.HEADER_MAGIC)) {
            throw new XZFormatException();
        }
        long length = seekableInputStream.length();
        if ((length & 0x3L) != 0x0L) {
            throw new CorruptedInputException("XZ file size is not a multiple of 4 bytes");
        }
        final byte[] array2 = new byte[12];
        long n = 0L;
        while (length > 0L) {
            if (length < 12L) {
                throw new CorruptedInputException();
            }
            seekableInputStream.seek(length - 12L);
            dataInputStream.readFully(array2);
            if (array2[8] == 0 && array2[9] == 0 && array2[10] == 0 && array2[11] == 0) {
                n += 4L;
                length -= 4L;
            }
            else {
                final long n2 = length - 12L;
                final StreamFlags decodeStreamFooter = DecoderUtil.decodeStreamFooter(array2);
                if (decodeStreamFooter.backwardSize >= n2) {
                    throw new CorruptedInputException("Backward Size in XZ Stream Footer is too big");
                }
                this.check = Check.getInstance(decodeStreamFooter.checkType);
                this.checkTypes |= 1 << decodeStreamFooter.checkType;
                seekableInputStream.seek(n2 - decodeStreamFooter.backwardSize);
                try {
                    this.index = new IndexDecoder(seekableInputStream, decodeStreamFooter, n, memoryLimit);
                }
                catch (MemoryLimitException ex) {
                    assert memoryLimit >= 0;
                    throw new MemoryLimitException(ex.getMemoryNeeded() + this.indexMemoryUsage, memoryLimit + this.indexMemoryUsage);
                }
                this.indexMemoryUsage += this.index.getMemoryUsage();
                if (memoryLimit >= 0) {
                    memoryLimit -= this.index.getMemoryUsage();
                    assert memoryLimit >= 0;
                }
                if (this.largestBlockSize < this.index.getLargestBlockSize()) {
                    this.largestBlockSize = this.index.getLargestBlockSize();
                }
                final long n3 = this.index.getStreamSize() - 12L;
                if (n2 < n3) {
                    throw new CorruptedInputException("XZ Index indicates too big compressed size for the XZ Stream");
                }
                length = n2 - n3;
                seekableInputStream.seek(length);
                dataInputStream.readFully(array2);
                if (!DecoderUtil.areStreamFlagsEqual(DecoderUtil.decodeStreamHeader(array2), decodeStreamFooter)) {
                    throw new CorruptedInputException("XZ Stream Footer does not match Stream Header");
                }
                this.uncompressedSize += this.index.getUncompressedSize();
                if (this.uncompressedSize < 0L) {
                    throw new UnsupportedOptionsException("XZ file is too big");
                }
                this.streams.add(this.index);
                n = 0L;
            }
        }
        assert length == 0L;
        this.memoryLimit = memoryLimit;
    }
    
    public int getCheckTypes() {
        return this.checkTypes;
    }
    
    public int getIndexMemoryUsage() {
        return this.indexMemoryUsage;
    }
    
    public long getLargestBlockSize() {
        return this.largestBlockSize;
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
        int n2 = 0;
        try {
            if (this.seekNeeded) {
                this.seek();
            }
            if (this.endReached) {
                return -1;
            }
            while (i > 0) {
                if (this.blockDecoder == null) {
                    this.seek();
                    if (this.endReached) {
                        break;
                    }
                }
                final int read = this.blockDecoder.read(array, n, i);
                if (read > 0) {
                    this.curPos += read;
                    n2 += read;
                    n += read;
                    i -= read;
                }
                else {
                    if (read != -1) {
                        continue;
                    }
                    this.blockDecoder = null;
                }
            }
        }
        catch (IOException exception) {
            if (exception instanceof EOFException) {
                exception = new CorruptedInputException();
            }
            this.exception = exception;
            if (n2 == 0) {
                throw exception;
            }
        }
        return n2;
    }
    
    public int available() throws IOException {
        if (this.in == null) {
            throw new XZIOException("Stream closed");
        }
        if (this.exception != null) {
            throw this.exception;
        }
        if (this.endReached || this.seekNeeded || this.blockDecoder == null) {
            return 0;
        }
        return this.blockDecoder.available();
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
    
    public long length() {
        return this.uncompressedSize;
    }
    
    public long position() throws IOException {
        if (this.in == null) {
            throw new XZIOException("Stream closed");
        }
        return this.seekNeeded ? this.seekPos : this.curPos;
    }
    
    public void seek(final long n) throws IOException {
        if (this.in == null) {
            throw new XZIOException("Stream closed");
        }
        if (n < 0L) {
            throw new XZIOException("Negative seek position: " + n);
        }
        this.seekPos = n;
        this.seekNeeded = true;
    }
    
    private void seek() throws IOException {
        if (!this.seekNeeded) {
            if (this.index.hasNext()) {
                this.initBlockDecoder(this.index.getNext());
                return;
            }
            this.seekPos = this.curPos;
        }
        this.seekNeeded = false;
        if (this.seekPos >= this.uncompressedSize) {
            this.curPos = this.seekPos;
            this.blockDecoder = null;
            this.endReached = true;
            return;
        }
        this.endReached = false;
        int size = this.streams.size();
        assert size >= 1;
        long n = 0L;
        long n2 = 0L;
        do {
            this.index = (IndexDecoder)this.streams.get(--size);
            if (n + this.index.getUncompressedSize() > this.seekPos) {
                final BlockInfo locate = this.index.locate(this.seekPos - n);
                assert (locate.compressedOffset & 0x3L) == 0x0L : locate.compressedOffset;
                final BlockInfo blockInfo = locate;
                blockInfo.compressedOffset += n2;
                final BlockInfo blockInfo2 = locate;
                blockInfo2.uncompressedOffset += n;
                assert this.seekPos >= locate.uncompressedOffset;
                assert this.seekPos < locate.uncompressedOffset + locate.uncompressedSize;
                if (this.curPos <= locate.uncompressedOffset || this.curPos > this.seekPos) {
                    this.in.seek(locate.compressedOffset);
                    this.check = Check.getInstance(locate.streamFlags.checkType);
                    this.initBlockDecoder(locate);
                    this.curPos = locate.uncompressedOffset;
                }
                if (this.seekPos > this.curPos) {
                    final long n3 = this.seekPos - this.curPos;
                    if (this.blockDecoder.skip(n3) != n3) {
                        throw new CorruptedInputException();
                    }
                }
                this.curPos = this.seekPos;
                return;
            }
            else {
                n += this.index.getUncompressedSize();
                n2 += this.index.getStreamAndPaddingSize();
            }
        } while (SeekableXZInputStream.$assertionsDisabled || (n2 & 0x3L) == 0x0L);
        throw new AssertionError();
    }
    
    private void initBlockDecoder(final BlockInfo blockInfo) throws IOException {
        try {
            this.blockDecoder = null;
            this.blockDecoder = new BlockInputStream(this.in, this.check, this.memoryLimit, blockInfo.unpaddedSize, blockInfo.uncompressedSize);
        }
        catch (MemoryLimitException ex) {
            assert this.memoryLimit >= 0;
            throw new MemoryLimitException(ex.getMemoryNeeded() + this.indexMemoryUsage, this.memoryLimit + this.indexMemoryUsage);
        }
        catch (IndexIndicatorException ex2) {
            throw new CorruptedInputException();
        }
    }
}
