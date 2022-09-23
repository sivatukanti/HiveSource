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

public class AsPropertyTypeSerializer extends AsArrayTypeSerializer
{
    protected final String _typePropertyName;
    
    public AsPropertyTypeSerializer(final TypeIdResolver idRes, final BeanProperty property, final String propName) {
        super(idRes, property);
        this._typePropertyName = propName;
    }
    
    @Override
    public AsPropertyTypeSerializer forProperty(final BeanProperty prop) {
        return (this._property == prop) ? this : new AsPropertyTypeSerializer(this._idResolver, prop, this._typePropertyName);
    }
    
    @Override
    public String getPropertyName() {
        return this._typePropertyName;
    }
    
    @Override
    public JsonTypeInfo.As getTypeInclusion() {
        return JsonTypeInfo.As.PROPERTY;
    }
    
    @Override
    public void writeTypePrefixForObject(final Object value, final JsonGenerator jgen) throws IOException {
        final String typeId = this.idFromValue(value);
        if (jgen.canWriteTypeId()) {
            jgen.writeTypeId(typeId);
            jgen.writeStartObject();
        }
        else {
            jgen.writeStartObject();
            jgen.writeStringField(this._typePropertyName, typeId);
        }
    }
    
    @Override
    public void writeTypePrefixForObject(final Object value, final JsonGenerator jgen, final Class<?> type) throws IOException {
        final String typeId = this.idFromValueAndType(value, type);
        if (jgen.canWriteTypeId()) {
            jgen.writeTypeId(typeId);
            jgen.writeStartObject();
        }
        else {
            jgen.writeStartObject();
            jgen.writeStringField(this._typePropertyName, typeId);
        }
    }
    
    @Override
    public void writeTypeSuffixForObject(final Object value, final JsonGenerator jgen) throws IOException {
        jgen.writeEndObject();
    }
    
    @Override
    public void writeCustomTypePrefixForObject(final Object value, final JsonGenerator jgen, final String typeId) throws IOException {
        if (jgen.canWriteTypeId()) {
            jgen.writeTypeId(typeId);
            jgen.writeStartObject();
        }
        else {
            jgen.writeStartObject();
            jgen.writeStringField(this._typePropertyName, typeId);
        }
    }
    
    @Override
    public void writeCustomTypeSuffixForObject(final Object value, final JsonGenerator jgen, final String typeId) throws IOException {
        jgen.writeEndObject();
    }
}
