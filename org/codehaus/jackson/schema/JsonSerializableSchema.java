// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.jackson.schema;

import org.codehaus.jackson.annotate.JacksonAnnotation;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Retention;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import java.lang.annotation.Annotation;

@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@JacksonAnnotation
public @interface JsonSerializableSchema {
    String schemaType() default "any";
    
    String schemaObjectPropertiesDefinition() default "##irrelevant";
    
    String schemaItemDefinition() default "##irrelevant";
}
