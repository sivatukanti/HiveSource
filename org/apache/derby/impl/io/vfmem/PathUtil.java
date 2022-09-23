// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.io.vfmem;

import java.io.File;

public class PathUtil
{
    public static final char SEP;
    public static final String SEP_STR;
    
    private PathUtil() {
    }
    
    private static void basicPathChecks(final String str) {
        if (str == null) {
            throw new IllegalArgumentException("Path is null");
        }
        if (!str.equals(str.trim())) {
            throw new IllegalArgumentException("Path has not been trimmed: '" + str + "'");
        }
    }
    
    public static String getBaseName(final String s) {
        basicPathChecks(s);
        final int lastIndex = s.lastIndexOf(PathUtil.SEP);
        if (lastIndex != -1 && lastIndex != s.length() - 1) {
            return s.substring(lastIndex + 1);
        }
        return s;
    }
    
    public static String getParent(String substring) {
        basicPathChecks(substring);
        if (substring.equals(PathUtil.SEP_STR)) {
            return null;
        }
        if (substring.length() > 0 && substring.charAt(substring.length() - 1) == PathUtil.SEP) {
            substring = substring.substring(0, substring.length() - 1);
        }
        final int lastIndex = substring.lastIndexOf(PathUtil.SEP);
        if (lastIndex == 0) {
            return PathUtil.SEP_STR;
        }
        if (lastIndex > 0) {
            return substring.substring(0, lastIndex);
        }
        return null;
    }
    
    public static String join(final String s, final String s2) {
        if (s.charAt(s.length() - 1) == PathUtil.SEP) {
            return s + s2;
        }
        return s + PathUtil.SEP + s2;
    }
    
    static {
        SEP = File.separatorChar;
        SEP_STR = String.valueOf(PathUtil.SEP);
    }
}
