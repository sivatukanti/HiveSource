// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.io.retry;

import org.apache.hadoop.classification.InterfaceStability;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Retention;
import java.lang.annotation.Inherited;
import java.lang.annotation.Annotation;

@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD })
@InterfaceStability.Evolving
public @interface Idempotent {
}
