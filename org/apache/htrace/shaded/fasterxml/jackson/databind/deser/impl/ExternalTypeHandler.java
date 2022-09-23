// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.htrace.shaded.fasterxml.jackson.databind.deser.impl;

import java.util.ArrayList;
import org.apache.htrace.shaded.fasterxml.jackson.databind.deser.SettableBeanProperty;
import org.apache.htrace.shaded.fasterxml.jackson.core.JsonToken;
import org.apache.htrace.shaded.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import org.apache.htrace.shaded.fasterxml.jackson.core.JsonProcessingException;
import java.io.IOException;
import org.apache.htrace.shaded.fasterxml.jackson.databind.DeserializationContext;
import org.apache.htrace.shaded.fasterxml.jackson.core.JsonParser;
import org.apache.htrace.shaded.fasterxml.jackson.databind.util.TokenBuffer;
import java.util.HashMap;

public class ExternalTypeHandler
{
    private final ExtTypedProperty[] _properties;
    private final HashMap<String, Integer> _nameToPropertyIndex;
    private final String[] _typeIds;
    private final TokenBuffer[] _tokens;
    
    protected ExternalTypeHandler(final ExtTypedProperty[] properties, final HashMap<String, Integer> nameToPropertyIndex, final String[] typeIds, final TokenBuffer[] tokens) {
        this._properties = properties;
        this._nameToPropertyIndex = nameToPropertyIndex;
        this._typeIds = typeIds;
        this._tokens = tokens;
    }
    
    protected ExternalTypeHandler(final ExternalTypeHandler h) {
        this._properties = h._properties;
        this._nameToPropertyIndex = h._nameToPropertyIndex;
        final int len = this._properties.length;
        this._typeIds = new String[len];
        this._tokens = new TokenBuffer[len];
    }
    
    public ExternalTypeHandler start() {
        return new ExternalTypeHandler(this);
    }
    
    public boolean handleTypePropertyValue(final JsonParser jp, final DeserializationContext ctxt, final String propName, final Object bean) throws IOException, JsonProcessingException {
        final Integer I = this._nameToPropertyIndex.get(propName);
        if (I == null) {
            return false;
        }
        final int index = I;
        final ExtTypedProperty prop = this._properties[index];
        if (!prop.hasTypePropertyName(propName)) {
            return false;
        }
        final String typeId = jp.getText();
        final boolean canDeserialize = bean != null && this._tokens[index] != null;
        if (canDeserialize) {
            this._deserializeAndSet(jp, ctxt, bean, index, typeId);
            this._tokens[index] = null;
        }
        else {
            this._typeIds[index] = typeId;
        }
        return true;
    }
    
    public boolean handlePropertyValue(final JsonParser jp, final DeserializationContext ctxt, final String propName, final Object bean) throws IOException, JsonProcessingException {
        final Integer I = this._nameToPropertyIndex.get(propName);
        if (I == null) {
            return false;
        }
        final int index = I;
        final ExtTypedProperty prop = this._properties[index];
        boolean canDeserialize;
        if (prop.hasTypePropertyName(propName)) {
            this._typeIds[index] = jp.getText();
            jp.skipChildren();
            canDeserialize = (bean != null && this._tokens[index] != null);
        }
        else {
            final TokenBuffer tokens = new TokenBuffer(jp);
            tokens.copyCurrentStructure(jp);
            this._tokens[index] = tokens;
            canDeserialize = (bean != null && this._typeIds[index] != null);
        }
        if (canDeserialize) {
            final String typeId = this._typeIds[index];
            this._typeIds[index] = null;
            this._deserializeAndSet(jp, ctxt, bean, index, typeId);
            this._tokens[index] = null;
        }
        return true;
    }
    
    public Object complete(final JsonParser jp, final DeserializationContext ctxt, final Object bean) throws IOException, JsonProcessingException {
        for (int i = 0, len = this._properties.length; i < len; ++i) {
            String typeId = this._typeIds[i];
            if (typeId == null) {
                final TokenBuffer tokens = this._tokens[i];
                if (tokens == null) {
                    continue;
                }
                final JsonToken t = tokens.firstToken();
                if (t != null && t.isScalarValue()) {
                    final JsonParser buffered = tokens.asParser(jp);
                    buffered.nextToken();
                    final SettableBeanProperty extProp = this._properties[i].getProperty();
                    final Object result = TypeDeserializer.deserializeIfNatural(buffered, ctxt, extProp.getType());
                    if (result != null) {
                        extProp.set(bean, result);
                        continue;
                    }
                    if (!this._properties[i].hasDefaultType()) {
                        throw ctxt.mappingException("Missing external type id property '" + this._properties[i].getTypePropertyName() + "'");
                    }
                    typeId = this._properties[i].getDefaultTypeId();
                }
            }
            else if (this._tokens[i] == null) {
                final SettableBeanProperty prop = this._properties[i].getProperty();
                throw ctxt.mappingException("Missing property '" + prop.getName() + "' for external type id '" + this._properties[i].getTypePropertyName());
            }
            this._deserializeAndSet(jp, ctxt, bean, i, typeId);
        }
        return bean;
    }
    
    public Object complete(final JsonParser jp, final DeserializationContext ctxt, final PropertyValueBuffer buffer, final PropertyBasedCreator creator) throws IOException, JsonProcessingException {
        final int len = this._properties.length;
        final Object[] values = new Object[len];
        for (int i = 0; i < len; ++i) {
            String typeId = this._typeIds[i];
            if (typeId == null) {
                if (this._tokens[i] == null) {
                    continue;
                }
                if (!this._properties[i].hasDefaultType()) {
                    throw ctxt.mappingException("Missing external type id property '" + this._properties[i].getTypePropertyName() + "'");
                }
                typeId = this._properties[i].getDefaultTypeId();
            }
            else if (this._tokens[i] == null) {
                final SettableBeanProperty prop = this._properties[i].getProperty();
                throw ctxt.mappingException("Missing property '" + prop.getName() + "' for external type id '" + this._properties[i].getTypePropertyName());
            }
            values[i] = this._deserialize(jp, ctxt, i, typeId);
        }
        for (int i = 0; i < len; ++i) {
            final SettableBeanProperty prop2 = this._properties[i].getProperty();
            if (creator.findCreatorProperty(prop2.getName()) != null) {
                buffer.assignParameter(prop2.getCreatorIndex(), values[i]);
            }
        }
        final Object bean = creator.build(ctxt, buffer);
        for (int j = 0; j < len; ++j) {
            final SettableBeanProperty prop = this._properties[j].getProperty();
            if (creator.findCreatorProperty(prop.getName()) == null) {
                prop.set(bean, values[j]);
            }
        }
        return bean;
    }
    
    protected final Object _deserialize(final JsonParser jp, final DeserializationContext ctxt, final int index, final String typeId) throws IOException, JsonProcessingException {
        final TokenBuffer merged = new TokenBuffer(jp);
        merged.writeStartArray();
        merged.writeString(typeId);
        JsonParser p2 = this._tokens[index].asParser(jp);
        p2.nextToken();
        merged.copyCurrentStructure(p2);
        merged.writeEndArray();
        p2 = merged.asParser(jp);
        p2.nextToken();
        return this._properties[index].getProperty().deserialize(p2, ctxt);
    }
    
    protected final void _deserializeAndSet(final JsonParser jp, final DeserializationContext ctxt, final Object bean, final int index, final String typeId) throws IOException, JsonProcessingException {
        final TokenBuffer merged = new TokenBuffer(jp);
        merged.writeStartArray();
        merged.writeString(typeId);
        JsonParser p2 = this._tokens[index].asParser(jp);
        p2.nextToken();
        merged.copyCurrentStructure(p2);
        merged.writeEndArray();
        p2 = merged.asParser(jp);
        p2.nextToken();
        this._properties[index].getProperty().deserializeAndSet(p2, ctxt, bean);
    }
    
    public static class Builder
    {
        private final ArrayList<ExtTypedProperty> _properties;
        private final HashMap<String, Integer> _nameToPropertyIndex;
        
        public Builder() {
            this._properties = new ArrayList<ExtTypedProperty>();
            this._nameToPropertyIndex = new HashMap<String, Integer>();
        }
        
        public void addExternal(final SettableBeanProperty property, final TypeDeserializer typeDeser) {
            final Integer index = this._properties.size();
            this._properties.add(new ExtTypedProperty(property, typeDeser));
            this._nameToPropertyIndex.put(property.getName(), index);
            this._nameToPropertyIndex.put(typeDeser.getPropertyName(), index);
        }
        
        public ExternalTypeHandler build() {
            return new ExternalTypeHandler(this._properties.toArray(new ExtTypedProperty[this._properties.size()]), this._nameToPropertyIndex, null, null);
        }
    }
    
    private static final class ExtTypedProperty
    {
        private final SettableBeanProperty _property;
        private final TypeDeserializer _typeDeserializer;
        private final String _typePropertyName;
        
        public ExtTypedProperty(final SettableBeanProperty property, final TypeDeserializer typeDeser) {
            this._property = property;
            this._typeDeserializer = typeDeser;
            this._typePropertyName = typeDeser.getPropertyName();
        }
        
        public boolean hasTypePropertyName(final String n) {
            return n.equals(this._typePropertyName);
        }
        
        public boolean hasDefaultType() {
            return this._typeDeserializer.getDefaultImpl() != null;
        }
        
        public String getDefaultTypeId() {
            final Class<?> defaultType = this._typeDeserializer.getDefaultImpl();
            if (defaultType == null) {
                return null;
            }
            return this._typeDeserializer.getTypeIdResolver().idFromValueAndType(null, defaultType);
        }
        
        public String getTypePropertyName() {
            return this._typePropertyName;
        }
        
        public SettableBeanProperty getProperty() {
            return this._property;
        }
    }
}
