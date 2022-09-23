// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.classification;

import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Retention;
import java.lang.annotation.Documented;
import java.lang.annotation.Annotation;

@Public
@InterfaceStability.Evolving
public class InterfaceAudience
{
    private InterfaceAudience() {
    }
    
    @Documented
    @Retention(RetentionPolicy.RUNTIME)
    public @interface Private {
    }
    
    @Documented
    @Retention(RetentionPolicy.RUNTIME)
    public @interface LimitedPrivate {
        String[] value();
    }
    
    @Documented
    @Retention(RetentionPolicy.RUNTIME)
    public @interface Public {
    }
}
