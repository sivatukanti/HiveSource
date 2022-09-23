// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hive.service.cli;

import org.apache.log4j.PatternLayout;
import org.apache.log4j.Layout;

public class CLIServiceUtils
{
    private static final char SEARCH_STRING_ESCAPE = '\\';
    public static final Layout verboseLayout;
    public static final Layout nonVerboseLayout;
    
    public static String patternToRegex(final String pattern) {
        if (pattern == null) {
            return ".*";
        }
        final StringBuilder result = new StringBuilder(pattern.length());
        boolean escaped = false;
        for (int i = 0, len = pattern.length(); i < len; ++i) {
            final char c = pattern.charAt(i);
            if (escaped) {
                if (c != '\\') {
                    escaped = false;
                }
                result.append(c);
            }
            else if (c == '\\') {
                escaped = true;
            }
            else if (c == '%') {
                result.append(".*");
            }
            else if (c == '_') {
                result.append('.');
            }
            else {
                result.append(Character.toLowerCase(c));
            }
        }
        return result.toString();
    }
    
    static {
        verboseLayout = new PatternLayout("%d{yy/MM/dd HH:mm:ss} %p %c{2}: %m%n");
        nonVerboseLayout = new PatternLayout("%-5p : %m%n");
    }
}
