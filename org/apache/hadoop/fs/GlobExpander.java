// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.fs;

import java.util.Iterator;
import java.io.IOException;
import java.util.Collection;
import java.util.ArrayList;
import java.util.List;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Private
@InterfaceStability.Unstable
public class GlobExpander
{
    public static List<String> expand(final String filePattern) throws IOException {
        final List<String> fullyExpanded = new ArrayList<String>();
        final List<StringWithOffset> toExpand = new ArrayList<StringWithOffset>();
        toExpand.add(new StringWithOffset(filePattern, 0));
        while (!toExpand.isEmpty()) {
            final StringWithOffset path = toExpand.remove(0);
            final List<StringWithOffset> expanded = expandLeftmost(path);
            if (expanded == null) {
                fullyExpanded.add(path.string);
            }
            else {
                toExpand.addAll(0, expanded);
            }
        }
        return fullyExpanded;
    }
    
    private static List<StringWithOffset> expandLeftmost(final StringWithOffset filePatternWithOffset) throws IOException {
        final String filePattern = filePatternWithOffset.string;
        final int leftmost = leftmostOuterCurlyContainingSlash(filePattern, filePatternWithOffset.offset);
        if (leftmost == -1) {
            return null;
        }
        int curlyOpen = 0;
        final StringBuilder prefix = new StringBuilder(filePattern.substring(0, leftmost));
        final StringBuilder suffix = new StringBuilder();
        final List<String> alts = new ArrayList<String>();
        final StringBuilder alt = new StringBuilder();
        StringBuilder cur = prefix;
        for (int i = leftmost; i < filePattern.length(); ++i) {
            char c = filePattern.charAt(i);
            if (cur == suffix) {
                cur.append(c);
            }
            else if (c == '\\') {
                if (++i >= filePattern.length()) {
                    throw new IOException("Illegal file pattern: An escaped character does not present for glob " + filePattern + " at " + i);
                }
                c = filePattern.charAt(i);
                cur.append(c);
            }
            else if (c == '{') {
                if (curlyOpen++ == 0) {
                    alt.setLength(0);
                    cur = alt;
                }
                else {
                    cur.append(c);
                }
            }
            else if (c == '}' && curlyOpen > 0) {
                if (--curlyOpen == 0) {
                    alts.add(alt.toString());
                    alt.setLength(0);
                    cur = suffix;
                }
                else {
                    cur.append(c);
                }
            }
            else if (c == ',') {
                if (curlyOpen == 1) {
                    alts.add(alt.toString());
                    alt.setLength(0);
                }
                else {
                    cur.append(c);
                }
            }
            else {
                cur.append(c);
            }
        }
        final List<StringWithOffset> exp = new ArrayList<StringWithOffset>();
        for (final String string : alts) {
            exp.add(new StringWithOffset((Object)prefix + string + (Object)suffix, prefix.length()));
        }
        return exp;
    }
    
    private static int leftmostOuterCurlyContainingSlash(final String filePattern, final int offset) throws IOException {
        int curlyOpen = 0;
        int leftmost = -1;
        boolean seenSlash = false;
        for (int i = offset; i < filePattern.length(); ++i) {
            final char c = filePattern.charAt(i);
            if (c == '\\') {
                if (++i >= filePattern.length()) {
                    throw new IOException("Illegal file pattern: An escaped character does not present for glob " + filePattern + " at " + i);
                }
            }
            else if (c == '{') {
                if (curlyOpen++ == 0) {
                    leftmost = i;
                }
            }
            else if (c == '}' && curlyOpen > 0) {
                if (--curlyOpen == 0 && leftmost != -1 && seenSlash) {
                    return leftmost;
                }
            }
            else if (c == '/' && curlyOpen > 0) {
                seenSlash = true;
            }
        }
        return -1;
    }
    
    static class StringWithOffset
    {
        String string;
        int offset;
        
        public StringWithOffset(final String string, final int offset) {
            this.string = string;
            this.offset = offset;
        }
    }
}
