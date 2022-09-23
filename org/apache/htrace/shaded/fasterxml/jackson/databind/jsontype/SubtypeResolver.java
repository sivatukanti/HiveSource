// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.htrace.shaded.fasterxml.jackson.databind.jsontype;

import org.apache.htrace.shaded.fasterxml.jackson.databind.introspect.AnnotatedClass;
import java.util.Collection;
import org.apache.htrace.shaded.fasterxml.jackson.databind.JavaType;
import org.apache.htrace.shaded.fasterxml.jackson.databind.AnnotationIntrospector;
import org.apache.htrace.shaded.fasterxml.jackson.databind.cfg.MapperConfig;
import org.apache.htrace.shaded.fasterxml.jackson.databind.introspect.AnnotatedMember;

public abstract class SubtypeResolver
{
    public abstract void registerSubtypes(final NamedType... p0);
    
    public abstract void registerSubtypes(final Class<?>... p0);
    
    public abstract Collection<NamedType> collectAndResolveSubtypes(final AnnotatedMember p0, final MapperConfig<?> p1, final AnnotationIntrospector p2, final JavaType p3);
    
    public abstract Collection<NamedType> collectAndResolveSubtypes(final AnnotatedClass p0, final MapperConfig<?> p1, final AnnotationIntrospector p2);
}
