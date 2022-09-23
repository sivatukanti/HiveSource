// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.util.security;

public class UnixCrypt
{
    private static final byte[] IP;
    private static final byte[] ExpandTr;
    private static final byte[] PC1;
    private static final byte[] Rotates;
    private static final byte[] PC2;
    private static final byte[][] S;
    private static final byte[] P32Tr;
    private static final byte[] CIFP;
    private static final byte[] ITOA64;
    private static final byte[] A64TOI;
    private static final long[][] PC1ROT;
    private static final long[][][] PC2ROT;
    private static final long[][] IE3264;
    private static final long[][] SPE;
    private static final long[][] CF6464;
    
    private UnixCrypt() {
    }
    
    private static int to_six_bit(final int num) {
        return (num << 26 & 0xFC000000) | (num << 12 & 0xFC0000) | (num >> 2 & 0xFC00) | (num >> 16 & 0xFC);
    }
    
    private static long to_six_bit(final long num) {
        return (num << 26 & 0xFC000000FC000000L) | (num << 12 & 0xFC000000FC0000L) | (num >> 2 & 0xFC000000FC00L) | (num >> 16 & 0xFC000000FCL);
    }
    
    private static long perm6464(long c, final long[][] p) {
        long out = 0L;
        int i = 8;
        while (--i >= 0) {
            final int t = (int)(0xFFL & c);
            c >>= 8;
            long tp = p[i << 1][t & 0xF];
            out |= tp;
            tp = p[(i << 1) + 1][t >> 4];
            out |= tp;
        }
        return out;
    }
    
    private static long perm3264(int c, final long[][] p) {
        long out = 0L;
        int i = 4;
        while (--i >= 0) {
            final int t = 0xFF & c;
            c >>= 8;
            long tp = p[i << 1][t & 0xF];
            out |= tp;
            tp = p[(i << 1) + 1][t >> 4];
            out |= tp;
        }
        return out;
    }
    
    private static long[] des_setkey(final long keyword) {
        long K = perm6464(keyword, UnixCrypt.PC1ROT);
        final long[] KS = new long[16];
        KS[0] = (K & 0xFCFCFCFCFFFFFFFFL);
        for (int i = 1; i < 16; ++i) {
            KS[i] = K;
            K = perm6464(K, UnixCrypt.PC2ROT[UnixCrypt.Rotates[i] - 1]);
            KS[i] = (K & 0xFCFCFCFCFFFFFFFFL);
        }
        return KS;
    }
    
    private static long des_cipher(final long in, int salt, int num_iter, final long[] KS) {
        salt = to_six_bit(salt);
        long R;
        long L = R = in;
        L &= 0x5555555555555555L;
        R = ((R & 0xAAAAAAAA00000000L) | (R >> 1 & 0x55555555L));
        L = (((L << 1 | L << 32) & 0xFFFFFFFF00000000L) | ((R | R >> 32) & 0xFFFFFFFFL));
        L = perm3264((int)(L >> 32), UnixCrypt.IE3264);
        R = perm3264((int)(L & -1L), UnixCrypt.IE3264);
        while (--num_iter >= 0) {
            for (int loop_count = 0; loop_count < 8; ++loop_count) {
                long kp = KS[loop_count << 1];
                long k = (R >> 32 ^ R) & (long)salt & 0xFFFFFFFFL;
                k |= k << 32;
                long B = k ^ R ^ kp;
                L ^= (UnixCrypt.SPE[0][(int)(B >> 58 & 0x3FL)] ^ UnixCrypt.SPE[1][(int)(B >> 50 & 0x3FL)] ^ UnixCrypt.SPE[2][(int)(B >> 42 & 0x3FL)] ^ UnixCrypt.SPE[3][(int)(B >> 34 & 0x3FL)] ^ UnixCrypt.SPE[4][(int)(B >> 26 & 0x3FL)] ^ UnixCrypt.SPE[5][(int)(B >> 18 & 0x3FL)] ^ UnixCrypt.SPE[6][(int)(B >> 10 & 0x3FL)] ^ UnixCrypt.SPE[7][(int)(B >> 2 & 0x3FL)]);
                kp = KS[(loop_count << 1) + 1];
                k = ((L >> 32 ^ L) & (long)salt & 0xFFFFFFFFL);
                k |= k << 32;
                B = (k ^ L ^ kp);
                R ^= (UnixCrypt.SPE[0][(int)(B >> 58 & 0x3FL)] ^ UnixCrypt.SPE[1][(int)(B >> 50 & 0x3FL)] ^ UnixCrypt.SPE[2][(int)(B >> 42 & 0x3FL)] ^ UnixCrypt.SPE[3][(int)(B >> 34 & 0x3FL)] ^ UnixCrypt.SPE[4][(int)(B >> 26 & 0x3FL)] ^ UnixCrypt.SPE[5][(int)(B >> 18 & 0x3FL)] ^ UnixCrypt.SPE[6][(int)(B >> 10 & 0x3FL)] ^ UnixCrypt.SPE[7][(int)(B >> 2 & 0x3FL)]);
            }
            L ^= R;
            R ^= L;
            L ^= R;
        }
        L = (((L >> 35 & 0xF0F0F0FL) | ((L & -1L) << 1 & 0xF0F0F0F0L)) << 32 | ((R >> 35 & 0xF0F0F0FL) | ((R & -1L) << 1 & 0xF0F0F0F0L)));
        L = perm6464(L, UnixCrypt.CF6464);
        return L;
    }
    
    private static void init_perm(final long[][] perm, final byte[] p, final int chars_out) {
        for (int k = 0; k < chars_out * 8; ++k) {
            int l = p[k] - 1;
            if (l >= 0) {
                final int i = l >> 2;
                l = 1 << (l & 0x3);
                for (int j = 0; j < 16; ++j) {
                    final int s = (k & 0x7) + (7 - (k >> 3) << 3);
                    if ((j & l) != 0x0) {
                        final long[] array = perm[i];
                        final int n = j;
                        array[n] |= 1L << s;
                    }
                }
            }
        }
    }
    
    public static String crypt(final String key, final String setting) {
        final long constdatablock = 0L;
        final byte[] cryptresult = new byte[13];
        long keyword = 0L;
        if (key == null || setting == null) {
            return "*";
        }
        final int keylen = key.length();
        for (int i = 0; i < 8; ++i) {
            keyword = (keyword << 8 | (long)((i < keylen) ? ('\u0002' * key.charAt(i)) : 0));
        }
        final long[] KS = des_setkey(keyword);
        int salt = 0;
        int j = 2;
        while (--j >= 0) {
            final char c = (j < setting.length()) ? setting.charAt(j) : '.';
            cryptresult[j] = (byte)c;
            salt = (salt << 6 | (0xFF & UnixCrypt.A64TOI[c]));
        }
        long rsltblock = des_cipher(constdatablock, salt, 25, KS);
        cryptresult[12] = UnixCrypt.ITOA64[(int)rsltblock << 2 & 0x3F];
        rsltblock >>= 4;
        int k = 12;
        while (--k >= 2) {
            cryptresult[k] = UnixCrypt.ITOA64[(int)rsltblock & 0x3F];
            rsltblock >>= 6;
        }
        return new String(cryptresult, 0, 13);
    }
    
    public static void main(final String[] arg) {
        if (arg.length != 2) {
            System.err.println("Usage - java org.eclipse.util.UnixCrypt <key> <salt>");
            System.exit(1);
        }
        System.err.println("Crypt=" + crypt(arg[0], arg[1]));
    }
    
    static {
        IP = new byte[] { 58, 50, 42, 34, 26, 18, 10, 2, 60, 52, 44, 36, 28, 20, 12, 4, 62, 54, 46, 38, 30, 22, 14, 6, 64, 56, 48, 40, 32, 24, 16, 8, 57, 49, 41, 33, 25, 17, 9, 1, 59, 51, 43, 35, 27, 19, 11, 3, 61, 53, 45, 37, 29, 21, 13, 5, 63, 55, 47, 39, 31, 23, 15, 7 };
        ExpandTr = new byte[] { 32, 1, 2, 3, 4, 5, 4, 5, 6, 7, 8, 9, 8, 9, 10, 11, 12, 13, 12, 13, 14, 15, 16, 17, 16, 17, 18, 19, 20, 21, 20, 21, 22, 23, 24, 25, 24, 25, 26, 27, 28, 29, 28, 29, 30, 31, 32, 1 };
        PC1 = new byte[] { 57, 49, 41, 33, 25, 17, 9, 1, 58, 50, 42, 34, 26, 18, 10, 2, 59, 51, 43, 35, 27, 19, 11, 3, 60, 52, 44, 36, 63, 55, 47, 39, 31, 23, 15, 7, 62, 54, 46, 38, 30, 22, 14, 6, 61, 53, 45, 37, 29, 21, 13, 5, 28, 20, 12, 4 };
        Rotates = new byte[] { 1, 1, 2, 2, 2, 2, 2, 2, 1, 2, 2, 2, 2, 2, 2, 1 };
        PC2 = new byte[] { 9, 18, 14, 17, 11, 24, 1, 5, 22, 25, 3, 28, 15, 6, 21, 10, 35, 38, 23, 19, 12, 4, 26, 8, 43, 54, 16, 7, 27, 20, 13, 2, 0, 0, 41, 52, 31, 37, 47, 55, 0, 0, 30, 40, 51, 45, 33, 48, 0, 0, 44, 49, 39, 56, 34, 53, 0, 0, 46, 42, 50, 36, 29, 32 };
        S = new byte[][] { { 14, 4, 13, 1, 2, 15, 11, 8, 3, 10, 6, 12, 5, 9, 0, 7, 0, 15, 7, 4, 14, 2, 13, 1, 10, 6, 12, 11, 9, 5, 3, 8, 4, 1, 14, 8, 13, 6, 2, 11, 15, 12, 9, 7, 3, 10, 5, 0, 15, 12, 8, 2, 4, 9, 1, 7, 5, 11, 3, 14, 10, 0, 6, 13 }, { 15, 1, 8, 14, 6, 11, 3, 4, 9, 7, 2, 13, 12, 0, 5, 10, 3, 13, 4, 7, 15, 2, 8, 14, 12, 0, 1, 10, 6, 9, 11, 5, 0, 14, 7, 11, 10, 4, 13, 1, 5, 8, 12, 6, 9, 3, 2, 15, 13, 8, 10, 1, 3, 15, 4, 2, 11, 6, 7, 12, 0, 5, 14, 9 }, { 10, 0, 9, 14, 6, 3, 15, 5, 1, 13, 12, 7, 11, 4, 2, 8, 13, 7, 0, 9, 3, 4, 6, 10, 2, 8, 5, 14, 12, 11, 15, 1, 13, 6, 4, 9, 8, 15, 3, 0, 11, 1, 2, 12, 5, 10, 14, 7, 1, 10, 13, 0, 6, 9, 8, 7, 4, 15, 14, 3, 11, 5, 2, 12 }, { 7, 13, 14, 3, 0, 6, 9, 10, 1, 2, 8, 5, 11, 12, 4, 15, 13, 8, 11, 5, 6, 15, 0, 3, 4, 7, 2, 12, 1, 10, 14, 9, 10, 6, 9, 0, 12, 11, 7, 13, 15, 1, 3, 14, 5, 2, 8, 4, 3, 15, 0, 6, 10, 1, 13, 8, 9, 4, 5, 11, 12, 7, 2, 14 }, { 2, 12, 4, 1, 7, 10, 11, 6, 8, 5, 3, 15, 13, 0, 14, 9, 14, 11, 2, 12, 4, 7, 13, 1, 5, 0, 15, 10, 3, 9, 8, 6, 4, 2, 1, 11, 10, 13, 7, 8, 15, 9, 12, 5, 6, 3, 0, 14, 11, 8, 12, 7, 1, 14, 2, 13, 6, 15, 0, 9, 10, 4, 5, 3 }, { 12, 1, 10, 15, 9, 2, 6, 8, 0, 13, 3, 4, 14, 7, 5, 11, 10, 15, 4, 2, 7, 12, 9, 5, 6, 1, 13, 14, 0, 11, 3, 8, 9, 14, 15, 5, 2, 8, 12, 3, 7, 0, 4, 10, 1, 13, 11, 6, 4, 3, 2, 12, 9, 5, 15, 10, 11, 14, 1, 7, 6, 0, 8, 13 }, { 4, 11, 2, 14, 15, 0, 8, 13, 3, 12, 9, 7, 5, 10, 6, 1, 13, 0, 11, 7, 4, 9, 1, 10, 14, 3, 5, 12, 2, 15, 8, 6, 1, 4, 11, 13, 12, 3, 7, 14, 10, 15, 6, 8, 0, 5, 9, 2, 6, 11, 13, 8, 1, 4, 10, 7, 9, 5, 0, 15, 14, 2, 3, 12 }, { 13, 2, 8, 4, 6, 15, 11, 1, 10, 9, 3, 14, 5, 0, 12, 7, 1, 15, 13, 8, 10, 3, 7, 4, 12, 5, 6, 11, 0, 14, 9, 2, 7, 11, 4, 1, 9, 12, 14, 2, 0, 6, 10, 13, 15, 3, 5, 8, 2, 1, 14, 7, 4, 10, 8, 13, 15, 12, 9, 0, 3, 5, 6, 11 } };
        P32Tr = new byte[] { 16, 7, 20, 21, 29, 12, 28, 17, 1, 15, 23, 26, 5, 18, 31, 10, 2, 8, 24, 14, 32, 27, 3, 9, 19, 13, 30, 6, 22, 11, 4, 25 };
        CIFP = new byte[] { 1, 2, 3, 4, 17, 18, 19, 20, 5, 6, 7, 8, 21, 22, 23, 24, 9, 10, 11, 12, 25, 26, 27, 28, 13, 14, 15, 16, 29, 30, 31, 32, 33, 34, 35, 36, 49, 50, 51, 52, 37, 38, 39, 40, 53, 54, 55, 56, 41, 42, 43, 44, 57, 58, 59, 60, 45, 46, 47, 48, 61, 62, 63, 64 };
        ITOA64 = new byte[] { 46, 47, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 78, 79, 80, 81, 82, 83, 84, 85, 86, 87, 88, 89, 90, 97, 98, 99, 100, 101, 102, 103, 104, 105, 106, 107, 108, 109, 110, 111, 112, 113, 114, 115, 116, 117, 118, 119, 120, 121, 122 };
        A64TOI = new byte[128];
        PC1ROT = new long[16][16];
        PC2ROT = new long[2][16][16];
        IE3264 = new long[8][16];
        SPE = new long[8][64];
        CF6464 = new long[16][16];
        final byte[] perm = new byte[64];
        final byte[] temp = new byte[64];
        for (int i = 0; i < 64; ++i) {
            UnixCrypt.A64TOI[UnixCrypt.ITOA64[i]] = (byte)i;
        }
        for (int i = 0; i < 64; ++i) {
            perm[i] = 0;
        }
        for (int i = 0; i < 64; ++i) {
            int k;
            if ((k = UnixCrypt.PC2[i]) != 0) {
                k += UnixCrypt.Rotates[0] - 1;
                if (k % 28 < UnixCrypt.Rotates[0]) {
                    k -= 28;
                }
                k = UnixCrypt.PC1[k];
                if (k > 0) {
                    k = (--k | 0x7) - (k & 0x7);
                    ++k;
                }
                perm[i] = (byte)k;
            }
        }
        init_perm(UnixCrypt.PC1ROT, perm, 8);
        for (int j = 0; j < 2; ++j) {
            for (int l = 0; l < 64; ++l) {
                perm[l] = (temp[l] = 0);
            }
            for (int l = 0; l < 64; ++l) {
                final int k;
                if ((k = UnixCrypt.PC2[l]) != 0) {
                    temp[k - 1] = (byte)(l + 1);
                }
            }
            for (int l = 0; l < 64; ++l) {
                int k;
                if ((k = UnixCrypt.PC2[l]) != 0) {
                    k += j;
                    if (k % 28 <= j) {
                        k -= 28;
                    }
                    perm[l] = temp[k];
                }
            }
            init_perm(UnixCrypt.PC2ROT[j], perm, 8);
        }
        for (int i = 0; i < 8; ++i) {
            for (int m = 0; m < 8; ++m) {
                int k2 = (m < 2) ? 0 : UnixCrypt.IP[UnixCrypt.ExpandTr[i * 6 + m - 2] - 1];
                if (k2 > 32) {
                    k2 -= 32;
                }
                else if (k2 > 0) {
                    --k2;
                }
                if (k2 > 0) {
                    k2 = (--k2 | 0x7) - (k2 & 0x7);
                    ++k2;
                }
                perm[i * 8 + m] = (byte)k2;
            }
        }
        init_perm(UnixCrypt.IE3264, perm, 8);
        for (int i = 0; i < 64; ++i) {
            int k = UnixCrypt.IP[UnixCrypt.CIFP[i] - 1];
            if (k > 0) {
                k = (--k | 0x7) - (k & 0x7);
                ++k;
            }
            perm[k - 1] = (byte)(i + 1);
        }
        init_perm(UnixCrypt.CF6464, perm, 8);
        for (int i = 0; i < 48; ++i) {
            perm[i] = UnixCrypt.P32Tr[UnixCrypt.ExpandTr[i] - 1];
        }
        for (int t = 0; t < 8; ++t) {
            for (int m = 0; m < 64; ++m) {
                int k2 = (m >> 0 & 0x1) << 5 | (m >> 1 & 0x1) << 3 | (m >> 2 & 0x1) << 2 | (m >> 3 & 0x1) << 1 | (m >> 4 & 0x1) << 0 | (m >> 5 & 0x1) << 4;
                k2 = UnixCrypt.S[t][k2];
                k2 = ((k2 >> 3 & 0x1) << 0 | (k2 >> 2 & 0x1) << 1 | (k2 >> 1 & 0x1) << 2 | (k2 >> 0 & 0x1) << 3);
                for (int i2 = 0; i2 < 32; ++i2) {
                    temp[i2] = 0;
                }
                for (int i2 = 0; i2 < 4; ++i2) {
                    temp[4 * t + i2] = (byte)(k2 >> i2 & 0x1);
                }
                long kk = 0L;
                int i3 = 24;
                while (--i3 >= 0) {
                    kk = (kk << 1 | (long)temp[perm[i3] - 1] << 32 | (long)temp[perm[i3 + 24] - 1]);
                }
                UnixCrypt.SPE[t][m] = to_six_bit(kk);
            }
        }
    }
}
