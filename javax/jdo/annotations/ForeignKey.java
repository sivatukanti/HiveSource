// 
// Decompiled by Procyon v0.5.36
// 

package javax.jdo.annotations;

import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Retention;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import java.lang.annotation.Annotation;

@Target({ ElementType.TYPE, ElementType.FIELD, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface ForeignKey {
    String name() default "";
    
    String table() default "";
    
    String deferred() default "";
    
    String unique() default "";
    
    ForeignKeyAction deleteAction() default ForeignKeyAction.RESTRICT;
    
    ForeignKeyAction updateAction() default ForeignKeyAction.RESTRICT;
    
    String[] members() default {};
    
    Column[] columns() default {};
}
