// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.htrace.shaded.fasterxml.jackson.databind.ser;

import org.apache.htrace.shaded.fasterxml.jackson.databind.AnnotationIntrospector;
import org.apache.htrace.shaded.fasterxml.jackson.databind.introspect.Annotated;
import org.apache.htrace.shaded.fasterxml.jackson.databind.BeanProperty;
import org.apache.htrace.shaded.fasterxml.jackson.databind.SerializerProvider;
import org.apache.htrace.shaded.fasterxml.jackson.databind.JsonSerializer;
import org.apache.htrace.shaded.fasterxml.jackson.databind.JavaType;
import org.apache.htrace.shaded.fasterxml.jackson.databind.jsontype.TypeSerializer;
import org.apache.htrace.shaded.fasterxml.jackson.databind.ser.std.StdSerializer;

public abstract class ContainerSerializer<T> extends StdSerializer<T>
{
    protected ContainerSerializer(final Class<T> t) {
        super(t);
    }
    
    protected ContainerSerializer(final Class<?> t, final boolean dummy) {
        super(t, dummy);
    }
    
    protected ContainerSerializer(final ContainerSerializer<?> src) {
        super(src._handledType, false);
    }
    
    public ContainerSerializer<?> withValueTypeSerializer(final TypeSerializer vts) {
        if (vts == null) {
            return this;
        }
        return this._withValueTypeSerializer(vts);
    }
    
    public abstract JavaType getContentType();
    
    public abstract JsonSerializer<?> getContentSerializer();
    
    @Override
    public abstract boolean isEmpty(final T p0);
    
    public abstract boolean hasSingleElement(final T p0);
    
    protected abstract ContainerSerializer<?> _withValueTypeSerializer(final TypeSerializer p0);
    
    protected boolean hasContentTypeAnnotation(final SerializerProvider provider, final BeanProperty property) {
        if (property != null) {
            final AnnotationIntrospector intr = provider.getAnnotationIntrospector();
            if (intr != null && intr.findSerializationContentType(property.getMember(), property.getType()) != null) {
                return true;
            }
        }
        return false;
    }
}
