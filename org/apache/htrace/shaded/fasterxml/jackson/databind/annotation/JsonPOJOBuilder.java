// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.htrace.shaded.fasterxml.jackson.databind.annotation;

import org.apache.htrace.shaded.fasterxml.jackson.annotation.JacksonAnnotation;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Retention;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import java.lang.annotation.Annotation;

@Target({ ElementType.ANNOTATION_TYPE, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@JacksonAnnotation
public @interface JsonPOJOBuilder {
    String buildMethodName() default "build";
    
    String withPrefix() default "with";
    
    public static class Value
    {
        public final String buildMethodName;
        public final String withPrefix;
        
        public Value(final JsonPOJOBuilder ann) {
            this.buildMethodName = ann.buildMethodName();
            this.withPrefix = ann.withPrefix();
        }
    }
}
