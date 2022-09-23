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

public class AsWrapperTypeSerializer extends TypeSerializerBase
{
    public AsWrapperTypeSerializer(final TypeIdResolver idRes, final BeanProperty property) {
        super(idRes, property);
    }
    
    @Override
    public AsWrapperTypeSerializer forProperty(final BeanProperty prop) {
        return (this._property == prop) ? this : new AsWrapperTypeSerializer(this._idResolver, prop);
    }
    
    @Override
    public JsonTypeInfo.As getTypeInclusion() {
        return JsonTypeInfo.As.WRAPPER_OBJECT;
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
            jgen.writeObjectFieldStart(typeId);
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
            jgen.writeObjectFieldStart(typeId);
        }
    }
    
    @Override
    public void writeTypePrefixForArray(final Object value, final JsonGenerator jgen) throws IOException {
        final String typeId = this.idFromValue(value);
        if (jgen.canWriteTypeId()) {
            jgen.writeTypeId(typeId);
            jgen.writeStartObject();
        }
        else {
            jgen.writeStartObject();
            jgen.writeArrayFieldStart(typeId);
        }
    }
    
    @Override
    public void writeTypePrefixForArray(final Object value, final JsonGenerator jgen, final Class<?> type) throws IOException {
        final String typeId = this.idFromValueAndType(value, type);
        if (jgen.canWriteTypeId()) {
            jgen.writeTypeId(typeId);
            jgen.writeStartObject();
        }
        else {
            jgen.writeStartObject();
            jgen.writeArrayFieldStart(typeId);
        }
    }
    
    @Override
    public void writeTypePrefixForScalar(final Object value, final JsonGenerator jgen) throws IOException {
        final String typeId = this.idFromValue(value);
        if (jgen.canWriteTypeId()) {
            jgen.writeTypeId(typeId);
        }
        else {
            jgen.writeStartObject();
            jgen.writeFieldName(typeId);
        }
    }
    
    @Override
    public void writeTypePrefixForScalar(final Object value, final JsonGenerator jgen, final Class<?> type) throws IOException {
        final String typeId = this.idFromValueAndType(value, type);
        if (jgen.canWriteTypeId()) {
            jgen.writeTypeId(typeId);
        }
        else {
            jgen.writeStartObject();
            jgen.writeFieldName(typeId);
        }
    }
    
    @Override
    public void writeTypeSuffixForObject(final Object value, final JsonGenerator jgen) throws IOException {
        jgen.writeEndObject();
        if (!jgen.canWriteTypeId()) {
            jgen.writeEndObject();
        }
    }
    
    @Override
    public void writeTypeSuffixForArray(final Object value, final JsonGenerator jgen) throws IOException {
        jgen.writeEndArray();
        if (!jgen.canWriteTypeId()) {
            jgen.writeEndObject();
        }
    }
    
    @Override
    public void writeTypeSuffixForScalar(final Object value, final JsonGenerator jgen) throws IOException {
        if (!jgen.canWriteTypeId()) {
            jgen.writeEndObject();
        }
    }
    
    @Override
    public void writeCustomTypePrefixForObject(final Object value, final JsonGenerator jgen, final String typeId) throws IOException {
        if (jgen.canWriteTypeId()) {
            jgen.writeTypeId(typeId);
            jgen.writeStartObject();
        }
        else {
            jgen.writeStartObject();
            jgen.writeObjectFieldStart(typeId);
        }
    }
    
    @Override
    public void writeCustomTypePrefixForArray(final Object value, final JsonGenerator jgen, final String typeId) throws IOException {
        if (jgen.canWriteTypeId()) {
            jgen.writeTypeId(typeId);
            jgen.writeStartArray();
        }
        else {
            jgen.writeStartObject();
            jgen.writeArrayFieldStart(typeId);
        }
    }
    
    @Override
    public void writeCustomTypePrefixForScalar(final Object value, final JsonGenerator jgen, final String typeId) throws IOException {
        if (jgen.canWriteTypeId()) {
            jgen.writeTypeId(typeId);
        }
        else {
            jgen.writeStartObject();
            jgen.writeFieldName(typeId);
        }
    }
    
    @Override
    public void writeCustomTypeSuffixForObject(final Object value, final JsonGenerator jgen, final String typeId) throws IOException {
        if (!jgen.canWriteTypeId()) {
            this.writeTypeSuffixForObject(value, jgen);
        }
    }
    
    @Override
    public void writeCustomTypeSuffixForArray(final Object value, final JsonGenerator jgen, final String typeId) throws IOException {
        if (!jgen.canWriteTypeId()) {
            this.writeTypeSuffixForArray(value, jgen);
        }
    }
    
    @Override
    public void writeCustomTypeSuffixForScalar(final Object value, final JsonGenerator jgen, final String typeId) throws IOException {
        if (!jgen.canWriteTypeId()) {
            this.writeTypeSuffixForScalar(value, jgen);
        }
    }
}
