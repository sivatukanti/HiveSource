// 
// Decompiled by Procyon v0.5.36
// 

package javax.annotation.meta;

import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Retention;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import java.lang.annotation.Documented;
import java.lang.annotation.Annotation;

@Documented
@Target({ ElementType.ANNOTATION_TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface TypeQualifier {
    Class<?> applicableTo() default Object.class;
}
