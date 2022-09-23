// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.util;

import java.text.CharacterIterator;
import java.text.StringCharacterIterator;

public class RegularExpressionConverter
{
    private final char zeroOrMoreChar;
    private final char anyChar;
    private final char escapeChar;
    
    public RegularExpressionConverter(final char zeroOrMoreChar, final char anyChar, final char escapeChar) {
        this.zeroOrMoreChar = zeroOrMoreChar;
        this.anyChar = anyChar;
        this.escapeChar = escapeChar;
    }
    
    public String convert(final String input) {
        final StringBuilder lit = new StringBuilder();
        final CharacterIterator ci = new StringCharacterIterator(input);
        char c;
        while ((c = ci.current()) != '\uffff') {
            if (c == '\\') {
                final char ch = ci.next();
                if (ch == '\uffff') {
                    lit.append(this.escapeChar + "\\");
                }
                else if (ch == '.') {
                    lit.append(".");
                }
                else if (ch == '\\') {
                    lit.append(this.escapeChar + "\\" + this.escapeChar + ch);
                }
                else {
                    lit.append(this.escapeChar + "\\" + ch);
                }
            }
            else if (c == '.') {
                final int savedIdx = ci.getIndex();
                if (ci.next() == '*') {
                    lit.append(this.zeroOrMoreChar);
                }
                else {
                    ci.setIndex(savedIdx);
                    lit.append(this.anyChar);
                }
            }
            else if (c == this.anyChar) {
                lit.append("" + this.escapeChar + this.anyChar);
            }
            else if (c == this.zeroOrMoreChar) {
                lit.append("" + this.escapeChar + this.zeroOrMoreChar);
            }
            else {
                lit.append(c);
            }
            ci.next();
        }
        return lit.toString();
    }
}
