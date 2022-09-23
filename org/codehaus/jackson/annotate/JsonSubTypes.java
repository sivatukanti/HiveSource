// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.jackson.annotate;

import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Retention;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import java.lang.annotation.Annotation;

@Target({ ElementType.TYPE, ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
@JacksonAnnotation
public @interface JsonSubTypes {
    Type[] value();
    
    public @interface Type {
        Class<?> value();
        
        String name() default "";
    }
}
