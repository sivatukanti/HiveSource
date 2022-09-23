// 
// Decompiled by Procyon v0.5.36
// 

package org.tukaani.xz.lzma;

import org.tukaani.xz.rangecoder.RangeEncoder;
import org.tukaani.xz.lz.LZEncoder;
import org.tukaani.xz.lz.Matches;

final class LZMAEncoderFast extends LZMAEncoder
{
    private static int EXTRA_SIZE_BEFORE;
    private static int EXTRA_SIZE_AFTER;
    private Matches matches;
    
    static int getMemoryUsage(final int n, final int a, final int n2) {
        return LZEncoder.getMemoryUsage(n, Math.max(a, LZMAEncoderFast.EXTRA_SIZE_BEFORE), LZMAEncoderFast.EXTRA_SIZE_AFTER, 273, n2);
    }
    
    LZMAEncoderFast(final RangeEncoder rangeEncoder, final int n, final int n2, final int n3, final int n4, final int a, final int n5, final int n6, final int n7) {
        super(rangeEncoder, LZEncoder.getInstance(n4, Math.max(a, LZMAEncoderFast.EXTRA_SIZE_BEFORE), LZMAEncoderFast.EXTRA_SIZE_AFTER, n5, 273, n6, n7), n, n2, n3, n4, n5);
        this.matches = null;
    }
    
    private boolean changePair(final int n, final int n2) {
        return n < n2 >>> 7;
    }
    
    int getNextSymbol() {
        if (this.readAhead == -1) {
            this.matches = this.getMatches();
        }
        this.back = -1;
        final int min = Math.min(this.lz.getAvail(), 273);
        if (min < 2) {
            return 1;
        }
        int n = 0;
        int back = 0;
        for (int i = 0; i < 4; ++i) {
            final int matchLen = this.lz.getMatchLen(this.reps[i], min);
            if (matchLen >= 2) {
                if (matchLen >= this.niceLen) {
                    this.back = i;
                    this.skip(matchLen - 1);
                    return matchLen;
                }
                if (matchLen > n) {
                    back = i;
                    n = matchLen;
                }
            }
        }
        int n2 = 0;
        int n3 = 0;
        if (this.matches.count > 0) {
            n2 = this.matches.len[this.matches.count - 1];
            n3 = this.matches.dist[this.matches.count - 1];
            if (n2 >= this.niceLen) {
                this.back = n3 + 4;
                this.skip(n2 - 1);
                return n2;
            }
            while (this.matches.count > 1 && n2 == this.matches.len[this.matches.count - 2] + 1 && this.changePair(this.matches.dist[this.matches.count - 2], n3)) {
                final Matches matches = this.matches;
                --matches.count;
                n2 = this.matches.len[this.matches.count - 1];
                n3 = this.matches.dist[this.matches.count - 1];
            }
            if (n2 == 2 && n3 >= 128) {
                n2 = 1;
            }
        }
        if (n >= 2 && (n + 1 >= n2 || (n + 2 >= n2 && n3 >= 512) || (n + 3 >= n2 && n3 >= 32768))) {
            this.back = back;
            this.skip(n - 1);
            return n;
        }
        if (n2 < 2 || min <= 2) {
            return 1;
        }
        this.matches = this.getMatches();
        if (this.matches.count > 0) {
            final int n4 = this.matches.len[this.matches.count - 1];
            final int n5 = this.matches.dist[this.matches.count - 1];
            if ((n4 >= n2 && n5 < n3) || (n4 == n2 + 1 && !this.changePair(n3, n5)) || n4 > n2 + 1 || (n4 + 1 >= n2 && n2 >= 3 && this.changePair(n5, n3))) {
                return 1;
            }
        }
        final int max = Math.max(n2 - 1, 2);
        for (int j = 0; j < 4; ++j) {
            if (this.lz.getMatchLen(this.reps[j], max) == max) {
                return 1;
            }
        }
        this.back = n3 + 4;
        this.skip(n2 - 2);
        return n2;
    }
    
    static {
        LZMAEncoderFast.EXTRA_SIZE_BEFORE = 1;
        LZMAEncoderFast.EXTRA_SIZE_AFTER = 272;
    }
}
