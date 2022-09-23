// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.metrics2.annotation;

import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Retention;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import java.lang.annotation.Documented;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;
import java.lang.annotation.Annotation;

@InterfaceAudience.Public
@InterfaceStability.Evolving
@Documented
@Target({ ElementType.FIELD, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface Metric {
    String[] value() default {};
    
    String about() default "";
    
    String sampleName() default "Ops";
    
    String valueName() default "Time";
    
    boolean always() default false;
    
    Type type() default Type.DEFAULT;
    
    int interval() default 10;
    
    public enum Type
    {
        DEFAULT, 
        COUNTER, 
        GAUGE, 
        TAG;
    }
}
