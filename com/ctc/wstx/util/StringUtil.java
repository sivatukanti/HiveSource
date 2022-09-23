// 
// Decompiled by Procyon v0.5.36
// 

package com.ctc.wstx.util;

import java.util.Iterator;
import java.util.Collection;

public final class StringUtil
{
    static final char CHAR_SPACE = ' ';
    private static final char INT_SPACE = ' ';
    static String sLF;
    private static final int EOS = 65536;
    
    public static String getLF() {
        String lf = StringUtil.sLF;
        if (lf == null) {
            try {
                lf = System.getProperty("line.separator");
                StringUtil.sLF = ((lf == null) ? "\n" : lf);
            }
            catch (Throwable t) {
                lf = (StringUtil.sLF = "\n");
            }
        }
        return lf;
    }
    
    public static void appendLF(final StringBuilder sb) {
        sb.append(getLF());
    }
    
    public static String concatEntries(final Collection<?> coll, final String sep, String lastSep) {
        if (lastSep == null) {
            lastSep = sep;
        }
        final int len = coll.size();
        final StringBuilder sb = new StringBuilder(16 + (len << 3));
        final Iterator<?> it = coll.iterator();
        int i = 0;
        while (it.hasNext()) {
            if (i != 0) {
                if (i == len - 1) {
                    sb.append(lastSep);
                }
                else {
                    sb.append(sep);
                }
            }
            ++i;
            sb.append(it.next());
        }
        return sb.toString();
    }
    
    public static String normalizeSpaces(final char[] buf, final int origStart, int origEnd) {
        --origEnd;
        int start;
        int end;
        for (start = origStart, end = origEnd; start <= end && buf[start] == ' '; ++start) {}
        if (start > end) {
            return "";
        }
        while (end > start && buf[end] == ' ') {
            --end;
        }
        int i = start + 1;
        while (i < end) {
            if (buf[i] == ' ') {
                if (buf[i + 1] == ' ') {
                    break;
                }
                i += 2;
            }
            else {
                ++i;
            }
        }
        if (i < end) {
            final StringBuilder sb = new StringBuilder(end - start);
            sb.append(buf, start, i - start);
            while (i <= end) {
                char c = buf[i++];
                if (c == ' ') {
                    sb.append(' ');
                    do {
                        c = buf[i++];
                    } while (c == ' ');
                    sb.append(c);
                }
                else {
                    sb.append(c);
                }
            }
            return sb.toString();
        }
        if (start == origStart && end == origEnd) {
            return null;
        }
        return new String(buf, start, end - start + 1);
    }
    
    public static boolean isAllWhitespace(final String str) {
        for (int i = 0, len = str.length(); i < len; ++i) {
            if (str.charAt(i) > ' ') {
                return false;
            }
        }
        return true;
    }
    
    public static boolean isAllWhitespace(final char[] ch, int start, int len) {
        for (len += start; start < len; ++start) {
            if (ch[start] > ' ') {
                return false;
            }
        }
        return true;
    }
    
    public static boolean equalEncodings(final String str1, final String str2) {
        final int len1 = str1.length();
        final int len2 = str2.length();
        int i1 = 0;
        int i2 = 0;
        while (i1 < len1 || i2 < len2) {
            int c1 = (i1 >= len1) ? 65536 : str1.charAt(i1++);
            int c2 = (i2 >= len2) ? 65536 : str2.charAt(i2++);
            if (c1 == c2) {
                continue;
            }
            while (c1 <= 32 || c1 == 95 || c1 == 45) {
                c1 = ((i1 >= len1) ? 65536 : str1.charAt(i1++));
            }
            while (c2 <= 32 || c2 == 95 || c2 == 45) {
                c2 = ((i2 >= len2) ? 65536 : str2.charAt(i2++));
            }
            if (c1 == c2) {
                continue;
            }
            if (c1 == 65536 || c2 == 65536) {
                return false;
            }
            if (c1 < 127) {
                if (c1 <= 90 && c1 >= 65) {
                    c1 += 32;
                }
            }
            else {
                c1 = Character.toLowerCase((char)c1);
            }
            if (c2 < 127) {
                if (c2 <= 90 && c2 >= 65) {
                    c2 += 32;
                }
            }
            else {
                c2 = Character.toLowerCase((char)c2);
            }
            if (c1 != c2) {
                return false;
            }
        }
        return true;
    }
    
    public static boolean encodingStartsWith(final String enc, final String prefix) {
        final int len1 = enc.length();
        final int len2 = prefix.length();
        int i1 = 0;
        int i2 = 0;
        while (i1 < len1 || i2 < len2) {
            int c1 = (i1 >= len1) ? 65536 : enc.charAt(i1++);
            int c2 = (i2 >= len2) ? 65536 : prefix.charAt(i2++);
            if (c1 == c2) {
                continue;
            }
            while (c1 <= 32 || c1 == 95 || c1 == 45) {
                c1 = ((i1 >= len1) ? 65536 : enc.charAt(i1++));
            }
            while (c2 <= 32 || c2 == 95 || c2 == 45) {
                c2 = ((i2 >= len2) ? 65536 : prefix.charAt(i2++));
            }
            if (c1 == c2) {
                continue;
            }
            if (c2 == 65536) {
                return true;
            }
            if (c1 == 65536) {
                return false;
            }
            if (Character.toLowerCase((char)c1) != Character.toLowerCase((char)c2)) {
                return false;
            }
        }
        return true;
    }
    
    public static String trimEncoding(final String str, final boolean upperCase) {
        int i;
        int len;
        for (i = 0, len = str.length(); i < len; ++i) {
            final char c = str.charAt(i);
            if (c <= ' ') {
                break;
            }
            if (!Character.isLetterOrDigit(c)) {
                break;
            }
        }
        if (i == len) {
            return str;
        }
        final StringBuilder sb = new StringBuilder();
        if (i > 0) {
            sb.append(str.substring(0, i));
        }
        while (i < len) {
            char c2 = str.charAt(i);
            if (c2 > ' ' && Character.isLetterOrDigit(c2)) {
                if (upperCase) {
                    c2 = Character.toUpperCase(c2);
                }
                sb.append(c2);
            }
            ++i;
        }
        return sb.toString();
    }
    
    public static boolean matches(final String str, final char[] cbuf, final int offset, final int len) {
        if (str.length() != len) {
            return false;
        }
        for (int i = 0; i < len; ++i) {
            if (str.charAt(i) != cbuf[offset + i]) {
                return false;
            }
        }
        return true;
    }
    
    public static final boolean isSpace(final char c) {
        return c <= ' ';
    }
    
    static {
        StringUtil.sLF = null;
    }
}
