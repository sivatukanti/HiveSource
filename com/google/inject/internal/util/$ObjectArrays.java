// 
// Decompiled by Procyon v0.5.36
// 

package com.google.inject.internal.util;

import java.lang.reflect.Array;

public final class $ObjectArrays
{
    private $ObjectArrays() {
    }
    
    public static <T> T[] newArray(final T[] reference, final int length) {
        final Class<?> type = reference.getClass().getComponentType();
        final T[] result = (T[])Array.newInstance(type, length);
        return result;
    }
}
