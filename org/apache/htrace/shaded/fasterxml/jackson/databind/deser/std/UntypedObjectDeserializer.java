// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.htrace.shaded.fasterxml.jackson.databind.deser.std;

import java.util.LinkedHashMap;
import org.apache.htrace.shaded.fasterxml.jackson.databind.util.ObjectBuffer;
import java.util.ArrayList;
import org.apache.htrace.shaded.fasterxml.jackson.core.JsonToken;
import org.apache.htrace.shaded.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import java.io.IOException;
import org.apache.htrace.shaded.fasterxml.jackson.databind.DeserializationFeature;
import org.apache.htrace.shaded.fasterxml.jackson.core.JsonParser;
import org.apache.htrace.shaded.fasterxml.jackson.databind.BeanProperty;
import org.apache.htrace.shaded.fasterxml.jackson.databind.util.ClassUtil;
import org.apache.htrace.shaded.fasterxml.jackson.databind.JsonMappingException;
import org.apache.htrace.shaded.fasterxml.jackson.databind.type.TypeFactory;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;
import org.apache.htrace.shaded.fasterxml.jackson.databind.JavaType;
import java.util.Map;
import org.apache.htrace.shaded.fasterxml.jackson.databind.DeserializationContext;
import org.apache.htrace.shaded.fasterxml.jackson.databind.JsonDeserializer;
import org.apache.htrace.shaded.fasterxml.jackson.databind.annotation.JacksonStdImpl;
import org.apache.htrace.shaded.fasterxml.jackson.databind.deser.ContextualDeserializer;
import org.apache.htrace.shaded.fasterxml.jackson.databind.deser.ResolvableDeserializer;

@JacksonStdImpl
public class UntypedObjectDeserializer extends StdDeserializer<Object> implements ResolvableDeserializer, ContextualDeserializer
{
    private static final long serialVersionUID = 1L;
    protected static final Object[] NO_OBJECTS;
    @Deprecated
    public static final UntypedObjectDeserializer instance;
    protected JsonDeserializer<Object> _mapDeserializer;
    protected JsonDeserializer<Object> _listDeserializer;
    protected JsonDeserializer<Object> _stringDeserializer;
    protected JsonDeserializer<Object> _numberDeserializer;
    
    public UntypedObjectDeserializer() {
        super(Object.class);
    }
    
    public UntypedObjectDeserializer(final UntypedObjectDeserializer base, final JsonDeserializer<?> mapDeser, final JsonDeserializer<?> listDeser, final JsonDeserializer<?> stringDeser, final JsonDeserializer<?> numberDeser) {
        super(Object.class);
        this._mapDeserializer = (JsonDeserializer<Object>)mapDeser;
        this._listDeserializer = (JsonDeserializer<Object>)listDeser;
        this._stringDeserializer = (JsonDeserializer<Object>)stringDeser;
        this._numberDeserializer = (JsonDeserializer<Object>)numberDeser;
    }
    
    @Override
    public void resolve(final DeserializationContext ctxt) throws JsonMappingException {
        final JavaType obType = ctxt.constructType(Object.class);
        final JavaType stringType = ctxt.constructType(String.class);
        final TypeFactory tf = ctxt.getTypeFactory();
        this._mapDeserializer = this._findCustomDeser(ctxt, tf.constructMapType(Map.class, stringType, obType));
        this._listDeserializer = this._findCustomDeser(ctxt, tf.constructCollectionType(List.class, obType));
        this._stringDeserializer = this._findCustomDeser(ctxt, stringType);
        this._numberDeserializer = this._findCustomDeser(ctxt, tf.constructType(Number.class));
    }
    
    protected JsonDeserializer<Object> _findCustomDeser(final DeserializationContext ctxt, final JavaType type) throws JsonMappingException {
        final JsonDeserializer<?> deser = ctxt.findRootValueDeserializer(type);
        if (ClassUtil.isJacksonStdImpl(deser)) {
            return null;
        }
        return (JsonDeserializer<Object>)deser;
    }
    
    @Override
    public JsonDeserializer<?> createContextual(final DeserializationContext ctxt, final BeanProperty property) throws JsonMappingException {
        if (this._stringDeserializer == null && this._numberDeserializer == null && this._mapDeserializer == null && this._listDeserializer == null && this.getClass() == UntypedObjectDeserializer.class) {
            return Vanilla.std;
        }
        JsonDeserializer<?> mapDeserializer = this._mapDeserializer;
        if (mapDeserializer instanceof ContextualDeserializer) {
            mapDeserializer = ((ContextualDeserializer)mapDeserializer).createContextual(ctxt, property);
        }
        JsonDeserializer<?> listDeserializer = this._listDeserializer;
        if (listDeserializer instanceof ContextualDeserializer) {
            listDeserializer = ((ContextualDeserializer)listDeserializer).createContextual(ctxt, property);
        }
        JsonDeserializer<?> stringDeserializer = this._stringDeserializer;
        if (stringDeserializer instanceof ContextualDeserializer) {
            stringDeserializer = ((ContextualDeserializer)stringDeserializer).createContextual(ctxt, property);
        }
        JsonDeserializer<?> numberDeserializer = this._numberDeserializer;
        if (numberDeserializer instanceof ContextualDeserializer) {
            numberDeserializer = ((ContextualDeserializer)numberDeserializer).createContextual(ctxt, property);
        }
        if (mapDeserializer != this._mapDeserializer || listDeserializer != this._listDeserializer || stringDeserializer != this._stringDeserializer || numberDeserializer != this._numberDeserializer) {
            return this._withResolved(mapDeserializer, listDeserializer, stringDeserializer, numberDeserializer);
        }
        return this;
    }
    
    protected JsonDeserializer<?> _withResolved(final JsonDeserializer<?> mapDeser, final JsonDeserializer<?> listDeser, final JsonDeserializer<?> stringDeser, final JsonDeserializer<?> numberDeser) {
        return new UntypedObjectDeserializer(this, mapDeser, listDeser, stringDeser, numberDeser);
    }
    
    @Override
    public Object deserialize(final JsonParser jp, final DeserializationContext ctxt) throws IOException {
        switch (jp.getCurrentToken()) {
            case FIELD_NAME:
            case START_OBJECT: {
                if (this._mapDeserializer != null) {
                    return this._mapDeserializer.deserialize(jp, ctxt);
                }
                return this.mapObject(jp, ctxt);
            }
            case START_ARRAY: {
                if (ctxt.isEnabled(DeserializationFeature.USE_JAVA_ARRAY_FOR_JSON_ARRAY)) {
                    return this.mapArrayToArray(jp, ctxt);
                }
                if (this._listDeserializer != null) {
                    return this._listDeserializer.deserialize(jp, ctxt);
                }
                return this.mapArray(jp, ctxt);
            }
            case VALUE_EMBEDDED_OBJECT: {
                return jp.getEmbeddedObject();
            }
            case VALUE_STRING: {
                if (this._stringDeserializer != null) {
                    return this._stringDeserializer.deserialize(jp, ctxt);
                }
                return jp.getText();
            }
            case VALUE_NUMBER_INT: {
                if (this._numberDeserializer != null) {
                    return this._numberDeserializer.deserialize(jp, ctxt);
                }
                if (ctxt.isEnabled(DeserializationFeature.USE_BIG_INTEGER_FOR_INTS)) {
                    return jp.getBigIntegerValue();
                }
                return jp.getNumberValue();
            }
            case VALUE_NUMBER_FLOAT: {
                if (this._numberDeserializer != null) {
                    return this._numberDeserializer.deserialize(jp, ctxt);
                }
                if (ctxt.isEnabled(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS)) {
                    return jp.getDecimalValue();
                }
                return jp.getDoubleValue();
            }
            case VALUE_TRUE: {
                return Boolean.TRUE;
            }
            case VALUE_FALSE: {
                return Boolean.FALSE;
            }
            case VALUE_NULL: {
                return null;
            }
            default: {
                throw ctxt.mappingException(Object.class);
            }
        }
    }
    
    @Override
    public Object deserializeWithType(final JsonParser jp, final DeserializationContext ctxt, final TypeDeserializer typeDeserializer) throws IOException {
        final JsonToken t = jp.getCurrentToken();
        switch (t) {
            case FIELD_NAME:
            case START_OBJECT:
            case START_ARRAY: {
                return typeDeserializer.deserializeTypedFromAny(jp, ctxt);
            }
            case VALUE_STRING: {
                if (this._stringDeserializer != null) {
                    return this._stringDeserializer.deserialize(jp, ctxt);
                }
                return jp.getText();
            }
            case VALUE_NUMBER_INT: {
                if (this._numberDeserializer != null) {
                    return this._numberDeserializer.deserialize(jp, ctxt);
                }
                if (ctxt.isEnabled(DeserializationFeature.USE_BIG_INTEGER_FOR_INTS)) {
                    return jp.getBigIntegerValue();
                }
                return jp.getNumberValue();
            }
            case VALUE_NUMBER_FLOAT: {
                if (this._numberDeserializer != null) {
                    return this._numberDeserializer.deserialize(jp, ctxt);
                }
                if (ctxt.isEnabled(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS)) {
                    return jp.getDecimalValue();
                }
                return jp.getDoubleValue();
            }
            case VALUE_TRUE: {
                return Boolean.TRUE;
            }
            case VALUE_FALSE: {
                return Boolean.FALSE;
            }
            case VALUE_EMBEDDED_OBJECT: {
                return jp.getEmbeddedObject();
            }
            case VALUE_NULL: {
                return null;
            }
            default: {
                throw ctxt.mappingException(Object.class);
            }
        }
    }
    
    protected Object mapArray(final JsonParser jp, final DeserializationContext ctxt) throws IOException {
        if (jp.nextToken() == JsonToken.END_ARRAY) {
            return new ArrayList(2);
        }
        Object value = this.deserialize(jp, ctxt);
        if (jp.nextToken() == JsonToken.END_ARRAY) {
            final ArrayList<Object> l = new ArrayList<Object>(2);
            l.add(value);
            return l;
        }
        final Object value2 = this.deserialize(jp, ctxt);
        if (jp.nextToken() == JsonToken.END_ARRAY) {
            final ArrayList<Object> i = new ArrayList<Object>(2);
            i.add(value);
            i.add(value2);
            return i;
        }
        final ObjectBuffer buffer = ctxt.leaseObjectBuffer();
        Object[] values = buffer.resetAndStart();
        int ptr = 0;
        values[ptr++] = value;
        values[ptr++] = value2;
        int totalSize = ptr;
        do {
            value = this.deserialize(jp, ctxt);
            ++totalSize;
            if (ptr >= values.length) {
                values = buffer.appendCompletedChunk(values);
                ptr = 0;
            }
            values[ptr++] = value;
        } while (jp.nextToken() != JsonToken.END_ARRAY);
        final ArrayList<Object> result = new ArrayList<Object>(totalSize);
        buffer.completeAndClearBuffer(values, ptr, result);
        return result;
    }
    
    protected Object mapObject(final JsonParser jp, final DeserializationContext ctxt) throws IOException {
        JsonToken t = jp.getCurrentToken();
        if (t == JsonToken.START_OBJECT) {
            t = jp.nextToken();
        }
        if (t == JsonToken.END_OBJECT) {
            return new LinkedHashMap(2);
        }
        final String field1 = jp.getCurrentName();
        jp.nextToken();
        final Object value1 = this.deserialize(jp, ctxt);
        if (jp.nextToken() == JsonToken.END_OBJECT) {
            final LinkedHashMap<String, Object> result = new LinkedHashMap<String, Object>(2);
            result.put(field1, value1);
            return result;
        }
        final String field2 = jp.getCurrentName();
        jp.nextToken();
        final Object value2 = this.deserialize(jp, ctxt);
        if (jp.nextToken() == JsonToken.END_OBJECT) {
            final LinkedHashMap<String, Object> result2 = new LinkedHashMap<String, Object>(4);
            result2.put(field1, value1);
            result2.put(field2, value2);
            return result2;
        }
        final LinkedHashMap<String, Object> result2 = new LinkedHashMap<String, Object>();
        result2.put(field1, value1);
        result2.put(field2, value2);
        do {
            final String fieldName = jp.getCurrentName();
            jp.nextToken();
            result2.put(fieldName, this.deserialize(jp, ctxt));
        } while (jp.nextToken() != JsonToken.END_OBJECT);
        return result2;
    }
    
    protected Object[] mapArrayToArray(final JsonParser jp, final DeserializationContext ctxt) throws IOException {
        if (jp.nextToken() == JsonToken.END_ARRAY) {
            return UntypedObjectDeserializer.NO_OBJECTS;
        }
        final ObjectBuffer buffer = ctxt.leaseObjectBuffer();
        Object[] values = buffer.resetAndStart();
        int ptr = 0;
        do {
            final Object value = this.deserialize(jp, ctxt);
            if (ptr >= values.length) {
                values = buffer.appendCompletedChunk(values);
                ptr = 0;
            }
            values[ptr++] = value;
        } while (jp.nextToken() != JsonToken.END_ARRAY);
        return buffer.completeAndClearBuffer(values, ptr);
    }
    
    static {
        NO_OBJECTS = new Object[0];
        instance = new UntypedObjectDeserializer();
    }
    
    @JacksonStdImpl
    public static class Vanilla extends StdDeserializer<Object>
    {
        private static final long serialVersionUID = 1L;
        public static final Vanilla std;
        
        public Vanilla() {
            super(Object.class);
        }
        
        @Override
        public Object deserialize(final JsonParser jp, final DeserializationContext ctxt) throws IOException {
            switch (jp.getCurrentTokenId()) {
                case 1: {
                    final JsonToken t = jp.nextToken();
                    if (t == JsonToken.END_OBJECT) {
                        return new LinkedHashMap(2);
                    }
                    return this.mapObject(jp, ctxt);
                }
                case 5: {
                    return this.mapObject(jp, ctxt);
                }
                case 3: {
                    final JsonToken t = jp.nextToken();
                    if (t == JsonToken.END_ARRAY) {
                        if (ctxt.isEnabled(DeserializationFeature.USE_JAVA_ARRAY_FOR_JSON_ARRAY)) {
                            return UntypedObjectDeserializer.NO_OBJECTS;
                        }
                        return new ArrayList(2);
                    }
                    else {
                        if (ctxt.isEnabled(DeserializationFeature.USE_JAVA_ARRAY_FOR_JSON_ARRAY)) {
                            return this.mapArrayToArray(jp, ctxt);
                        }
                        return this.mapArray(jp, ctxt);
                    }
                    break;
                }
                case 12: {
                    return jp.getEmbeddedObject();
                }
                case 6: {
                    return jp.getText();
                }
                case 7: {
                    if (ctxt.isEnabled(DeserializationFeature.USE_BIG_INTEGER_FOR_INTS)) {
                        return jp.getBigIntegerValue();
                    }
                    return jp.getNumberValue();
                }
                case 8: {
                    if (ctxt.isEnabled(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS)) {
                        return jp.getDecimalValue();
                    }
                    return jp.getDoubleValue();
                }
                case 9: {
                    return Boolean.TRUE;
                }
                case 10: {
                    return Boolean.FALSE;
                }
                case 11: {
                    return null;
                }
                default: {
                    throw ctxt.mappingException(Object.class);
                }
            }
        }
        
        @Override
        public Object deserializeWithType(final JsonParser jp, final DeserializationContext ctxt, final TypeDeserializer typeDeserializer) throws IOException {
            switch (jp.getCurrentTokenId()) {
                case 1:
                case 3:
                case 5: {
                    return typeDeserializer.deserializeTypedFromAny(jp, ctxt);
                }
                case 6: {
                    return jp.getText();
                }
                case 7: {
                    if (ctxt.isEnabled(DeserializationFeature.USE_BIG_INTEGER_FOR_INTS)) {
                        return jp.getBigIntegerValue();
                    }
                    return jp.getNumberValue();
                }
                case 8: {
                    if (ctxt.isEnabled(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS)) {
                        return jp.getDecimalValue();
                    }
                    return jp.getDoubleValue();
                }
                case 9: {
                    return Boolean.TRUE;
                }
                case 10: {
                    return Boolean.FALSE;
                }
                case 12: {
                    return jp.getEmbeddedObject();
                }
                case 11: {
                    return null;
                }
                default: {
                    throw ctxt.mappingException(Object.class);
                }
            }
        }
        
        protected Object mapArray(final JsonParser jp, final DeserializationContext ctxt) throws IOException {
            Object value = this.deserialize(jp, ctxt);
            if (jp.nextToken() == JsonToken.END_ARRAY) {
                final ArrayList<Object> l = new ArrayList<Object>(2);
                l.add(value);
                return l;
            }
            final Object value2 = this.deserialize(jp, ctxt);
            if (jp.nextToken() == JsonToken.END_ARRAY) {
                final ArrayList<Object> i = new ArrayList<Object>(2);
                i.add(value);
                i.add(value2);
                return i;
            }
            final ObjectBuffer buffer = ctxt.leaseObjectBuffer();
            Object[] values = buffer.resetAndStart();
            int ptr = 0;
            values[ptr++] = value;
            values[ptr++] = value2;
            int totalSize = ptr;
            do {
                value = this.deserialize(jp, ctxt);
                ++totalSize;
                if (ptr >= values.length) {
                    values = buffer.appendCompletedChunk(values);
                    ptr = 0;
                }
                values[ptr++] = value;
            } while (jp.nextToken() != JsonToken.END_ARRAY);
            final ArrayList<Object> result = new ArrayList<Object>(totalSize);
            buffer.completeAndClearBuffer(values, ptr, result);
            return result;
        }
        
        protected Object mapObject(final JsonParser jp, final DeserializationContext ctxt) throws IOException {
            final String field1 = jp.getText();
            jp.nextToken();
            final Object value1 = this.deserialize(jp, ctxt);
            if (jp.nextToken() == JsonToken.END_OBJECT) {
                final LinkedHashMap<String, Object> result = new LinkedHashMap<String, Object>(2);
                result.put(field1, value1);
                return result;
            }
            final String field2 = jp.getText();
            jp.nextToken();
            final Object value2 = this.deserialize(jp, ctxt);
            if (jp.nextToken() == JsonToken.END_OBJECT) {
                final LinkedHashMap<String, Object> result2 = new LinkedHashMap<String, Object>(4);
                result2.put(field1, value1);
                result2.put(field2, value2);
                return result2;
            }
            final LinkedHashMap<String, Object> result2 = new LinkedHashMap<String, Object>();
            result2.put(field1, value1);
            result2.put(field2, value2);
            do {
                final String fieldName = jp.getText();
                jp.nextToken();
                result2.put(fieldName, this.deserialize(jp, ctxt));
            } while (jp.nextToken() != JsonToken.END_OBJECT);
            return result2;
        }
        
        protected Object[] mapArrayToArray(final JsonParser jp, final DeserializationContext ctxt) throws IOException {
            final ObjectBuffer buffer = ctxt.leaseObjectBuffer();
            Object[] values = buffer.resetAndStart();
            int ptr = 0;
            do {
                final Object value = this.deserialize(jp, ctxt);
                if (ptr >= values.length) {
                    values = buffer.appendCompletedChunk(values);
                    ptr = 0;
                }
                values[ptr++] = value;
            } while (jp.nextToken() != JsonToken.END_ARRAY);
            return buffer.completeAndClearBuffer(values, ptr);
        }
        
        static {
            std = new Vanilla();
        }
    }
}
