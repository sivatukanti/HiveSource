// 
// Decompiled by Procyon v0.5.36
// 

package com.google.inject.internal;

import com.google.inject.BindingAnnotation;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Retention;
import java.lang.annotation.Annotation;
import java.util.concurrent.atomic.AtomicInteger;

public class UniqueAnnotations
{
    private static final AtomicInteger nextUniqueValue;
    
    private UniqueAnnotations() {
    }
    
    public static Annotation create() {
        return create(UniqueAnnotations.nextUniqueValue.getAndIncrement());
    }
    
    static Annotation create(final int value) {
        return new Internal() {
            public int value() {
                return value;
            }
            
            public Class<? extends Annotation> annotationType() {
                return Internal.class;
            }
            
            @Override
            public String toString() {
                return "@" + Internal.class.getName() + "(value=" + value + ")";
            }
            
            @Override
            public boolean equals(final Object o) {
                return o instanceof Internal && ((Internal)o).value() == this.value();
            }
            
            @Override
            public int hashCode() {
                return 127 * "value".hashCode() ^ value;
            }
        };
    }
    
    static {
        nextUniqueValue = new AtomicInteger(1);
    }
    
    @Retention(RetentionPolicy.RUNTIME)
    @BindingAnnotation
    @interface Internal {
        int value();
    }
}
