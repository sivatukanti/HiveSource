// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.spi;

import java.lang.annotation.Documented;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Retention;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import java.lang.annotation.Annotation;

public interface StringReader<T>
{
    T fromString(final String p0);
    
    @Target({ ElementType.TYPE })
    @Retention(RetentionPolicy.RUNTIME)
    @Documented
    public @interface ValidateDefaultValue {
        boolean value() default true;
    }
}
