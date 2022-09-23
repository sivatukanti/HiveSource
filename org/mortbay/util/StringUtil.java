// 
// Decompiled by Procyon v0.5.36
// 

package org.mortbay.util;

import java.io.UnsupportedEncodingException;

public class StringUtil
{
    public static final String CRLF = "\r\n";
    public static final String __LINE_SEPARATOR;
    public static final String __ISO_8859_1;
    public static final String __UTF8 = "UTF-8";
    public static final String __UTF8Alt = "UTF8";
    public static final String __UTF16 = "UTF-16";
    private static char[] lowercases;
    
    public static String asciiToLowerCase(final String s) {
        char[] c = null;
        int i = s.length();
        while (i-- > 0) {
            final char c2 = s.charAt(i);
            if (c2 <= '\u007f') {
                final char c3 = StringUtil.lowercases[c2];
                if (c2 != c3) {
                    c = s.toCharArray();
                    c[i] = c3;
                    break;
                }
                continue;
            }
        }
        while (i-- > 0) {
            if (c[i] <= '\u007f') {
                c[i] = StringUtil.lowercases[c[i]];
            }
        }
        return (c == null) ? s : new String(c);
    }
    
    public static boolean startsWithIgnoreCase(final String s, final String w) {
        if (w == null) {
            return true;
        }
        if (s == null || s.length() < w.length()) {
            return false;
        }
        for (int i = 0; i < w.length(); ++i) {
            char c1 = s.charAt(i);
            char c2 = w.charAt(i);
            if (c1 != c2) {
                if (c1 <= '\u007f') {
                    c1 = StringUtil.lowercases[c1];
                }
                if (c2 <= '\u007f') {
                    c2 = StringUtil.lowercases[c2];
                }
                if (c1 != c2) {
                    return false;
                }
            }
        }
        return true;
    }
    
    public static boolean endsWithIgnoreCase(final String s, final String w) {
        if (w == null) {
            return true;
        }
        if (s == null) {
            return false;
        }
        int sl = s.length();
        final int wl = w.length();
        if (sl < wl) {
            return false;
        }
        int i = wl;
        while (i-- > 0) {
            char c1 = s.charAt(--sl);
            char c2 = w.charAt(i);
            if (c1 != c2) {
                if (c1 <= '\u007f') {
                    c1 = StringUtil.lowercases[c1];
                }
                if (c2 <= '\u007f') {
                    c2 = StringUtil.lowercases[c2];
                }
                if (c1 != c2) {
                    return false;
                }
                continue;
            }
        }
        return true;
    }
    
    public static int indexFrom(final String s, final String chars) {
        for (int i = 0; i < s.length(); ++i) {
            if (chars.indexOf(s.charAt(i)) >= 0) {
                return i;
            }
        }
        return -1;
    }
    
    public static String replace(final String s, final String sub, final String with) {
        int c = 0;
        int i = s.indexOf(sub, c);
        if (i == -1) {
            return s;
        }
        final StringBuffer buf = new StringBuffer(s.length() + with.length());
        synchronized (buf) {
            do {
                buf.append(s.substring(c, i));
                buf.append(with);
                c = i + sub.length();
            } while ((i = s.indexOf(sub, c)) != -1);
            if (c < s.length()) {
                buf.append(s.substring(c, s.length()));
            }
            return buf.toString();
        }
    }
    
    public static String unquote(final String s) {
        return QuotedStringTokenizer.unquote(s);
    }
    
    public static void append(final StringBuffer buf, final String s, final int offset, final int length) {
        synchronized (buf) {
            for (int end = offset + length, i = offset; i < end && i < s.length(); ++i) {
                buf.append(s.charAt(i));
            }
        }
    }
    
    public static void append(final StringBuffer buf, final byte b, final int base) {
        final int bi = 0xFF & b;
        int c = 48 + bi / base % base;
        if (c > 57) {
            c = 97 + (c - 48 - 10);
        }
        buf.append((char)c);
        c = 48 + bi % base;
        if (c > 57) {
            c = 97 + (c - 48 - 10);
        }
        buf.append((char)c);
    }
    
    public static void append2digits(final StringBuffer buf, final int i) {
        if (i < 100) {
            buf.append((char)(i / 10 + 48));
            buf.append((char)(i % 10 + 48));
        }
    }
    
    public static String nonNull(final String s) {
        if (s == null) {
            return "";
        }
        return s;
    }
    
    public static boolean equals(final String s, final char[] buf, final int offset, final int length) {
        if (s.length() != length) {
            return false;
        }
        for (int i = 0; i < length; ++i) {
            if (buf[offset + i] != s.charAt(i)) {
                return false;
            }
        }
        return true;
    }
    
    public static String toUTF8String(final byte[] b, final int offset, final int length) {
        try {
            if (length < 32) {
                final Utf8StringBuffer buffer = new Utf8StringBuffer(length);
                buffer.append(b, offset, length);
                return buffer.toString();
            }
            return new String(b, offset, length, "UTF-8");
        }
        catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public static String toString(final byte[] b, final int offset, final int length, final String charset) {
        if (charset == null || isUTF8(charset)) {
            return toUTF8String(b, offset, length);
        }
        try {
            return new String(b, offset, length, charset);
        }
        catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public static boolean isUTF8(final String charset) {
        return charset == "UTF-8" || "UTF-8".equalsIgnoreCase(charset) || "UTF8".equalsIgnoreCase(charset);
    }
    
    public static String printable(final String name) {
        if (name == null) {
            return null;
        }
        final StringBuffer buf = new StringBuffer(name.length());
        for (int i = 0; i < name.length(); ++i) {
            final char c = name.charAt(i);
            if (!Character.isISOControl(c)) {
                buf.append(c);
            }
        }
        return buf.toString();
    }
    
    static {
        __LINE_SEPARATOR = System.getProperty("line.separator", "\n");
        String iso = System.getProperty("ISO_8859_1");
        if (iso == null) {
            try {
                final String s = new String(new byte[] { 20 }, "ISO-8859-1");
                iso = "ISO-8859-1";
            }
            catch (UnsupportedEncodingException e) {
                iso = "ISO8859_1";
            }
        }
        __ISO_8859_1 = iso;
        StringUtil.lowercases = new char[] { '\0', '\u0001', '\u0002', '\u0003', '\u0004', '\u0005', '\u0006', '\u0007', '\b', '\t', '\n', '\u000b', '\f', '\r', '\u000e', '\u000f', '\u0010', '\u0011', '\u0012', '\u0013', '\u0014', '\u0015', '\u0016', '\u0017', '\u0018', '\u0019', '\u001a', '\u001b', '\u001c', '\u001d', '\u001e', '\u001f', ' ', '!', '\"', '#', '$', '%', '&', '\'', '(', ')', '*', '+', ',', '-', '.', '/', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', ':', ';', '<', '=', '>', '?', '@', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '[', '\\', ']', '^', '_', '`', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '{', '|', '}', '~', '\u007f' };
    }
}
