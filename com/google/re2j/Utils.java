// 
// Decompiled by Procyon v0.5.36
// 

package com.google.re2j;

abstract class Utils
{
    static final int[] EMPTY_INTS;
    private static final String METACHARACTERS = "\\.+*?()|[]{}^$";
    static final int EMPTY_BEGIN_LINE = 1;
    static final int EMPTY_END_LINE = 2;
    static final int EMPTY_BEGIN_TEXT = 4;
    static final int EMPTY_END_TEXT = 8;
    static final int EMPTY_WORD_BOUNDARY = 16;
    static final int EMPTY_NO_WORD_BOUNDARY = 32;
    static final int EMPTY_ALL = -1;
    
    static boolean isalnum(final int c) {
        return (48 <= c && c <= 57) || (65 <= c && c <= 90) || (97 <= c && c <= 122);
    }
    
    static int unhex(final int c) {
        if (48 <= c && c <= 57) {
            return c - 48;
        }
        if (97 <= c && c <= 102) {
            return c - 97 + 10;
        }
        if (65 <= c && c <= 70) {
            return c - 65 + 10;
        }
        return -1;
    }
    
    static void escapeRune(final StringBuilder out, final int rune) {
        if (Unicode.isPrint(rune)) {
            if ("\\.+*?()|[]{}^$".indexOf((char)rune) >= 0) {
                out.append('\\');
            }
            out.appendCodePoint(rune);
            return;
        }
        switch (rune) {
            case 34: {
                out.append("\\\"");
                break;
            }
            case 92: {
                out.append("\\\\");
                break;
            }
            case 9: {
                out.append("\\t");
                break;
            }
            case 10: {
                out.append("\\n");
                break;
            }
            case 13: {
                out.append("\\r");
                break;
            }
            case 8: {
                out.append("\\b");
                break;
            }
            case 12: {
                out.append("\\f");
                break;
            }
            default: {
                final String s = Integer.toHexString(rune);
                if (rune < 256) {
                    out.append("\\x");
                    if (s.length() == 1) {
                        out.append('0');
                    }
                    out.append(s);
                    break;
                }
                out.append("\\x{").append(s).append('}');
                break;
            }
        }
    }
    
    static int[] stringToRunes(final String str) {
        final int charlen = str.length();
        final int runelen = str.codePointCount(0, charlen);
        final int[] runes = new int[runelen];
        int r = 0;
        int rune;
        for (int c = 0; c < charlen; c += Character.charCount(rune)) {
            rune = str.codePointAt(c);
            runes[r++] = rune;
        }
        return runes;
    }
    
    static String runeToString(final int r) {
        final char c = (char)r;
        return (r == c) ? String.valueOf(c) : new String(Character.toChars(c));
    }
    
    static int[] subarray(final int[] array, final int start, final int end) {
        final int[] r = new int[end - start];
        for (int i = start; i < end; ++i) {
            r[i - start] = array[i];
        }
        return r;
    }
    
    static byte[] subarray(final byte[] array, final int start, final int end) {
        final byte[] r = new byte[end - start];
        for (int i = start; i < end; ++i) {
            r[i - start] = array[i];
        }
        return r;
    }
    
    static int indexOf(final byte[] source, final byte[] target, int fromIndex) {
        if (fromIndex >= source.length) {
            return (target.length == 0) ? source.length : -1;
        }
        if (fromIndex < 0) {
            fromIndex = 0;
        }
        if (target.length == 0) {
            return fromIndex;
        }
        final byte first = target[0];
        for (int i = fromIndex, max = source.length - target.length; i <= max; ++i) {
            if (source[i] != first) {
                while (++i <= max && source[i] != first) {}
            }
            if (i <= max) {
                int j = i + 1;
                final int end = j + target.length - 1;
                for (int k = 1; j < end && source[j] == target[k]; ++j, ++k) {}
                if (j == end) {
                    return i;
                }
            }
        }
        return -1;
    }
    
    static boolean isWordRune(final int r) {
        return (65 <= r && r <= 90) || (97 <= r && r <= 122) || (48 <= r && r <= 57) || r == 95;
    }
    
    static int emptyOpContext(final int r1, final int r2) {
        int op = 0;
        if (r1 < 0) {
            op |= 0x5;
        }
        if (r1 == 10) {
            op |= 0x1;
        }
        if (r2 < 0) {
            op |= 0xA;
        }
        if (r2 == 10) {
            op |= 0x2;
        }
        if (isWordRune(r1) != isWordRune(r2)) {
            op |= 0x10;
        }
        else {
            op |= 0x20;
        }
        return op;
    }
    
    private Utils() {
    }
    
    static {
        EMPTY_INTS = new int[0];
    }
}
