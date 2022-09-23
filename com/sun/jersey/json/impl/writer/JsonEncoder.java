// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.json.impl.writer;

import java.util.Formatter;

public class JsonEncoder
{
    public static String encode(final String text) {
        if (null == text || text.length() == 0) {
            return text;
        }
        final Formatter formatter = new Formatter();
        final StringBuffer result = new StringBuffer();
        for (int i = 0; i < text.length(); ++i) {
            final char c = text.charAt(i);
            switch (c) {
                case '\"': {
                    result.append("\\\"");
                    break;
                }
                case '\\': {
                    result.append("\\\\");
                    break;
                }
                case '\b': {
                    result.append("\\b");
                    break;
                }
                case '\f': {
                    result.append("\\f");
                    break;
                }
                case '\n': {
                    result.append("\\n");
                    break;
                }
                case '\r': {
                    result.append("\\r");
                    break;
                }
                case '\t': {
                    result.append("\\t");
                    break;
                }
                default: {
                    if (c < ' ') {
                        result.append(formatter.format("\\u%04X", (int)c));
                        break;
                    }
                    result.append(c);
                    break;
                }
            }
        }
        return result.toString();
    }
}
