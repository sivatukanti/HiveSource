// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.common.classification;

import java.lang.annotation.Documented;
import java.lang.annotation.Annotation;

public class InterfaceStability
{
    @Documented
    public @interface Unstable {
    }
    
    @Documented
    public @interface Evolving {
    }
    
    @Documented
    public @interface Stable {
    }
}
