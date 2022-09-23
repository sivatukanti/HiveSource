// 
// Decompiled by Procyon v0.5.36
// 

package com.google.inject.internal;

import com.google.inject.internal.util.$Nullable;
import java.lang.annotation.Annotation;

public class Nullability
{
    private Nullability() {
    }
    
    public static boolean allowsNull(final Annotation[] annotations) {
        for (final Annotation a : annotations) {
            final Class<? extends Annotation> type = a.annotationType();
            if ("Nullable".equals(type.getSimpleName()) || type == $Nullable.class) {
                return true;
            }
        }
        return false;
    }
}
