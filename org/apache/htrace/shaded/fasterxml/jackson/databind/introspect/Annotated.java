// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.htrace.shaded.fasterxml.jackson.databind.introspect;

import java.lang.reflect.Type;
import org.apache.htrace.shaded.fasterxml.jackson.databind.JavaType;
import org.apache.htrace.shaded.fasterxml.jackson.databind.type.TypeBindings;
import java.lang.reflect.Modifier;
import java.lang.reflect.AnnotatedElement;
import java.lang.annotation.Annotation;

public abstract class Annotated
{
    protected Annotated() {
    }
    
    public abstract <A extends Annotation> A getAnnotation(final Class<A> p0);
    
    public final <A extends Annotation> boolean hasAnnotation(final Class<A> acls) {
        return this.getAnnotation(acls) != null;
    }
    
    public abstract Annotated withAnnotations(final AnnotationMap p0);
    
    public final Annotated withFallBackAnnotationsFrom(final Annotated annotated) {
        return this.withAnnotations(AnnotationMap.merge(this.getAllAnnotations(), annotated.getAllAnnotations()));
    }
    
    public abstract AnnotatedElement getAnnotated();
    
    protected abstract int getModifiers();
    
    public final boolean isPublic() {
        return Modifier.isPublic(this.getModifiers());
    }
    
    public abstract String getName();
    
    public JavaType getType(final TypeBindings context) {
        return context.resolveType(this.getGenericType());
    }
    
    public abstract Type getGenericType();
    
    public abstract Class<?> getRawType();
    
    public abstract Iterable<Annotation> annotations();
    
    protected abstract AnnotationMap getAllAnnotations();
}
