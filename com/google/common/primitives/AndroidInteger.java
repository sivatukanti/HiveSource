// 
// Decompiled by Procyon v0.5.36
// 

package com.google.common.primitives;

import com.google.common.base.Preconditions;
import javax.annotation.CheckForNull;

final class AndroidInteger
{
    @CheckForNull
    static Integer tryParse(final String string) {
        return tryParse(string, 10);
    }
    
    @CheckForNull
    static Integer tryParse(final String string, final int radix) {
        Preconditions.checkNotNull(string);
        Preconditions.checkArgument(radix >= 2, "Invalid radix %s, min radix is %s", radix, 2);
        Preconditions.checkArgument(radix <= 36, "Invalid radix %s, max radix is %s", radix, 36);
        final int length = string.length();
        int i = 0;
        if (length == 0) {
            return null;
        }
        final boolean negative = string.charAt(i) == '-';
        if (negative && ++i == length) {
            return null;
        }
        return tryParse(string, i, radix, negative);
    }
    
    @CheckForNull
    private static Integer tryParse(final String string, int offset, final int radix, final boolean negative) {
        final int max = Integer.MIN_VALUE / radix;
        int result = 0;
        final int length = string.length();
        while (offset < length) {
            final int digit = Character.digit(string.charAt(offset++), radix);
            if (digit == -1) {
                return null;
            }
            if (max > result) {
                return null;
            }
            final int next = result * radix - digit;
            if (next > result) {
                return null;
            }
            result = next;
        }
        if (!negative) {
            result = -result;
            if (result < 0) {
                return null;
            }
        }
        if (result > Integer.MAX_VALUE || result < Integer.MIN_VALUE) {
            return null;
        }
        return result;
    }
    
    private AndroidInteger() {
    }
}
