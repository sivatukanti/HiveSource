// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.utils;

import java.lang.annotation.Documented;
import java.lang.annotation.Annotation;

@Documented
public @interface Lock {
    Class[] value();
    
    public static class NoLock
    {
    }
}
