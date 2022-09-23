// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.json.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.HashSet;

class NameUtil
{
    protected static final int UPPER_LETTER = 0;
    protected static final int LOWER_LETTER = 1;
    protected static final int OTHER_LETTER = 2;
    protected static final int DIGIT = 3;
    protected static final int OTHER = 4;
    private static final byte[] actionTable;
    private static final byte ACTION_CHECK_PUNCT = 0;
    private static final byte ACTION_CHECK_C2 = 1;
    private static final byte ACTION_BREAK = 2;
    private static final byte ACTION_NOBREAK = 3;
    private static HashSet<String> reservedKeywords;
    
    protected static boolean isPunct(final char c) {
        return c == '-' || c == '.' || c == ':' || c == '_' || c == '·' || c == '\u0387' || c == '\u06dd' || c == '\u06de';
    }
    
    protected static boolean isDigit(final char c) {
        return (c >= '0' && c <= '9') || Character.isDigit(c);
    }
    
    protected static boolean isUpper(final char c) {
        return (c >= 'A' && c <= 'Z') || Character.isUpperCase(c);
    }
    
    protected static boolean isLower(final char c) {
        return (c >= 'a' && c <= 'z') || Character.isLowerCase(c);
    }
    
    protected boolean isLetter(final char c) {
        return (c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z') || Character.isLetter(c);
    }
    
    protected static String capitalize(final String s) {
        if (!isLower(s.charAt(0))) {
            return s;
        }
        final StringBuilder sb = new StringBuilder(s.length());
        sb.append(Character.toUpperCase(s.charAt(0)));
        sb.append(s.substring(1).toLowerCase());
        return sb.toString();
    }
    
    private static int nextBreak(final String s, final int start) {
        final int n = s.length();
        char c1 = s.charAt(start);
        int t1 = classify(c1);
        for (int i = start + 1; i < n; ++i) {
            final int t2 = t1;
            c1 = s.charAt(i);
            t1 = classify(c1);
            switch (NameUtil.actionTable[t2 * 5 + t1]) {
                case 0: {
                    if (isPunct(c1)) {
                        return i;
                    }
                    break;
                }
                case 1: {
                    if (i >= n - 1) {
                        break;
                    }
                    final char c2 = s.charAt(i + 1);
                    if (isLower(c2)) {
                        return i;
                    }
                    break;
                }
                case 2: {
                    return i;
                }
            }
        }
        return -1;
    }
    
    private static byte decideAction(final int t0, final int t1) {
        if (t0 == 4 && t1 == 4) {
            return 0;
        }
        if (!xor(t0 == 3, t1 == 3)) {
            return 2;
        }
        if (t0 == 1 && t1 != 1) {
            return 2;
        }
        if (!xor(t0 <= 2, t1 <= 2)) {
            return 2;
        }
        if (!xor(t0 == 2, t1 == 2)) {
            return 2;
        }
        if (t0 == 0 && t1 == 0) {
            return 1;
        }
        return 3;
    }
    
    private static boolean xor(final boolean x, final boolean y) {
        return (x && y) || (!x && !y);
    }
    
    protected static int classify(final char c0) {
        switch (Character.getType(c0)) {
            case 1: {
                return 0;
            }
            case 2: {
                return 1;
            }
            case 3:
            case 4:
            case 5: {
                return 2;
            }
            case 9: {
                return 3;
            }
            default: {
                return 4;
            }
        }
    }
    
    protected static List<String> toWordList(final String s) {
        final ArrayList<String> ss = new ArrayList<String>();
        int b;
        for (int n = s.length(), i = 0; i < n; i = b) {
            while (i < n && isPunct(s.charAt(i))) {
                ++i;
            }
            if (i >= n) {
                break;
            }
            b = nextBreak(s, i);
            final String w = (b == -1) ? s.substring(i) : s.substring(i, b);
            ss.add(escape(capitalize(w)));
            if (b == -1) {
                break;
            }
        }
        return ss;
    }
    
    protected static String toMixedCaseName(final List<String> ss, final boolean startUpper) {
        final StringBuilder sb = new StringBuilder();
        if (!ss.isEmpty()) {
            sb.append(startUpper ? ss.get(0) : ss.get(0).toLowerCase());
            for (int i = 1; i < ss.size(); ++i) {
                sb.append(ss.get(i));
            }
        }
        return sb.toString();
    }
    
    protected static String toMixedCaseVariableName(final String[] ss, final boolean startUpper, final boolean cdrUpper) {
        if (cdrUpper) {
            for (int i = 1; i < ss.length; ++i) {
                ss[i] = capitalize(ss[i]);
            }
        }
        final StringBuilder sb = new StringBuilder();
        if (ss.length > 0) {
            sb.append(startUpper ? ss[0] : ss[0].toLowerCase());
            for (int j = 1; j < ss.length; ++j) {
                sb.append(ss[j]);
            }
        }
        return sb.toString();
    }
    
    protected static String toConstantName(final String s) {
        return toConstantName(toWordList(s));
    }
    
    protected static String toConstantName(final List<String> ss) {
        final StringBuilder sb = new StringBuilder();
        if (!ss.isEmpty()) {
            sb.append(ss.get(0).toUpperCase());
            for (int i = 1; i < ss.size(); ++i) {
                sb.append('_');
                sb.append(ss.get(i).toUpperCase());
            }
        }
        return sb.toString();
    }
    
    protected static void escape(final StringBuilder sb, final String s, final int start) {
        for (int n = s.length(), i = start; i < n; ++i) {
            final char c = s.charAt(i);
            if (Character.isJavaIdentifierPart(c)) {
                sb.append(c);
            }
            else {
                sb.append('_');
                if (c <= '\u000f') {
                    sb.append("000");
                }
                else if (c <= '\u00ff') {
                    sb.append("00");
                }
                else if (c <= '\u0fff') {
                    sb.append('0');
                }
                sb.append(Integer.toString(c, 16));
            }
        }
    }
    
    private static String escape(final String s) {
        for (int n = s.length(), i = 0; i < n; ++i) {
            if (!Character.isJavaIdentifierPart(s.charAt(i))) {
                final StringBuilder sb = new StringBuilder(s.substring(0, i));
                escape(sb, s, i);
                return sb.toString();
            }
        }
        return s;
    }
    
    protected static boolean isJavaIdentifier(final String s) {
        if (s.length() == 0) {
            return false;
        }
        if (NameUtil.reservedKeywords.contains(s)) {
            return false;
        }
        if (!Character.isJavaIdentifierStart(s.charAt(0))) {
            return false;
        }
        for (int i = 1; i < s.length(); ++i) {
            if (!Character.isJavaIdentifierPart(s.charAt(i))) {
                return false;
            }
        }
        return true;
    }
    
    protected static boolean isJavaPackageName(String s) {
        while (s.length() != 0) {
            int idx = s.indexOf(46);
            if (idx == -1) {
                idx = s.length();
            }
            if (!isJavaIdentifier(s.substring(0, idx))) {
                return false;
            }
            s = s.substring(idx);
            if (s.length() == 0) {
                continue;
            }
            s = s.substring(1);
        }
        return true;
    }
    
    static {
        actionTable = new byte[25];
        for (int t0 = 0; t0 < 5; ++t0) {
            for (int t2 = 0; t2 < 5; ++t2) {
                NameUtil.actionTable[t0 * 5 + t2] = decideAction(t0, t2);
            }
        }
        NameUtil.reservedKeywords = new HashSet<String>();
        final String[] arr$;
        final String[] words = arr$ = new String[] { "abstract", "boolean", "break", "byte", "case", "catch", "char", "class", "const", "continue", "default", "do", "double", "else", "extends", "final", "finally", "float", "for", "goto", "if", "implements", "import", "instanceof", "int", "interface", "long", "native", "new", "package", "private", "protected", "public", "return", "short", "static", "strictfp", "super", "switch", "synchronized", "this", "throw", "throws", "transient", "try", "void", "volatile", "while", "true", "false", "null", "assert", "enum" };
        for (final String word : arr$) {
            NameUtil.reservedKeywords.add(word);
        }
    }
}
