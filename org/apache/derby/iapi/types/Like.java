// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.types;

import org.apache.derby.iapi.error.StandardException;
import java.text.RuleBasedCollator;

public class Like
{
    private static final char anyChar = '_';
    private static final char anyString = '%';
    private static final String SUPER_STRING = "\uffff";
    
    private Like() {
    }
    
    public static Boolean like(final char[] array, final int n, final char[] array2, final int n2, final char[] array3, final int n3, final RuleBasedCollator ruleBasedCollator) throws StandardException {
        return like(array, 0, n, array2, 0, n2, array3, n3, ruleBasedCollator);
    }
    
    private static Boolean like(final char[] array, int n, final int n2, final char[] array2, int n3, final int n4, final char[] array3, final int n5, final RuleBasedCollator ruleBasedCollator) throws StandardException {
        char c = ' ';
        boolean b = true;
        if (array == null) {
            return null;
        }
        if (array2 == null) {
            return null;
        }
        if (array3 == null) {
            b = false;
        }
        else {
            c = array3[0];
        }
        Boolean checkLengths;
        while ((checkLengths = checkLengths(n, n2, n3, array2, n4)) == null) {
            while (array2[n3] != '_' && array2[n3] != '%' && (!b || array2[n3] != c)) {
                if (!checkEquality(array, n, array2, n3, ruleBasedCollator)) {
                    return Boolean.FALSE;
                }
                ++n;
                ++n3;
                final Boolean checkLengths2 = checkLengths(n, n2, n3, array2, n4);
                if (checkLengths2 != null) {
                    return checkLengths2;
                }
            }
            if (b && array2[n3] == c) {
                if (++n3 == n4) {
                    throw StandardException.newException("22025");
                }
                if (array2[n3] != c && array2[n3] != '_' && array2[n3] != '%') {
                    throw StandardException.newException("22025");
                }
                if (!checkEquality(array, n, array2, n3, ruleBasedCollator)) {
                    return Boolean.FALSE;
                }
                ++n;
                ++n3;
                final Boolean checkLengths3 = checkLengths(n, n2, n3, array2, n4);
                if (checkLengths3 != null) {
                    return checkLengths3;
                }
                continue;
            }
            else if (array2[n3] == '_') {
                ++n;
                ++n3;
                final Boolean checkLengths4 = checkLengths(n, n2, n3, array2, n4);
                if (checkLengths4 != null) {
                    return checkLengths4;
                }
                continue;
            }
            else {
                if (array2[n3] != '%') {
                    continue;
                }
                if (n3 + 1 == n4) {
                    return Boolean.TRUE;
                }
                boolean b2 = true;
                for (int i = n3 + 1; i < n4; ++i) {
                    if (array2[i] != '%') {
                        b2 = false;
                        break;
                    }
                }
                if (b2) {
                    return Boolean.TRUE;
                }
                final int n6 = n2 - n;
                int n7 = 0;
                for (int minLen = getMinLen(array2, n3 + 1, n4, b, c), j = n6; j >= minLen; --j) {
                    final Boolean like = like(array, n + n7, n + n7 + j, array2, n3 + 1, n4, array3, n5, ruleBasedCollator);
                    if (like) {
                        return like;
                    }
                    ++n7;
                }
                return Boolean.FALSE;
            }
        }
        return checkLengths;
    }
    
    private static boolean checkEquality(final char[] value, final int offset, final char[] value2, final int offset2, final RuleBasedCollator ruleBasedCollator) {
        return value[offset] == value2[offset2] || (ruleBasedCollator != null && ruleBasedCollator.compare(new String(value, offset, 1), new String(value2, offset2, 1)) == 0);
    }
    
    static int getMinLen(final char[] array, final int n, final int n2, final boolean b, final char c) {
        int n3 = 0;
        int i = n;
        while (i < n2) {
            if (b && array[i] == c) {
                i += 2;
                ++n3;
            }
            else if (array[i] == '%') {
                ++i;
            }
            else {
                ++i;
                ++n3;
            }
        }
        return n3;
    }
    
    static Boolean checkLengths(final int n, final int n2, final int n3, final char[] array, final int n4) {
        if (n == n2) {
            if (n3 == n4) {
                return Boolean.TRUE;
            }
            for (int i = n3; i < n4; ++i) {
                if (array[i] != '%') {
                    return Boolean.FALSE;
                }
            }
            return Boolean.TRUE;
        }
        else {
            if (n3 == n4) {
                return Boolean.FALSE;
            }
            return null;
        }
    }
    
    public static Boolean like(final char[] array, final int n, final char[] array2, final int n2, final RuleBasedCollator ruleBasedCollator) throws StandardException {
        if (array == null || array2 == null) {
            return null;
        }
        return like(array, n, array2, n2, null, 0, ruleBasedCollator);
    }
    
    public static boolean isOptimizable(final String s) {
        if (s == null) {
            return false;
        }
        if (s.length() == 0) {
            return true;
        }
        final char char1 = s.charAt(0);
        return char1 != '_' && char1 != '%';
    }
    
    public static String greaterEqualStringFromParameter(final String s, final int n) throws StandardException {
        if (s == null) {
            return null;
        }
        return greaterEqualString(s, null, n);
    }
    
    public static String greaterEqualStringFromParameterWithEsc(final String s, final String s2, final int n) throws StandardException {
        if (s == null) {
            return null;
        }
        return greaterEqualString(s, s2, n);
    }
    
    public static String greaterEqualString(String s, final String s2, final int n) throws StandardException {
        final int index = s.indexOf(95);
        final int index2 = s.indexOf(37);
        if (s2 != null && s2.length() != 0) {
            final char char1 = s2.charAt(0);
            if (s.indexOf(char1) != -1) {
                return padWithNulls(greaterEqualString(s, char1), n);
            }
        }
        if (index == -1) {
            if (index2 != -1) {
                s = s.substring(0, index2);
            }
        }
        else if (index2 == -1) {
            s = s.substring(0, index);
        }
        else {
            s = s.substring(0, (index > index2) ? index2 : index);
        }
        return padWithNulls(s, n);
    }
    
    private static String greaterEqualString(final String s, final char c) throws StandardException {
        final int length = s.length();
        final char[] dst = new char[length];
        final char[] array = new char[length];
        s.getChars(0, length, dst, 0);
        int n = 0;
        for (int n2 = 0; n2 < length && n < length; ++n2) {
            final char c2 = dst[n2];
            if (c2 == c) {
                if (++n2 >= length) {
                    throw StandardException.newException("22025");
                }
                array[n++] = dst[n2];
            }
            else {
                if (c2 == '_' || c2 == '%') {
                    return new String(array, 0, n);
                }
                array[n++] = dst[n2];
            }
        }
        return new String(array, 0, n);
    }
    
    public static String stripEscapesNoPatternChars(final String s, final char c) throws StandardException {
        final int length = s.length();
        final char[] dst = new char[length];
        final char[] value = new char[length];
        s.getChars(0, length, dst, 0);
        int count = 0;
        for (int index = 0; index < length && count < length; ++index) {
            final char char1 = s.charAt(index);
            if (char1 == c) {
                if (++index >= length) {
                    throw StandardException.newException("22025");
                }
                value[count++] = dst[index];
            }
            else {
                if (char1 == '_' || char1 == '%') {
                    return null;
                }
                value[count++] = dst[index];
            }
        }
        return new String(value, 0, count);
    }
    
    public static String lessThanStringFromParameter(final String s, final int n) throws StandardException {
        if (s == null) {
            return null;
        }
        return lessThanString(s, null, n);
    }
    
    public static String lessThanStringFromParameterWithEsc(final String s, final String s2, final int n) throws StandardException {
        if (s == null) {
            return null;
        }
        return lessThanString(s, s2, n);
    }
    
    public static String lessThanString(final String s, final String s2, final int n) throws StandardException {
        int char1;
        if (s2 != null && s2.length() != 0) {
            char1 = s2.charAt(0);
        }
        else {
            char1 = -1;
        }
        final StringBuffer sb = new StringBuffer(n);
        for (int i = 0; i < s.length(); ++i) {
            char c = s.charAt(i);
            if (c == char1) {
                if (++i >= s.length()) {
                    throw StandardException.newException("22025");
                }
                c = s.charAt(i);
            }
            else {
                if (c == '_') {
                    break;
                }
                if (c == '%') {
                    break;
                }
            }
            sb.append(c);
        }
        if (sb.length() == 0) {
            return "\uffff";
        }
        final int n2 = sb.length() - 1;
        final char char2 = sb.charAt(n2);
        final char ch = (char)(char2 + '\u0001');
        if (ch < char2) {
            return "\uffff";
        }
        sb.setCharAt(n2, ch);
        if (sb.length() < n) {
            sb.setLength(n);
        }
        return sb.toString();
    }
    
    public static boolean isLikeComparisonNeeded(final String s) {
        final int index = s.indexOf(95);
        final int index2 = s.indexOf(37);
        return (index != -1 || index2 != -1) && (index != -1 || index2 != s.length() - 1);
    }
    
    private static String padWithNulls(final String str, final int n) {
        if (str.length() >= n) {
            return str;
        }
        final StringBuffer append = new StringBuffer(n).append(str);
        append.setLength(n);
        return append.toString();
    }
}
