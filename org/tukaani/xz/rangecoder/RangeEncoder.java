// 
// Decompiled by Procyon v0.5.36
// 

package org.tukaani.xz.rangecoder;

import java.io.IOException;
import java.io.OutputStream;

public final class RangeEncoder extends RangeCoder
{
    private static final int MOVE_REDUCING_BITS = 4;
    private static final int BIT_PRICE_SHIFT_BITS = 4;
    private static final int[] prices;
    private long low;
    private int range;
    private int cacheSize;
    private byte cache;
    private final byte[] buf;
    private int bufPos;
    
    public RangeEncoder(final int n) {
        this.buf = new byte[n];
        this.reset();
    }
    
    public void reset() {
        this.low = 0L;
        this.range = -1;
        this.cache = 0;
        this.cacheSize = 1;
        this.bufPos = 0;
    }
    
    public int getPendingSize() {
        return this.bufPos + this.cacheSize + 5 - 1;
    }
    
    public int finish() {
        for (int i = 0; i < 5; ++i) {
            this.shiftLow();
        }
        return this.bufPos;
    }
    
    public void write(final OutputStream outputStream) throws IOException {
        outputStream.write(this.buf, 0, this.bufPos);
    }
    
    private void shiftLow() {
        final int n = (int)(this.low >>> 32);
        if (n != 0 || this.low < 4278190080L) {
            int cache = this.cache;
            do {
                this.buf[this.bufPos++] = (byte)(cache + n);
                cache = 255;
            } while (--this.cacheSize != 0);
            this.cache = (byte)(this.low >>> 24);
        }
        ++this.cacheSize;
        this.low = (this.low & 0xFFFFFFL) << 8;
    }
    
    public void encodeBit(final short[] array, final int n, final int n2) {
        final short n3 = array[n];
        final int range = (this.range >>> 11) * n3;
        if (n2 == 0) {
            this.range = range;
            array[n] = (short)(n3 + (2048 - n3 >>> 5));
        }
        else {
            this.low += ((long)range & 0xFFFFFFFFL);
            this.range -= range;
            array[n] = (short)(n3 - (n3 >>> 5));
        }
        if ((this.range & 0xFF000000) == 0x0) {
            this.range <<= 8;
            this.shiftLow();
        }
    }
    
    public static int getBitPrice(final int n, final int n2) {
        assert n2 == 1;
        return RangeEncoder.prices[(n ^ (-n2 & 0x7FF)) >>> 4];
    }
    
    public void encodeBitTree(final short[] array, final int n) {
        int n2 = 1;
        int i = array.length;
        do {
            i >>>= 1;
            final int n3 = n & i;
            this.encodeBit(array, n2, n3);
            n2 <<= 1;
            if (n3 != 0) {
                n2 |= 0x1;
            }
        } while (i != 1);
    }
    
    public static int getBitTreePrice(final short[] array, int i) {
        int n = 0;
        i |= array.length;
        do {
            final int n2 = i & 0x1;
            i >>>= 1;
            n += getBitPrice(array[i], n2);
        } while (i != 1);
        return n;
    }
    
    public void encodeReverseBitTree(final short[] array, int i) {
        int n = 1;
        i |= array.length;
        do {
            final int n2 = i & 0x1;
            i >>>= 1;
            this.encodeBit(array, n, n2);
            n = (n << 1 | n2);
        } while (i != 1);
    }
    
    public static int getReverseBitTreePrice(final short[] array, int i) {
        int n = 0;
        int n2 = 1;
        i |= array.length;
        do {
            final int n3 = i & 0x1;
            i >>>= 1;
            n += getBitPrice(array[n2], n3);
            n2 = (n2 << 1 | n3);
        } while (i != 1);
        return n;
    }
    
    public void encodeDirectBits(final int n, int i) {
        do {
            this.range >>>= 1;
            this.low += (this.range & 0 - (n >>> --i & 0x1));
            if ((this.range & 0xFF000000) == 0x0) {
                this.range <<= 8;
                this.shiftLow();
            }
        } while (i != 0);
    }
    
    public static int getDirectBitsPrice(final int n) {
        return n << 4;
    }
    
    static {
        prices = new int[128];
        for (int i = 8; i < 2048; i += 16) {
            int n = i;
            int n2 = 0;
            for (int j = 0; j < 4; ++j) {
                for (n *= n, n2 <<= 1; (n & 0xFFFF0000) != 0x0; n >>>= 1, ++n2) {}
            }
            RangeEncoder.prices[i >> 4] = 161 - n2;
        }
    }
}
