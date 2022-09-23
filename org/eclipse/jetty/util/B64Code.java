// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.util;

import java.io.IOException;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.charset.Charset;

public class B64Code
{
    private static final char __pad = '=';
    private static final char[] __rfc1421alphabet;
    private static final byte[] __rfc1421nibbles;
    private static final char[] __rfc4648urlAlphabet;
    private static final byte[] __rfc4648urlNibbles;
    
    private B64Code() {
    }
    
    public static String encode(final String s) {
        return encode(s, (Charset)null);
    }
    
    public static String encode(final String s, final String charEncoding) {
        byte[] bytes;
        if (charEncoding == null) {
            bytes = s.getBytes(StandardCharsets.ISO_8859_1);
        }
        else {
            bytes = s.getBytes(Charset.forName(charEncoding));
        }
        return new String(encode(bytes));
    }
    
    public static String encode(final String s, final Charset charEncoding) {
        final byte[] bytes = s.getBytes((charEncoding == null) ? StandardCharsets.ISO_8859_1 : charEncoding);
        return new String(encode(bytes));
    }
    
    public static char[] encode(final byte[] b) {
        if (b == null) {
            return null;
        }
        final int bLen = b.length;
        final int cLen = (bLen + 2) / 3 * 4;
        final char[] c = new char[cLen];
        int ci = 0;
        int bi = 0;
        byte b2;
        byte b3;
        byte b4;
        for (int stop = bLen / 3 * 3; bi < stop; b2 = b[bi++], b3 = b[bi++], b4 = b[bi++], c[ci++] = B64Code.__rfc1421alphabet[b2 >>> 2 & 0x3F], c[ci++] = B64Code.__rfc1421alphabet[(b2 << 4 & 0x3F) | (b3 >>> 4 & 0xF)], c[ci++] = B64Code.__rfc1421alphabet[(b3 << 2 & 0x3F) | (b4 >>> 6 & 0x3)], c[ci++] = B64Code.__rfc1421alphabet[b4 & 0x3F]) {}
        if (bLen != bi) {
            switch (bLen % 3) {
                case 2: {
                    b2 = b[bi++];
                    b3 = b[bi++];
                    c[ci++] = B64Code.__rfc1421alphabet[b2 >>> 2 & 0x3F];
                    c[ci++] = B64Code.__rfc1421alphabet[(b2 << 4 & 0x3F) | (b3 >>> 4 & 0xF)];
                    c[ci++] = B64Code.__rfc1421alphabet[b3 << 2 & 0x3F];
                    c[ci++] = '=';
                    break;
                }
                case 1: {
                    b2 = b[bi++];
                    c[ci++] = B64Code.__rfc1421alphabet[b2 >>> 2 & 0x3F];
                    c[ci++] = B64Code.__rfc1421alphabet[b2 << 4 & 0x3F];
                    c[ci++] = '=';
                    c[ci++] = '=';
                    break;
                }
            }
        }
        return c;
    }
    
    public static char[] encode(final byte[] b, final boolean rfc2045) {
        if (b == null) {
            return null;
        }
        if (!rfc2045) {
            return encode(b);
        }
        final int bLen = b.length;
        int cLen = (bLen + 2) / 3 * 4;
        cLen += 2 + 2 * (cLen / 76);
        final char[] c = new char[cLen];
        int ci = 0;
        int bi = 0;
        final int stop = bLen / 3 * 3;
        int l = 0;
        while (bi < stop) {
            final byte b2 = b[bi++];
            final byte b3 = b[bi++];
            final byte b4 = b[bi++];
            c[ci++] = B64Code.__rfc1421alphabet[b2 >>> 2 & 0x3F];
            c[ci++] = B64Code.__rfc1421alphabet[(b2 << 4 & 0x3F) | (b3 >>> 4 & 0xF)];
            c[ci++] = B64Code.__rfc1421alphabet[(b3 << 2 & 0x3F) | (b4 >>> 6 & 0x3)];
            c[ci++] = B64Code.__rfc1421alphabet[b4 & 0x3F];
            l += 4;
            if (l % 76 == 0) {
                c[ci++] = '\r';
                c[ci++] = '\n';
            }
        }
        if (bLen != bi) {
            switch (bLen % 3) {
                case 2: {
                    final byte b2 = b[bi++];
                    final byte b3 = b[bi++];
                    c[ci++] = B64Code.__rfc1421alphabet[b2 >>> 2 & 0x3F];
                    c[ci++] = B64Code.__rfc1421alphabet[(b2 << 4 & 0x3F) | (b3 >>> 4 & 0xF)];
                    c[ci++] = B64Code.__rfc1421alphabet[b3 << 2 & 0x3F];
                    c[ci++] = '=';
                    break;
                }
                case 1: {
                    final byte b2 = b[bi++];
                    c[ci++] = B64Code.__rfc1421alphabet[b2 >>> 2 & 0x3F];
                    c[ci++] = B64Code.__rfc1421alphabet[b2 << 4 & 0x3F];
                    c[ci++] = '=';
                    c[ci++] = '=';
                    break;
                }
            }
        }
        c[ci++] = '\r';
        c[ci++] = '\n';
        return c;
    }
    
    public static String decode(final String encoded, final String charEncoding) {
        final byte[] decoded = decode(encoded);
        if (charEncoding == null) {
            return new String(decoded);
        }
        return new String(decoded, Charset.forName(charEncoding));
    }
    
    public static String decode(final String encoded, final Charset charEncoding) {
        final byte[] decoded = decode(encoded);
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
                final byte b2 = B64Code.__rfc1421nibbles[b[bi++]];
                final byte b3 = B64Code.__rfc1421nibbles[b[bi++]];
                final byte b4 = B64Code.__rfc1421nibbles[b[bi++]];
                final byte b5 = B64Code.__rfc1421nibbles[b[bi++]];
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
                        final byte b2 = B64Code.__rfc1421nibbles[b[bi++]];
                        final byte b3 = B64Code.__rfc1421nibbles[b[bi++]];
                        final byte b4 = B64Code.__rfc1421nibbles[b[bi++]];
                        if (b2 < 0 || b3 < 0 || b4 < 0) {
                            throw new IllegalArgumentException("Not B64 encoded");
                        }
                        r[ri++] = (byte)(b2 << 2 | b3 >>> 4);
                        r[ri++] = (byte)(b3 << 4 | b4 >>> 2);
                        break;
                    }
                    case 1: {
                        final byte b2 = B64Code.__rfc1421nibbles[b[bi++]];
                        final byte b3 = B64Code.__rfc1421nibbles[b[bi++]];
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
    
    public static byte[] decode(final String encoded) {
        if (encoded == null) {
            return null;
        }
        final ByteArrayOutputStream bout = new ByteArrayOutputStream(4 * encoded.length() / 3);
        decode(encoded, bout);
        return bout.toByteArray();
    }
    
    public static void decode(final String encoded, final ByteArrayOutputStream bout) {
        if (encoded == null) {
            return;
        }
        if (bout == null) {
            throw new IllegalArgumentException("No outputstream for decoded bytes");
        }
        int ci = 0;
        final byte[] nibbles = new byte[4];
        int s = 0;
        while (ci < encoded.length()) {
            final char c = encoded.charAt(ci++);
            if (c == '=') {
                break;
            }
            if (Character.isWhitespace(c)) {
                continue;
            }
            final byte nibble = B64Code.__rfc1421nibbles[c];
            if (nibble < 0) {
                throw new IllegalArgumentException("Not B64 encoded");
            }
            nibbles[s++] = B64Code.__rfc1421nibbles[c];
            switch (s) {
                case 1: {
                    continue;
                }
                case 2: {
                    bout.write(nibbles[0] << 2 | nibbles[1] >>> 4);
                    continue;
                }
                case 3: {
                    bout.write(nibbles[1] << 4 | nibbles[2] >>> 2);
                    continue;
                }
                case 4: {
                    bout.write(nibbles[2] << 6 | nibbles[3]);
                    s = 0;
                    continue;
                }
            }
        }
    }
    
    public static byte[] decodeRFC4648URL(final String encoded) {
        if (encoded == null) {
            return null;
        }
        final ByteArrayOutputStream bout = new ByteArrayOutputStream(4 * encoded.length() / 3);
        decodeRFC4648URL(encoded, bout);
        return bout.toByteArray();
    }
    
    public static void decodeRFC4648URL(final String encoded, final ByteArrayOutputStream bout) {
        if (encoded == null) {
            return;
        }
        if (bout == null) {
            throw new IllegalArgumentException("No outputstream for decoded bytes");
        }
        int ci = 0;
        final byte[] nibbles = new byte[4];
        int s = 0;
        while (ci < encoded.length()) {
            final char c = encoded.charAt(ci++);
            if (c == '=') {
                break;
            }
            if (Character.isWhitespace(c)) {
                continue;
            }
            final byte nibble = B64Code.__rfc4648urlNibbles[c];
            if (nibble < 0) {
                throw new IllegalArgumentException("Not B64 encoded");
            }
            nibbles[s++] = B64Code.__rfc4648urlNibbles[c];
            switch (s) {
                case 1: {
                    continue;
                }
                case 2: {
                    bout.write(nibbles[0] << 2 | nibbles[1] >>> 4);
                    continue;
                }
                case 3: {
                    bout.write(nibbles[1] << 4 | nibbles[2] >>> 2);
                    continue;
                }
                case 4: {
                    bout.write(nibbles[2] << 6 | nibbles[3]);
                    s = 0;
                    continue;
                }
            }
        }
    }
    
    public static void encode(final int value, final Appendable buf) throws IOException {
        buf.append(B64Code.__rfc1421alphabet[0x3F & (0xFC000000 & value) >> 26]);
        buf.append(B64Code.__rfc1421alphabet[0x3F & (0x3F00000 & value) >> 20]);
        buf.append(B64Code.__rfc1421alphabet[0x3F & (0xFC000 & value) >> 14]);
        buf.append(B64Code.__rfc1421alphabet[0x3F & (0x3F00 & value) >> 8]);
        buf.append(B64Code.__rfc1421alphabet[0x3F & (0xFC & value) >> 2]);
        buf.append(B64Code.__rfc1421alphabet[0x3F & (0x3 & value) << 4]);
        buf.append('=');
    }
    
    public static void encode(final long lvalue, final Appendable buf) throws IOException {
        int value = (int)(0xFFFFFFFFFFFFFFFCL & lvalue >> 32);
        buf.append(B64Code.__rfc1421alphabet[0x3F & (0xFC000000 & value) >> 26]);
        buf.append(B64Code.__rfc1421alphabet[0x3F & (0x3F00000 & value) >> 20]);
        buf.append(B64Code.__rfc1421alphabet[0x3F & (0xFC000 & value) >> 14]);
        buf.append(B64Code.__rfc1421alphabet[0x3F & (0x3F00 & value) >> 8]);
        buf.append(B64Code.__rfc1421alphabet[0x3F & (0xFC & value) >> 2]);
        buf.append(B64Code.__rfc1421alphabet[0x3F & ((0x3 & value) << 4) + (0xF & (int)(lvalue >> 28))]);
        value = (0xFFFFFFF & (int)lvalue);
        buf.append(B64Code.__rfc1421alphabet[0x3F & (0xFC00000 & value) >> 22]);
        buf.append(B64Code.__rfc1421alphabet[0x3F & (0x3F0000 & value) >> 16]);
        buf.append(B64Code.__rfc1421alphabet[0x3F & (0xFC00 & value) >> 10]);
        buf.append(B64Code.__rfc1421alphabet[0x3F & (0x3F0 & value) >> 4]);
        buf.append(B64Code.__rfc1421alphabet[0x3F & (0xF & value) << 2]);
    }
    
    static {
        __rfc1421alphabet = new char[] { 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '+', '/' };
        __rfc1421nibbles = new byte[256];
        for (int i = 0; i < 256; ++i) {
            B64Code.__rfc1421nibbles[i] = -1;
        }
        for (byte b = 0; b < 64; ++b) {
            B64Code.__rfc1421nibbles[(byte)B64Code.__rfc1421alphabet[b]] = b;
        }
        B64Code.__rfc1421nibbles[61] = 0;
        __rfc4648urlAlphabet = new char[] { 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '-', '_' };
        __rfc4648urlNibbles = new byte[256];
        for (int i = 0; i < 256; ++i) {
            B64Code.__rfc4648urlNibbles[i] = -1;
        }
        for (byte b = 0; b < 64; ++b) {
            B64Code.__rfc4648urlNibbles[(byte)B64Code.__rfc4648urlAlphabet[b]] = b;
        }
        B64Code.__rfc4648urlNibbles[61] = 0;
    }
}
