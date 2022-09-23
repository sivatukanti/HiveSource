// 
// Decompiled by Procyon v0.5.36
// 

package parquet.org.apache.thrift.protocol;

class TBase64Utils
{
    private static final String ENCODE_TABLE = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/";
    private static final byte[] DECODE_TABLE;
    
    static final void encode(final byte[] src, final int srcOff, final int len, final byte[] dst, final int dstOff) {
        dst[dstOff] = (byte)"ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/".charAt(src[srcOff] >> 2 & 0x3F);
        if (len == 3) {
            dst[dstOff + 1] = (byte)"ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/".charAt((src[srcOff] << 4) + (src[srcOff + 1] >> 4) & 0x3F);
            dst[dstOff + 2] = (byte)"ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/".charAt((src[srcOff + 1] << 2) + (src[srcOff + 2] >> 6) & 0x3F);
            dst[dstOff + 3] = (byte)"ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/".charAt(src[srcOff + 2] & 0x3F);
        }
        else if (len == 2) {
            dst[dstOff + 1] = (byte)"ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/".charAt((src[srcOff] << 4) + (src[srcOff + 1] >> 4) & 0x3F);
            dst[dstOff + 2] = (byte)"ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/".charAt(src[srcOff + 1] << 2 & 0x3F);
        }
        else {
            dst[dstOff + 1] = (byte)"ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/".charAt(src[srcOff] << 4 & 0x3F);
        }
    }
    
    static final void decode(final byte[] src, final int srcOff, final int len, final byte[] dst, final int dstOff) {
        dst[dstOff] = (byte)(TBase64Utils.DECODE_TABLE[src[srcOff] & 0xFF] << 2 | TBase64Utils.DECODE_TABLE[src[srcOff + 1] & 0xFF] >> 4);
        if (len > 2) {
            dst[dstOff + 1] = (byte)((TBase64Utils.DECODE_TABLE[src[srcOff + 1] & 0xFF] << 4 & 0xF0) | TBase64Utils.DECODE_TABLE[src[srcOff + 2] & 0xFF] >> 2);
            if (len > 3) {
                dst[dstOff + 2] = (byte)((TBase64Utils.DECODE_TABLE[src[srcOff + 2] & 0xFF] << 6 & 0xC0) | TBase64Utils.DECODE_TABLE[src[srcOff + 3] & 0xFF]);
            }
        }
    }
    
    static {
        DECODE_TABLE = new byte[] { -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 62, -1, -1, -1, 63, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61, -1, -1, -1, -1, -1, -1, -1, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, -1, -1, -1, -1, -1, -1, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 };
    }
}
