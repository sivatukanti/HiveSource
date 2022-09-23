// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.zookeeper.common;

public class PathUtils
{
    public static void validatePath(final String path, final boolean isSequential) throws IllegalArgumentException {
        validatePath(isSequential ? (path + "1") : path);
    }
    
    public static void validatePath(final String path) throws IllegalArgumentException {
        if (path == null) {
            throw new IllegalArgumentException("Path cannot be null");
        }
        if (path.length() == 0) {
            throw new IllegalArgumentException("Path length must be > 0");
        }
        if (path.charAt(0) != '/') {
            throw new IllegalArgumentException("Path must start with / character");
        }
        if (path.length() == 1) {
            return;
        }
        if (path.charAt(path.length() - 1) == '/') {
            throw new IllegalArgumentException("Path must not end with / character");
        }
        String reason = null;
        char lastc = '/';
        final char[] chars = path.toCharArray();
        for (int i = 1; i < chars.length; ++i) {
            final char c = chars[i];
            if (c == '\0') {
                reason = "null character not allowed @" + i;
                break;
            }
            if (c == '/' && lastc == '/') {
                reason = "empty node name specified @" + i;
                break;
            }
            if (c == '.' && lastc == '.') {
                if (chars[i - 2] == '/' && (i + 1 == chars.length || chars[i + 1] == '/')) {
                    reason = "relative paths not allowed @" + i;
                    break;
                }
            }
            else if (c == '.') {
                if (chars[i - 1] == '/' && (i + 1 == chars.length || chars[i + 1] == '/')) {
                    reason = "relative paths not allowed @" + i;
                    break;
                }
            }
            else if ((c > '\0' && c < '\u001f') || (c > '\u007f' && c < '\u009f') || (c > '\ud800' && c < '\uf8ff') || (c > '\ufff0' && c < '\uffff')) {
                reason = "invalid character @" + i;
                break;
            }
            lastc = chars[i];
        }
        if (reason != null) {
            throw new IllegalArgumentException("Invalid path string \"" + path + "\" caused by " + reason);
        }
    }
}
