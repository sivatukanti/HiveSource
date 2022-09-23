// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.htrace.shaded.fasterxml.jackson.databind.ser.impl;

import org.apache.htrace.shaded.fasterxml.jackson.databind.JsonMappingException;
import org.apache.htrace.shaded.fasterxml.jackson.databind.JavaType;
import org.apache.htrace.shaded.fasterxml.jackson.databind.BeanProperty;
import java.util.Iterator;
import java.util.Map;
import org.apache.htrace.shaded.fasterxml.jackson.databind.JsonNode;
import org.apache.htrace.shaded.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.htrace.shaded.fasterxml.jackson.core.SerializableString;
import org.apache.htrace.shaded.fasterxml.jackson.databind.JsonSerializer;
import org.apache.htrace.shaded.fasterxml.jackson.databind.SerializerProvider;
import org.apache.htrace.shaded.fasterxml.jackson.core.JsonGenerator;
import org.apache.htrace.shaded.fasterxml.jackson.core.io.SerializedString;
import org.apache.htrace.shaded.fasterxml.jackson.databind.util.NameTransformer;
import org.apache.htrace.shaded.fasterxml.jackson.databind.ser.BeanPropertyWriter;

public class UnwrappingBeanPropertyWriter extends BeanPropertyWriter
{
    protected final NameTransformer _nameTransformer;
    
    public UnwrappingBeanPropertyWriter(final BeanPropertyWriter base, final NameTransformer unwrapper) {
        super(base);
        this._nameTransformer = unwrapper;
    }
    
    private UnwrappingBeanPropertyWriter(final UnwrappingBeanPropertyWriter base, final NameTransformer transformer, final SerializedString name) {
        super(base, name);
        this._nameTransformer = transformer;
    }
    
    @Override
    public UnwrappingBeanPropertyWriter rename(NameTransformer transformer) {
        final String oldName = this._name.getValue();
        final String newName = transformer.transform(oldName);
        transformer = NameTransformer.chainedTransformer(transformer, this._nameTransformer);
        return new UnwrappingBeanPropertyWriter(this, transformer, new SerializedString(newName));
    }
    
    @Override
    public boolean isUnwrapping() {
        return true;
    }
    
    @Override
    public void serializeAsField(final Object bean, final JsonGenerator jgen, final SerializerProvider prov) throws Exception {
        final Object value = this.get(bean);
        if (value == null) {
            return;
        }
        JsonSerializer<Object> ser = this._serializer;
        if (ser == null) {
            final Class<?> cls = value.getClass();
            final PropertySerializerMap map = this._dynamicSerializers;
            ser = map.serializerFor(cls);
            if (ser == null) {
                ser = this._findAndAddDynamic(map, cls, prov);
            }
        }
        if (this._suppressableValue != null) {
            if (UnwrappingBeanPropertyWriter.MARKER_FOR_EMPTY == this._suppressableValue) {
                if (ser.isEmpty(value)) {
                    return;
                }
            }
            else if (this._suppressableValue.equals(value)) {
                return;
            }
        }
        if (value == bean && this._handleSelfReference(bean, jgen, prov, ser)) {
            return;
        }
        if (!ser.isUnwrappingSerializer()) {
            jgen.writeFieldName(this._name);
        }
        if (this._typeSerializer == null) {
            ser.serialize(value, jgen, prov);
        }
        else {
            ser.serializeWithType(value, jgen, prov, this._typeSerializer);
        }
    }
    
    @Override
    public void assignSerializer(final JsonSerializer<Object> ser) {
        super.assignSerializer(ser);
        if (this._serializer != null) {
            NameTransformer t = this._nameTransformer;
            if (this._serializer.isUnwrappingSerializer()) {
                t = NameTransformer.chainedTransformer(t, ((UnwrappingBeanSerializer)this._serializer)._nameTransformer);
            }
            this._serializer = this._serializer.unwrappingSerializer(t);
        }
    }
    
    @Override
    protected void _depositSchemaProperty(final ObjectNode propertiesNode, final JsonNode schemaNode) {
        final JsonNode props = schemaNode.get("properties");
        if (props != null) {
            final Iterator<Map.Entry<String, JsonNode>> it = props.fields();
            while (it.hasNext()) {
                final Map.Entry<String, JsonNode> entry = it.next();
                String name = entry.getKey();
                if (this._nameTransformer != null) {
                    name = this._nameTransformer.transform(name);
                }
                propertiesNode.set(name, entry.getValue());
            }
        }
    }
    
    @Override
    protected JsonSerializer<Object> _findAndAddDynamic(final PropertySerializerMap map, final Class<?> type, final SerializerProvider provider) throws JsonMappingException {
        JsonSerializer<Object> serializer;
        if (this._nonTrivialBaseType != null) {
            final JavaType subtype = provider.constructSpecializedType(this._nonTrivialBaseType, type);
            serializer = provider.findValueSerializer(subtype, this);
        }
        else {
            serializer = provider.findValueSerializer(type, this);
        }
        NameTransformer t = this._nameTransformer;
        if (serializer.isUnwrappingSerializer()) {
            t = NameTransformer.chainedTransformer(t, ((UnwrappingBeanSerializer)serializer)._nameTransformer);
        }
        serializer = serializer.unwrappingSerializer(t);
        this._dynamicSerializers = this._dynamicSerializers.newWith(type, serializer);
        return serializer;
    }
}
