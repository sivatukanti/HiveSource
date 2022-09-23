// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.jute;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class Utils
{
    private static final char[] hexchars;
    
    private Utils() {
    }
    
    public static boolean bufEquals(final byte[] onearray, final byte[] twoarray) {
        if (onearray == twoarray) {
            return true;
        }
        final boolean ret = onearray.length == twoarray.length;
        if (!ret) {
            return ret;
        }
        for (int idx = 0; idx < onearray.length; ++idx) {
            if (onearray[idx] != twoarray[idx]) {
                return false;
            }
        }
        return true;
    }
    
    static String toXMLString(final String s) {
        if (s == null) {
            return "";
        }
        final StringBuilder sb = new StringBuilder();
        for (int idx = 0; idx < s.length(); ++idx) {
            final char ch = s.charAt(idx);
            if (ch == '<') {
                sb.append("&lt;");
            }
            else if (ch == '&') {
                sb.append("&amp;");
            }
            else if (ch == '%') {
                sb.append("%25");
            }
            else if (ch < ' ') {
                sb.append("%");
                sb.append(Utils.hexchars[ch / '\u0010']);
                sb.append(Utils.hexchars[ch % '\u0010']);
            }
            else {
                sb.append(ch);
            }
        }
        return sb.toString();
    }
    
    private static int h2c(final char ch) {
        if (ch >= '0' && ch <= '9') {
            return ch - '0';
        }
        if (ch >= 'A' && ch <= 'F') {
            return ch - 'A';
        }
        if (ch >= 'a' && ch <= 'f') {
            return ch - 'a';
        }
        return 0;
    }
    
    static String fromXMLString(final String s) {
        final StringBuilder sb = new StringBuilder();
        int idx = 0;
        while (idx < s.length()) {
            final char ch = s.charAt(idx++);
            if (ch == '%') {
                final char ch2 = s.charAt(idx++);
                final char ch3 = s.charAt(idx++);
                final char res = (char)(h2c(ch2) * 16 + h2c(ch3));
                sb.append(res);
            }
            else {
                sb.append(ch);
            }
        }
        return sb.toString();
    }
    
    static String toCSVString(final String s) {
        if (s == null) {
            return "";
        }
        final StringBuilder sb = new StringBuilder(s.length() + 1);
        sb.append('\'');
        for (int len = s.length(), i = 0; i < len; ++i) {
            final char c = s.charAt(i);
            switch (c) {
                case '\0': {
                    sb.append("%00");
                    break;
                }
                case '\n': {
                    sb.append("%0A");
                    break;
                }
                case '\r': {
                    sb.append("%0D");
                    break;
                }
                case ',': {
                    sb.append("%2C");
                    break;
                }
                case '}': {
                    sb.append("%7D");
                    break;
                }
                case '%': {
                    sb.append("%25");
                    break;
                }
                default: {
                    sb.append(c);
                    break;
                }
            }
        }
        return sb.toString();
    }
    
    static String fromCSVString(final String s) throws IOException {
        if (s.charAt(0) != '\'') {
            throw new IOException("Error deserializing string.");
        }
        final int len = s.length();
        final StringBuilder sb = new StringBuilder(len - 1);
        for (int i = 1; i < len; ++i) {
            final char c = s.charAt(i);
            if (c == '%') {
                final char ch1 = s.charAt(i + 1);
                final char ch2 = s.charAt(i + 2);
                i += 2;
                if (ch1 == '0' && ch2 == '0') {
                    sb.append('\0');
                }
                else if (ch1 == '0' && ch2 == 'A') {
                    sb.append('\n');
                }
                else if (ch1 == '0' && ch2 == 'D') {
                    sb.append('\r');
                }
                else if (ch1 == '2' && ch2 == 'C') {
                    sb.append(',');
                }
                else if (ch1 == '7' && ch2 == 'D') {
                    sb.append('}');
                }
                else {
                    if (ch1 != '2' || ch2 != '5') {
                        throw new IOException("Error deserializing string.");
                    }
                    sb.append('%');
                }
            }
            else {
                sb.append(c);
            }
        }
        return sb.toString();
    }
    
    static String toXMLBuffer(final byte[] barr) {
        if (barr == null || barr.length == 0) {
            return "";
        }
        final StringBuilder sb = new StringBuilder(2 * barr.length);
        for (int idx = 0; idx < barr.length; ++idx) {
            sb.append(Integer.toHexString(barr[idx]));
        }
        return sb.toString();
    }
    
    static byte[] fromXMLBuffer(final String s) throws IOException {
        final ByteArrayOutputStream stream = new ByteArrayOutputStream();
        if (s.length() == 0) {
            return stream.toByteArray();
        }
        final int blen = s.length() / 2;
        final byte[] barr = new byte[blen];
        for (int idx = 0; idx < blen; ++idx) {
            final char c1 = s.charAt(2 * idx);
            final char c2 = s.charAt(2 * idx + 1);
            barr[idx] = Byte.parseByte("" + c1 + c2, 16);
        }
        stream.write(barr);
        return stream.toByteArray();
    }
    
    static String toCSVBuffer(final byte[] barr) {
        if (barr == null || barr.length == 0) {
            return "";
        }
        final StringBuilder sb = new StringBuilder(barr.length + 1);
        sb.append('#');
        for (int idx = 0; idx < barr.length; ++idx) {
            sb.append(Integer.toHexString(barr[idx]));
        }
        return sb.toString();
    }
    
    static byte[] fromCSVBuffer(final String s) throws IOException {
        if (s.charAt(0) != '#') {
            throw new IOException("Error deserializing buffer.");
        }
        final ByteArrayOutputStream stream = new ByteArrayOutputStream();
        if (s.length() == 1) {
            return stream.toByteArray();
        }
        final int blen = (s.length() - 1) / 2;
        final byte[] barr = new byte[blen];
        for (int idx = 0; idx < blen; ++idx) {
            final char c1 = s.charAt(2 * idx + 1);
            final char c2 = s.charAt(2 * idx + 2);
            barr[idx] = Byte.parseByte("" + c1 + c2, 16);
        }
        stream.write(barr);
        return stream.toByteArray();
    }
    
    public static int compareBytes(final byte[] b1, final int off1, final int len1, final byte[] b2, final int off2, final int len2) {
        for (int i = 0; i < len1 && i < len2; ++i) {
            if (b1[off1 + i] != b2[off2 + i]) {
                return (b1[off1 + i] < b2[off2 + i]) ? -1 : 1;
            }
        }
        if (len1 != len2) {
            return (len1 < len2) ? -1 : 1;
        }
        return 0;
    }
    
    static {
        hexchars = new char[] { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
    }
}
