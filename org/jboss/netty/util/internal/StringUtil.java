// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.util.internal;

import java.util.Formatter;
import java.util.List;
import java.util.ArrayList;

public final class StringUtil
{
    public static final String NEWLINE;
    private static final String EMPTY_STRING = "";
    
    private StringUtil() {
    }
    
    public static String stripControlCharacters(final Object value) {
        if (value == null) {
            return null;
        }
        return stripControlCharacters(value.toString());
    }
    
    public static String stripControlCharacters(final String value) {
        if (value == null) {
            return null;
        }
        boolean hasControlChars = false;
        for (int i = value.length() - 1; i >= 0; --i) {
            if (Character.isISOControl(value.charAt(i))) {
                hasControlChars = true;
                break;
            }
        }
        if (!hasControlChars) {
            return value;
        }
        final StringBuilder buf = new StringBuilder(value.length());
        int j;
        for (j = 0; j < value.length() && Character.isISOControl(value.charAt(j)); ++j) {}
        boolean suppressingControlChars = false;
        while (j < value.length()) {
            if (Character.isISOControl(value.charAt(j))) {
                suppressingControlChars = true;
            }
            else {
                if (suppressingControlChars) {
                    suppressingControlChars = false;
                    buf.append(' ');
                }
                buf.append(value.charAt(j));
            }
            ++j;
        }
        return buf.toString();
    }
    
    public static String[] split(final String value, final char delim) {
        final int end = value.length();
        final List<String> res = new ArrayList<String>();
        int start = 0;
        for (int i = 0; i < end; ++i) {
            if (value.charAt(i) == delim) {
                if (start == i) {
                    res.add("");
                }
                else {
                    res.add(value.substring(start, i));
                }
                start = i + 1;
            }
        }
        if (start == 0) {
            res.add(value);
        }
        else if (start != end) {
            res.add(value.substring(start, end));
        }
        else {
            for (int i = res.size() - 1; i >= 0 && res.get(i).length() == 0; --i) {
                res.remove(i);
            }
        }
        return res.toArray(new String[res.size()]);
    }
    
    public static String[] split(final String value, final char delim, final int maxParts) {
        final int end = value.length();
        final List<String> res = new ArrayList<String>();
        int start = 0;
        for (int cpt = 1, i = 0; i < end && cpt < maxParts; ++i) {
            if (value.charAt(i) == delim) {
                if (start == i) {
                    res.add("");
                }
                else {
                    res.add(value.substring(start, i));
                }
                start = i + 1;
                ++cpt;
            }
        }
        if (start == 0) {
            res.add(value);
        }
        else if (start != end) {
            res.add(value.substring(start, end));
        }
        else {
            for (int i = res.size() - 1; i >= 0 && res.get(i).length() == 0; --i) {
                res.remove(i);
            }
        }
        return res.toArray(new String[res.size()]);
    }
    
    public static String substringAfter(final String value, final char delim) {
        final int pos = value.indexOf(delim);
        if (pos >= 0) {
            return value.substring(pos + 1);
        }
        return null;
    }
    
    static {
        String newLine;
        try {
            newLine = new Formatter().format("%n", new Object[0]).toString();
        }
        catch (Exception e) {
            newLine = "\n";
        }
        NEWLINE = newLine;
    }
}
