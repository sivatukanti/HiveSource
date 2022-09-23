// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.security.token;

import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Retention;
import java.lang.annotation.Annotation;

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE })
@InterfaceAudience.Public
@InterfaceStability.Evolving
public @interface TokenInfo {
    Class<? extends TokenSelector<? extends TokenIdentifier>> value();
}
