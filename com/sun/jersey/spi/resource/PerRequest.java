// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.spi.resource;

import com.sun.jersey.server.impl.resource.PerRequestFactory;
import com.sun.jersey.server.spi.component.ResourceComponentProviderFactoryClass;
import java.lang.annotation.Documented;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Retention;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import java.lang.annotation.Annotation;

@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Documented
@ResourceComponentProviderFactoryClass(PerRequestFactory.class)
public @interface PerRequest {
}
