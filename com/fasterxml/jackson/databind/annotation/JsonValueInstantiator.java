// 
// Decompiled by Procyon v0.5.36
// 

package com.fasterxml.jackson.databind.annotation;

import com.fasterxml.jackson.databind.deser.ValueInstantiator;
import com.fasterxml.jackson.annotation.JacksonAnnotation;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Retention;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import java.lang.annotation.Annotation;

@Target({ ElementType.ANNOTATION_TYPE, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@JacksonAnnotation
public @interface JsonValueInstantiator {
    Class<? extends ValueInstantiator> value();
}
