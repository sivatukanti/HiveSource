// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.htrace.shaded.fasterxml.jackson.databind.ser.std;

import org.apache.htrace.shaded.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.htrace.shaded.fasterxml.jackson.databind.JsonMappingException;
import org.apache.htrace.shaded.fasterxml.jackson.databind.jsonFormatVisitors.JsonObjectFormatVisitor;
import java.io.IOException;
import org.apache.htrace.shaded.fasterxml.jackson.databind.SerializerProvider;
import org.apache.htrace.shaded.fasterxml.jackson.core.JsonGenerator;
import org.apache.htrace.shaded.fasterxml.jackson.databind.PropertyName;
import org.apache.htrace.shaded.fasterxml.jackson.databind.JsonSerializer;
import org.apache.htrace.shaded.fasterxml.jackson.databind.jsontype.TypeSerializer;
import org.apache.htrace.shaded.fasterxml.jackson.databind.ser.PropertyWriter;

public class MapProperty extends PropertyWriter
{
    protected TypeSerializer _typeSerializer;
    protected Object _key;
    protected Object _value;
    protected JsonSerializer<Object> _keySerializer;
    protected JsonSerializer<Object> _valueSerializer;
    
    public MapProperty(final TypeSerializer typeSer) {
        this._typeSerializer = typeSer;
    }
    
    public void reset(final Object key, final Object value, final JsonSerializer<Object> keySer, final JsonSerializer<Object> valueSer) {
        this._key = key;
        this._value = value;
        this._keySerializer = keySer;
        this._valueSerializer = valueSer;
    }
    
    @Override
    public String getName() {
        if (this._key instanceof String) {
            return (String)this._key;
        }
        return String.valueOf(this._key);
    }
    
    @Override
    public PropertyName getFullName() {
        return new PropertyName(this.getName());
    }
    
    @Override
    public void serializeAsField(final Object pojo, final JsonGenerator jgen, final SerializerProvider provider) throws IOException {
        this._keySerializer.serialize(this._key, jgen, provider);
        if (this._typeSerializer == null) {
            this._valueSerializer.serialize(this._value, jgen, provider);
        }
        else {
            this._valueSerializer.serializeWithType(this._value, jgen, provider, this._typeSerializer);
        }
    }
    
    @Override
    public void serializeAsOmittedField(final Object pojo, final JsonGenerator jgen, final SerializerProvider provider) throws Exception {
        if (!jgen.canOmitFields()) {
            jgen.writeOmittedField(this.getName());
        }
    }
    
    @Override
    public void serializeAsElement(final Object pojo, final JsonGenerator jgen, final SerializerProvider provider) throws Exception {
        if (this._typeSerializer == null) {
            this._valueSerializer.serialize(this._value, jgen, provider);
        }
        else {
            this._valueSerializer.serializeWithType(this._value, jgen, provider, this._typeSerializer);
        }
    }
    
    @Override
    public void serializeAsPlaceholder(final Object pojo, final JsonGenerator jgen, final SerializerProvider provider) throws Exception {
        jgen.writeNull();
    }
    
    @Override
    public void depositSchemaProperty(final JsonObjectFormatVisitor objectVisitor) throws JsonMappingException {
    }
    
    @Deprecated
    @Override
    public void depositSchemaProperty(final ObjectNode propertiesNode, final SerializerProvider provider) throws JsonMappingException {
    }
}
