// 
// Decompiled by Procyon v0.5.36
// 

package antlr;

public class StringUtils
{
    public static String stripBack(String substring, final char c) {
        while (substring.length() > 0 && substring.charAt(substring.length() - 1) == c) {
            substring = substring.substring(0, substring.length() - 1);
        }
        return substring;
    }
    
    public static String stripBack(String substring, final String s) {
        boolean b;
        do {
            b = false;
            for (int i = 0; i < s.length(); ++i) {
                for (char char1 = s.charAt(i); substring.length() > 0 && substring.charAt(substring.length() - 1) == char1; substring = substring.substring(0, substring.length() - 1)) {
                    b = true;
                }
            }
        } while (b);
        return substring;
    }
    
    public static String stripFront(String substring, final char c) {
        while (substring.length() > 0 && substring.charAt(0) == c) {
            substring = substring.substring(1);
        }
        return substring;
    }
    
    public static String stripFront(String substring, final String s) {
        boolean b;
        do {
            b = false;
            for (int i = 0; i < s.length(); ++i) {
                for (char char1 = s.charAt(i); substring.length() > 0 && substring.charAt(0) == char1; substring = substring.substring(1)) {
                    b = true;
                }
            }
        } while (b);
        return substring;
    }
    
    public static String stripFrontBack(final String s, final String str, final String str2) {
        final int index = s.indexOf(str);
        final int lastIndex = s.lastIndexOf(str2);
        if (index == -1 || lastIndex == -1) {
            return s;
        }
        return s.substring(index + 1, lastIndex);
    }
}
