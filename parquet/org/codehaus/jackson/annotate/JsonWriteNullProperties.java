// 
// Decompiled by Procyon v0.5.36
// 

package parquet.org.codehaus.jackson.annotate;

import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Retention;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import java.lang.annotation.Annotation;

@Target({ ElementType.TYPE, ElementType.METHOD, ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
@JacksonAnnotation
@Deprecated
public @interface JsonWriteNullProperties {
    boolean value() default true;
}
