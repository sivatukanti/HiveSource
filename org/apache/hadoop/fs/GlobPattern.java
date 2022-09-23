// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.fs;

import com.google.re2j.PatternSyntaxException;
import com.google.re2j.Pattern;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Private
@InterfaceStability.Evolving
public class GlobPattern
{
    private static final char BACKSLASH = '\\';
    private Pattern compiled;
    private boolean hasWildcard;
    
    public GlobPattern(final String globPattern) {
        this.hasWildcard = false;
        this.set(globPattern);
    }
    
    public Pattern compiled() {
        return this.compiled;
    }
    
    public static Pattern compile(final String globPattern) {
        return new GlobPattern(globPattern).compiled();
    }
    
    public boolean matches(final CharSequence s) {
        return this.compiled.matcher(s).matches();
    }
    
    public void set(final String glob) {
        final StringBuilder regex = new StringBuilder();
        int setOpen = 0;
        int curlyOpen = 0;
        final int len = glob.length();
        this.hasWildcard = false;
        for (int i = 0; i < len; ++i) {
            final char c = glob.charAt(i);
            switch (c) {
                case '\\': {
                    if (++i >= len) {
                        error("Missing escaped character", glob, i);
                    }
                    regex.append(c).append(glob.charAt(i));
                    continue;
                }
                case '$':
                case '(':
                case ')':
                case '+':
                case '.':
                case '|': {
                    regex.append('\\');
                    break;
                }
                case '*': {
                    regex.append('.');
                    this.hasWildcard = true;
                    break;
                }
                case '?': {
                    regex.append('.');
                    this.hasWildcard = true;
                    continue;
                }
                case '{': {
                    regex.append("(?:");
                    ++curlyOpen;
                    this.hasWildcard = true;
                    continue;
                }
                case ',': {
                    regex.append((curlyOpen > 0) ? '|' : c);
                    continue;
                }
                case '}': {
                    if (curlyOpen > 0) {
                        --curlyOpen;
                        regex.append(")");
                        continue;
                    }
                    break;
                }
                case '[': {
                    if (setOpen > 0) {
                        error("Unclosed character class", glob, i);
                    }
                    ++setOpen;
                    this.hasWildcard = true;
                    break;
                }
                case '^': {
                    if (setOpen == 0) {
                        regex.append('\\');
                        break;
                    }
                    break;
                }
                case '!': {
                    regex.append((setOpen > 0 && '[' == glob.charAt(i - 1)) ? '^' : '!');
                    continue;
                }
                case ']': {
                    setOpen = 0;
                    break;
                }
            }
            regex.append(c);
        }
        if (setOpen > 0) {
            error("Unclosed character class", glob, len);
        }
        if (curlyOpen > 0) {
            error("Unclosed group", glob, len);
        }
        this.compiled = Pattern.compile(regex.toString(), 2);
    }
    
    public boolean hasWildcard() {
        return this.hasWildcard;
    }
    
    private static void error(final String message, final String pattern, final int pos) {
        final String fullMessage = String.format("%s at pos %d", message, pos);
        throw new PatternSyntaxException(fullMessage, pattern);
    }
}
