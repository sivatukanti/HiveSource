// 
// Decompiled by Procyon v0.5.36
// 

package com.nimbusds.jose.util;

import java.util.Arrays;

public class ArrayUtils
{
    public static <T> T[] concat(final T[] first, final T[]... rest) {
        int totalLength = first.length;
        for (final Object[] array : rest) {
            totalLength += array.length;
        }
        final Object[] result = Arrays.copyOf(first, totalLength);
        int offset = first.length;
        for (final Object[] array2 : rest) {
            System.arraycopy(array2, 0, result, offset, array2.length);
            offset += array2.length;
        }
        return (T[])result;
    }
    
    private ArrayUtils() {
    }
}
