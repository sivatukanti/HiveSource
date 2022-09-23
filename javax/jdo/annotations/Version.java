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
public @interface Version {
    VersionStrategy strategy() default VersionStrategy.UNSPECIFIED;
    
    String customStrategy() default "";
    
    String column() default "";
    
    String indexed() default "";
    
    Column[] columns() default {};
    
    Extension[] extensions() default {};
}
