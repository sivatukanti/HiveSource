// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.htrace.shaded.fasterxml.jackson.databind.annotation;

import org.apache.htrace.shaded.fasterxml.jackson.databind.util.Converter;
import org.apache.htrace.shaded.fasterxml.jackson.databind.KeyDeserializer;
import org.apache.htrace.shaded.fasterxml.jackson.databind.JsonDeserializer;
import org.apache.htrace.shaded.fasterxml.jackson.annotation.JacksonAnnotation;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Retention;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import java.lang.annotation.Annotation;

@Target({ ElementType.ANNOTATION_TYPE, ElementType.METHOD, ElementType.FIELD, ElementType.TYPE, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
@JacksonAnnotation
public @interface JsonDeserialize {
    Class<? extends JsonDeserializer<?>> using() default JsonDeserializer.None.class;
    
    Class<? extends JsonDeserializer<?>> contentUsing() default JsonDeserializer.None.class;
    
    Class<? extends KeyDeserializer> keyUsing() default KeyDeserializer.None.class;
    
    Class<?> builder() default Void.class;
    
    Class<? extends Converter<?, ?>> converter() default Converter.None.class;
    
    Class<? extends Converter<?, ?>> contentConverter() default Converter.None.class;
    
    Class<?> as() default Void.class;
    
    Class<?> keyAs() default Void.class;
    
    Class<?> contentAs() default Void.class;
}
