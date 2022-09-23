// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.util;

import java.util.Locale;
import java.util.StringTokenizer;

public class StringUtil
{
    private static char[] hex_table;
    
    public static String[] split(final String str, final char c) {
        if (str == null) {
            throw new NullPointerException("str can't be null");
        }
        final StringTokenizer stringTokenizer = new StringTokenizer(str, String.valueOf(c));
        final int countTokens = stringTokenizer.countTokens();
        final String[] array = new String[countTokens];
        for (int i = 0; i < countTokens; ++i) {
            array[i] = stringTokenizer.nextToken();
        }
        return array;
    }
    
    public static final String formatForPrint(String string) {
        if (string.length() > 60) {
            final StringBuffer sb = new StringBuffer(string.substring(0, 60));
            sb.append("&");
            string = sb.toString();
        }
        return string;
    }
    
    public static String[] toStringArray(final Object[] array) {
        final int length = array.length;
        final String[] array2 = new String[length];
        for (int i = 0; i < length; ++i) {
            array2[i] = array[i].toString();
        }
        return array2;
    }
    
    public static byte[] getAsciiBytes(final String s) {
        final char[] charArray = s.toCharArray();
        final byte[] array = new byte[charArray.length];
        for (int i = 0; i < charArray.length; ++i) {
            array[i] = (byte)(charArray[i] & '\u007f');
        }
        return array;
    }
    
    public static String trimTrailing(final String s) {
        if (s == null) {
            return null;
        }
        int length;
        for (length = s.length(); length > 0 && Character.isWhitespace(s.charAt(length - 1)); --length) {}
        return s.substring(0, length);
    }
    
    public static String truncate(String substring, final int endIndex) {
        if (substring != null && substring.length() > endIndex) {
            substring = substring.substring(0, endIndex);
        }
        return substring;
    }
    
    public static String slice(final String s, final int beginIndex, final int n, final boolean b) {
        String s2 = s.substring(beginIndex, n + 1);
        if (b) {
            s2 = s2.trim();
        }
        return s2;
    }
    
    public static String toHexString(final byte[] array, final int n, final int n2) {
        final StringBuffer sb = new StringBuffer(n2 * 2);
        for (int n3 = n + n2, i = n; i < n3; ++i) {
            final int n4 = (array[i] & 0xF0) >>> 4;
            final int n5 = array[i] & 0xF;
            sb.append(StringUtil.hex_table[n4]);
            sb.append(StringUtil.hex_table[n5]);
        }
        return sb.toString();
    }
    
    public static byte[] fromHexString(final String s, final int n, final int n2) {
        if (n2 % 2 != 0) {
            return null;
        }
        final byte[] array = new byte[n2 / 2];
        int n3 = 0;
        for (int n4 = n + n2, i = n; i < n4; i += 2) {
            final int digit = Character.digit(s.charAt(i), 16);
            final int digit2 = Character.digit(s.charAt(i + 1), 16);
            if (digit == -1 || digit2 == -1) {
                return null;
            }
            array[n3++] = (byte)((digit << 4 & 0xF0) | (digit2 & 0xF));
        }
        return array;
    }
    
    public static String hexDump(final byte[] array) {
        final StringBuffer sb = new StringBuffer(array.length * 3);
        sb.append("Hex dump:\n");
        for (int i = 0; i < array.length; i += 16) {
            final String hexString = Integer.toHexString(i);
            for (int j = hexString.length(); j < 8; ++j) {
                sb.append("0");
            }
            sb.append(hexString);
            sb.append(":");
            for (int n = 0; n < 16 && i + n < array.length; ++n) {
                final byte b = array[i + n];
                if (n % 2 == 0) {
                    sb.append(" ");
                }
                final byte b2 = (byte)((b & 0xF0) >>> 4);
                final byte b3 = (byte)(b & 0xF);
                sb.append(StringUtil.hex_table[b2]);
                sb.append(StringUtil.hex_table[b3]);
            }
            sb.append("  ");
            for (int n2 = 0; n2 < 16 && i + n2 < array.length; ++n2) {
                final char c = (char)array[i + n2];
                if (Character.isLetterOrDigit(c)) {
                    sb.append(String.valueOf(c));
                }
                else {
                    sb.append(".");
                }
            }
            sb.append("\n");
        }
        return sb.toString();
    }
    
    public static String SQLToUpperCase(final String s) {
        return s.toUpperCase(Locale.ENGLISH);
    }
    
    public static boolean SQLEqualsIgnoreCase(final String s, final String s2) {
        return s2 != null && SQLToUpperCase(s).equals(SQLToUpperCase(s2));
    }
    
    public static String normalizeSQLIdentifier(final String s) {
        if (s.length() == 0) {
            return s;
        }
        if (s.charAt(0) == '\"' && s.length() >= 3 && s.charAt(s.length() - 1) == '\"') {
            return compressQuotes(s.substring(1, s.length() - 1), "\"\"");
        }
        return SQLToUpperCase(s);
    }
    
    public static String compressQuotes(final String s, final String s2) {
        String string = s;
        for (int i = string.indexOf(s2); i != -1; i = string.indexOf(s2, i + 1)) {
            string = string.substring(0, i + 1) + string.substring(i + 2);
        }
        return string;
    }
    
    static String quoteString(final String s, final char c) {
        final StringBuffer sb = new StringBuffer(s.length() + 2);
        sb.append(c);
        for (int i = 0; i < s.length(); ++i) {
            final char char1 = s.charAt(i);
            if (char1 == c) {
                sb.append(c);
            }
            sb.append(char1);
        }
        sb.append(c);
        return sb.toString();
    }
    
    public static String quoteStringLiteral(final String s) {
        return quoteString(s, '\'');
    }
    
    public static String stringify(final int[] array) {
        if (array == null) {
            return "null";
        }
        final StringBuffer sb = new StringBuffer();
        final int length = array.length;
        sb.append("[ ");
        for (int i = 0; i < length; ++i) {
            if (i > 0) {
                sb.append(", ");
            }
            sb.append(array[i]);
        }
        sb.append(" ]");
        return sb.toString();
    }
    
    public static String ensureIndent(String s, int n) {
        final StringBuffer sb = new StringBuffer();
        while (n-- > 0) {
            sb.append("\t");
        }
        if (s == null) {
            return sb.toString() + "null";
        }
        s = doRegExpA(s, sb.toString());
        s = doRegExpB(s);
        s = doRegExpC(s, sb.toString());
        return s;
    }
    
    private static String doRegExpA(final String s, final String str) {
        final StringBuffer sb = new StringBuffer();
        int n;
        for (n = 0; n < s.length() && s.charAt(n) == '\t'; ++n) {}
        sb.append(str);
        sb.append(s.substring(n));
        return sb.toString();
    }
    
    private static String doRegExpB(final String s) {
        final StringBuffer sb = new StringBuffer();
        int index;
        for (index = s.length() - 1; index >= 0 && s.charAt(index) == '\n'; --index) {}
        sb.append(s.substring(0, index + 1));
        return sb.toString();
    }
    
    private static String doRegExpC(final String s, final String str) {
        final StringBuffer sb = new StringBuffer();
        int i = 0;
        while (i < s.length()) {
            final char char1 = s.charAt(i);
            if (char1 == '\n') {
                sb.append(char1);
                int index;
                for (index = i + 1; index < s.length() && s.charAt(index) == '\t'; ++index) {}
                sb.append(str);
                i = index;
            }
            else {
                sb.append(char1);
                ++i;
            }
        }
        return sb.toString();
    }
    
    static {
        StringUtil.hex_table = new char[] { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
    }
}
