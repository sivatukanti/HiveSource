// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.core.util;

public final class Base64
{
    private static final int BASELENGTH = 256;
    private static final int LOOKUPLENGTH = 64;
    private static final int TWENTYFOURBITGROUP = 24;
    private static final int EIGHTBIT = 8;
    private static final int SIXTEENBIT = 16;
    private static final int FOURBYTE = 4;
    private static final int SIGN = -128;
    private static final byte PAD = 61;
    private static byte[] base64Alphabet;
    private static byte[] lookUpBase64Alphabet;
    static final int[] base64;
    
    public static boolean isBase64(final String isValidString) {
        return isArrayByteBase64(isValidString.getBytes());
    }
    
    public static boolean isBase64(final byte octet) {
        return octet == 61 || Base64.base64Alphabet[(octet & 0xF) + (octet & 0xF0)] != -1;
    }
    
    public static boolean isArrayByteBase64(final byte[] arrayOctect) {
        final int length = arrayOctect.length;
        if (length == 0) {
            return true;
        }
        for (int i = 0; i < length; ++i) {
            if (!isBase64(arrayOctect[i])) {
                return false;
            }
        }
        return true;
    }
    
    public static byte[] encode(final byte[] binaryData) {
        final int lengthDataBits = binaryData.length * 8;
        final int fewerThan24bits = lengthDataBits % 24;
        final int numberTriplets = lengthDataBits / 24;
        byte[] encodedData;
        if (fewerThan24bits != 0) {
            encodedData = new byte[(numberTriplets + 1) * 4];
        }
        else {
            encodedData = new byte[numberTriplets * 4];
        }
        int i;
        for (i = 0; i < numberTriplets; ++i) {
            final int dataIndex = i * 3;
            final byte b1 = binaryData[dataIndex];
            final byte b2 = binaryData[dataIndex + 1];
            final byte b3 = binaryData[dataIndex + 2];
            final byte l = (byte)(b2 & 0xF);
            final byte k = (byte)(b1 & 0x3);
            final int encodedIndex = i * 4;
            final byte val1 = ((b1 & 0xFFFFFF80) == 0x0) ? ((byte)(b1 >> 2)) : ((byte)(b1 >> 2 ^ 0xC0));
            final byte val2 = ((b2 & 0xFFFFFF80) == 0x0) ? ((byte)(b2 >> 4)) : ((byte)(b2 >> 4 ^ 0xF0));
            final byte val3 = ((b3 & 0xFFFFFF80) == 0x0) ? ((byte)(b3 >> 6)) : ((byte)(b3 >> 6 ^ 0xFC));
            encodedData[encodedIndex] = Base64.lookUpBase64Alphabet[val1];
            encodedData[encodedIndex + 1] = Base64.lookUpBase64Alphabet[val2 | k << 4];
            encodedData[encodedIndex + 2] = Base64.lookUpBase64Alphabet[l << 2 | val3];
            encodedData[encodedIndex + 3] = Base64.lookUpBase64Alphabet[b3 & 0x3F];
        }
        final int dataIndex = i * 3;
        final int encodedIndex = i * 4;
        if (fewerThan24bits == 8) {
            final byte b1 = binaryData[dataIndex];
            final byte k = (byte)(b1 & 0x3);
            final byte val1 = ((b1 & 0xFFFFFF80) == 0x0) ? ((byte)(b1 >> 2)) : ((byte)(b1 >> 2 ^ 0xC0));
            encodedData[encodedIndex] = Base64.lookUpBase64Alphabet[val1];
            encodedData[encodedIndex + 1] = Base64.lookUpBase64Alphabet[k << 4];
            encodedData[encodedIndex + 3] = (encodedData[encodedIndex + 2] = 61);
        }
        else if (fewerThan24bits == 16) {
            final byte b1 = binaryData[dataIndex];
            final byte b2 = binaryData[dataIndex + 1];
            final byte l = (byte)(b2 & 0xF);
            final byte k = (byte)(b1 & 0x3);
            final byte val1 = ((b1 & 0xFFFFFF80) == 0x0) ? ((byte)(b1 >> 2)) : ((byte)(b1 >> 2 ^ 0xC0));
            final byte val2 = ((b2 & 0xFFFFFF80) == 0x0) ? ((byte)(b2 >> 4)) : ((byte)(b2 >> 4 ^ 0xF0));
            encodedData[encodedIndex] = Base64.lookUpBase64Alphabet[val1];
            encodedData[encodedIndex + 1] = Base64.lookUpBase64Alphabet[val2 | k << 4];
            encodedData[encodedIndex + 2] = Base64.lookUpBase64Alphabet[l << 2];
            encodedData[encodedIndex + 3] = 61;
        }
        return encodedData;
    }
    
    public static byte[] decode(final byte[] base64Data) {
        if (base64Data.length == 0) {
            return new byte[0];
        }
        final int numberQuadruple = base64Data.length / 4;
        int encodedIndex = 0;
        int lastData = base64Data.length;
        while (base64Data[lastData - 1] == 61) {
            if (--lastData == 0) {
                return new byte[0];
            }
        }
        final byte[] decodedData = new byte[lastData - numberQuadruple];
        for (int i = 0; i < numberQuadruple; ++i) {
            final int dataIndex = i * 4;
            final byte marker0 = base64Data[dataIndex + 2];
            final byte marker2 = base64Data[dataIndex + 3];
            final byte b1 = Base64.base64Alphabet[base64Data[dataIndex]];
            final byte b2 = Base64.base64Alphabet[base64Data[dataIndex + 1]];
            if (marker0 != 61 && marker2 != 61) {
                final byte b3 = Base64.base64Alphabet[marker0];
                final byte b4 = Base64.base64Alphabet[marker2];
                decodedData[encodedIndex] = (byte)(b1 << 2 | b2 >> 4);
                decodedData[encodedIndex + 1] = (byte)((b2 & 0xF) << 4 | (b3 >> 2 & 0xF));
                decodedData[encodedIndex + 2] = (byte)(b3 << 6 | b4);
            }
            else if (marker0 == 61) {
                decodedData[encodedIndex] = (byte)(b1 << 2 | b2 >> 4);
            }
            else if (marker2 == 61) {
                final byte b3 = Base64.base64Alphabet[marker0];
                decodedData[encodedIndex] = (byte)(b1 << 2 | b2 >> 4);
                decodedData[encodedIndex + 1] = (byte)((b2 & 0xF) << 4 | (b3 >> 2 & 0xF));
            }
            encodedIndex += 3;
        }
        return decodedData;
    }
    
    public static byte[] encode(final String data) {
        return encode(data.getBytes());
    }
    
    public static byte[] decode(final String data) {
        return decode(data.getBytes());
    }
    
    public static String base64Decode(final String orig) {
        final char[] chars = orig.toCharArray();
        final StringBuilder sb = new StringBuilder();
        int shift = 0;
        int acc = 0;
        for (int i = 0; i < chars.length; ++i) {
            final int v = Base64.base64[chars[i] & '\u00ff'];
            if (v < 64) {
                acc = (acc << 6 | v);
                shift += 6;
                if (shift >= 8) {
                    shift -= 8;
                    sb.append((char)(acc >> shift & 0xFF));
                }
            }
        }
        return sb.toString();
    }
    
    static {
        Base64.base64Alphabet = new byte[256];
        Base64.lookUpBase64Alphabet = new byte[64];
        for (int i = 0; i < 256; ++i) {
            Base64.base64Alphabet[i] = -1;
        }
        for (int i = 90; i >= 65; --i) {
            Base64.base64Alphabet[i] = (byte)(i - 65);
        }
        for (int i = 122; i >= 97; --i) {
            Base64.base64Alphabet[i] = (byte)(i - 97 + 26);
        }
        for (int i = 57; i >= 48; --i) {
            Base64.base64Alphabet[i] = (byte)(i - 48 + 52);
        }
        Base64.base64Alphabet[43] = 62;
        Base64.base64Alphabet[47] = 63;
        for (int i = 0; i <= 25; ++i) {
            Base64.lookUpBase64Alphabet[i] = (byte)(65 + i);
        }
        for (int i = 26, j = 0; i <= 51; ++i, ++j) {
            Base64.lookUpBase64Alphabet[i] = (byte)(97 + j);
        }
        for (int i = 52, j = 0; i <= 61; ++i, ++j) {
            Base64.lookUpBase64Alphabet[i] = (byte)(48 + j);
        }
        Base64.lookUpBase64Alphabet[62] = 43;
        Base64.lookUpBase64Alphabet[63] = 47;
        base64 = new int[] { 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 62, 64, 64, 64, 63, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61, 64, 64, 64, 64, 64, 64, 64, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 64, 64, 64, 64, 64, 64, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64 };
    }
}
