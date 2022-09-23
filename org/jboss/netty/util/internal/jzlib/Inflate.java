// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.util.internal.jzlib;

final class Inflate
{
    private static final int METHOD = 0;
    private static final int FLAG = 1;
    private static final int DICT4 = 2;
    private static final int DICT3 = 3;
    private static final int DICT2 = 4;
    private static final int DICT1 = 5;
    private static final int DICT0 = 6;
    private static final int BLOCKS = 7;
    private static final int CHECK4 = 8;
    private static final int CHECK3 = 9;
    private static final int CHECK2 = 10;
    private static final int CHECK1 = 11;
    private static final int DONE = 12;
    private static final int BAD = 13;
    private static final int GZIP_ID1 = 14;
    private static final int GZIP_ID2 = 15;
    private static final int GZIP_CM = 16;
    private static final int GZIP_FLG = 17;
    private static final int GZIP_MTIME_XFL_OS = 18;
    private static final int GZIP_XLEN = 19;
    private static final int GZIP_FEXTRA = 20;
    private static final int GZIP_FNAME = 21;
    private static final int GZIP_FCOMMENT = 22;
    private static final int GZIP_FHCRC = 23;
    private static final int GZIP_CRC32 = 24;
    private static final int GZIP_ISIZE = 25;
    private int mode;
    private int method;
    private final long[] was;
    private long need;
    private int marker;
    private JZlib.WrapperType wrapperType;
    private int wbits;
    private InfBlocks blocks;
    private int gzipFlag;
    private int gzipBytesToRead;
    private int gzipXLen;
    private int gzipUncompressedBytes;
    private int gzipCRC32;
    private int gzipISize;
    private static final byte[] mark;
    
    Inflate() {
        this.was = new long[1];
    }
    
    private int inflateReset(final ZStream z) {
        if (z == null || z.istate == null) {
            return -2;
        }
        final long n = 0L;
        z.total_out = n;
        z.total_in = n;
        z.msg = null;
        switch (this.wrapperType) {
            case NONE: {
                z.istate.mode = 7;
                break;
            }
            case ZLIB:
            case ZLIB_OR_NONE: {
                z.istate.mode = 0;
                break;
            }
            case GZIP: {
                z.istate.mode = 14;
                break;
            }
        }
        z.istate.blocks.reset(z, null);
        return this.gzipUncompressedBytes = 0;
    }
    
    int inflateEnd(final ZStream z) {
        if (this.blocks != null) {
            this.blocks.free(z);
        }
        this.blocks = null;
        return 0;
    }
    
    int inflateInit(final ZStream z, final int w, final JZlib.WrapperType wrapperType) {
        z.msg = null;
        this.blocks = null;
        this.wrapperType = wrapperType;
        if (w < 0) {
            throw new IllegalArgumentException("w: " + w);
        }
        if (w < 8 || w > 15) {
            this.inflateEnd(z);
            return -2;
        }
        this.wbits = w;
        z.istate.blocks = new InfBlocks(z, (z.istate.wrapperType == JZlib.WrapperType.NONE) ? null : this, 1 << w);
        this.inflateReset(z);
        return 0;
    }
    
    int inflate(final ZStream z, int f) {
        if (z == null || z.istate == null || z.next_in == null) {
            return -2;
        }
        f = ((f == 4) ? -5 : 0);
        int r = -5;
        Label_0749: {
            Label_0670: {
                Label_0591: {
                Label_0517:
                    while (true) {
                        Label_2232: {
                            switch (z.istate.mode) {
                                case 0: {
                                    if (z.avail_in == 0) {
                                        return r;
                                    }
                                    if (z.istate.wrapperType == JZlib.WrapperType.ZLIB_OR_NONE) {
                                        if ((z.next_in[z.next_in_index] & 0xF) != 0x8 || (z.next_in[z.next_in_index] >> 4) + 8 > z.istate.wbits) {
                                            z.istate.wrapperType = JZlib.WrapperType.NONE;
                                            z.istate.mode = 7;
                                            continue;
                                        }
                                        z.istate.wrapperType = JZlib.WrapperType.ZLIB;
                                    }
                                    r = f;
                                    --z.avail_in;
                                    ++z.total_in;
                                    final Inflate istate = z.istate;
                                    final byte method = z.next_in[z.next_in_index++];
                                    istate.method = method;
                                    if ((method & 0xF) != 0x8) {
                                        z.istate.mode = 13;
                                        z.msg = "unknown compression method";
                                        z.istate.marker = 5;
                                        continue;
                                    }
                                    if ((z.istate.method >> 4) + 8 > z.istate.wbits) {
                                        z.istate.mode = 13;
                                        z.msg = "invalid window size";
                                        z.istate.marker = 5;
                                        continue;
                                    }
                                    z.istate.mode = 1;
                                }
                                case 1: {
                                    if (z.avail_in == 0) {
                                        return r;
                                    }
                                    r = f;
                                    --z.avail_in;
                                    ++z.total_in;
                                    final int b = z.next_in[z.next_in_index++] & 0xFF;
                                    if (((z.istate.method << 8) + b) % 31 != 0) {
                                        z.istate.mode = 13;
                                        z.msg = "incorrect header check";
                                        z.istate.marker = 5;
                                        continue;
                                    }
                                    if ((b & 0x20) == 0x0) {
                                        z.istate.mode = 7;
                                        continue;
                                    }
                                    z.istate.mode = 2;
                                    break Label_0517;
                                }
                                case 2: {
                                    break Label_0517;
                                }
                                case 3: {
                                    break Label_0591;
                                }
                                case 4: {
                                    break Label_0670;
                                }
                                case 5: {
                                    break Label_0749;
                                }
                                case 6: {
                                    z.istate.mode = 13;
                                    z.msg = "need dictionary";
                                    z.istate.marker = 0;
                                    return -2;
                                }
                                case 7: {
                                    final int old_next_out_index = z.next_out_index;
                                    try {
                                        r = z.istate.blocks.proc(z, r);
                                        if (r == -3) {
                                            z.istate.mode = 13;
                                            z.istate.marker = 0;
                                            continue;
                                        }
                                        if (r == 0) {
                                            r = f;
                                        }
                                        if (r != 1) {
                                            return r;
                                        }
                                        r = f;
                                        z.istate.blocks.reset(z, z.istate.was);
                                    }
                                    finally {
                                        final int decompressedBytes = z.next_out_index - old_next_out_index;
                                        this.gzipUncompressedBytes += decompressedBytes;
                                        z.crc32 = CRC32.crc32(z.crc32, z.next_out, old_next_out_index, decompressedBytes);
                                    }
                                    if (z.istate.wrapperType == JZlib.WrapperType.NONE) {
                                        z.istate.mode = 12;
                                        continue;
                                    }
                                    if (z.istate.wrapperType == JZlib.WrapperType.ZLIB) {
                                        z.istate.mode = 8;
                                        break Label_2232;
                                    }
                                    if (z.istate.wrapperType == JZlib.WrapperType.GZIP) {
                                        this.gzipCRC32 = 0;
                                        this.gzipISize = 0;
                                        this.gzipBytesToRead = 4;
                                        z.istate.mode = 24;
                                        continue;
                                    }
                                    z.istate.mode = 13;
                                    z.msg = "unexpected state";
                                    z.istate.marker = 0;
                                    continue;
                                }
                                case 8: {
                                    if (z.avail_in == 0) {
                                        return r;
                                    }
                                    r = f;
                                    --z.avail_in;
                                    ++z.total_in;
                                    z.istate.need = ((long)((z.next_in[z.next_in_index++] & 0xFF) << 24) & 0xFF000000L);
                                    z.istate.mode = 9;
                                }
                                case 9: {
                                    if (z.avail_in == 0) {
                                        return r;
                                    }
                                    r = f;
                                    --z.avail_in;
                                    ++z.total_in;
                                    final Inflate istate2 = z.istate;
                                    istate2.need += ((long)((z.next_in[z.next_in_index++] & 0xFF) << 16) & 0xFF0000L);
                                    z.istate.mode = 10;
                                }
                                case 10: {
                                    if (z.avail_in == 0) {
                                        return r;
                                    }
                                    r = f;
                                    --z.avail_in;
                                    ++z.total_in;
                                    final Inflate istate3 = z.istate;
                                    istate3.need += ((long)((z.next_in[z.next_in_index++] & 0xFF) << 8) & 0xFF00L);
                                    z.istate.mode = 11;
                                }
                                case 11: {
                                    if (z.avail_in == 0) {
                                        return r;
                                    }
                                    r = f;
                                    --z.avail_in;
                                    ++z.total_in;
                                    final Inflate istate4 = z.istate;
                                    istate4.need += ((long)z.next_in[z.next_in_index++] & 0xFFL);
                                    if ((int)z.istate.was[0] != (int)z.istate.need) {
                                        z.istate.mode = 13;
                                        z.msg = "incorrect data check";
                                        z.istate.marker = 5;
                                        continue;
                                    }
                                    z.istate.mode = 12;
                                    return 1;
                                }
                                case 12: {
                                    return 1;
                                }
                                case 13: {
                                    return -3;
                                }
                                case 14: {
                                    if (z.avail_in == 0) {
                                        return r;
                                    }
                                    r = f;
                                    --z.avail_in;
                                    ++z.total_in;
                                    if ((z.next_in[z.next_in_index++] & 0xFF) != 0x1F) {
                                        z.istate.mode = 13;
                                        z.msg = "not a gzip stream";
                                        z.istate.marker = 5;
                                        continue;
                                    }
                                    z.istate.mode = 15;
                                }
                                case 15: {
                                    if (z.avail_in == 0) {
                                        return r;
                                    }
                                    r = f;
                                    --z.avail_in;
                                    ++z.total_in;
                                    if ((z.next_in[z.next_in_index++] & 0xFF) != 0x8B) {
                                        z.istate.mode = 13;
                                        z.msg = "not a gzip stream";
                                        z.istate.marker = 5;
                                        continue;
                                    }
                                    z.istate.mode = 16;
                                }
                                case 16: {
                                    if (z.avail_in == 0) {
                                        return r;
                                    }
                                    r = f;
                                    --z.avail_in;
                                    ++z.total_in;
                                    if ((z.next_in[z.next_in_index++] & 0xFF) != 0x8) {
                                        z.istate.mode = 13;
                                        z.msg = "unknown compression method";
                                        z.istate.marker = 5;
                                        continue;
                                    }
                                    z.istate.mode = 17;
                                }
                                case 17: {
                                    if (z.avail_in == 0) {
                                        return r;
                                    }
                                    r = f;
                                    --z.avail_in;
                                    ++z.total_in;
                                    this.gzipFlag = (z.next_in[z.next_in_index++] & 0xFF);
                                    if ((this.gzipFlag & 0xE2) != 0x0) {
                                        z.istate.mode = 13;
                                        z.msg = "unsupported flag";
                                        z.istate.marker = 5;
                                        continue;
                                    }
                                    this.gzipBytesToRead = 6;
                                    z.istate.mode = 18;
                                }
                                case 18: {
                                    while (this.gzipBytesToRead > 0) {
                                        if (z.avail_in == 0) {
                                            return r;
                                        }
                                        r = f;
                                        --z.avail_in;
                                        ++z.total_in;
                                        ++z.next_in_index;
                                        --this.gzipBytesToRead;
                                    }
                                    z.istate.mode = 19;
                                    this.gzipXLen = 0;
                                    this.gzipBytesToRead = 2;
                                }
                                case 19: {
                                    if ((this.gzipFlag & 0x4) != 0x0) {
                                        while (this.gzipBytesToRead > 0) {
                                            if (z.avail_in == 0) {
                                                return r;
                                            }
                                            r = f;
                                            --z.avail_in;
                                            ++z.total_in;
                                            this.gzipXLen |= (z.next_in[z.next_in_index++] & 0xFF) << (1 - this.gzipBytesToRead) * 8;
                                            --this.gzipBytesToRead;
                                        }
                                        this.gzipBytesToRead = this.gzipXLen;
                                        z.istate.mode = 20;
                                        break Label_2232;
                                    }
                                    z.istate.mode = 21;
                                    continue;
                                }
                                case 20: {
                                    while (this.gzipBytesToRead > 0) {
                                        if (z.avail_in == 0) {
                                            return r;
                                        }
                                        r = f;
                                        --z.avail_in;
                                        ++z.total_in;
                                        ++z.next_in_index;
                                        --this.gzipBytesToRead;
                                    }
                                    z.istate.mode = 21;
                                }
                                case 21: {
                                    Label_2362: {
                                        if ((this.gzipFlag & 0x8) != 0x0) {
                                            while (z.avail_in != 0) {
                                                r = f;
                                                --z.avail_in;
                                                ++z.total_in;
                                                if (z.next_in[z.next_in_index++] == 0) {
                                                    break Label_2362;
                                                }
                                            }
                                            return r;
                                        }
                                    }
                                    z.istate.mode = 22;
                                }
                                case 22: {
                                    Label_2431: {
                                        if ((this.gzipFlag & 0x10) != 0x0) {
                                            while (z.avail_in != 0) {
                                                r = f;
                                                --z.avail_in;
                                                ++z.total_in;
                                                if (z.next_in[z.next_in_index++] == 0) {
                                                    break Label_2431;
                                                }
                                            }
                                            return r;
                                        }
                                    }
                                    this.gzipBytesToRead = 2;
                                    z.istate.mode = 23;
                                }
                                case 23: {
                                    if ((this.gzipFlag & 0x2) != 0x0) {
                                        while (this.gzipBytesToRead > 0) {
                                            if (z.avail_in == 0) {
                                                return r;
                                            }
                                            r = f;
                                            --z.avail_in;
                                            ++z.total_in;
                                            ++z.next_in_index;
                                            --this.gzipBytesToRead;
                                        }
                                    }
                                    z.istate.mode = 7;
                                    continue;
                                }
                                case 24: {
                                    while (this.gzipBytesToRead > 0) {
                                        if (z.avail_in == 0) {
                                            return r;
                                        }
                                        r = f;
                                        --z.avail_in;
                                        ++z.total_in;
                                        --this.gzipBytesToRead;
                                        final Inflate istate5 = z.istate;
                                        istate5.gzipCRC32 |= (z.next_in[z.next_in_index++] & 0xFF) << (3 - this.gzipBytesToRead) * 8;
                                    }
                                    if (z.crc32 != z.istate.gzipCRC32) {
                                        z.istate.mode = 13;
                                        z.msg = "incorrect CRC32 checksum";
                                        z.istate.marker = 5;
                                        continue;
                                    }
                                    this.gzipBytesToRead = 4;
                                    z.istate.mode = 25;
                                }
                                case 25: {
                                    while (this.gzipBytesToRead > 0) {
                                        if (z.avail_in == 0) {
                                            return r;
                                        }
                                        r = f;
                                        --z.avail_in;
                                        ++z.total_in;
                                        --this.gzipBytesToRead;
                                        final Inflate istate6 = z.istate;
                                        istate6.gzipISize |= (z.next_in[z.next_in_index++] & 0xFF) << (3 - this.gzipBytesToRead) * 8;
                                    }
                                    if (this.gzipUncompressedBytes != z.istate.gzipISize) {
                                        z.istate.mode = 13;
                                        z.msg = "incorrect ISIZE checksum";
                                        z.istate.marker = 5;
                                        continue;
                                    }
                                    z.istate.mode = 12;
                                    continue;
                                }
                                default: {
                                    return -2;
                                }
                            }
                        }
                    }
                    if (z.avail_in == 0) {
                        return r;
                    }
                    r = f;
                    --z.avail_in;
                    ++z.total_in;
                    z.istate.need = ((long)((z.next_in[z.next_in_index++] & 0xFF) << 24) & 0xFF000000L);
                    z.istate.mode = 3;
                }
                if (z.avail_in == 0) {
                    return r;
                }
                r = f;
                --z.avail_in;
                ++z.total_in;
                final Inflate istate7 = z.istate;
                istate7.need += ((long)((z.next_in[z.next_in_index++] & 0xFF) << 16) & 0xFF0000L);
                z.istate.mode = 4;
            }
            if (z.avail_in == 0) {
                return r;
            }
            r = f;
            --z.avail_in;
            ++z.total_in;
            final Inflate istate8 = z.istate;
            istate8.need += ((long)((z.next_in[z.next_in_index++] & 0xFF) << 8) & 0xFF00L);
            z.istate.mode = 5;
        }
        if (z.avail_in == 0) {
            return r;
        }
        --z.avail_in;
        ++z.total_in;
        final Inflate istate9 = z.istate;
        istate9.need += ((long)z.next_in[z.next_in_index++] & 0xFFL);
        z.adler = z.istate.need;
        z.istate.mode = 6;
        return 2;
    }
    
    static int inflateSetDictionary(final ZStream z, final byte[] dictionary, final int dictLength) {
        int index = 0;
        int length = dictLength;
        if (z == null || z.istate == null || z.istate.mode != 6) {
            return -2;
        }
        if (Adler32.adler32(1L, dictionary, 0, dictLength) != z.adler) {
            return -3;
        }
        z.adler = Adler32.adler32(0L, null, 0, 0);
        if (length >= 1 << z.istate.wbits) {
            length = (1 << z.istate.wbits) - 1;
            index = dictLength - length;
        }
        z.istate.blocks.set_dictionary(dictionary, index, length);
        z.istate.mode = 7;
        return 0;
    }
    
    int inflateSync(final ZStream z) {
        if (z == null || z.istate == null) {
            return -2;
        }
        if (z.istate.mode != 13) {
            z.istate.mode = 13;
            z.istate.marker = 0;
        }
        int n;
        if ((n = z.avail_in) == 0) {
            return -5;
        }
        int p = z.next_in_index;
        int m;
        for (m = z.istate.marker; n != 0 && m < 4; --n) {
            if (z.next_in[p] == Inflate.mark[m]) {
                ++m;
            }
            else if (z.next_in[p] != 0) {
                m = 0;
            }
            else {
                m = 4 - m;
            }
            ++p;
        }
        z.total_in += p - z.next_in_index;
        z.next_in_index = p;
        z.avail_in = n;
        if ((z.istate.marker = m) != 4) {
            return -3;
        }
        final long r = z.total_in;
        final long w = z.total_out;
        this.inflateReset(z);
        z.total_in = r;
        z.total_out = w;
        z.istate.mode = 7;
        return 0;
    }
    
    static {
        mark = new byte[] { 0, 0, -1, -1 };
    }
}
