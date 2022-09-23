// 
// Decompiled by Procyon v0.5.36
// 

package javax.annotation;

import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Retention;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import java.lang.annotation.Annotation;

@Target({ ElementType.TYPE, ElementType.FIELD, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface Resource {
    String name() default "";
    
    Class type() default Object.class;
    
    AuthenticationType authenticationType() default AuthenticationType.CONTAINER;
    
    boolean shareable() default true;
    
    String mappedName() default "";
    
    String description() default "";
    
    public enum AuthenticationType
    {
        CONTAINER, 
        APPLICATION;
    }
}
