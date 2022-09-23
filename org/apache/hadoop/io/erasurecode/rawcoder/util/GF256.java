// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.io.erasurecode.rawcoder.util;

import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Private
public final class GF256
{
    private static final byte[] GF_BASE;
    private static final byte[] GF_LOG_BASE;
    private static byte[][] theGfMulTab;
    
    private GF256() {
    }
    
    public static byte[] gfBase() {
        return GF256.GF_BASE;
    }
    
    public static byte[] gfLogBase() {
        return GF256.GF_LOG_BASE;
    }
    
    public static byte[][] gfMulTab() {
        return GF256.theGfMulTab;
    }
    
    public static byte gfMul(final byte a, final byte b) {
        if (a == 0 || b == 0) {
            return 0;
        }
        int tmp = (GF256.GF_LOG_BASE[a & 0xFF] & 0xFF) + (GF256.GF_LOG_BASE[b & 0xFF] & 0xFF);
        if (tmp > 254) {
            tmp -= 255;
        }
        return GF256.GF_BASE[tmp];
    }
    
    public static byte gfInv(final byte a) {
        if (a == 0) {
            return 0;
        }
        return GF256.GF_BASE[255 - GF256.GF_LOG_BASE[a & 0xFF] & 0xFF];
    }
    
    public static void gfInvertMatrix(final byte[] inMatrix, final byte[] outMatrix, final int n) {
        for (int i = 0; i < n * n; ++i) {
            outMatrix[i] = 0;
        }
        for (int i = 0; i < n; ++i) {
            outMatrix[i * n + i] = 1;
        }
        for (int j = 0; j < n; ++j) {
            if (inMatrix[j * n + j] == 0) {
                int k;
                for (k = j + 1; k < n && inMatrix[k * n + j] == 0; ++k) {}
                if (k == n) {
                    throw new RuntimeException("Not invertible");
                }
                for (int l = 0; l < n; ++l) {
                    byte temp = inMatrix[j * n + l];
                    inMatrix[j * n + l] = inMatrix[k * n + l];
                    inMatrix[k * n + l] = temp;
                    temp = outMatrix[j * n + l];
                    outMatrix[j * n + l] = outMatrix[k * n + l];
                    outMatrix[k * n + l] = temp;
                }
            }
            byte temp = gfInv(inMatrix[j * n + j]);
            for (int k = 0; k < n; ++k) {
                inMatrix[j * n + k] = gfMul(inMatrix[j * n + k], temp);
                outMatrix[j * n + k] = gfMul(outMatrix[j * n + k], temp);
            }
            for (int k = 0; k < n; ++k) {
                if (k != j) {
                    temp = inMatrix[k * n + j];
                    for (int l = 0; l < n; ++l) {
                        final int n2 = k * n + l;
                        outMatrix[n2] ^= gfMul(temp, outMatrix[j * n + l]);
                        final int n3 = k * n + l;
                        inMatrix[n3] ^= gfMul(temp, inMatrix[j * n + l]);
                    }
                }
            }
        }
    }
    
    public static void gfVectMulInit(final byte c, final byte[] tbl, final int offset) {
        final byte c2 = (byte)(c << 1 ^ (((c & 0x80) != 0x0) ? 29 : 0));
        final byte c3 = (byte)(c2 << 1 ^ (((c2 & 0x80) != 0x0) ? 29 : 0));
        final byte c4 = (byte)(c3 << 1 ^ (((c3 & 0x80) != 0x0) ? 29 : 0));
        final byte c5 = (byte)(c2 ^ c);
        final byte c6 = (byte)(c3 ^ c);
        final byte c7 = (byte)(c3 ^ c2);
        final byte c8 = (byte)(c3 ^ c5);
        final byte c9 = (byte)(c4 ^ c);
        final byte c10 = (byte)(c4 ^ c2);
        final byte c11 = (byte)(c4 ^ c5);
        final byte c12 = (byte)(c4 ^ c3);
        final byte c13 = (byte)(c4 ^ c6);
        final byte c14 = (byte)(c4 ^ c7);
        final byte c15 = (byte)(c4 ^ c8);
        tbl[offset + 0] = 0;
        tbl[offset + 1] = c;
        tbl[offset + 2] = c2;
        tbl[offset + 3] = c5;
        tbl[offset + 4] = c3;
        tbl[offset + 5] = c6;
        tbl[offset + 6] = c7;
        tbl[offset + 7] = c8;
        tbl[offset + 8] = c4;
        tbl[offset + 9] = c9;
        tbl[offset + 10] = c10;
        tbl[offset + 11] = c11;
        tbl[offset + 12] = c12;
        tbl[offset + 13] = c13;
        tbl[offset + 14] = c14;
        tbl[offset + 15] = c15;
        final byte c16 = (byte)(c4 << 1 ^ (((c4 & 0x80) != 0x0) ? 29 : 0));
        final byte c17 = (byte)(c16 << 1 ^ (((c16 & 0x80) != 0x0) ? 29 : 0));
        final byte c18 = (byte)(c17 ^ c16);
        final byte c19 = (byte)(c17 << 1 ^ (((c17 & 0x80) != 0x0) ? 29 : 0));
        final byte c20 = (byte)(c19 ^ c16);
        final byte c21 = (byte)(c19 ^ c17);
        final byte c22 = (byte)(c19 ^ c18);
        final byte c23 = (byte)(c19 << 1 ^ (((c19 & 0x80) != 0x0) ? 29 : 0));
        final byte c24 = (byte)(c23 ^ c16);
        final byte c25 = (byte)(c23 ^ c17);
        final byte c26 = (byte)(c23 ^ c18);
        final byte c27 = (byte)(c23 ^ c19);
        final byte c28 = (byte)(c23 ^ c20);
        final byte c29 = (byte)(c23 ^ c21);
        final byte c30 = (byte)(c23 ^ c22);
        tbl[offset + 16] = 0;
        tbl[offset + 17] = c16;
        tbl[offset + 18] = c17;
        tbl[offset + 19] = c18;
        tbl[offset + 20] = c19;
        tbl[offset + 21] = c20;
        tbl[offset + 22] = c21;
        tbl[offset + 23] = c22;
        tbl[offset + 24] = c23;
        tbl[offset + 25] = c24;
        tbl[offset + 26] = c25;
        tbl[offset + 27] = c26;
        tbl[offset + 28] = c27;
        tbl[offset + 29] = c28;
        tbl[offset + 30] = c29;
        tbl[offset + 31] = c30;
    }
    
    static {
        GF_BASE = new byte[] { 1, 2, 4, 8, 16, 32, 64, -128, 29, 58, 116, -24, -51, -121, 19, 38, 76, -104, 45, 90, -76, 117, -22, -55, -113, 3, 6, 12, 24, 48, 96, -64, -99, 39, 78, -100, 37, 74, -108, 53, 106, -44, -75, 119, -18, -63, -97, 35, 70, -116, 5, 10, 20, 40, 80, -96, 93, -70, 105, -46, -71, 111, -34, -95, 95, -66, 97, -62, -103, 47, 94, -68, 101, -54, -119, 15, 30, 60, 120, -16, -3, -25, -45, -69, 107, -42, -79, 127, -2, -31, -33, -93, 91, -74, 113, -30, -39, -81, 67, -122, 17, 34, 68, -120, 13, 26, 52, 104, -48, -67, 103, -50, -127, 31, 62, 124, -8, -19, -57, -109, 59, 118, -20, -59, -105, 51, 102, -52, -123, 23, 46, 92, -72, 109, -38, -87, 79, -98, 33, 66, -124, 21, 42, 84, -88, 77, -102, 41, 82, -92, 85, -86, 73, -110, 57, 114, -28, -43, -73, 115, -26, -47, -65, 99, -58, -111, 63, 126, -4, -27, -41, -77, 123, -10, -15, -1, -29, -37, -85, 75, -106, 49, 98, -60, -107, 55, 110, -36, -91, 87, -82, 65, -126, 25, 50, 100, -56, -115, 7, 14, 28, 56, 112, -32, -35, -89, 83, -90, 81, -94, 89, -78, 121, -14, -7, -17, -61, -101, 43, 86, -84, 69, -118, 9, 18, 36, 72, -112, 61, 122, -12, -11, -9, -13, -5, -21, -53, -117, 11, 22, 44, 88, -80, 125, -6, -23, -49, -125, 27, 54, 108, -40, -83, 71, -114, 1 };
        GF_LOG_BASE = new byte[] { 0, -1, 1, 25, 2, 50, 26, -58, 3, -33, 51, -18, 27, 104, -57, 75, 4, 100, -32, 14, 52, -115, -17, -127, 28, -63, 105, -8, -56, 8, 76, 113, 5, -118, 101, 47, -31, 36, 15, 33, 53, -109, -114, -38, -16, 18, -126, 69, 29, -75, -62, 125, 106, 39, -7, -71, -55, -102, 9, 120, 77, -28, 114, -90, 6, -65, -117, 98, 102, -35, 48, -3, -30, -104, 37, -77, 16, -111, 34, -120, 54, -48, -108, -50, -113, -106, -37, -67, -15, -46, 19, 92, -125, 56, 70, 64, 30, 66, -74, -93, -61, 72, 126, 110, 107, 58, 40, 84, -6, -123, -70, 61, -54, 94, -101, -97, 10, 21, 121, 43, 78, -44, -27, -84, 115, -13, -89, 87, 7, 112, -64, -9, -116, -128, 99, 13, 103, 74, -34, -19, 49, -59, -2, 24, -29, -91, -103, 119, 38, -72, -76, 124, 17, 68, -110, -39, 35, 32, -119, 46, 55, 63, -47, 91, -107, -68, -49, -51, -112, -121, -105, -78, -36, -4, -66, 97, -14, 86, -45, -85, 20, 42, 93, -98, -124, 60, 57, 83, 71, 109, 65, -94, 31, 45, 67, -40, -73, 123, -92, 118, -60, 23, 73, -20, 127, 12, 111, -10, 108, -95, 59, 82, 41, -99, 85, -86, -5, 96, -122, -79, -69, -52, 62, 90, -53, 89, 95, -80, -100, -87, -96, 81, 11, -11, 22, -21, 122, 117, 44, -41, 79, -82, -43, -23, -26, -25, -83, -24, 116, -42, -12, -22, -88, 80, 88, -81 };
        GF256.theGfMulTab = new byte[256][256];
        for (int i = 0; i < 256; ++i) {
            for (int j = 0; j < 256; ++j) {
                GF256.theGfMulTab[i][j] = gfMul((byte)i, (byte)j);
            }
        }
    }
}
