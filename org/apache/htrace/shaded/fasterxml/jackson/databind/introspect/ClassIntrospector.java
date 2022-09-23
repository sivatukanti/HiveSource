// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.htrace.shaded.fasterxml.jackson.databind.introspect;

import org.apache.htrace.shaded.fasterxml.jackson.databind.cfg.MapperConfig;
import org.apache.htrace.shaded.fasterxml.jackson.databind.DeserializationConfig;
import org.apache.htrace.shaded.fasterxml.jackson.databind.BeanDescription;
import org.apache.htrace.shaded.fasterxml.jackson.databind.JavaType;
import org.apache.htrace.shaded.fasterxml.jackson.databind.SerializationConfig;

public abstract class ClassIntrospector
{
    protected ClassIntrospector() {
    }
    
    public abstract BeanDescription forSerialization(final SerializationConfig p0, final JavaType p1, final MixInResolver p2);
    
    public abstract BeanDescription forDeserialization(final DeserializationConfig p0, final JavaType p1, final MixInResolver p2);
    
    public abstract BeanDescription forDeserializationWithBuilder(final DeserializationConfig p0, final JavaType p1, final MixInResolver p2);
    
    public abstract BeanDescription forCreation(final DeserializationConfig p0, final JavaType p1, final MixInResolver p2);
    
    public abstract BeanDescription forClassAnnotations(final MapperConfig<?> p0, final JavaType p1, final MixInResolver p2);
    
    public abstract BeanDescription forDirectClassAnnotations(final MapperConfig<?> p0, final JavaType p1, final MixInResolver p2);
    
    public interface MixInResolver
    {
        Class<?> findMixInClassFor(final Class<?> p0);
    }
}
