// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.server.spi.component;

import java.lang.annotation.Documented;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Retention;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import java.lang.annotation.Annotation;

@Target({ ElementType.ANNOTATION_TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ResourceComponentProviderFactoryClass {
    Class<? extends ResourceComponentProviderFactory> value();
}
