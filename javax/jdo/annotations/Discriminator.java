// 
// Decompiled by Procyon v0.5.36
// 

package javax.jdo.annotations;

import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Retention;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import java.lang.annotation.Annotation;

@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface Discriminator {
    DiscriminatorStrategy strategy() default DiscriminatorStrategy.UNSPECIFIED;
    
    String customStrategy() default "";
    
    String indexed() default "";
    
    String column() default "";
    
    String value() default "";
    
    Column[] columns() default {};
}
