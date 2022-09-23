// 
// Decompiled by Procyon v0.5.36
// 

package com.ctc.wstx.io;

import java.io.IOException;
import java.io.Writer;

public final class TextEscaper
{
    private TextEscaper() {
    }
    
    public static void writeEscapedAttrValue(final Writer w, final String value) throws IOException {
        int i = 0;
        final int len = value.length();
        do {
            final int start = i;
            char c = '\0';
            while (i < len) {
                c = value.charAt(i);
                if (c == '<' || c == '&') {
                    break;
                }
                if (c == '\"') {
                    break;
                }
                ++i;
            }
            final int outLen = i - start;
            if (outLen > 0) {
                w.write(value, start, outLen);
            }
            if (i < len) {
                if (c == '<') {
                    w.write("&lt;");
                }
                else if (c == '&') {
                    w.write("&amp;");
                }
                else {
                    if (c != '\"') {
                        continue;
                    }
                    w.write("&quot;");
                }
            }
        } while (++i < len);
    }
    
    public static void outputDTDText(final Writer w, final char[] ch, final int offset, int len) throws IOException {
        int i = offset;
        len += offset;
        do {
            final int start = i;
            char c = '\0';
            while (i < len) {
                c = ch[i];
                if (c == '&' || c == '%') {
                    break;
                }
                if (c == '\"') {
                    break;
                }
                ++i;
            }
            final int outLen = i - start;
            if (outLen > 0) {
                w.write(ch, start, outLen);
            }
            if (i < len) {
                if (c == '&') {
                    w.write("&amp;");
                }
                else if (c == '%') {
                    w.write("&#37;");
                }
                else {
                    if (c != '\"') {
                        continue;
                    }
                    w.write("&#34;");
                }
            }
        } while (++i < len);
    }
}
