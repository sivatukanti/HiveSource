// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.util;

public class FastNumberFormat
{
    public static StringBuilder format(final StringBuilder sb, long value, int minimumDigits) {
        if (value < 0L) {
            sb.append('-');
            value = -value;
        }
        long tmp = value;
        do {
            tmp /= 10L;
        } while (--minimumDigits > 0 && tmp > 0L);
        for (int i = minimumDigits; i > 0; --i) {
            sb.append('0');
        }
        sb.append(value);
        return sb;
    }
}
