// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.configuration2.convert;

import org.apache.commons.lang3.StringUtils;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class LegacyListDelimiterHandler extends AbstractListDelimiterHandler
{
    private static final String ESCAPE = "\\";
    private static final String DOUBLE_ESC = "\\\\";
    private static final String QUAD_ESC = "\\\\\\\\";
    private final char delimiter;
    
    public LegacyListDelimiterHandler(final char listDelimiter) {
        this.delimiter = listDelimiter;
    }
    
    public char getDelimiter() {
        return this.delimiter;
    }
    
    @Override
    public Object escape(final Object value, final ValueTransformer transformer) {
        return this.escapeValue(value, false, transformer);
    }
    
    @Override
    public Object escapeList(final List<?> values, final ValueTransformer transformer) {
        if (!values.isEmpty()) {
            final Iterator<?> it = values.iterator();
            String lastValue = this.escapeValue(it.next(), true, transformer);
            final StringBuilder buf = new StringBuilder(lastValue);
            while (it.hasNext()) {
                if (lastValue.endsWith("\\") && countTrailingBS(lastValue) / 2 % 2 != 0) {
                    buf.append("\\").append("\\");
                }
                buf.append(this.getDelimiter());
                lastValue = this.escapeValue(it.next(), true, transformer);
                buf.append(lastValue);
            }
            return buf.toString();
        }
        return null;
    }
    
    @Override
    protected Collection<String> splitString(final String s, final boolean trim) {
        if (s.indexOf(this.getDelimiter()) < 0) {
            return Collections.singleton(s);
        }
        final List<String> list = new ArrayList<String>();
        StringBuilder token = new StringBuilder();
        int begin = 0;
        boolean inEscape = false;
        final char esc = "\\".charAt(0);
        while (begin < s.length()) {
            final char c = s.charAt(begin);
            if (inEscape) {
                if (c != this.getDelimiter() && c != esc) {
                    token.append(esc);
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
            else if (c == esc) {
                inEscape = true;
            }
            else {
                token.append(c);
            }
            ++begin;
        }
        if (inEscape) {
            token.append(esc);
        }
        String t2 = token.toString();
        if (trim) {
            t2 = t2.trim();
        }
        list.add(t2);
        return list;
    }
    
    @Override
    protected String escapeString(final String s) {
        return null;
    }
    
    protected String escapeBackslashs(final Object value, final boolean inList) {
        String strValue = String.valueOf(value);
        if (inList && strValue.indexOf("\\\\") >= 0) {
            strValue = StringUtils.replace(strValue, "\\\\", "\\\\\\\\");
        }
        return strValue;
    }
    
    protected String escapeValue(final Object value, final boolean inList, final ValueTransformer transformer) {
        String escapedValue = String.valueOf(transformer.transformValue(this.escapeBackslashs(value, inList)));
        if (this.getDelimiter() != '\0') {
            escapedValue = StringUtils.replace(escapedValue, String.valueOf(this.getDelimiter()), "\\" + this.getDelimiter());
        }
        return escapedValue;
    }
    
    private static int countTrailingBS(final String line) {
        int bsCount = 0;
        for (int idx = line.length() - 1; idx >= 0 && line.charAt(idx) == '\\'; --idx) {
            ++bsCount;
        }
        return bsCount;
    }
}
