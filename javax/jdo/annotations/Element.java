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
public @interface Element {
    Class[] types() default {};
    
    String serialized() default "";
    
    String embedded() default "";
    
    Embedded[] embeddedMapping() default {};
    
    String dependent() default "";
    
    String table() default "";
    
    String column() default "";
    
    ForeignKeyAction deleteAction() default ForeignKeyAction.UNSPECIFIED;
    
    ForeignKeyAction updateAction() default ForeignKeyAction.UNSPECIFIED;
    
    String indexed() default "";
    
    String index() default "";
    
    String unique() default "";
    
    String uniqueKey() default "";
    
    String mappedBy() default "";
    
    Column[] columns() default {};
    
    String generateForeignKey() default "";
    
    String foreignKey() default "";
    
    Extension[] extensions() default {};
}
