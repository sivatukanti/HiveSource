// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.ipc;

import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Retention;
import java.lang.annotation.Annotation;

@Retention(RetentionPolicy.RUNTIME)
public @interface ProtocolInfo {
    String protocolName();
    
    long protocolVersion() default -1L;
}
