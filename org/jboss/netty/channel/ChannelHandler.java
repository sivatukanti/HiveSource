// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.channel;

import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Retention;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import java.lang.annotation.Documented;
import java.lang.annotation.Inherited;
import java.lang.annotation.Annotation;

public interface ChannelHandler
{
    @Inherited
    @Documented
    @Target({ ElementType.TYPE })
    @Retention(RetentionPolicy.RUNTIME)
    public @interface Sharable {
    }
}
