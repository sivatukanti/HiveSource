// 
// Decompiled by Procyon v0.5.36
// 

package com.nimbusds.jose.util;

import java.util.Arrays;

final class Base64Codec
{
    private static final char[] CA;
    private static final char[] CA_URL_SAFE;
    private static final int[] IA;
    private static final int[] IA_URL_SAFE;
    
    static {
        CA = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/".toCharArray();
        CA_URL_SAFE = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789-_".toCharArray();
        IA = new int[256];
        IA_URL_SAFE = new int[256];
        Arrays.fill(Base64Codec.IA, -1);
        for (int i = 0, iS = Base64Codec.CA.length; i < iS; ++i) {
            Base64Codec.IA[Base64Codec.CA[i]] = i;
        }
        Base64Codec.IA[61] = 0;
        Arrays.fill(Base64Codec.IA_URL_SAFE, -1);
        for (int i = 0, iS = Base64Codec.CA_URL_SAFE.length; i < iS; ++i) {
            Base64Codec.IA_URL_SAFE[Base64Codec.CA_URL_SAFE[i]] = i;
        }
        Base64Codec.IA_URL_SAFE[61] = 0;
    }
    
    public static int computeEncodedLength(final int inputLength, final boolean urlSafe) {
        if (inputLength == 0) {
            return 0;
        }
        if (urlSafe) {
            final int fullQuadLength = inputLength / 3 << 2;
            final int remainder = inputLength % 3;
            return (remainder == 0) ? fullQuadLength : (fullQuadLength + remainder + 1);
        }
        return (inputLength - 1) / 3 + 1 << 2;
    }
    
    public static String normalizeEncodedString(final String b64String) {
        final int inputLen = b64String.length();
        final int legalLen = inputLen - countIllegalChars(b64String);
        final int padLength = (legalLen % 4 == 0) ? 0 : (4 - legalLen % 4);
        final char[] chars = new char[inputLen + padLength];
        b64String.getChars(0, inputLen, chars, 0);
        for (int i = 0; i < padLength; ++i) {
            chars[inputLen + i] = '=';
        }
        for (int i = 0; i < inputLen; ++i) {
            if (chars[i] == '_') {
                chars[i] = '/';
            }
            else if (chars[i] == '-') {
                chars[i] = '+';
            }
        }
        return new String(chars);
    }
    
    public static int countIllegalChars(final String b64String) {
        int illegalCharCount = 0;
        for (int i = 0; i < b64String.length(); ++i) {
            final char c = b64String.charAt(i);
            if (Base64Codec.IA[c] == -1 && Base64Codec.IA_URL_SAFE[c] == -1) {
                ++illegalCharCount;
            }
        }
        return illegalCharCount;
    }
    
    public static char[] encodeToChar(final byte[] byteArray, final boolean urlSafe) {
        final int sLen = (byteArray != null) ? byteArray.length : 0;
        if (sLen == 0) {
            return new char[0];
        }
        final int eLen = sLen / 3 * 3;
        final int dLen = computeEncodedLength(sLen, urlSafe);
        final char[] out = new char[dLen];
        int s = 0;
        int d = 0;
        while (s < eLen) {
            final int i = (byteArray[s++] & 0xFF) << 16 | (byteArray[s++] & 0xFF) << 8 | (byteArray[s++] & 0xFF);
            if (urlSafe) {
                out[d++] = Base64Codec.CA_URL_SAFE[i >>> 18 & 0x3F];
                out[d++] = Base64Codec.CA_URL_SAFE[i >>> 12 & 0x3F];
                out[d++] = Base64Codec.CA_URL_SAFE[i >>> 6 & 0x3F];
                out[d++] = Base64Codec.CA_URL_SAFE[i & 0x3F];
            }
            else {
                out[d++] = Base64Codec.CA[i >>> 18 & 0x3F];
                out[d++] = Base64Codec.CA[i >>> 12 & 0x3F];
                out[d++] = Base64Codec.CA[i >>> 6 & 0x3F];
                out[d++] = Base64Codec.CA[i & 0x3F];
            }
        }
        final int left = sLen - eLen;
        if (left > 0) {
            final int j = (byteArray[eLen] & 0xFF) << 10 | ((left == 2) ? ((byteArray[sLen - 1] & 0xFF) << 2) : 0);
            if (urlSafe) {
                if (left == 2) {
                    out[dLen - 3] = Base64Codec.CA_URL_SAFE[j >> 12];
                    out[dLen - 2] = Base64Codec.CA_URL_SAFE[j >>> 6 & 0x3F];
                    out[dLen - 1] = Base64Codec.CA_URL_SAFE[j & 0x3F];
                }
                else {
                    out[dLen - 2] = Base64Codec.CA_URL_SAFE[j >> 12];
                    out[dLen - 1] = Base64Codec.CA_URL_SAFE[j >>> 6 & 0x3F];
                }
            }
            else {
                out[dLen - 4] = Base64Codec.CA[j >> 12];
                out[dLen - 3] = Base64Codec.CA[j >>> 6 & 0x3F];
                out[dLen - 2] = ((left == 2) ? Base64Codec.CA[j & 0x3F] : '=');
                out[dLen - 1] = '=';
            }
        }
        return out;
    }
    
    public static String encodeToString(final byte[] byteArray, final boolean urlSafe) {
        return new String(encodeToChar(byteArray, urlSafe));
    }
    
    public static byte[] decode(final String b64String) {
        if (b64String == null || b64String.isEmpty()) {
            return new byte[0];
        }
        final String nStr = normalizeEncodedString(b64String);
        final int sLen = nStr.length();
        final int sepCnt = countIllegalChars(nStr);
        if ((sLen - sepCnt) % 4 != 0) {
            return new byte[0];
        }
        int pad = 0;
        int i = sLen;
        while (i > 1 && Base64Codec.IA[nStr.charAt(--i)] <= 0) {
            if (nStr.charAt(i) == '=') {
                ++pad;
            }
        }
        final int len = ((sLen - sepCnt) * 6 >> 3) - pad;
        final byte[] dArr = new byte[len];
        int s = 0;
        int d = 0;
        while (d < len) {
            int j = 0;
            for (int k = 0; k < 4; ++k) {
                final int c = Base64Codec.IA[nStr.charAt(s++)];
                if (c >= 0) {
                    j |= c << 18 - k * 6;
                }
                else {
                    --k;
                }
            }
            dArr[d++] = (byte)(j >> 16);
            if (d < len) {
                dArr[d++] = (byte)(j >> 8);
                if (d >= len) {
                    continue;
                }
                dArr[d++] = (byte)j;
            }
        }
        return dArr;
    }
}
