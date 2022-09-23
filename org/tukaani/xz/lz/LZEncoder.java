// 
// Decompiled by Procyon v0.5.36
// 

package org.tukaani.xz.lz;

import java.io.IOException;
import java.io.OutputStream;

public abstract class LZEncoder
{
    public static final int MF_HC4 = 4;
    public static final int MF_BT4 = 20;
    private final int keepSizeBefore;
    private final int keepSizeAfter;
    final int matchLenMax;
    final int niceLen;
    final byte[] buf;
    int readPos;
    private int readLimit;
    private boolean finishing;
    private int writePos;
    private int pendingSize;
    
    static void normalize(final int[] array, final int n) {
        for (int i = 0; i < array.length; ++i) {
            if (array[i] <= n) {
                array[i] = 0;
            }
            else {
                final int n2 = i;
                array[n2] -= n;
            }
        }
    }
    
    private static int getBufSize(final int n, final int n2, final int n3, final int n4) {
        return n2 + n + (n3 + n4) + Math.min(n / 2 + 262144, 536870912);
    }
    
    public static int getMemoryUsage(final int n, final int n2, final int n3, final int n4, final int n5) {
        final int n6 = getBufSize(n, n2, n3, n4) / 1024 + 10;
        int n7 = 0;
        switch (n5) {
            case 4: {
                n7 = n6 + HC4.getMemoryUsage(n);
                break;
            }
            case 20: {
                n7 = n6 + BT4.getMemoryUsage(n);
                break;
            }
            default: {
                throw new IllegalArgumentException();
            }
        }
        return n7;
    }
    
    public static LZEncoder getInstance(final int n, final int n2, final int n3, final int n4, final int n5, final int n6, final int n7) {
        switch (n6) {
            case 4: {
                return new HC4(n, n2, n3, n4, n5, n7);
            }
            case 20: {
                return new BT4(n, n2, n3, n4, n5, n7);
            }
            default: {
                throw new IllegalArgumentException();
            }
        }
    }
    
    LZEncoder(final int n, final int n2, final int n3, final int niceLen, final int matchLenMax) {
        this.readPos = -1;
        this.readLimit = -1;
        this.finishing = false;
        this.writePos = 0;
        this.pendingSize = 0;
        this.buf = new byte[getBufSize(n, n2, n3, matchLenMax)];
        this.keepSizeBefore = n2 + n;
        this.keepSizeAfter = n3 + matchLenMax;
        this.matchLenMax = matchLenMax;
        this.niceLen = niceLen;
    }
    
    public void setPresetDict(final int b, final byte[] array) {
        assert !this.isStarted();
        if (array != null) {
            final int min = Math.min(array.length, b);
            System.arraycopy(array, array.length - min, this.buf, 0, min);
            this.skip(min);
        }
    }
    
    private void moveWindow() {
        final int n = this.readPos + 1 - this.keepSizeBefore & 0xFFFFFFF0;
        System.arraycopy(this.buf, n, this.buf, 0, this.writePos - n);
        this.readPos -= n;
        this.readLimit -= n;
        this.writePos -= n;
    }
    
    public int fillWindow(final byte[] array, final int n, int n2) {
        assert !this.finishing;
        if (this.readPos >= this.buf.length - this.keepSizeAfter) {
            this.moveWindow();
        }
        if (n2 > this.buf.length - this.writePos) {
            n2 = this.buf.length - this.writePos;
        }
        System.arraycopy(array, n, this.buf, this.writePos, n2);
        this.writePos += n2;
        if (this.writePos >= this.keepSizeAfter) {
            this.readLimit = this.writePos - this.keepSizeAfter;
        }
        if (this.pendingSize > 0 && this.readPos < this.readLimit) {
            this.readPos -= this.pendingSize;
            final int pendingSize = this.pendingSize;
            this.pendingSize = 0;
            this.skip(pendingSize);
            assert this.pendingSize < pendingSize;
        }
        return n2;
    }
    
    public boolean isStarted() {
        return this.readPos != -1;
    }
    
    public void setFlushing() {
        this.readLimit = this.writePos - 1;
    }
    
    public void setFinishing() {
        this.readLimit = this.writePos - 1;
        this.finishing = true;
    }
    
    public boolean hasEnoughData(final int n) {
        return this.readPos - n < this.readLimit;
    }
    
    public void copyUncompressed(final OutputStream outputStream, final int n, final int len) throws IOException {
        outputStream.write(this.buf, this.readPos + 1 - n, len);
    }
    
    public int getAvail() {
        assert this.isStarted();
        return this.writePos - this.readPos;
    }
    
    public int getPos() {
        return this.readPos;
    }
    
    public int getByte(final int n) {
        return this.buf[this.readPos - n] & 0xFF;
    }
    
    public int getByte(final int n, final int n2) {
        return this.buf[this.readPos + n - n2] & 0xFF;
    }
    
    public int getMatchLen(final int n, final int n2) {
        int n3;
        int n4;
        for (n3 = this.readPos - n - 1, n4 = 0; n4 < n2 && this.buf[this.readPos + n4] == this.buf[n3 + n4]; ++n4) {}
        return n4;
    }
    
    public int getMatchLen(final int n, final int n2, final int n3) {
        int n4;
        int n5;
        int n6;
        for (n4 = this.readPos + n, n5 = n4 - n2 - 1, n6 = 0; n6 < n3 && this.buf[n4 + n6] == this.buf[n5 + n6]; ++n6) {}
        return n6;
    }
    
    public boolean verifyMatches(final Matches matches) {
        final int min = Math.min(this.getAvail(), this.matchLenMax);
        for (int i = 0; i < matches.count; ++i) {
            if (this.getMatchLen(matches.dist[i], min) != matches.len[i]) {
                return false;
            }
        }
        return true;
    }
    
    int movePos(final int n, final int n2) {
        assert n >= n2;
        ++this.readPos;
        int n3 = this.writePos - this.readPos;
        if (n3 < n && (n3 < n2 || !this.finishing)) {
            ++this.pendingSize;
            n3 = 0;
        }
        return n3;
    }
    
    public abstract Matches getMatches();
    
    public abstract void skip(final int p0);
}
