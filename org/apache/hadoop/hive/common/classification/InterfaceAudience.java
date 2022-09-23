// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.common.classification;

import java.lang.annotation.Documented;
import java.lang.annotation.Annotation;

public class InterfaceAudience
{
    private InterfaceAudience() {
    }
    
    @Documented
    public @interface Private {
    }
    
    @Documented
    public @interface LimitedPrivate {
        String[] value();
    }
    
    @Documented
    public @interface Public {
    }
}
