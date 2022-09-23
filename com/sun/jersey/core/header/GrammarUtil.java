// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.core.header;

public final class GrammarUtil
{
    public static final int TOKEN = 0;
    public static final int QUOTED_STRING = 1;
    public static final int COMMENT = 2;
    public static final int SEPARATOR = 3;
    public static final int CONTROL = 4;
    public static final char[] WHITE_SPACE;
    public static final char[] SEPARATORS;
    public static final int[] TYPE_TABLE;
    public static final boolean[] IS_WHITE_SPACE;
    public static final boolean[] IS_TOKEN;
    
    private static int[] createEventTable() {
        final int[] table = new int[128];
        for (int i = 0; i < 127; ++i) {
            table[i] = 0;
        }
        for (final char c : GrammarUtil.SEPARATORS) {
            table[c] = 3;
        }
        table[40] = 2;
        table[34] = 1;
        for (int i = 0; i < 32; ++i) {
            table[i] = 4;
        }
        table[127] = 4;
        for (final char c : GrammarUtil.WHITE_SPACE) {
            table[c] = -1;
        }
        return table;
    }
    
    private static boolean[] createWhiteSpaceTable() {
        final boolean[] table = new boolean[128];
        for (final char c : GrammarUtil.WHITE_SPACE) {
            table[c] = true;
        }
        return table;
    }
    
    private static boolean[] createTokenTable() {
        final boolean[] table = new boolean[128];
        for (int i = 0; i < 128; ++i) {
            table[i] = (GrammarUtil.TYPE_TABLE[i] == 0);
        }
        return table;
    }
    
    public static boolean isWhiteSpace(final char c) {
        return c < '\u0080' && GrammarUtil.IS_WHITE_SPACE[c];
    }
    
    public static boolean isToken(final char c) {
        return c < '\u0080' && GrammarUtil.IS_TOKEN[c];
    }
    
    public static boolean isTokenString(final String s) {
        for (final char c : s.toCharArray()) {
            if (!isToken(c)) {
                return false;
            }
        }
        return true;
    }
    
    public static boolean containsWhiteSpace(final String s) {
        for (final char c : s.toCharArray()) {
            if (isWhiteSpace(c)) {
                return true;
            }
        }
        return false;
    }
    
    public static String filterToken(final String s, final int start, final int end) {
        return filterToken(s, start, end, false);
    }
    
    public static String filterToken(final String s, final int start, final int end, final boolean preserveBackslash) {
        final StringBuilder sb = new StringBuilder();
        boolean gotEscape = false;
        boolean gotCR = false;
        for (int i = start; i < end; ++i) {
            final char c = s.charAt(i);
            if (c == '\n' && gotCR) {
                gotCR = false;
            }
            else {
                gotCR = false;
                if (!gotEscape) {
                    if (!preserveBackslash && c == '\\') {
                        gotEscape = true;
                    }
                    else if (c == '\r') {
                        gotCR = true;
                    }
                    else {
                        sb.append(c);
                    }
                }
                else {
                    sb.append(c);
                    gotEscape = false;
                }
            }
        }
        return sb.toString();
    }
    
    static {
        WHITE_SPACE = new char[] { '\t', '\r', '\n', ' ' };
        SEPARATORS = new char[] { '(', ')', '<', '>', '@', ',', ';', ':', '\\', '\"', '/', '[', ']', '?', '=', '{', '}', ' ', '\t' };
        TYPE_TABLE = createEventTable();
        IS_WHITE_SPACE = createWhiteSpaceTable();
        IS_TOKEN = createTokenTable();
    }
}
