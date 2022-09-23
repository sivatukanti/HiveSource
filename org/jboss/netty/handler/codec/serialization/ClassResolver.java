// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.handler.codec.serialization;

public interface ClassResolver
{
    Class<?> resolve(final String p0) throws ClassNotFoundException;
}
