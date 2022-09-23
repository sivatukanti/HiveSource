// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.htrace.shaded.fasterxml.jackson.databind.jsontype.impl;

import org.apache.htrace.shaded.fasterxml.jackson.databind.jsontype.TypeSerializer;
import java.io.IOException;
import org.apache.htrace.shaded.fasterxml.jackson.core.JsonGenerator;
import org.apache.htrace.shaded.fasterxml.jackson.annotation.JsonTypeInfo;
import org.apache.htrace.shaded.fasterxml.jackson.databind.BeanProperty;
import org.apache.htrace.shaded.fasterxml.jackson.databind.jsontype.TypeIdResolver;

public class AsExternalTypeSerializer extends TypeSerializerBase
{
    protected final String _typePropertyName;
    
    public AsExternalTypeSerializer(final TypeIdResolver idRes, final BeanProperty property, final String propName) {
        super(idRes, property);
        this._typePropertyName = propName;
    }
    
    @Override
    public AsExternalTypeSerializer forProperty(final BeanProperty prop) {
        return (this._property == prop) ? this : new AsExternalTypeSerializer(this._idResolver, prop, this._typePropertyName);
    }
    
    @Override
    public String getPropertyName() {
        return this._typePropertyName;
    }
    
    @Override
    public JsonTypeInfo.As getTypeInclusion() {
        return JsonTypeInfo.As.EXTERNAL_PROPERTY;
    }
    
    @Override
    public void writeTypePrefixForObject(final Object value, final JsonGenerator jgen) throws IOException {
        this._writeObjectPrefix(value, jgen);
    }
    
    @Override
    public void writeTypePrefixForObject(final Object value, final JsonGenerator jgen, final Class<?> type) throws IOException {
        this._writeObjectPrefix(value, jgen);
    }
    
    @Override
    public void writeTypePrefixForArray(final Object value, final JsonGenerator jgen) throws IOException {
        this._writeArrayPrefix(value, jgen);
    }
    
    @Override
    public void writeTypePrefixForArray(final Object value, final JsonGenerator jgen, final Class<?> type) throws IOException {
        this._writeArrayPrefix(value, jgen);
    }
    
    @Override
    public void writeTypePrefixForScalar(final Object value, final JsonGenerator jgen) throws IOException {
        this._writeScalarPrefix(value, jgen);
    }
    
    @Override
    public void writeTypePrefixForScalar(final Object value, final JsonGenerator jgen, final Class<?> type) throws IOException {
        this._writeScalarPrefix(value, jgen);
    }
    
    @Override
    public void writeTypeSuffixForObject(final Object value, final JsonGenerator jgen) throws IOException {
        this._writeObjectSuffix(value, jgen, this.idFromValue(value));
    }
    
    @Override
    public void writeTypeSuffixForArray(final Object value, final JsonGenerator jgen) throws IOException {
        this._writeArraySuffix(value, jgen, this.idFromValue(value));
    }
    
    @Override
    public void writeTypeSuffixForScalar(final Object value, final JsonGenerator jgen) throws IOException {
        this._writeScalarSuffix(value, jgen, this.idFromValue(value));
    }
    
    @Override
    public void writeCustomTypePrefixForScalar(final Object value, final JsonGenerator jgen, final String typeId) throws IOException {
        this._writeScalarPrefix(value, jgen);
    }
    
    @Override
    public void writeCustomTypePrefixForObject(final Object value, final JsonGenerator jgen, final String typeId) throws IOException {
        this._writeObjectPrefix(value, jgen);
    }
    
    @Override
    public void writeCustomTypePrefixForArray(final Object value, final JsonGenerator jgen, final String typeId) throws IOException {
        this._writeArrayPrefix(value, jgen);
    }
    
    @Override
    public void writeCustomTypeSuffixForScalar(final Object value, final JsonGenerator jgen, final String typeId) throws IOException {
        this._writeScalarSuffix(value, jgen, typeId);
    }
    
    @Override
    public void writeCustomTypeSuffixForObject(final Object value, final JsonGenerator jgen, final String typeId) throws IOException {
        this._writeObjectSuffix(value, jgen, typeId);
    }
    
    @Override
    public void writeCustomTypeSuffixForArray(final Object value, final JsonGenerator jgen, final String typeId) throws IOException {
        this._writeArraySuffix(value, jgen, typeId);
    }
    
    protected final void _writeScalarPrefix(final Object value, final JsonGenerator jgen) throws IOException {
    }
    
    protected final void _writeObjectPrefix(final Object value, final JsonGenerator jgen) throws IOException {
        jgen.writeStartObject();
    }
    
    protected final void _writeArrayPrefix(final Object value, final JsonGenerator jgen) throws IOException {
        jgen.writeStartArray();
    }
    
    protected final void _writeScalarSuffix(final Object value, final JsonGenerator jgen, final String typeId) throws IOException {
        jgen.writeStringField(this._typePropertyName, typeId);
    }
    
    protected final void _writeObjectSuffix(final Object value, final JsonGenerator jgen, final String typeId) throws IOException {
        jgen.writeEndObject();
        jgen.writeStringField(this._typePropertyName, typeId);
    }
    
    protected final void _writeArraySuffix(final Object value, final JsonGenerator jgen, final String typeId) throws IOException {
        jgen.writeEndArray();
        jgen.writeStringField(this._typePropertyName, typeId);
    }
}
