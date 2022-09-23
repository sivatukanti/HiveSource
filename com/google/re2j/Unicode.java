// 
// Decompiled by Procyon v0.5.36
// 

package com.google.re2j;

class Unicode
{
    static final int MAX_RUNE = 1114111;
    static final int MAX_ASCII = 127;
    static final int MAX_LATIN1 = 255;
    private static final int MAX_CASE = 3;
    private static final int REPLACEMENT_CHAR = 65533;
    static final int MIN_FOLD = 65;
    static final int MAX_FOLD = 66639;
    
    private static boolean is32(final int[][] ranges, final int r) {
        int lo = 0;
        int hi = ranges.length;
        while (lo < hi) {
            final int m = lo + (hi - lo) / 2;
            final int[] range = ranges[m];
            if (range[0] <= r && r <= range[1]) {
                return (r - range[0]) % range[2] == 0;
            }
            if (r < range[0]) {
                hi = m;
            }
            else {
                lo = m + 1;
            }
        }
        return false;
    }
    
    private static boolean is(final int[][] ranges, final int r) {
        if (r <= 255) {
            for (final int[] range : ranges) {
                if (r <= range[1]) {
                    return r >= range[0] && (r - range[0]) % range[2] == 0;
                }
            }
            return false;
        }
        return ranges.length > 0 && r >= ranges[0][0] && is32(ranges, r);
    }
    
    static boolean isUpper(final int r) {
        if (r <= 255) {
            return Character.isUpperCase((char)r);
        }
        return is(UnicodeTables.Upper, r);
    }
    
    static boolean isLower(final int r) {
        if (r <= 255) {
            return Character.isLowerCase((char)r);
        }
        return is(UnicodeTables.Lower, r);
    }
    
    static boolean isTitle(final int r) {
        return r > 255 && is(UnicodeTables.Title, r);
    }
    
    static boolean isPrint(final int r) {
        if (r <= 255) {
            return (r >= 32 && r < 127) || (r >= 161 && r != 173);
        }
        return is(UnicodeTables.L, r) || is(UnicodeTables.M, r) || is(UnicodeTables.N, r) || is(UnicodeTables.P, r) || is(UnicodeTables.S, r);
    }
    
    private static int to(final int kase, final int r, final int[][] caseRange) {
        if (kase < 0 || 3 <= kase) {
            return 65533;
        }
        int lo = 0;
        int hi = caseRange.length;
        while (lo < hi) {
            final int m = lo + (hi - lo) / 2;
            final int[] cr = caseRange[m];
            final int crlo = cr[0];
            final int crhi = cr[1];
            if (crlo <= r && r <= crhi) {
                final int delta = cr[2 + kase];
                if (delta > 1114111) {
                    return crlo + ((r - crlo & 0xFFFFFFFE) | (kase & 0x1));
                }
                return r + delta;
            }
            else if (r < crlo) {
                hi = m;
            }
            else {
                lo = m + 1;
            }
        }
        return r;
    }
    
    private static int to(final int kase, final int r) {
        return to(kase, r, UnicodeTables.CASE_RANGES);
    }
    
    static int toUpper(int r) {
        if (r <= 127) {
            if (97 <= r && r <= 122) {
                r -= 32;
            }
            return r;
        }
        return to(0, r);
    }
    
    static int toLower(int r) {
        if (r <= 127) {
            if (65 <= r && r <= 90) {
                r += 32;
            }
            return r;
        }
        return to(1, r);
    }
    
    static int simpleFold(final int r) {
        int lo = 0;
        int hi = UnicodeTables.CASE_ORBIT.length;
        while (lo < hi) {
            final int m = lo + (hi - lo) / 2;
            if (UnicodeTables.CASE_ORBIT[m][0] < r) {
                lo = m + 1;
            }
            else {
                hi = m;
            }
        }
        if (lo < UnicodeTables.CASE_ORBIT.length && UnicodeTables.CASE_ORBIT[lo][0] == r) {
            return UnicodeTables.CASE_ORBIT[lo][1];
        }
        final int l = toLower(r);
        if (l != r) {
            return l;
        }
        return toUpper(r);
    }
    
    private Unicode() {
    }
}
