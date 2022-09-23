// 
// Decompiled by Procyon v0.5.36
// 

package javax.xml.bind.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Retention;
import java.lang.annotation.Annotation;

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD, ElementType.METHOD, ElementType.PACKAGE })
public @interface XmlSchemaType {
    String name();
    
    String namespace() default "http://www.w3.org/2001/XMLSchema";
    
    Class type() default DEFAULT.class;
    
    public static final class DEFAULT
    {
    }
}
