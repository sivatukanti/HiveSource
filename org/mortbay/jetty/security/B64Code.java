// 
// Decompiled by Procyon v0.5.36
// 

package org.mortbay.jetty.security;

import org.mortbay.util.StringUtil;
import java.io.UnsupportedEncodingException;

public class B64Code
{
    static final char pad = '=';
    static final char[] nibble2code;
    static byte[] code2nibble;
    
    public static String encode(final String s) {
        try {
            return encode(s, null);
        }
        catch (UnsupportedEncodingException e) {
            throw new IllegalArgumentException(e.toString());
        }
    }
    
    public static String encode(final String s, final String charEncoding) throws UnsupportedEncodingException {
        byte[] bytes;
        if (charEncoding == null) {
            bytes = s.getBytes(StringUtil.__ISO_8859_1);
        }
        else {
            bytes = s.getBytes(charEncoding);
        }
        return new String(encode(bytes));
    }
    
    public static char[] encode(final byte[] b) {
        if (b == null) {
            return null;
        }
        final int bLen = b.length;
        final char[] r = new char[(bLen + 2) / 3 * 4];
        int ri = 0;
        int bi = 0;
        byte b2;
        byte b3;
        byte b4;
        for (int stop = bLen / 3 * 3; bi < stop; b2 = b[bi++], b3 = b[bi++], b4 = b[bi++], r[ri++] = B64Code.nibble2code[b2 >>> 2 & 0x3F], r[ri++] = B64Code.nibble2code[(b2 << 4 & 0x3F) | (b3 >>> 4 & 0xF)], r[ri++] = B64Code.nibble2code[(b3 << 2 & 0x3F) | (b4 >>> 6 & 0x3)], r[ri++] = B64Code.nibble2code[b4 & 0x3F]) {}
        if (bLen != bi) {
            switch (bLen % 3) {
                case 2: {
                    b2 = b[bi++];
                    b3 = b[bi++];
                    r[ri++] = B64Code.nibble2code[b2 >>> 2 & 0x3F];
                    r[ri++] = B64Code.nibble2code[(b2 << 4 & 0x3F) | (b3 >>> 4 & 0xF)];
                    r[ri++] = B64Code.nibble2code[b3 << 2 & 0x3F];
                    r[ri++] = '=';
                    break;
                }
                case 1: {
                    b2 = b[bi++];
                    r[ri++] = B64Code.nibble2code[b2 >>> 2 & 0x3F];
                    r[ri++] = B64Code.nibble2code[b2 << 4 & 0x3F];
                    r[ri++] = '=';
                    r[ri++] = '=';
                    break;
                }
            }
        }
        return r;
    }
    
    public static String decode(final String s) {
        try {
            return decode(s, StringUtil.__ISO_8859_1);
        }
        catch (UnsupportedEncodingException e) {
            throw new IllegalArgumentException(e.toString());
        }
    }
    
    public static String decode(final String s, final String charEncoding) throws UnsupportedEncodingException {
        final byte[] decoded = decode(s.toCharArray());
        if (charEncoding == null) {
            return new String(decoded);
        }
        return new String(decoded, charEncoding);
    }
    
    public static byte[] decode(final char[] b) {
        if (b == null) {
            return null;
        }
        final int bLen = b.length;
        if (bLen % 4 != 0) {
            throw new IllegalArgumentException("Input block size is not 4");
        }
        int li;
        for (li = bLen - 1; li >= 0 && b[li] == '='; --li) {}
        if (li < 0) {
            return new byte[0];
        }
        final int rLen = (li + 1) * 3 / 4;
        final byte[] r = new byte[rLen];
        int ri = 0;
        int bi = 0;
        final int stop = rLen / 3 * 3;
        try {
            while (ri < stop) {
                final byte b2 = B64Code.code2nibble[b[bi++]];
                final byte b3 = B64Code.code2nibble[b[bi++]];
                final byte b4 = B64Code.code2nibble[b[bi++]];
                final byte b5 = B64Code.code2nibble[b[bi++]];
                if (b2 < 0 || b3 < 0 || b4 < 0 || b5 < 0) {
                    throw new IllegalArgumentException("Not B64 encoded");
                }
                r[ri++] = (byte)(b2 << 2 | b3 >>> 4);
                r[ri++] = (byte)(b3 << 4 | b4 >>> 2);
                r[ri++] = (byte)(b4 << 6 | b5);
            }
            if (rLen != ri) {
                switch (rLen % 3) {
                    case 2: {
                        final byte b2 = B64Code.code2nibble[b[bi++]];
                        final byte b3 = B64Code.code2nibble[b[bi++]];
                        final byte b4 = B64Code.code2nibble[b[bi++]];
                        if (b2 < 0 || b3 < 0 || b4 < 0) {
                            throw new IllegalArgumentException("Not B64 encoded");
                        }
                        r[ri++] = (byte)(b2 << 2 | b3 >>> 4);
                        r[ri++] = (byte)(b3 << 4 | b4 >>> 2);
                        break;
                    }
                    case 1: {
                        final byte b2 = B64Code.code2nibble[b[bi++]];
                        final byte b3 = B64Code.code2nibble[b[bi++]];
                        if (b2 < 0 || b3 < 0) {
                            throw new IllegalArgumentException("Not B64 encoded");
                        }
                        r[ri++] = (byte)(b2 << 2 | b3 >>> 4);
                        break;
                    }
                }
            }
        }
        catch (IndexOutOfBoundsException e) {
            throw new IllegalArgumentException("char " + bi + " was not B64 encoded");
        }
        return r;
    }
    
    static {
        nibble2code = new char[] { 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '+', '/' };
        B64Code.code2nibble = null;
        B64Code.code2nibble = new byte[256];
        for (int i = 0; i < 256; ++i) {
            B64Code.code2nibble[i] = -1;
        }
        for (byte b = 0; b < 64; ++b) {
            B64Code.code2nibble[(byte)B64Code.nibble2code[b]] = b;
        }
        B64Code.code2nibble[61] = 0;
    }
}
