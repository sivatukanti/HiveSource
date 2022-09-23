// 
// Decompiled by Procyon v0.5.36
// 

package javax.jdo.annotations;

import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Retention;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import java.lang.annotation.Annotation;

@Target({ ElementType.FIELD, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface Embedded {
    String ownerMember() default "";
    
    String nullIndicatorColumn() default "";
    
    String nullIndicatorValue() default "";
    
    Persistent[] members() default {};
}
