// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.mail.mbox;

class match
{
    public static boolean path(final String s, final String pat, final char delim) {
        try {
            return path(s, 0, s.length(), pat, 0, pat.length(), delim);
        }
        catch (StringIndexOutOfBoundsException e) {
            return false;
        }
    }
    
    private static boolean path(final String s, int s_index, final int s_len, final String pat, int p_index, final int p_len, final char delim) throws StringIndexOutOfBoundsException {
        while (p_index < p_len) {
            final char c = pat.charAt(p_index);
            switch (c) {
                case '%': {
                    if (++p_index >= p_len) {
                        return delim == '\0' || s.indexOf(delim, s_index) < 0;
                    }
                    while (!path(s, s_index, s_len, pat, p_index, p_len, delim)) {
                        if (s.charAt(s_index) == delim || ++s_index >= s_len) {
                            return false;
                        }
                    }
                    return true;
                }
                case '*': {
                    if (++p_index >= p_len) {
                        return true;
                    }
                    while (!path(s, s_index, s_len, pat, p_index, p_len, delim)) {
                        if (++s_index >= s_len) {
                            return false;
                        }
                    }
                    return true;
                }
                default: {
                    if (s_index >= s_len || c != s.charAt(s_index)) {
                        return false;
                    }
                    ++s_index;
                    ++p_index;
                    continue;
                }
            }
        }
        return s_index >= s_len;
    }
    
    public static boolean dir(final String s, final String pat, final char delim) {
        try {
            return dir(s, 0, s.length(), pat, 0, pat.length(), delim);
        }
        catch (StringIndexOutOfBoundsException e) {
            return false;
        }
    }
    
    private static boolean dir(final String s, int s_index, final int s_len, final String pat, int p_index, final int p_len, final char delim) throws StringIndexOutOfBoundsException {
        while (p_index < p_len) {
            final char c = pat.charAt(p_index);
            switch (c) {
                case '%': {
                    if (s_index >= s_len) {
                        return true;
                    }
                    if (++p_index >= p_len) {
                        return false;
                    }
                    while (!dir(s, s_index, s_len, pat, p_index, p_len, delim)) {
                        if (s.charAt(s_index) == delim || ++s_index >= s_len) {
                            return s_index + 1 == s_len || dir(s, s_index, s_len, pat, p_index, p_len, delim);
                        }
                    }
                    return true;
                }
                case '*': {
                    return true;
                }
                default: {
                    if (s_index >= s_len) {
                        return c == delim;
                    }
                    if (c != s.charAt(s_index)) {
                        return false;
                    }
                    ++s_index;
                    ++p_index;
                    continue;
                }
            }
        }
        return s_index >= s_len;
    }
}
