// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.io.compress.bzip2;

import java.io.IOException;
import java.io.BufferedInputStream;
import org.apache.hadoop.io.compress.SplittableCompressionCodec;
import java.io.InputStream;

public class CBZip2InputStream extends InputStream implements BZip2Constants
{
    public static final long BLOCK_DELIMITER = 54156738319193L;
    public static final long EOS_DELIMITER = 25779555029136L;
    private static final int DELIMITER_BIT_LENGTH = 48;
    SplittableCompressionCodec.READ_MODE readMode;
    private long reportedBytesReadFromCompressedStream;
    private long bytesReadFromCompressedStream;
    private boolean lazyInitialization;
    private byte[] array;
    private int last;
    private int origPtr;
    private int blockSize100k;
    private boolean blockRandomised;
    private long bsBuff;
    private long bsLive;
    private final CRC crc;
    private int nInUse;
    private BufferedInputStream in;
    private int currentChar;
    private STATE currentState;
    private int storedBlockCRC;
    private int storedCombinedCRC;
    private int computedBlockCRC;
    private int computedCombinedCRC;
    private boolean skipResult;
    private boolean skipDecompression;
    private int su_count;
    private int su_ch2;
    private int su_chPrev;
    private int su_i2;
    private int su_j2;
    private int su_rNToGo;
    private int su_rTPos;
    private int su_tPos;
    private char su_z;
    private Data data;
    
    public long getProcessedByteCount() {
        return this.reportedBytesReadFromCompressedStream;
    }
    
    protected void updateProcessedByteCount(final int count) {
        this.bytesReadFromCompressedStream += count;
    }
    
    public void updateReportedByteCount(final int count) {
        this.reportedBytesReadFromCompressedStream += count;
        this.updateProcessedByteCount(count);
    }
    
    private int readAByte(final InputStream inStream) throws IOException {
        final int read = inStream.read();
        if (read >= 0) {
            this.updateProcessedByteCount(1);
        }
        return read;
    }
    
    public boolean skipToNextMarker(final long marker, final int markerBitLength) throws IOException, IllegalArgumentException {
        try {
            if (markerBitLength > 63) {
                throw new IllegalArgumentException("skipToNextMarker can not find patterns greater than 63 bits");
            }
            long bytes = 0L;
            bytes = this.bsR(markerBitLength);
            if (bytes == -1L) {
                this.reportedBytesReadFromCompressedStream = this.bytesReadFromCompressedStream;
                return false;
            }
            while (bytes != marker) {
                bytes <<= 1;
                bytes &= (1L << markerBitLength) - 1L;
                final int oneBit = (int)this.bsR(1L);
                if (oneBit == -1) {
                    this.reportedBytesReadFromCompressedStream = this.bytesReadFromCompressedStream;
                    return false;
                }
                bytes |= oneBit;
            }
            final long markerBytesRead = (markerBitLength + this.bsLive + 7L) / 8L;
            this.reportedBytesReadFromCompressedStream = this.bytesReadFromCompressedStream - markerBytesRead;
            return true;
        }
        catch (IOException ex) {
            this.reportedBytesReadFromCompressedStream = this.bytesReadFromCompressedStream;
            return false;
        }
    }
    
    protected void reportCRCError() throws IOException {
        throw new IOException("crc error");
    }
    
    private void makeMaps() {
        final boolean[] inUse = this.data.inUse;
        final byte[] seqToUnseq = this.data.seqToUnseq;
        int nInUseShadow = 0;
        for (int i = 0; i < 256; ++i) {
            if (inUse[i]) {
                seqToUnseq[nInUseShadow++] = (byte)i;
            }
        }
        this.nInUse = nInUseShadow;
    }
    
    public CBZip2InputStream(final InputStream in, final SplittableCompressionCodec.READ_MODE readMode) throws IOException {
        this(in, readMode, false);
    }
    
    private CBZip2InputStream(final InputStream in, final SplittableCompressionCodec.READ_MODE readMode, final boolean skipDecompression) throws IOException {
        this.readMode = SplittableCompressionCodec.READ_MODE.CONTINUOUS;
        this.reportedBytesReadFromCompressedStream = 0L;
        this.bytesReadFromCompressedStream = 0L;
        this.lazyInitialization = false;
        this.array = new byte[1];
        this.blockRandomised = false;
        this.crc = new CRC();
        this.currentChar = -1;
        this.currentState = STATE.START_BLOCK_STATE;
        this.skipResult = false;
        this.skipDecompression = false;
        final int blockSize = 57;
        this.blockSize100k = blockSize - 48;
        this.in = new BufferedInputStream(in, 9216);
        this.readMode = readMode;
        this.skipDecompression = skipDecompression;
        if (readMode == SplittableCompressionCodec.READ_MODE.CONTINUOUS) {
            this.currentState = STATE.START_BLOCK_STATE;
            if (!(this.lazyInitialization = (in.available() == 0))) {
                this.init();
            }
        }
        else if (readMode == SplittableCompressionCodec.READ_MODE.BYBLOCK) {
            this.currentState = STATE.NO_PROCESS_STATE;
            this.skipResult = this.skipToNextMarker(54156738319193L, 48);
            if (!skipDecompression) {
                this.changeStateToProcessABlock();
            }
        }
    }
    
    public static long numberOfBytesTillNextMarker(final InputStream in) throws IOException {
        final CBZip2InputStream anObject = new CBZip2InputStream(in, SplittableCompressionCodec.READ_MODE.BYBLOCK, true);
        return anObject.getProcessedByteCount();
    }
    
    public CBZip2InputStream(final InputStream in) throws IOException {
        this(in, SplittableCompressionCodec.READ_MODE.CONTINUOUS);
    }
    
    private void changeStateToProcessABlock() throws IOException {
        if (this.skipResult) {
            this.initBlock();
            this.setupBlock();
        }
        else {
            this.currentState = STATE.EOF;
        }
    }
    
    @Override
    public int read() throws IOException {
        if (this.in != null) {
            final int result = this.read(this.array, 0, 1);
            final int value = 0xFF & this.array[0];
            return (result > 0) ? value : result;
        }
        throw new IOException("stream closed");
    }
    
    @Override
    public int read(final byte[] dest, final int offs, final int len) throws IOException {
        if (offs < 0) {
            throw new IndexOutOfBoundsException("offs(" + offs + ") < 0.");
        }
        if (len < 0) {
            throw new IndexOutOfBoundsException("len(" + len + ") < 0.");
        }
        if (offs + len > dest.length) {
            throw new IndexOutOfBoundsException("offs(" + offs + ") + len(" + len + ") > dest.length(" + dest.length + ").");
        }
        if (this.in == null) {
            throw new IOException("stream closed");
        }
        if (this.lazyInitialization) {
            this.init();
            this.lazyInitialization = false;
        }
        if (this.skipDecompression) {
            this.changeStateToProcessABlock();
            this.skipDecompression = false;
        }
        int hi;
        int destOffs;
        int b;
        for (hi = offs + len, destOffs = offs, b = 0; destOffs < hi && (b = this.read0()) >= 0; dest[destOffs++] = (byte)b) {}
        int result = destOffs - offs;
        if (result == 0) {
            result = b;
            this.skipResult = this.skipToNextMarker(54156738319193L, 48);
            this.changeStateToProcessABlock();
        }
        return result;
    }
    
    private int read0() throws IOException {
        final int retChar = this.currentChar;
        switch (this.currentState) {
            case EOF: {
                return -1;
            }
            case NO_PROCESS_STATE: {
                return -2;
            }
            case START_BLOCK_STATE: {
                throw new IllegalStateException();
            }
            case RAND_PART_A_STATE: {
                throw new IllegalStateException();
            }
            case RAND_PART_B_STATE: {
                this.setupRandPartB();
                break;
            }
            case RAND_PART_C_STATE: {
                this.setupRandPartC();
                break;
            }
            case NO_RAND_PART_A_STATE: {
                throw new IllegalStateException();
            }
            case NO_RAND_PART_B_STATE: {
                this.setupNoRandPartB();
                break;
            }
            case NO_RAND_PART_C_STATE: {
                this.setupNoRandPartC();
                break;
            }
            default: {
                throw new IllegalStateException();
            }
        }
        return retChar;
    }
    
    private void init() throws IOException {
        final int magic2 = this.readAByte(this.in);
        if (magic2 != 104) {
            throw new IOException("Stream is not BZip2 formatted: expected 'h' as first byte but got '" + (char)magic2 + "'");
        }
        final int blockSize = this.readAByte(this.in);
        if (blockSize < 49 || blockSize > 57) {
            throw new IOException("Stream is not BZip2 formatted: illegal blocksize " + (char)blockSize);
        }
        this.blockSize100k = blockSize - 48;
        this.initBlock();
        this.setupBlock();
    }
    
    private void initBlock() throws IOException {
        if (this.readMode == SplittableCompressionCodec.READ_MODE.BYBLOCK) {
            this.storedBlockCRC = this.bsGetInt();
            this.blockRandomised = (this.bsR(1L) == 1L);
            if (this.data == null) {
                this.data = new Data(this.blockSize100k);
            }
            this.getAndMoveToFrontDecode();
            this.crc.initialiseCRC();
            this.currentState = STATE.START_BLOCK_STATE;
            return;
        }
        final char magic0 = this.bsGetUByte();
        final char magic2 = this.bsGetUByte();
        final char magic3 = this.bsGetUByte();
        final char magic4 = this.bsGetUByte();
        final char magic5 = this.bsGetUByte();
        final char magic6 = this.bsGetUByte();
        if (magic0 == '\u0017' && magic2 == 'r' && magic3 == 'E' && magic4 == '8' && magic5 == 'P' && magic6 == '\u0090') {
            this.complete();
        }
        else {
            if (magic0 != '1' || magic2 != 'A' || magic3 != 'Y' || magic4 != '&' || magic5 != 'S' || magic6 != 'Y') {
                this.currentState = STATE.EOF;
                throw new IOException("bad block header");
            }
            this.storedBlockCRC = this.bsGetInt();
            this.blockRandomised = (this.bsR(1L) == 1L);
            if (this.data == null) {
                this.data = new Data(this.blockSize100k);
            }
            this.getAndMoveToFrontDecode();
            this.crc.initialiseCRC();
            this.currentState = STATE.START_BLOCK_STATE;
        }
    }
    
    private void endBlock() throws IOException {
        this.computedBlockCRC = this.crc.getFinalCRC();
        if (this.storedBlockCRC != this.computedBlockCRC) {
            this.computedCombinedCRC = (this.storedCombinedCRC << 1 | this.storedCombinedCRC >>> 31);
            this.computedCombinedCRC ^= this.storedBlockCRC;
            this.reportCRCError();
        }
        this.computedCombinedCRC = (this.computedCombinedCRC << 1 | this.computedCombinedCRC >>> 31);
        this.computedCombinedCRC ^= this.computedBlockCRC;
    }
    
    private void complete() throws IOException {
        this.storedCombinedCRC = this.bsGetInt();
        this.currentState = STATE.EOF;
        this.data = null;
        if (this.storedCombinedCRC != this.computedCombinedCRC) {
            this.reportCRCError();
        }
    }
    
    @Override
    public void close() throws IOException {
        final InputStream inShadow = this.in;
        if (inShadow != null) {
            try {
                if (inShadow != System.in) {
                    inShadow.close();
                }
            }
            finally {
                this.data = null;
                this.in = null;
            }
        }
    }
    
    private long bsR(final long n) throws IOException {
        long bsLiveShadow = this.bsLive;
        long bsBuffShadow = this.bsBuff;
        if (bsLiveShadow < n) {
            final InputStream inShadow = this.in;
            do {
                final int thech = this.readAByte(inShadow);
                if (thech < 0) {
                    throw new IOException("unexpected end of stream");
                }
                bsBuffShadow = (bsBuffShadow << 8 | (long)thech);
                bsLiveShadow += 8L;
            } while (bsLiveShadow < n);
            this.bsBuff = bsBuffShadow;
        }
        this.bsLive = bsLiveShadow - n;
        return bsBuffShadow >> (int)(bsLiveShadow - n) & (1L << (int)n) - 1L;
    }
    
    private boolean bsGetBit() throws IOException {
        long bsLiveShadow = this.bsLive;
        long bsBuffShadow = this.bsBuff;
        if (bsLiveShadow < 1L) {
            final int thech = this.readAByte(this.in);
            if (thech < 0) {
                throw new IOException("unexpected end of stream");
            }
            bsBuffShadow = (bsBuffShadow << 8 | (long)thech);
            bsLiveShadow += 8L;
            this.bsBuff = bsBuffShadow;
        }
        this.bsLive = bsLiveShadow - 1L;
        return (bsBuffShadow >> (int)(bsLiveShadow - 1L) & 0x1L) != 0x0L;
    }
    
    private char bsGetUByte() throws IOException {
        return (char)this.bsR(8L);
    }
    
    private int bsGetInt() throws IOException {
        return (int)(((this.bsR(8L) << 8 | this.bsR(8L)) << 8 | this.bsR(8L)) << 8 | this.bsR(8L));
    }
    
    private static void hbCreateDecodeTables(final int[] limit, final int[] base, final int[] perm, final char[] length, final int minLen, final int maxLen, final int alphaSize) {
        int i = minLen;
        int pp = 0;
        while (i <= maxLen) {
            for (int j = 0; j < alphaSize; ++j) {
                if (length[j] == i) {
                    perm[pp++] = j;
                }
            }
            ++i;
        }
        i = 23;
        while (--i > 0) {
            limit[i] = (base[i] = 0);
        }
        for (i = 0; i < alphaSize; ++i) {
            final int n = length[i] + '\u0001';
            ++base[n];
        }
        i = 1;
        int b = base[0];
        while (i < 23) {
            b += base[i];
            base[i] = b;
            ++i;
        }
        i = minLen;
        int vec = 0;
        int b2 = base[i];
        while (i <= maxLen) {
            final int nb = base[i + 1];
            vec += nb - b2;
            b2 = nb;
            limit[i] = vec - 1;
            vec <<= 1;
            ++i;
        }
        for (i = minLen + 1; i <= maxLen; ++i) {
            base[i] = (limit[i - 1] + 1 << 1) - base[i];
        }
    }
    
    private void recvDecodingTables() throws IOException {
        final Data dataShadow = this.data;
        final boolean[] inUse = dataShadow.inUse;
        final byte[] pos = dataShadow.recvDecodingTables_pos;
        final byte[] selector = dataShadow.selector;
        final byte[] selectorMtf = dataShadow.selectorMtf;
        int inUse2 = 0;
        for (int i = 0; i < 16; ++i) {
            if (this.bsGetBit()) {
                inUse2 |= 1 << i;
            }
        }
        int i = 256;
        while (--i >= 0) {
            inUse[i] = false;
        }
        for (i = 0; i < 16; ++i) {
            if ((inUse2 & 1 << i) != 0x0) {
                final int i2 = i << 4;
                for (int j = 0; j < 16; ++j) {
                    if (this.bsGetBit()) {
                        inUse[i2 + j] = true;
                    }
                }
            }
        }
        this.makeMaps();
        final int alphaSize = this.nInUse + 2;
        final int nGroups = (int)this.bsR(3L);
        final int nSelectors = (int)this.bsR(15L);
        for (int k = 0; k < nSelectors; ++k) {
            int l = 0;
            while (this.bsGetBit()) {
                ++l;
            }
            selectorMtf[k] = (byte)l;
        }
        int v = nGroups;
        while (--v >= 0) {
            pos[v] = (byte)v;
        }
        for (int k = 0; k < nSelectors; ++k) {
            int v2 = selectorMtf[k] & 0xFF;
            final byte tmp = pos[v2];
            while (v2 > 0) {
                pos[v2] = pos[v2 - 1];
                --v2;
            }
            selector[k] = (pos[0] = tmp);
        }
        final char[][] len = dataShadow.temp_charArray2d;
        for (int t = 0; t < nGroups; ++t) {
            int curr = (int)this.bsR(5L);
            final char[] len_t = len[t];
            for (int m = 0; m < alphaSize; ++m) {
                while (this.bsGetBit()) {
                    curr += (this.bsGetBit() ? -1 : 1);
                }
                len_t[m] = (char)curr;
            }
        }
        this.createHuffmanDecodingTables(alphaSize, nGroups);
    }
    
    private void createHuffmanDecodingTables(final int alphaSize, final int nGroups) {
        final Data dataShadow = this.data;
        final char[][] len = dataShadow.temp_charArray2d;
        final int[] minLens = dataShadow.minLens;
        final int[][] limit = dataShadow.limit;
        final int[][] base = dataShadow.base;
        final int[][] perm = dataShadow.perm;
        for (int t = 0; t < nGroups; ++t) {
            int minLen = 32;
            int maxLen = 0;
            final char[] len_t = len[t];
            int i = alphaSize;
            while (--i >= 0) {
                final char lent = len_t[i];
                if (lent > maxLen) {
                    maxLen = lent;
                }
                if (lent < minLen) {
                    minLen = lent;
                }
            }
            hbCreateDecodeTables(limit[t], base[t], perm[t], len[t], minLen, maxLen, alphaSize);
            minLens[t] = minLen;
        }
    }
    
    private void getAndMoveToFrontDecode() throws IOException {
        this.origPtr = (int)this.bsR(24L);
        this.recvDecodingTables();
        final InputStream inShadow = this.in;
        final Data dataShadow = this.data;
        final byte[] ll8 = dataShadow.ll8;
        final int[] unzftab = dataShadow.unzftab;
        final byte[] selector = dataShadow.selector;
        final byte[] seqToUnseq = dataShadow.seqToUnseq;
        final char[] yy = dataShadow.getAndMoveToFrontDecode_yy;
        final int[] minLens = dataShadow.minLens;
        final int[][] limit = dataShadow.limit;
        final int[][] base = dataShadow.base;
        final int[][] perm = dataShadow.perm;
        final int limitLast = this.blockSize100k * 100000;
        int i = 256;
        while (--i >= 0) {
            yy[i] = (char)i;
            unzftab[i] = 0;
        }
        int groupNo = 0;
        int groupPos = 49;
        final int eob = this.nInUse + 1;
        int nextSym = this.getAndMoveToFrontDecode0(0);
        int bsBuffShadow = (int)this.bsBuff;
        int bsLiveShadow = (int)this.bsLive;
        int lastShadow = -1;
        int zt = selector[groupNo] & 0xFF;
        int[] base_zt = base[zt];
        int[] limit_zt = limit[zt];
        int[] perm_zt = perm[zt];
        int minLens_zt = minLens[zt];
        while (nextSym != eob) {
            if (nextSym == 0 || nextSym == 1) {
                int s = -1;
                int n = 1;
                while (true) {
                    if (nextSym == 0) {
                        s += n;
                    }
                    else if (nextSym == 1) {
                        s += n << 1;
                    }
                    else {
                        final byte ch = seqToUnseq[yy[0]];
                        final int[] array = unzftab;
                        final int n2 = ch & 0xFF;
                        array[n2] += s + 1;
                        while (s-- >= 0) {
                            ll8[++lastShadow] = ch;
                        }
                        if (lastShadow >= limitLast) {
                            throw new IOException("block overrun");
                        }
                        break;
                    }
                    if (groupPos == 0) {
                        groupPos = 49;
                        zt = (selector[++groupNo] & 0xFF);
                        base_zt = base[zt];
                        limit_zt = limit[zt];
                        perm_zt = perm[zt];
                        minLens_zt = minLens[zt];
                    }
                    else {
                        --groupPos;
                    }
                    int zn;
                    for (zn = minLens_zt; bsLiveShadow < zn; bsLiveShadow += 8) {
                        final int thech = this.readAByte(inShadow);
                        if (thech < 0) {
                            throw new IOException("unexpected end of stream");
                        }
                        bsBuffShadow = (bsBuffShadow << 8 | thech);
                    }
                    long zvec;
                    int thech2;
                    for (zvec = (bsBuffShadow >> bsLiveShadow - zn & (1 << zn) - 1), bsLiveShadow -= zn; zvec > limit_zt[zn]; zvec = (zvec << 1 | (long)(bsBuffShadow >> bsLiveShadow & 0x1))) {
                        ++zn;
                        while (bsLiveShadow < 1) {
                            thech2 = this.readAByte(inShadow);
                            if (thech2 < 0) {
                                throw new IOException("unexpected end of stream");
                            }
                            bsBuffShadow = (bsBuffShadow << 8 | thech2);
                            bsLiveShadow += 8;
                        }
                        --bsLiveShadow;
                    }
                    nextSym = perm_zt[(int)(zvec - base_zt[zn])];
                    n <<= 1;
                }
            }
            else {
                if (++lastShadow >= limitLast) {
                    throw new IOException("block overrun");
                }
                final char tmp = yy[nextSym - 1];
                final int[] array2 = unzftab;
                final int n3 = seqToUnseq[tmp] & 0xFF;
                ++array2[n3];
                ll8[lastShadow] = seqToUnseq[tmp];
                if (nextSym <= 16) {
                    for (int j = nextSym - 1; j > 0; yy[j] = yy[--j]) {}
                }
                else {
                    System.arraycopy(yy, 0, yy, 1, nextSym - 1);
                }
                yy[0] = tmp;
                if (groupPos == 0) {
                    groupPos = 49;
                    zt = (selector[++groupNo] & 0xFF);
                    base_zt = base[zt];
                    limit_zt = limit[zt];
                    perm_zt = perm[zt];
                    minLens_zt = minLens[zt];
                }
                else {
                    --groupPos;
                }
                int zn2;
                for (zn2 = minLens_zt; bsLiveShadow < zn2; bsLiveShadow += 8) {
                    final int thech3 = this.readAByte(inShadow);
                    if (thech3 < 0) {
                        throw new IOException("unexpected end of stream");
                    }
                    bsBuffShadow = (bsBuffShadow << 8 | thech3);
                }
                int zvec2;
                for (zvec2 = (bsBuffShadow >> bsLiveShadow - zn2 & (1 << zn2) - 1), bsLiveShadow -= zn2; zvec2 > limit_zt[zn2]; zvec2 = (zvec2 << 1 | (bsBuffShadow >> bsLiveShadow & 0x1))) {
                    ++zn2;
                    while (bsLiveShadow < 1) {
                        final int thech = this.readAByte(inShadow);
                        if (thech < 0) {
                            throw new IOException("unexpected end of stream");
                        }
                        bsBuffShadow = (bsBuffShadow << 8 | thech);
                        bsLiveShadow += 8;
                    }
                    --bsLiveShadow;
                }
                nextSym = perm_zt[zvec2 - base_zt[zn2]];
            }
        }
        this.last = lastShadow;
        this.bsLive = bsLiveShadow;
        this.bsBuff = bsBuffShadow;
    }
    
    private int getAndMoveToFrontDecode0(final int groupNo) throws IOException {
        final InputStream inShadow = this.in;
        final Data dataShadow = this.data;
        final int zt = dataShadow.selector[groupNo] & 0xFF;
        int[] limit_zt;
        int zn;
        int zvec;
        int bsLiveShadow;
        int bsBuffShadow;
        int thech;
        for (limit_zt = dataShadow.limit[zt], zn = dataShadow.minLens[zt], zvec = (int)this.bsR(zn), bsLiveShadow = (int)this.bsLive, bsBuffShadow = (int)this.bsBuff; zvec > limit_zt[zn]; zvec = (zvec << 1 | (bsBuffShadow >> bsLiveShadow & 0x1))) {
            ++zn;
            while (bsLiveShadow < 1) {
                thech = this.readAByte(inShadow);
                if (thech < 0) {
                    throw new IOException("unexpected end of stream");
                }
                bsBuffShadow = (bsBuffShadow << 8 | thech);
                bsLiveShadow += 8;
            }
            --bsLiveShadow;
        }
        this.bsLive = bsLiveShadow;
        this.bsBuff = bsBuffShadow;
        return dataShadow.perm[zt][zvec - dataShadow.base[zt][zn]];
    }
    
    private void setupBlock() throws IOException {
        if (this.data == null) {
            return;
        }
        final int[] cftab = this.data.cftab;
        final int[] tt = this.data.initTT(this.last + 1);
        final byte[] ll8 = this.data.ll8;
        cftab[0] = 0;
        System.arraycopy(this.data.unzftab, 0, cftab, 1, 256);
        int i = 1;
        int c = cftab[0];
        while (i <= 256) {
            c += cftab[i];
            cftab[i] = c;
            ++i;
        }
        i = 0;
        for (int lastShadow = this.last; i <= lastShadow; ++i) {
            tt[cftab[ll8[i] & 0xFF]++] = i;
        }
        if (this.origPtr < 0 || this.origPtr >= tt.length) {
            throw new IOException("stream corrupted");
        }
        this.su_tPos = tt[this.origPtr];
        this.su_count = 0;
        this.su_i2 = 0;
        this.su_ch2 = 256;
        if (this.blockRandomised) {
            this.su_rNToGo = 0;
            this.su_rTPos = 0;
            this.setupRandPartA();
        }
        else {
            this.setupNoRandPartA();
        }
    }
    
    private void setupRandPartA() throws IOException {
        if (this.su_i2 <= this.last) {
            this.su_chPrev = this.su_ch2;
            int su_ch2Shadow = this.data.ll8[this.su_tPos] & 0xFF;
            this.su_tPos = this.data.tt[this.su_tPos];
            if (this.su_rNToGo == 0) {
                this.su_rNToGo = BZip2Constants.rNums[this.su_rTPos] - 1;
                if (++this.su_rTPos == 512) {
                    this.su_rTPos = 0;
                }
            }
            else {
                --this.su_rNToGo;
            }
            su_ch2Shadow = (this.su_ch2 = (su_ch2Shadow ^ ((this.su_rNToGo == 1) ? 1 : 0)));
            ++this.su_i2;
            this.currentChar = su_ch2Shadow;
            this.currentState = STATE.RAND_PART_B_STATE;
            this.crc.updateCRC(su_ch2Shadow);
        }
        else {
            this.endBlock();
            if (this.readMode == SplittableCompressionCodec.READ_MODE.CONTINUOUS) {
                this.initBlock();
                this.setupBlock();
            }
            else if (this.readMode == SplittableCompressionCodec.READ_MODE.BYBLOCK) {
                this.currentState = STATE.NO_PROCESS_STATE;
            }
        }
    }
    
    private void setupNoRandPartA() throws IOException {
        if (this.su_i2 <= this.last) {
            this.su_chPrev = this.su_ch2;
            final int su_ch2Shadow = this.data.ll8[this.su_tPos] & 0xFF;
            this.su_ch2 = su_ch2Shadow;
            this.su_tPos = this.data.tt[this.su_tPos];
            ++this.su_i2;
            this.currentChar = su_ch2Shadow;
            this.currentState = STATE.NO_RAND_PART_B_STATE;
            this.crc.updateCRC(su_ch2Shadow);
        }
        else {
            this.currentState = STATE.NO_RAND_PART_A_STATE;
            this.endBlock();
            if (this.readMode == SplittableCompressionCodec.READ_MODE.CONTINUOUS) {
                this.initBlock();
                this.setupBlock();
            }
            else if (this.readMode == SplittableCompressionCodec.READ_MODE.BYBLOCK) {
                this.currentState = STATE.NO_PROCESS_STATE;
            }
        }
    }
    
    private void setupRandPartB() throws IOException {
        if (this.su_ch2 != this.su_chPrev) {
            this.currentState = STATE.RAND_PART_A_STATE;
            this.su_count = 1;
            this.setupRandPartA();
        }
        else if (++this.su_count >= 4) {
            this.su_z = (char)(this.data.ll8[this.su_tPos] & 0xFF);
            this.su_tPos = this.data.tt[this.su_tPos];
            if (this.su_rNToGo == 0) {
                this.su_rNToGo = BZip2Constants.rNums[this.su_rTPos] - 1;
                if (++this.su_rTPos == 512) {
                    this.su_rTPos = 0;
                }
            }
            else {
                --this.su_rNToGo;
            }
            this.su_j2 = 0;
            this.currentState = STATE.RAND_PART_C_STATE;
            if (this.su_rNToGo == 1) {
                this.su_z ^= '\u0001';
            }
            this.setupRandPartC();
        }
        else {
            this.currentState = STATE.RAND_PART_A_STATE;
            this.setupRandPartA();
        }
    }
    
    private void setupRandPartC() throws IOException {
        if (this.su_j2 < this.su_z) {
            this.currentChar = this.su_ch2;
            this.crc.updateCRC(this.su_ch2);
            ++this.su_j2;
        }
        else {
            this.currentState = STATE.RAND_PART_A_STATE;
            ++this.su_i2;
            this.su_count = 0;
            this.setupRandPartA();
        }
    }
    
    private void setupNoRandPartB() throws IOException {
        if (this.su_ch2 != this.su_chPrev) {
            this.su_count = 1;
            this.setupNoRandPartA();
        }
        else if (++this.su_count >= 4) {
            this.su_z = (char)(this.data.ll8[this.su_tPos] & 0xFF);
            this.su_tPos = this.data.tt[this.su_tPos];
            this.su_j2 = 0;
            this.setupNoRandPartC();
        }
        else {
            this.setupNoRandPartA();
        }
    }
    
    private void setupNoRandPartC() throws IOException {
        if (this.su_j2 < this.su_z) {
            final int su_ch2Shadow = this.su_ch2;
            this.currentChar = su_ch2Shadow;
            this.crc.updateCRC(su_ch2Shadow);
            ++this.su_j2;
            this.currentState = STATE.NO_RAND_PART_C_STATE;
        }
        else {
            ++this.su_i2;
            this.su_count = 0;
            this.setupNoRandPartA();
        }
    }
    
    public enum STATE
    {
        EOF, 
        START_BLOCK_STATE, 
        RAND_PART_A_STATE, 
        RAND_PART_B_STATE, 
        RAND_PART_C_STATE, 
        NO_RAND_PART_A_STATE, 
        NO_RAND_PART_B_STATE, 
        NO_RAND_PART_C_STATE, 
        NO_PROCESS_STATE;
    }
    
    private static final class Data
    {
        final boolean[] inUse;
        final byte[] seqToUnseq;
        final byte[] selector;
        final byte[] selectorMtf;
        final int[] unzftab;
        final int[][] limit;
        final int[][] base;
        final int[][] perm;
        final int[] minLens;
        final int[] cftab;
        final char[] getAndMoveToFrontDecode_yy;
        final char[][] temp_charArray2d;
        final byte[] recvDecodingTables_pos;
        int[] tt;
        byte[] ll8;
        
        Data(final int blockSize100k) {
            this.inUse = new boolean[256];
            this.seqToUnseq = new byte[256];
            this.selector = new byte[18002];
            this.selectorMtf = new byte[18002];
            this.unzftab = new int[256];
            this.limit = new int[6][258];
            this.base = new int[6][258];
            this.perm = new int[6][258];
            this.minLens = new int[6];
            this.cftab = new int[257];
            this.getAndMoveToFrontDecode_yy = new char[256];
            this.temp_charArray2d = new char[6][258];
            this.recvDecodingTables_pos = new byte[6];
            this.ll8 = new byte[blockSize100k * 100000];
        }
        
        final int[] initTT(final int length) {
            int[] ttShadow = this.tt;
            if (ttShadow == null || ttShadow.length < length) {
                ttShadow = (this.tt = new int[length]);
            }
            return ttShadow;
        }
    }
}
