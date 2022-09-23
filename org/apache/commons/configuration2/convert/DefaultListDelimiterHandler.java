// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.configuration2.convert;

import java.util.LinkedList;
import java.util.Collection;
import java.util.Iterator;
import org.apache.commons.lang3.StringUtils;
import java.util.List;

public class DefaultListDelimiterHandler extends AbstractListDelimiterHandler
{
    private static final char ESCAPE = '\\';
    private static final int BUF_SIZE = 16;
    private final char delimiter;
    
    public DefaultListDelimiterHandler(final char listDelimiter) {
        this.delimiter = listDelimiter;
    }
    
    public char getDelimiter() {
        return this.delimiter;
    }
    
    @Override
    public Object escapeList(final List<?> values, final ValueTransformer transformer) {
        final Object[] escapedValues = new String[values.size()];
        int idx = 0;
        for (final Object v : values) {
            escapedValues[idx++] = this.escape(v, transformer);
        }
        return StringUtils.join(escapedValues, this.getDelimiter());
    }
    
    @Override
    protected String escapeString(final String s) {
        final StringBuilder buf = new StringBuilder(s.length() + 16);
        for (int i = 0; i < s.length(); ++i) {
            final char c = s.charAt(i);
            if (c == this.getDelimiter() || c == '\\') {
                buf.append('\\');
            }
            buf.append(c);
        }
        return buf.toString();
    }
    
    @Override
    protected Collection<String> splitString(final String s, final boolean trim) {
        final List<String> list = new LinkedList<String>();
        StringBuilder token = new StringBuilder();
        boolean inEscape = false;
        for (int i = 0; i < s.length(); ++i) {
            final char c = s.charAt(i);
            if (inEscape) {
                if (c != this.getDelimiter() && c != '\\') {
                    token.append('\\');
                }
                token.append(c);
                inEscape = false;
            }
            else if (c == this.getDelimiter()) {
                String t = token.toString();
                if (trim) {
                    t = t.trim();
                }
                list.add(t);
                token = new StringBuilder();
            }
            else if (c == '\\') {
                inEscape = true;
            }
            else {
                token.append(c);
            }
        }
        if (inEscape) {
            token.append('\\');
        }
        String t2 = token.toString();
        if (trim) {
            t2 = t2.trim();
        }
        list.add(t2);
        return list;
    }
}
