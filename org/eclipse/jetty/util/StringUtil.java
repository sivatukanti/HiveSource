// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.util;

import org.eclipse.jetty.util.log.Log;
import java.util.List;
import java.util.ArrayList;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import org.eclipse.jetty.util.log.Logger;

public class StringUtil
{
    private static final Logger LOG;
    private static final Trie<String> CHARSETS;
    public static final String ALL_INTERFACES = "0.0.0.0";
    public static final String CRLF = "\r\n";
    @Deprecated
    public static final String __LINE_SEPARATOR;
    public static final String __ISO_8859_1 = "iso-8859-1";
    public static final String __UTF8 = "utf-8";
    public static final String __UTF16 = "utf-16";
    public static final char[] lowercases;
    
    public static String normalizeCharset(final String s) {
        final String n = StringUtil.CHARSETS.get(s);
        return (n == null) ? s : n;
    }
    
    public static String normalizeCharset(final String s, final int offset, final int length) {
        final String n = StringUtil.CHARSETS.get(s, offset, length);
        return (n == null) ? s.substring(offset, offset + length) : n;
    }
    
    public static String asciiToLowerCase(final String s) {
        if (s == null) {
            return null;
        }
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
        final StringBuilder buf = new StringBuilder(s.length() + with.length());
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
    
    @Deprecated
    public static String unquote(final String s) {
        return QuotedStringTokenizer.unquote(s);
    }
    
    public static void append(final StringBuilder buf, final String s, final int offset, final int length) {
        synchronized (buf) {
            for (int end = offset + length, i = offset; i < end && i < s.length(); ++i) {
                buf.append(s.charAt(i));
            }
        }
    }
    
    public static void append(final StringBuilder buf, final byte b, final int base) {
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
    
    public static void append2digits(final StringBuilder buf, final int i) {
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
        return new String(b, offset, length, StandardCharsets.UTF_8);
    }
    
    public static String toString(final byte[] b, final int offset, final int length, final String charset) {
        try {
            return new String(b, offset, length, charset);
        }
        catch (UnsupportedEncodingException e) {
            throw new IllegalArgumentException(e);
        }
    }
    
    public static int indexOfControlChars(final String str) {
        if (str == null) {
            return -1;
        }
        for (int len = str.length(), i = 0; i < len; ++i) {
            if (Character.isISOControl(str.codePointAt(i))) {
                return i;
            }
        }
        return -1;
    }
    
    public static boolean isBlank(final String str) {
        if (str == null) {
            return true;
        }
        for (int len = str.length(), i = 0; i < len; ++i) {
            if (!Character.isWhitespace(str.codePointAt(i))) {
                return false;
            }
        }
        return true;
    }
    
    public static boolean isNotBlank(final String str) {
        if (str == null) {
            return false;
        }
        for (int len = str.length(), i = 0; i < len; ++i) {
            if (!Character.isWhitespace(str.codePointAt(i))) {
                return true;
            }
        }
        return false;
    }
    
    public static boolean isUTF8(final String charset) {
        return "utf-8".equalsIgnoreCase(charset) || "utf-8".equalsIgnoreCase(normalizeCharset(charset));
    }
    
    public static String printable(final String name) {
        if (name == null) {
            return null;
        }
        final StringBuilder buf = new StringBuilder(name.length());
        for (int i = 0; i < name.length(); ++i) {
            final char c = name.charAt(i);
            if (!Character.isISOControl(c)) {
                buf.append(c);
            }
        }
        return buf.toString();
    }
    
    public static String printable(final byte[] b) {
        final StringBuilder buf = new StringBuilder();
        for (int i = 0; i < b.length; ++i) {
            final char c = (char)b[i];
            if (Character.isWhitespace(c) || (c > ' ' && c < '\u007f')) {
                buf.append(c);
            }
            else {
                buf.append("0x");
                TypeUtil.toHex(b[i], buf);
            }
        }
        return buf.toString();
    }
    
    public static byte[] getBytes(final String s) {
        return s.getBytes(StandardCharsets.ISO_8859_1);
    }
    
    public static byte[] getUtf8Bytes(final String s) {
        return s.getBytes(StandardCharsets.UTF_8);
    }
    
    public static byte[] getBytes(final String s, final String charset) {
        try {
            return s.getBytes(charset);
        }
        catch (Exception e) {
            StringUtil.LOG.warn(e);
            return s.getBytes();
        }
    }
    
    public static String sidBytesToString(final byte[] sidBytes) {
        final StringBuilder sidString = new StringBuilder();
        sidString.append("S-");
        sidString.append(Byte.toString(sidBytes[0])).append('-');
        final StringBuilder tmpBuilder = new StringBuilder();
        for (int i = 2; i <= 7; ++i) {
            tmpBuilder.append(Integer.toHexString(sidBytes[i] & 0xFF));
        }
        sidString.append(Long.parseLong(tmpBuilder.toString(), 16));
        for (int subAuthorityCount = sidBytes[1], j = 0; j < subAuthorityCount; ++j) {
            final int offset = j * 4;
            tmpBuilder.setLength(0);
            tmpBuilder.append(String.format("%02X%02X%02X%02X", sidBytes[11 + offset] & 0xFF, sidBytes[10 + offset] & 0xFF, sidBytes[9 + offset] & 0xFF, sidBytes[8 + offset] & 0xFF));
            sidString.append('-').append(Long.parseLong(tmpBuilder.toString(), 16));
        }
        return sidString.toString();
    }
    
    public static byte[] sidStringToBytes(final String sidString) {
        final String[] sidTokens = sidString.split("-");
        final int subAuthorityCount = sidTokens.length - 3;
        int byteCount = 0;
        final byte[] sidBytes = new byte[8 + 4 * subAuthorityCount];
        sidBytes[byteCount++] = (byte)Integer.parseInt(sidTokens[1]);
        sidBytes[byteCount++] = (byte)subAuthorityCount;
        String hexStr;
        for (hexStr = Long.toHexString(Long.parseLong(sidTokens[2])); hexStr.length() < 12; hexStr = "0" + hexStr) {}
        for (int i = 0; i < hexStr.length(); i += 2) {
            sidBytes[byteCount++] = (byte)Integer.parseInt(hexStr.substring(i, i + 2), 16);
        }
        for (int i = 3; i < sidTokens.length; ++i) {
            for (hexStr = Long.toHexString(Long.parseLong(sidTokens[i])); hexStr.length() < 8; hexStr = "0" + hexStr) {}
            for (int j = hexStr.length(); j > 0; j -= 2) {
                sidBytes[byteCount++] = (byte)Integer.parseInt(hexStr.substring(j - 2, j), 16);
            }
        }
        return sidBytes;
    }
    
    public static int toInt(final String string, final int from) {
        int val = 0;
        boolean started = false;
        boolean minus = false;
        for (int i = from; i < string.length(); ++i) {
            final char b = string.charAt(i);
            if (b <= ' ') {
                if (started) {
                    break;
                }
            }
            else if (b >= '0' && b <= '9') {
                val = val * 10 + (b - '0');
                started = true;
            }
            else {
                if (b != '-' || started) {
                    break;
                }
                minus = true;
            }
        }
        if (started) {
            return minus ? (-val) : val;
        }
        throw new NumberFormatException(string);
    }
    
    public static long toLong(final String string) {
        long val = 0L;
        boolean started = false;
        boolean minus = false;
        for (int i = 0; i < string.length(); ++i) {
            final char b = string.charAt(i);
            if (b <= ' ') {
                if (started) {
                    break;
                }
            }
            else if (b >= '0' && b <= '9') {
                val = val * 10L + (b - '0');
                started = true;
            }
            else {
                if (b != '-' || started) {
                    break;
                }
                minus = true;
            }
        }
        if (started) {
            return minus ? (-val) : val;
        }
        throw new NumberFormatException(string);
    }
    
    public static String truncate(final String str, final int maxSize) {
        if (str == null) {
            return null;
        }
        if (str.length() <= maxSize) {
            return str;
        }
        return str.substring(0, maxSize);
    }
    
    public static String[] arrayFromString(final String s) {
        if (s == null) {
            return new String[0];
        }
        if (!s.startsWith("[") || !s.endsWith("]")) {
            throw new IllegalArgumentException();
        }
        if (s.length() == 2) {
            return new String[0];
        }
        return csvSplit(s, 1, s.length() - 2);
    }
    
    public static String[] csvSplit(final String s) {
        if (s == null) {
            return null;
        }
        return csvSplit(s, 0, s.length());
    }
    
    public static String[] csvSplit(final String s, final int off, final int len) {
        if (s == null) {
            return null;
        }
        if (off < 0 || len < 0 || off > s.length()) {
            throw new IllegalArgumentException();
        }
        final List<String> list = new ArrayList<String>();
        csvSplit(list, s, off, len);
        return list.toArray(new String[list.size()]);
    }
    
    public static List<String> csvSplit(List<String> list, final String s, int off, int len) {
        if (list == null) {
            list = new ArrayList<String>();
        }
        CsvSplitState state = CsvSplitState.PRE_DATA;
        final StringBuilder out = new StringBuilder();
        int last = -1;
        while (len > 0) {
            final char ch = s.charAt(off++);
            --len;
            switch (state) {
                case PRE_DATA: {
                    if (Character.isWhitespace(ch)) {
                        continue;
                    }
                    if ('\"' == ch) {
                        state = CsvSplitState.QUOTE;
                        continue;
                    }
                    if (',' == ch) {
                        list.add("");
                        continue;
                    }
                    state = CsvSplitState.DATA;
                    out.append(ch);
                    continue;
                }
                case DATA: {
                    if (Character.isWhitespace(ch)) {
                        last = out.length();
                        out.append(ch);
                        state = CsvSplitState.WHITE;
                        continue;
                    }
                    if (',' == ch) {
                        list.add(out.toString());
                        out.setLength(0);
                        state = CsvSplitState.PRE_DATA;
                        continue;
                    }
                    out.append(ch);
                    continue;
                }
                case WHITE: {
                    if (Character.isWhitespace(ch)) {
                        out.append(ch);
                        continue;
                    }
                    if (',' == ch) {
                        out.setLength(last);
                        list.add(out.toString());
                        out.setLength(0);
                        state = CsvSplitState.PRE_DATA;
                        continue;
                    }
                    state = CsvSplitState.DATA;
                    out.append(ch);
                    last = -1;
                    continue;
                }
                case QUOTE: {
                    if ('\\' == ch) {
                        state = CsvSplitState.SLOSH;
                        continue;
                    }
                    if ('\"' == ch) {
                        list.add(out.toString());
                        out.setLength(0);
                        state = CsvSplitState.POST_DATA;
                        continue;
                    }
                    out.append(ch);
                    continue;
                }
                case SLOSH: {
                    out.append(ch);
                    state = CsvSplitState.QUOTE;
                    continue;
                }
                case POST_DATA: {
                    if (',' == ch) {
                        state = CsvSplitState.PRE_DATA;
                        continue;
                    }
                    continue;
                }
                default: {
                    continue;
                }
            }
        }
        switch (state) {
            case DATA:
            case QUOTE:
            case SLOSH: {
                list.add(out.toString());
                break;
            }
            case WHITE: {
                out.setLength(last);
                list.add(out.toString());
                break;
            }
        }
        return list;
    }
    
    public static String sanitizeXmlString(final String html) {
        if (html == null) {
            return null;
        }
        int i = 0;
    Label_0098:
        while (i < html.length()) {
            final char c = html.charAt(i);
            switch (c) {
                case '\"':
                case '&':
                case '\'':
                case '<':
                case '>': {
                    break Label_0098;
                }
                default: {
                    if (Character.isISOControl(c) && !Character.isWhitespace(c)) {
                        break Label_0098;
                    }
                    ++i;
                    continue;
                }
            }
        }
        if (i == html.length()) {
            return html;
        }
        final StringBuilder out = new StringBuilder(html.length() * 4 / 3);
        out.append(html, 0, i);
        while (i < html.length()) {
            final char c2 = html.charAt(i);
            switch (c2) {
                case '&': {
                    out.append("&amp;");
                    break;
                }
                case '<': {
                    out.append("&lt;");
                    break;
                }
                case '>': {
                    out.append("&gt;");
                    break;
                }
                case '\'': {
                    out.append("&apos;");
                    break;
                }
                case '\"': {
                    out.append("&quot;");
                    break;
                }
                default: {
                    if (Character.isISOControl(c2) && !Character.isWhitespace(c2)) {
                        out.append('?');
                        break;
                    }
                    out.append(c2);
                    break;
                }
            }
            ++i;
        }
        return out.toString();
    }
    
    public static String valueOf(final Object object) {
        return (object == null) ? null : String.valueOf(object);
    }
    
    static {
        LOG = Log.getLogger(StringUtil.class);
        CHARSETS = new ArrayTrie<String>(256);
        __LINE_SEPARATOR = System.lineSeparator();
        StringUtil.CHARSETS.put("utf-8", "utf-8");
        StringUtil.CHARSETS.put("utf8", "utf-8");
        StringUtil.CHARSETS.put("utf-16", "utf-16");
        StringUtil.CHARSETS.put("utf16", "utf-16");
        StringUtil.CHARSETS.put("iso-8859-1", "iso-8859-1");
        StringUtil.CHARSETS.put("iso_8859_1", "iso-8859-1");
        lowercases = new char[] { '\0', '\u0001', '\u0002', '\u0003', '\u0004', '\u0005', '\u0006', '\u0007', '\b', '\t', '\n', '\u000b', '\f', '\r', '\u000e', '\u000f', '\u0010', '\u0011', '\u0012', '\u0013', '\u0014', '\u0015', '\u0016', '\u0017', '\u0018', '\u0019', '\u001a', '\u001b', '\u001c', '\u001d', '\u001e', '\u001f', ' ', '!', '\"', '#', '$', '%', '&', '\'', '(', ')', '*', '+', ',', '-', '.', '/', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', ':', ';', '<', '=', '>', '?', '@', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '[', '\\', ']', '^', '_', '`', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '{', '|', '}', '~', '\u007f' };
    }
    
    enum CsvSplitState
    {
        PRE_DATA, 
        QUOTE, 
        SLOSH, 
        DATA, 
        WHITE, 
        POST_DATA;
    }
}
