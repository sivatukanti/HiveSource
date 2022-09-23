// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.htrace.shaded.fasterxml.jackson.databind.ser.std;

import org.apache.htrace.shaded.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatVisitorWrapper;
import org.apache.htrace.shaded.fasterxml.jackson.databind.jsonschema.JsonSchema;
import org.apache.htrace.shaded.fasterxml.jackson.databind.JsonNode;
import java.lang.reflect.Type;
import org.apache.htrace.shaded.fasterxml.jackson.core.JsonProcessingException;
import org.apache.htrace.shaded.fasterxml.jackson.databind.jsontype.TypeSerializer;
import org.apache.htrace.shaded.fasterxml.jackson.core.JsonGenerationException;
import java.lang.reflect.InvocationTargetException;
import java.io.IOException;
import org.apache.htrace.shaded.fasterxml.jackson.core.JsonGenerator;
import org.apache.htrace.shaded.fasterxml.jackson.databind.JsonMappingException;
import org.apache.htrace.shaded.fasterxml.jackson.databind.JavaType;
import java.lang.reflect.Modifier;
import org.apache.htrace.shaded.fasterxml.jackson.databind.MapperFeature;
import org.apache.htrace.shaded.fasterxml.jackson.databind.SerializerProvider;
import org.apache.htrace.shaded.fasterxml.jackson.databind.BeanProperty;
import org.apache.htrace.shaded.fasterxml.jackson.databind.JsonSerializer;
import java.lang.reflect.Method;
import org.apache.htrace.shaded.fasterxml.jackson.databind.annotation.JacksonStdImpl;
import org.apache.htrace.shaded.fasterxml.jackson.databind.jsonschema.SchemaAware;
import org.apache.htrace.shaded.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatVisitable;
import org.apache.htrace.shaded.fasterxml.jackson.databind.ser.ContextualSerializer;

@JacksonStdImpl
public class JsonValueSerializer extends StdSerializer<Object> implements ContextualSerializer, JsonFormatVisitable, SchemaAware
{
    protected final Method _accessorMethod;
    protected final JsonSerializer<Object> _valueSerializer;
    protected final BeanProperty _property;
    protected final boolean _forceTypeInformation;
    
    public JsonValueSerializer(final Method valueMethod, final JsonSerializer<Object> ser) {
        super(Object.class);
        this._accessorMethod = valueMethod;
        this._valueSerializer = ser;
        this._property = null;
        this._forceTypeInformation = true;
    }
    
    public JsonValueSerializer(final JsonValueSerializer src, final BeanProperty property, final JsonSerializer<?> ser, final boolean forceTypeInfo) {
        super(_notNullClass(src.handledType()));
        this._accessorMethod = src._accessorMethod;
        this._valueSerializer = (JsonSerializer<Object>)ser;
        this._property = property;
        this._forceTypeInformation = forceTypeInfo;
    }
    
    private static final Class<Object> _notNullClass(final Class<?> cls) {
        return (Class<Object>)((cls == null) ? Object.class : cls);
    }
    
    public JsonValueSerializer withResolved(final BeanProperty property, final JsonSerializer<?> ser, final boolean forceTypeInfo) {
        if (this._property == property && this._valueSerializer == ser && forceTypeInfo == this._forceTypeInformation) {
            return this;
        }
        return new JsonValueSerializer(this, property, ser, forceTypeInfo);
    }
    
    @Override
    public JsonSerializer<?> createContextual(final SerializerProvider provider, final BeanProperty property) throws JsonMappingException {
        JsonSerializer<?> ser = this._valueSerializer;
        if (ser != null) {
            ser = provider.handlePrimaryContextualization(ser, property);
            return this.withResolved(property, ser, this._forceTypeInformation);
        }
        if (provider.isEnabled(MapperFeature.USE_STATIC_TYPING) || Modifier.isFinal(this._accessorMethod.getReturnType().getModifiers())) {
            final JavaType t = provider.constructType(this._accessorMethod.getGenericReturnType());
            ser = provider.findPrimaryPropertySerializer(t, this._property);
            final boolean forceTypeInformation = this.isNaturalTypeWithStdHandling(t.getRawClass(), ser);
            return this.withResolved(property, ser, forceTypeInformation);
        }
        return this;
    }
    
    @Override
    public void serialize(final Object bean, final JsonGenerator jgen, final SerializerProvider prov) throws IOException, JsonGenerationException {
        try {
            final Object value = this._accessorMethod.invoke(bean, new Object[0]);
            if (value == null) {
                prov.defaultSerializeNull(jgen);
                return;
            }
            JsonSerializer<Object> ser = this._valueSerializer;
            if (ser == null) {
                final Class<?> c = value.getClass();
                ser = prov.findTypedValueSerializer(c, true, this._property);
            }
            ser.serialize(value, jgen, prov);
        }
        catch (IOException ioe) {
            throw ioe;
        }
        catch (Exception e) {
            Throwable t;
            for (t = e; t instanceof InvocationTargetException && t.getCause() != null; t = t.getCause()) {}
            if (t instanceof Error) {
                throw (Error)t;
            }
            throw JsonMappingException.wrapWithPath(t, bean, this._accessorMethod.getName() + "()");
        }
    }
    
    @Override
    public void serializeWithType(final Object bean, final JsonGenerator jgen, final SerializerProvider provider, final TypeSerializer typeSer0) throws IOException, JsonProcessingException {
        Object value = null;
        try {
            value = this._accessorMethod.invoke(bean, new Object[0]);
            if (value == null) {
                provider.defaultSerializeNull(jgen);
                return;
            }
            JsonSerializer<Object> ser = this._valueSerializer;
            if (ser == null) {
                ser = provider.findValueSerializer(value.getClass(), this._property);
            }
            else if (this._forceTypeInformation) {
                typeSer0.writeTypePrefixForScalar(bean, jgen);
                ser.serialize(value, jgen, provider);
                typeSer0.writeTypeSuffixForScalar(bean, jgen);
                return;
            }
            ser.serializeWithType(value, jgen, provider, typeSer0);
        }
        catch (IOException ioe) {
            throw ioe;
        }
        catch (Exception e) {
            Throwable t;
            for (t = e; t instanceof InvocationTargetException && t.getCause() != null; t = t.getCause()) {}
            if (t instanceof Error) {
                throw (Error)t;
            }
            throw JsonMappingException.wrapWithPath(t, bean, this._accessorMethod.getName() + "()");
        }
    }
    
    @Override
    public JsonNode getSchema(final SerializerProvider provider, final Type typeHint) throws JsonMappingException {
        if (this._valueSerializer instanceof SchemaAware) {
            return ((SchemaAware)this._valueSerializer).getSchema(provider, null);
        }
        return JsonSchema.getDefaultSchemaNode();
    }
    
    @Override
    public void acceptJsonFormatVisitor(final JsonFormatVisitorWrapper visitor, JavaType typeHint) throws JsonMappingException {
        JsonSerializer<Object> ser = this._valueSerializer;
        if (ser == null) {
            if (typeHint == null) {
                if (this._property != null) {
                    typeHint = this._property.getType();
                }
                if (typeHint == null) {
                    typeHint = visitor.getProvider().constructType(this._accessorMethod.getReturnType());
                }
            }
            ser = visitor.getProvider().findTypedValueSerializer(typeHint, false, this._property);
            if (ser == null) {
                visitor.expectAnyFormat(typeHint);
                return;
            }
        }
        ser.acceptJsonFormatVisitor(visitor, null);
    }
    
    protected boolean isNaturalTypeWithStdHandling(final Class<?> rawType, final JsonSerializer<?> ser) {
        if (rawType.isPrimitive()) {
            if (rawType != Integer.TYPE && rawType != Boolean.TYPE && rawType != Double.TYPE) {
                return false;
            }
        }
        else if (rawType != String.class && rawType != Integer.class && rawType != Boolean.class && rawType != Double.class) {
            return false;
        }
        return this.isDefaultSerializer(ser);
    }
    
    @Override
    public String toString() {
        return "(@JsonValue serializer for method " + this._accessorMethod.getDeclaringClass() + "#" + this._accessorMethod.getName() + ")";
    }
}
