// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.asn1.type;

import java.util.List;
import java.util.ArrayList;
import java.io.OutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.apache.kerby.asn1.UniversalTag;

public abstract class Asn1String extends Asn1Simple<String>
{
    public Asn1String(final UniversalTag universalTag) {
        super(universalTag, null);
    }
    
    public Asn1String(final UniversalTag tagNo, final String value) {
        super(tagNo, value);
    }
    
    @Override
    protected void toBytes() {
        if (this.getValue() != null) {
            final byte[] bytes = this.getValue().getBytes(StandardCharsets.US_ASCII);
            this.setBytes(bytes);
        }
    }
    
    @Override
    protected int encodingBodyLength() {
        if (this.getValue() != null) {
            return this.getValue().length();
        }
        return 0;
    }
    
    @Override
    protected void toValue() throws IOException {
        final byte[] bytes = this.getBytes();
        this.setValue(new String(bytes, StandardCharsets.US_ASCII));
    }
    
    public static String fromUTF8ByteArray(final byte[] bytes) {
        int i = 0;
        int length = 0;
        while (i < bytes.length) {
            ++length;
            if ((bytes[i] & 0xF0) == 0xF0) {
                ++length;
                i += 4;
            }
            else if ((bytes[i] & 0xE0) == 0xE0) {
                i += 3;
            }
            else if ((bytes[i] & 0xC0) == 0xC0) {
                i += 2;
            }
            else {
                ++i;
            }
        }
        final char[] cs = new char[length];
        i = 0;
        length = 0;
        while (i < bytes.length) {
            char ch;
            if ((bytes[i] & 0xF0) == 0xF0) {
                final int codePoint = (bytes[i] & 0x3) << 18 | (bytes[i + 1] & 0x3F) << 12 | (bytes[i + 2] & 0x3F) << 6 | (bytes[i + 3] & 0x3F);
                final int u = codePoint - 65536;
                final char w1 = (char)(0xD800 | u >> 10);
                final char w2 = (char)(0xDC00 | (u & 0x3FF));
                cs[length++] = w1;
                ch = w2;
                i += 4;
            }
            else if ((bytes[i] & 0xE0) == 0xE0) {
                ch = (char)((bytes[i] & 0xF) << 12 | (bytes[i + 1] & 0x3F) << 6 | (bytes[i + 2] & 0x3F));
                i += 3;
            }
            else if ((bytes[i] & 0xD0) == 0xD0) {
                ch = (char)((bytes[i] & 0x1F) << 6 | (bytes[i + 1] & 0x3F));
                i += 2;
            }
            else if ((bytes[i] & 0xC0) == 0xC0) {
                ch = (char)((bytes[i] & 0x1F) << 6 | (bytes[i + 1] & 0x3F));
                i += 2;
            }
            else {
                ch = (char)(bytes[i] & 0xFF);
                ++i;
            }
            cs[length++] = ch;
        }
        return new String(cs);
    }
    
    public static byte[] toUTF8ByteArray(final String string) {
        return toUTF8ByteArray(string.toCharArray());
    }
    
    public static byte[] toUTF8ByteArray(final char[] string) {
        final ByteArrayOutputStream bOut = new ByteArrayOutputStream();
        try {
            toUTF8ByteArray(string, bOut);
        }
        catch (IOException e) {
            throw new IllegalStateException("cannot encode string to byte array!");
        }
        return bOut.toByteArray();
    }
    
    public static void toUTF8ByteArray(final char[] string, final OutputStream sOut) throws IOException {
        final char[] c = string;
        for (int i = 0; i < c.length; ++i) {
            char ch = c[i];
            if (ch < '\u0080') {
                sOut.write(ch);
            }
            else if (ch < '\u0800') {
                sOut.write(0xC0 | ch >> 6);
                sOut.write(0x80 | (ch & '?'));
            }
            else if (ch >= '\ud800' && ch <= '\udfff') {
                if (i + 1 >= c.length) {
                    throw new IllegalStateException("invalid UTF-16 codepoint");
                }
                final char w1 = ch;
                final char w2;
                ch = (w2 = c[++i]);
                if (w1 > '\udbff') {
                    throw new IllegalStateException("invalid UTF-16 codepoint");
                }
                final int codePoint = (w1 & '\u03ff') << 10 | (w2 & '\u03ff') + 65536;
                sOut.write(0xF0 | codePoint >> 18);
                sOut.write(0x80 | (codePoint >> 12 & 0x3F));
                sOut.write(0x80 | (codePoint >> 6 & 0x3F));
                sOut.write(0x80 | (codePoint & 0x3F));
            }
            else {
                sOut.write(0xE0 | ch >> 12);
                sOut.write(0x80 | (ch >> 6 & 0x3F));
                sOut.write(0x80 | (ch & '?'));
            }
        }
    }
    
    public static String toUpperCase(final String string) {
        boolean changed = false;
        final char[] chars = string.toCharArray();
        for (int i = 0; i != chars.length; ++i) {
            final char ch = chars[i];
            if ('a' <= ch && 'z' >= ch) {
                changed = true;
                chars[i] = (char)(ch - 'a' + 65);
            }
        }
        if (changed) {
            return new String(chars);
        }
        return string;
    }
    
    public static String toLowerCase(final String string) {
        boolean changed = false;
        final char[] chars = string.toCharArray();
        for (int i = 0; i != chars.length; ++i) {
            final char ch = chars[i];
            if ('A' <= ch && 'Z' >= ch) {
                changed = true;
                chars[i] = (char)(ch - 'A' + 97);
            }
        }
        if (changed) {
            return new String(chars);
        }
        return string;
    }
    
    public static byte[] toByteArray(final char[] chars) {
        final byte[] bytes = new byte[chars.length];
        for (int i = 0; i != bytes.length; ++i) {
            bytes[i] = (byte)chars[i];
        }
        return bytes;
    }
    
    public static byte[] toByteArray(final String string) {
        final byte[] bytes = new byte[string.length()];
        for (int i = 0; i != bytes.length; ++i) {
            final char ch = string.charAt(i);
            bytes[i] = (byte)ch;
        }
        return bytes;
    }
    
    public static String fromByteArray(final byte[] bytes) {
        return new String(asCharArray(bytes));
    }
    
    public static char[] asCharArray(final byte[] bytes) {
        final char[] chars = new char[bytes.length];
        for (int i = 0; i != chars.length; ++i) {
            chars[i] = (char)(bytes[i] & 0xFF);
        }
        return chars;
    }
    
    public static String[] split(String input, final char delimiter) {
        final List<String> v = new ArrayList<String>();
        boolean moreTokens = true;
        while (moreTokens) {
            final int tokenLocation = input.indexOf(delimiter);
            if (tokenLocation > 0) {
                final String subString = input.substring(0, tokenLocation);
                v.add(subString);
                input = input.substring(tokenLocation + 1);
            }
            else {
                moreTokens = false;
                v.add(input);
            }
        }
        return v.toArray(new String[v.size()]);
    }
}
