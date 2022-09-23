// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb.crypto.util;

public class BytesUtil
{
    public static short bytes2short(final byte[] bytes, final int offset, final boolean bigEndian) {
        short val = 0;
        if (bigEndian) {
            val += (short)((bytes[offset + 0] & 0xFF) << 8);
            val += (short)(bytes[offset + 1] & 0xFF);
        }
        else {
            val += (short)((bytes[offset + 1] & 0xFF) << 8);
            val += (short)(bytes[offset + 0] & 0xFF);
        }
        return val;
    }
    
    public static short bytes2short(final byte[] bytes, final boolean bigEndian) {
        return bytes2short(bytes, 0, bigEndian);
    }
    
    public static byte[] short2bytes(final int val, final boolean bigEndian) {
        final byte[] bytes = new byte[2];
        short2bytes(val, bytes, 0, bigEndian);
        return bytes;
    }
    
    public static void short2bytes(final int val, final byte[] bytes, final int offset, final boolean bigEndian) {
        if (bigEndian) {
            bytes[offset + 0] = (byte)(val >> 8 & 0xFF);
            bytes[offset + 1] = (byte)(val & 0xFF);
        }
        else {
            bytes[offset + 1] = (byte)(val >> 8 & 0xFF);
            bytes[offset + 0] = (byte)(val & 0xFF);
        }
    }
    
    public static int bytes2int(final byte[] bytes, final boolean bigEndian) {
        return bytes2int(bytes, 0, bigEndian);
    }
    
    public static int bytes2int(final byte[] bytes, final int offset, final boolean bigEndian) {
        int val = 0;
        if (bigEndian) {
            val += (bytes[offset + 0] & 0xFF) << 24;
            val += (bytes[offset + 1] & 0xFF) << 16;
            val += (bytes[offset + 2] & 0xFF) << 8;
            val += (bytes[offset + 3] & 0xFF);
        }
        else {
            val += (bytes[offset + 3] & 0xFF) << 24;
            val += (bytes[offset + 2] & 0xFF) << 16;
            val += (bytes[offset + 1] & 0xFF) << 8;
            val += (bytes[offset + 0] & 0xFF);
        }
        return val;
    }
    
    public static byte[] int2bytes(final int val, final boolean bigEndian) {
        final byte[] bytes = new byte[4];
        int2bytes(val, bytes, 0, bigEndian);
        return bytes;
    }
    
    public static void int2bytes(final int val, final byte[] bytes, final int offset, final boolean bigEndian) {
        if (bigEndian) {
            bytes[offset + 0] = (byte)(val >> 24 & 0xFF);
            bytes[offset + 1] = (byte)(val >> 16 & 0xFF);
            bytes[offset + 2] = (byte)(val >> 8 & 0xFF);
            bytes[offset + 3] = (byte)(val & 0xFF);
        }
        else {
            bytes[offset + 3] = (byte)(val >> 24 & 0xFF);
            bytes[offset + 2] = (byte)(val >> 16 & 0xFF);
            bytes[offset + 1] = (byte)(val >> 8 & 0xFF);
            bytes[offset + 0] = (byte)(val & 0xFF);
        }
    }
    
    public static byte[] long2bytes(final long val, final boolean bigEndian) {
        final byte[] bytes = new byte[8];
        long2bytes(val, bytes, 0, bigEndian);
        return bytes;
    }
    
    public static void long2bytes(final long val, final byte[] bytes, final int offset, final boolean bigEndian) {
        if (bigEndian) {
            for (int i = 0; i < 8; ++i) {
                bytes[i + offset] = (byte)(val >> (7 - i) * 8 & 0xFFL);
            }
        }
        else {
            for (int i = 0; i < 8; ++i) {
                bytes[i + offset] = (byte)(val >> i * 8 & 0xFFL);
            }
        }
    }
    
    public static long bytes2long(final byte[] bytes, final boolean bigEndian) {
        return bytes2long(bytes, 0, bigEndian);
    }
    
    public static long bytes2long(final byte[] bytes, final int offset, final boolean bigEndian) {
        long val = 0L;
        if (bigEndian) {
            for (int i = 0; i < 8; ++i) {
                val |= ((long)bytes[i + offset] & 0xFFL) << (7 - i) * 8;
            }
        }
        else {
            for (int i = 0; i < 8; ++i) {
                val |= ((long)bytes[i + offset] & 0xFFL) << i * 8;
            }
        }
        return val;
    }
    
    public static byte[] padding(final byte[] data, final int block) {
        final int len = data.length;
        final int paddingLen = (len % block != 0) ? (8 - len % block) : 0;
        if (paddingLen == 0) {
            return data;
        }
        final byte[] result = new byte[len + paddingLen];
        System.arraycopy(data, 0, result, 0, len);
        return result;
    }
    
    public static byte[] duplicate(final byte[] bytes) {
        return duplicate(bytes, 0, bytes.length);
    }
    
    public static byte[] duplicate(final byte[] bytes, final int offset, final int len) {
        final byte[] dup = new byte[len];
        System.arraycopy(bytes, offset, dup, 0, len);
        return dup;
    }
    
    public static void xor(final byte[] input, final int offset, final byte[] output) {
        for (int i = 0; i < output.length / 4; ++i) {
            final int a = bytes2int(input, offset + i * 4, true);
            int b = bytes2int(output, i * 4, true);
            b ^= a;
            int2bytes(b, output, i * 4, true);
        }
    }
    
    public static void xor(final byte[] a, final byte[] b, final byte[] output) {
        for (int i = 0; i < a.length / 4; ++i) {
            final int av = bytes2int(a, i * 4, true);
            final int bv = bytes2int(b, i * 4, true);
            final int v = av ^ bv;
            int2bytes(v, output, i * 4, true);
        }
    }
}
