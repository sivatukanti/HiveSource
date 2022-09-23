// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hive.common;

import org.apache.hadoop.hive.common.classification.InterfaceStability;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Retention;
import java.lang.annotation.Annotation;

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.PACKAGE })
@InterfaceStability.Unstable
public @interface HiveVersionAnnotation {
    String version();
    
    String shortVersion();
    
    String user();
    
    String date();
    
    String url();
    
    String revision();
    
    String branch();
    
    String srcChecksum();
}
