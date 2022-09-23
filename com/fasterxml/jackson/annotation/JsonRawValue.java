// 
// Decompiled by Procyon v0.5.36
// 

package com.fasterxml.jackson.annotation;

import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Retention;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import java.lang.annotation.Annotation;

@Target({ ElementType.ANNOTATION_TYPE, ElementType.METHOD, ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
@JacksonAnnotation
public @interface JsonRawValue {
    boolean value() default true;
}
