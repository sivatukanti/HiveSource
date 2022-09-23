// 
// Decompiled by Procyon v0.5.36
// 

package parquet.org.codehaus.jackson.map.annotate;

import parquet.org.codehaus.jackson.map.deser.ValueInstantiator;
import parquet.org.codehaus.jackson.annotate.JacksonAnnotation;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Retention;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import java.lang.annotation.Annotation;

@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@JacksonAnnotation
public @interface JsonValueInstantiator {
    Class<? extends ValueInstantiator> value();
}
