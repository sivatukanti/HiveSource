// 
// Decompiled by Procyon v0.5.36
// 

package org.joda.time.format;

import java.io.Writer;
import java.io.IOException;

public class FormatUtils
{
    private static final double LOG_10;
    
    private FormatUtils() {
    }
    
    public static void appendPaddedInteger(final StringBuffer sb, final int n, final int n2) {
        try {
            appendPaddedInteger((Appendable)sb, n, n2);
        }
        catch (IOException ex) {}
    }
    
    public static void appendPaddedInteger(final Appendable appendable, int i, int j) throws IOException {
        if (i < 0) {
            appendable.append('-');
            if (i == Integer.MIN_VALUE) {
                while (j > 10) {
                    appendable.append('0');
                    --j;
                }
                appendable.append("2147483648");
                return;
            }
            i = -i;
        }
        if (i < 10) {
            while (j > 1) {
                appendable.append('0');
                --j;
            }
            appendable.append((char)(i + 48));
        }
        else if (i < 100) {
            while (j > 2) {
                appendable.append('0');
                --j;
            }
            final int n = (i + 1) * 13421772 >> 27;
            appendable.append((char)(n + 48));
            appendable.append((char)(i - (n << 3) - (n << 1) + 48));
        }
        else {
            int n2;
            if (i < 1000) {
                n2 = 3;
            }
            else if (i < 10000) {
                n2 = 4;
            }
            else {
                n2 = (int)(Math.log(i) / FormatUtils.LOG_10) + 1;
            }
            while (j > n2) {
                appendable.append('0');
                --j;
            }
            appendable.append(Integer.toString(i));
        }
    }
    
    public static void appendPaddedInteger(final StringBuffer sb, final long n, final int n2) {
        try {
            appendPaddedInteger((Appendable)sb, n, n2);
        }
        catch (IOException ex) {}
    }
    
    public static void appendPaddedInteger(final Appendable appendable, long n, int i) throws IOException {
        final int n2 = (int)n;
        if (n2 == n) {
            appendPaddedInteger(appendable, n2, i);
        }
        else if (i <= 19) {
            appendable.append(Long.toString(n));
        }
        else {
            if (n < 0L) {
                appendable.append('-');
                if (n == Long.MIN_VALUE) {
                    while (i > 19) {
                        appendable.append('0');
                        --i;
                    }
                    appendable.append("9223372036854775808");
                    return;
                }
                n = -n;
            }
            while (i > (int)(Math.log((double)n) / FormatUtils.LOG_10) + 1) {
                appendable.append('0');
                --i;
            }
            appendable.append(Long.toString(n));
        }
    }
    
    public static void writePaddedInteger(final Writer writer, int i, int j) throws IOException {
        if (i < 0) {
            writer.write(45);
            if (i == Integer.MIN_VALUE) {
                while (j > 10) {
                    writer.write(48);
                    --j;
                }
                writer.write("2147483648");
                return;
            }
            i = -i;
        }
        if (i < 10) {
            while (j > 1) {
                writer.write(48);
                --j;
            }
            writer.write(i + 48);
        }
        else if (i < 100) {
            while (j > 2) {
                writer.write(48);
                --j;
            }
            final int n = (i + 1) * 13421772 >> 27;
            writer.write(n + 48);
            writer.write(i - (n << 3) - (n << 1) + 48);
        }
        else {
            int n2;
            if (i < 1000) {
                n2 = 3;
            }
            else if (i < 10000) {
                n2 = 4;
            }
            else {
                n2 = (int)(Math.log(i) / FormatUtils.LOG_10) + 1;
            }
            while (j > n2) {
                writer.write(48);
                --j;
            }
            writer.write(Integer.toString(i));
        }
    }
    
    public static void writePaddedInteger(final Writer writer, long n, int i) throws IOException {
        final int n2 = (int)n;
        if (n2 == n) {
            writePaddedInteger(writer, n2, i);
        }
        else if (i <= 19) {
            writer.write(Long.toString(n));
        }
        else {
            if (n < 0L) {
                writer.write(45);
                if (n == Long.MIN_VALUE) {
                    while (i > 19) {
                        writer.write(48);
                        --i;
                    }
                    writer.write("9223372036854775808");
                    return;
                }
                n = -n;
            }
            while (i > (int)(Math.log((double)n) / FormatUtils.LOG_10) + 1) {
                writer.write(48);
                --i;
            }
            writer.write(Long.toString(n));
        }
    }
    
    public static void appendUnpaddedInteger(final StringBuffer sb, final int n) {
        try {
            appendUnpaddedInteger((Appendable)sb, n);
        }
        catch (IOException ex) {}
    }
    
    public static void appendUnpaddedInteger(final Appendable appendable, int i) throws IOException {
        if (i < 0) {
            appendable.append('-');
            if (i == Integer.MIN_VALUE) {
                appendable.append("2147483648");
                return;
            }
            i = -i;
        }
        if (i < 10) {
            appendable.append((char)(i + 48));
        }
        else if (i < 100) {
            final int n = (i + 1) * 13421772 >> 27;
            appendable.append((char)(n + 48));
            appendable.append((char)(i - (n << 3) - (n << 1) + 48));
        }
        else {
            appendable.append(Integer.toString(i));
        }
    }
    
    public static void appendUnpaddedInteger(final StringBuffer sb, final long n) {
        try {
            appendUnpaddedInteger((Appendable)sb, n);
        }
        catch (IOException ex) {}
    }
    
    public static void appendUnpaddedInteger(final Appendable appendable, final long i) throws IOException {
        final int n = (int)i;
        if (n == i) {
            appendUnpaddedInteger(appendable, n);
        }
        else {
            appendable.append(Long.toString(i));
        }
    }
    
    public static void writeUnpaddedInteger(final Writer writer, int i) throws IOException {
        if (i < 0) {
            writer.write(45);
            if (i == Integer.MIN_VALUE) {
                writer.write("2147483648");
                return;
            }
            i = -i;
        }
        if (i < 10) {
            writer.write(i + 48);
        }
        else if (i < 100) {
            final int n = (i + 1) * 13421772 >> 27;
            writer.write(n + 48);
            writer.write(i - (n << 3) - (n << 1) + 48);
        }
        else {
            writer.write(Integer.toString(i));
        }
    }
    
    public static void writeUnpaddedInteger(final Writer writer, final long i) throws IOException {
        final int n = (int)i;
        if (n == i) {
            writeUnpaddedInteger(writer, n);
        }
        else {
            writer.write(Long.toString(i));
        }
    }
    
    public static int calculateDigitCount(final long n) {
        if (n >= 0L) {
            return (n < 10L) ? 1 : ((n < 100L) ? 2 : ((n < 1000L) ? 3 : ((n < 10000L) ? 4 : ((int)(Math.log((double)n) / FormatUtils.LOG_10) + 1))));
        }
        if (n != Long.MIN_VALUE) {
            return calculateDigitCount(-n) + 1;
        }
        return 20;
    }
    
    static int parseTwoDigits(final CharSequence charSequence, final int n) {
        final int n2 = charSequence.charAt(n) - '0';
        return (n2 << 3) + (n2 << 1) + charSequence.charAt(n + 1) - 48;
    }
    
    static String createErrorMessage(final String s, final int beginIndex) {
        final int endIndex = beginIndex + 32;
        String concat;
        if (s.length() <= endIndex + 3) {
            concat = s;
        }
        else {
            concat = s.substring(0, endIndex).concat("...");
        }
        if (beginIndex <= 0) {
            return "Invalid format: \"" + concat + '\"';
        }
        if (beginIndex >= s.length()) {
            return "Invalid format: \"" + concat + "\" is too short";
        }
        return "Invalid format: \"" + concat + "\" is malformed at \"" + concat.substring(beginIndex) + '\"';
    }
    
    static {
        LOG_10 = Math.log(10.0);
    }
}
