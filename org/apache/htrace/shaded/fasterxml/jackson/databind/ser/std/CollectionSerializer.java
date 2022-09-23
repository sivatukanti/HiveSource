// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.htrace.shaded.fasterxml.jackson.databind.ser.std;

import org.apache.htrace.shaded.fasterxml.jackson.core.JsonGenerationException;
import java.io.IOException;
import org.apache.htrace.shaded.fasterxml.jackson.databind.ser.impl.PropertySerializerMap;
import org.apache.htrace.shaded.fasterxml.jackson.databind.SerializerProvider;
import org.apache.htrace.shaded.fasterxml.jackson.core.JsonGenerator;
import java.util.Iterator;
import org.apache.htrace.shaded.fasterxml.jackson.databind.ser.ContainerSerializer;
import org.apache.htrace.shaded.fasterxml.jackson.databind.JsonSerializer;
import org.apache.htrace.shaded.fasterxml.jackson.databind.BeanProperty;
import org.apache.htrace.shaded.fasterxml.jackson.databind.jsontype.TypeSerializer;
import org.apache.htrace.shaded.fasterxml.jackson.databind.JavaType;
import java.util.Collection;

public class CollectionSerializer extends AsArraySerializerBase<Collection<?>>
{
    public CollectionSerializer(final JavaType elemType, final boolean staticTyping, final TypeSerializer vts, final BeanProperty property, final JsonSerializer<Object> valueSerializer) {
        super(Collection.class, elemType, staticTyping, vts, property, valueSerializer);
    }
    
    public CollectionSerializer(final CollectionSerializer src, final BeanProperty property, final TypeSerializer vts, final JsonSerializer<?> valueSerializer) {
        super(src, property, vts, valueSerializer);
    }
    
    public ContainerSerializer<?> _withValueTypeSerializer(final TypeSerializer vts) {
        return new CollectionSerializer(this._elementType, this._staticTyping, vts, this._property, this._elementSerializer);
    }
    
    @Override
    public CollectionSerializer withResolved(final BeanProperty property, final TypeSerializer vts, final JsonSerializer<?> elementSerializer) {
        return new CollectionSerializer(this, property, vts, elementSerializer);
    }
    
    @Override
    public boolean isEmpty(final Collection<?> value) {
        return value == null || value.isEmpty();
    }
    
    @Override
    public boolean hasSingleElement(final Collection<?> value) {
        final Iterator<?> it = value.iterator();
        if (!it.hasNext()) {
            return false;
        }
        it.next();
        return !it.hasNext();
    }
    
    public void serializeContents(final Collection<?> value, final JsonGenerator jgen, final SerializerProvider provider) throws IOException, JsonGenerationException {
        if (this._elementSerializer != null) {
            this.serializeContentsUsing(value, jgen, provider, this._elementSerializer);
            return;
        }
        final Iterator<?> it = value.iterator();
        if (!it.hasNext()) {
            return;
        }
        PropertySerializerMap serializers = this._dynamicSerializers;
        final TypeSerializer typeSer = this._valueTypeSerializer;
        int i = 0;
        try {
            do {
                final Object elem = it.next();
                if (elem == null) {
                    provider.defaultSerializeNull(jgen);
                }
                else {
                    final Class<?> cc = elem.getClass();
                    JsonSerializer<Object> serializer = serializers.serializerFor(cc);
                    if (serializer == null) {
                        if (this._elementType.hasGenericTypes()) {
                            serializer = this._findAndAddDynamic(serializers, provider.constructSpecializedType(this._elementType, cc), provider);
                        }
                        else {
                            serializer = this._findAndAddDynamic(serializers, cc, provider);
                        }
                        serializers = this._dynamicSerializers;
                    }
                    if (typeSer == null) {
                        serializer.serialize(elem, jgen, provider);
                    }
                    else {
                        serializer.serializeWithType(elem, jgen, provider, typeSer);
                    }
                }
                ++i;
            } while (it.hasNext());
        }
        catch (Exception e) {
            this.wrapAndThrow(provider, e, value, i);
        }
    }
    
    public void serializeContentsUsing(final Collection<?> value, final JsonGenerator jgen, final SerializerProvider provider, final JsonSerializer<Object> ser) throws IOException, JsonGenerationException {
        final Iterator<?> it = value.iterator();
        if (it.hasNext()) {
            final TypeSerializer typeSer = this._valueTypeSerializer;
            int i = 0;
            do {
                final Object elem = it.next();
                try {
                    if (elem == null) {
                        provider.defaultSerializeNull(jgen);
                    }
                    else if (typeSer == null) {
                        ser.serialize(elem, jgen, provider);
                    }
                    else {
                        ser.serializeWithType(elem, jgen, provider, typeSer);
                    }
                    ++i;
                }
                catch (Exception e) {
                    this.wrapAndThrow(provider, e, value, i);
                }
            } while (it.hasNext());
        }
    }
}
