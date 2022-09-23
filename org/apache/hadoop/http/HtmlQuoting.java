// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.http;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class HtmlQuoting
{
    private static final byte[] AMP_BYTES;
    private static final byte[] APOS_BYTES;
    private static final byte[] GT_BYTES;
    private static final byte[] LT_BYTES;
    private static final byte[] QUOT_BYTES;
    
    public static boolean needsQuoting(final byte[] data, final int off, final int len) {
        int i = off;
        while (i < off + len) {
            switch (data[i]) {
                case 34:
                case 38:
                case 39:
                case 60:
                case 62: {
                    return true;
                }
                default: {
                    ++i;
                    continue;
                }
            }
        }
        return false;
    }
    
    public static boolean needsQuoting(final String str) {
        if (str == null) {
            return false;
        }
        final byte[] bytes = str.getBytes(StandardCharsets.UTF_8);
        return needsQuoting(bytes, 0, bytes.length);
    }
    
    public static void quoteHtmlChars(final OutputStream output, final byte[] buffer, final int off, final int len) throws IOException {
        for (int i = off; i < off + len; ++i) {
            switch (buffer[i]) {
                case 38: {
                    output.write(HtmlQuoting.AMP_BYTES);
                    break;
                }
                case 60: {
                    output.write(HtmlQuoting.LT_BYTES);
                    break;
                }
                case 62: {
                    output.write(HtmlQuoting.GT_BYTES);
                    break;
                }
                case 39: {
                    output.write(HtmlQuoting.APOS_BYTES);
                    break;
                }
                case 34: {
                    output.write(HtmlQuoting.QUOT_BYTES);
                    break;
                }
                default: {
                    output.write(buffer, i, 1);
                    break;
                }
            }
        }
    }
    
    public static String quoteHtmlChars(final String item) {
        if (item == null) {
            return null;
        }
        final byte[] bytes = item.getBytes(StandardCharsets.UTF_8);
        if (needsQuoting(bytes, 0, bytes.length)) {
            final ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            try {
                quoteHtmlChars(buffer, bytes, 0, bytes.length);
                return buffer.toString("UTF-8");
            }
            catch (IOException ioe) {
                return null;
            }
        }
        return item;
    }
    
    public static OutputStream quoteOutputStream(final OutputStream out) throws IOException {
        return new OutputStream() {
            private byte[] data = new byte[1];
            
            @Override
            public void write(final byte[] data, final int off, final int len) throws IOException {
                HtmlQuoting.quoteHtmlChars(out, data, off, len);
            }
            
            @Override
            public void write(final int b) throws IOException {
                this.data[0] = (byte)b;
                HtmlQuoting.quoteHtmlChars(out, this.data, 0, 1);
            }
            
            @Override
            public void flush() throws IOException {
                out.flush();
            }
            
            @Override
            public void close() throws IOException {
                out.close();
            }
        };
    }
    
    public static String unquoteHtmlChars(final String item) {
        if (item == null) {
            return null;
        }
        int next = item.indexOf(38);
        if (next == -1) {
            return item;
        }
        final int len = item.length();
        int posn = 0;
        final StringBuilder buffer = new StringBuilder();
        while (next != -1) {
            buffer.append(item.substring(posn, next));
            if (item.startsWith("&amp;", next)) {
                buffer.append('&');
                next += 5;
            }
            else if (item.startsWith("&apos;", next)) {
                buffer.append('\'');
                next += 6;
            }
            else if (item.startsWith("&gt;", next)) {
                buffer.append('>');
                next += 4;
            }
            else if (item.startsWith("&lt;", next)) {
                buffer.append('<');
                next += 4;
            }
            else {
                if (!item.startsWith("&quot;", next)) {
                    int end = item.indexOf(59, next) + 1;
                    if (end == 0) {
                        end = len;
                    }
                    throw new IllegalArgumentException("Bad HTML quoting for " + item.substring(next, end));
                }
                buffer.append('\"');
                next += 6;
            }
            posn = next;
            next = item.indexOf(38, posn);
        }
        buffer.append(item.substring(posn, len));
        return buffer.toString();
    }
    
    public static void main(final String[] args) throws Exception {
        for (final String arg : args) {
            System.out.println("Original: " + arg);
            final String quoted = quoteHtmlChars(arg);
            System.out.println("Quoted: " + quoted);
            final String unquoted = unquoteHtmlChars(quoted);
            System.out.println("Unquoted: " + unquoted);
            System.out.println();
        }
    }
    
    static {
        AMP_BYTES = "&amp;".getBytes(StandardCharsets.UTF_8);
        APOS_BYTES = "&apos;".getBytes(StandardCharsets.UTF_8);
        GT_BYTES = "&gt;".getBytes(StandardCharsets.UTF_8);
        LT_BYTES = "&lt;".getBytes(StandardCharsets.UTF_8);
        QUOT_BYTES = "&quot;".getBytes(StandardCharsets.UTF_8);
    }
}
