// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.util;

public class Hex
{
    public static byte[] decode(final String s) {
        final byte[] b = new byte[s.length() / 2];
        for (int i = 0; i < b.length; ++i) {
            final String hex = s.substring(2 * i, 2 * (i + 1));
            b[i] = (byte)Integer.parseInt(hex, 16);
        }
        return b;
    }
    
    public static byte[] decode(final byte[] hexString) {
        final byte[] b = new byte[hexString.length / 2];
        final char[] chars = new char[2];
        for (int i = 0; i < b.length; ++i) {
            chars[0] = (char)hexString[2 * i];
            chars[1] = (char)hexString[2 * i + 1];
            final String hex = new String(chars);
            b[i] = (byte)Integer.parseInt(hex, 16);
        }
        return b;
    }
    
    public static String encode(final byte[] b) {
        return encode(b, 0, b.length);
    }
    
    public static String encode(final byte[] b, final int offset, final int length) {
        final StringBuilder buf = new StringBuilder();
        for (int len = Math.min(offset + length, b.length), i = offset; i < len; ++i) {
            int c = b[i];
            if (c < 0) {
                c += 256;
            }
            if (c >= 0 && c <= 15) {
                buf.append('0');
            }
            buf.append(Integer.toHexString(c));
        }
        return buf.toString();
    }
}
