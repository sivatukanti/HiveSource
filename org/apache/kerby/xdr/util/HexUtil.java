// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.xdr.util;

public class HexUtil
{
    static final String HEX_CHARS_STR = "0123456789ABCDEF";
    static final char[] HEX_CHARS;
    
    public static String bytesToHexFriendly(final byte[] bytes) {
        int len = bytes.length * 2;
        len += bytes.length;
        len += 2;
        final char[] hexChars = new char[len];
        hexChars[0] = '0';
        hexChars[1] = 'x';
        for (int j = 0; j < bytes.length; ++j) {
            final int v = bytes[j] & 0xFF;
            hexChars[j * 3 + 2] = HexUtil.HEX_CHARS[v >>> 4];
            hexChars[j * 3 + 3] = HexUtil.HEX_CHARS[v & 0xF];
            hexChars[j * 3 + 4] = ' ';
        }
        return new String(hexChars);
    }
    
    public static byte[] hex2bytesFriendly(String hexString) {
        String hexStr;
        hexString = (hexStr = hexString.toUpperCase());
        if (hexString.startsWith("0X")) {
            hexStr = hexString.substring(2);
        }
        final String[] hexParts = hexStr.split(" ");
        final byte[] bytes = new byte[hexParts.length];
        for (int i = 0; i < hexParts.length; ++i) {
            final char[] hexPart = hexParts[i].toCharArray();
            if (hexPart.length != 2) {
                throw new IllegalArgumentException("Invalid hex string to convert");
            }
            bytes[i] = (byte)(("0123456789ABCDEF".indexOf(hexPart[0]) << 4) + "0123456789ABCDEF".indexOf(hexPart[1]));
        }
        return bytes;
    }
    
    public static String bytesToHex(final byte[] bytes) {
        final int len = bytes.length * 2;
        final char[] hexChars = new char[len];
        for (int j = 0; j < bytes.length; ++j) {
            final int v = bytes[j] & 0xFF;
            hexChars[j * 2] = HexUtil.HEX_CHARS[v >>> 4];
            hexChars[j * 2 + 1] = HexUtil.HEX_CHARS[v & 0xF];
        }
        return new String(hexChars);
    }
    
    public static byte[] hex2bytes(String hexString) {
        hexString = hexString.toUpperCase();
        final int len = hexString.length() / 2;
        final byte[] bytes = new byte[len];
        final char[] hexChars = hexString.toCharArray();
        int i = 0;
        int j = 0;
        while (i < len) {
            bytes[i] = (byte)(("0123456789ABCDEF".indexOf(hexChars[j++]) << 4) + "0123456789ABCDEF".indexOf(hexChars[j++]));
            ++i;
        }
        return bytes;
    }
    
    static {
        HEX_CHARS = "0123456789ABCDEF".toCharArray();
    }
}
