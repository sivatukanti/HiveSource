// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.core.impl.provider.header;

import com.sun.jersey.core.header.GrammarUtil;

public class WriterUtil
{
    public static void appendQuotedIfNonToken(final StringBuilder b, final String value) {
        if (value == null) {
            return;
        }
        final boolean quote = !GrammarUtil.isTokenString(value);
        if (quote) {
            b.append('\"');
        }
        appendEscapingQuotes(b, value);
        if (quote) {
            b.append('\"');
        }
    }
    
    public static void appendQuotedIfWhitespace(final StringBuilder b, final String value) {
        if (value == null) {
            return;
        }
        final boolean quote = GrammarUtil.containsWhiteSpace(value);
        if (quote) {
            b.append('\"');
        }
        appendEscapingQuotes(b, value);
        if (quote) {
            b.append('\"');
        }
    }
    
    public static void appendQuoted(final StringBuilder b, final String value) {
        b.append('\"');
        appendEscapingQuotes(b, value);
        b.append('\"');
    }
    
    public static void appendEscapingQuotes(final StringBuilder b, final String value) {
        for (int i = 0; i < value.length(); ++i) {
            final char c = value.charAt(i);
            if (c == '\"') {
                b.append('\\');
            }
            b.append(c);
        }
    }
}
