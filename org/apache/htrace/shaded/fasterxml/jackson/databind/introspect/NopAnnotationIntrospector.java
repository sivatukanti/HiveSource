// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.htrace.shaded.fasterxml.jackson.databind.introspect;

import org.apache.htrace.shaded.fasterxml.jackson.databind.cfg.PackageVersion;
import org.apache.htrace.shaded.fasterxml.jackson.core.Version;
import java.io.Serializable;
import org.apache.htrace.shaded.fasterxml.jackson.databind.AnnotationIntrospector;

public abstract class NopAnnotationIntrospector extends AnnotationIntrospector implements Serializable
{
    private static final long serialVersionUID = 1L;
    public static final NopAnnotationIntrospector instance;
    
    @Override
    public Version version() {
        return Version.unknownVersion();
    }
    
    static {
        instance = new NopAnnotationIntrospector() {
            private static final long serialVersionUID = 1L;
            
            @Override
            public Version version() {
                return PackageVersion.VERSION;
            }
        };
    }
}
