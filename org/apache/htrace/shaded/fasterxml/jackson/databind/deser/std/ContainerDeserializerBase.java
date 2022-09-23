// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.htrace.shaded.fasterxml.jackson.databind.deser.std;

import org.apache.htrace.shaded.fasterxml.jackson.databind.JsonDeserializer;
import org.apache.htrace.shaded.fasterxml.jackson.databind.deser.SettableBeanProperty;
import org.apache.htrace.shaded.fasterxml.jackson.databind.JavaType;

public abstract class ContainerDeserializerBase<T> extends StdDeserializer<T>
{
    protected ContainerDeserializerBase(final JavaType selfType) {
        super(selfType);
    }
    
    @Deprecated
    protected ContainerDeserializerBase(final Class<?> selfType) {
        super(selfType);
    }
    
    @Override
    public SettableBeanProperty findBackReference(final String refName) {
        final JsonDeserializer<Object> valueDeser = this.getContentDeserializer();
        if (valueDeser == null) {
            throw new IllegalArgumentException("Can not handle managed/back reference '" + refName + "': type: container deserializer of type " + this.getClass().getName() + " returned null for 'getContentDeserializer()'");
        }
        return valueDeser.findBackReference(refName);
    }
    
    public abstract JavaType getContentType();
    
    public abstract JsonDeserializer<Object> getContentDeserializer();
}
