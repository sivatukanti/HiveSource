// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.spi.inject;

import java.lang.annotation.Documented;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Retention;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import java.lang.annotation.Annotation;

@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ConstrainedTo {
    Class<? extends ConstrainedToType> value();
}
