// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.classification;

import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Retention;
import java.lang.annotation.Documented;
import java.lang.annotation.Annotation;

@InterfaceAudience.Public
@Evolving
public class InterfaceStability
{
    @Documented
    @Retention(RetentionPolicy.RUNTIME)
    public @interface Unstable {
    }
    
    @Documented
    @Retention(RetentionPolicy.RUNTIME)
    public @interface Evolving {
    }
    
    @Documented
    @Retention(RetentionPolicy.RUNTIME)
    public @interface Stable {
    }
}
