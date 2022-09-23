// 
// Decompiled by Procyon v0.5.36
// 

package parquet.org.codehaus.jackson.map.ser.std;

import parquet.org.codehaus.jackson.node.ObjectNode;
import parquet.org.codehaus.jackson.schema.SchemaAware;
import parquet.org.codehaus.jackson.schema.JsonSchema;
import parquet.org.codehaus.jackson.map.type.ArrayType;
import parquet.org.codehaus.jackson.JsonNode;
import java.lang.reflect.Type;
import parquet.org.codehaus.jackson.JsonGenerationException;
import parquet.org.codehaus.jackson.map.JsonMappingException;
import java.lang.reflect.InvocationTargetException;
import java.io.IOException;
import parquet.org.codehaus.jackson.map.SerializerProvider;
import parquet.org.codehaus.jackson.JsonGenerator;
import parquet.org.codehaus.jackson.map.BeanProperty;
import parquet.org.codehaus.jackson.map.TypeSerializer;
import parquet.org.codehaus.jackson.map.ser.impl.PropertySerializerMap;
import parquet.org.codehaus.jackson.map.JsonSerializer;
import parquet.org.codehaus.jackson.type.JavaType;
import parquet.org.codehaus.jackson.map.annotate.JacksonStdImpl;
import parquet.org.codehaus.jackson.map.ResolvableSerializer;

@JacksonStdImpl
public class ObjectArraySerializer extends StdArraySerializers.ArraySerializerBase<Object[]> implements ResolvableSerializer
{
    protected final boolean _staticTyping;
    protected final JavaType _elementType;
    protected JsonSerializer<Object> _elementSerializer;
    protected PropertySerializerMap _dynamicSerializers;
    
    @Deprecated
    public ObjectArraySerializer(final JavaType elemType, final boolean staticTyping, final TypeSerializer vts, final BeanProperty property) {
        this(elemType, staticTyping, vts, property, null);
    }
    
    public ObjectArraySerializer(final JavaType elemType, final boolean staticTyping, final TypeSerializer vts, final BeanProperty property, final JsonSerializer<Object> elementSerializer) {
        super(Object[].class, vts, property);
        this._elementType = elemType;
        this._staticTyping = staticTyping;
        this._dynamicSerializers = PropertySerializerMap.emptyMap();
        this._elementSerializer = elementSerializer;
    }
    
    @Override
    public ContainerSerializerBase<?> _withValueTypeSerializer(final TypeSerializer vts) {
        return new ObjectArraySerializer(this._elementType, this._staticTyping, vts, this._property, this._elementSerializer);
    }
    
    public void serializeContents(final Object[] value, final JsonGenerator jgen, final SerializerProvider provider) throws IOException, JsonGenerationException {
        final int len = value.length;
        if (len == 0) {
            return;
        }
        if (this._elementSerializer != null) {
            this.serializeContentsUsing(value, jgen, provider, this._elementSerializer);
            return;
        }
        if (this._valueTypeSerializer != null) {
            this.serializeTypedContents(value, jgen, provider);
            return;
        }
        int i = 0;
        Object elem = null;
        try {
            final PropertySerializerMap serializers = this._dynamicSerializers;
            while (i < len) {
                elem = value[i];
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
                    }
                    serializer.serialize(elem, jgen, provider);
                }
                ++i;
            }
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
            throw JsonMappingException.wrapWithPath(t, elem, i);
        }
    }
    
    public void serializeContentsUsing(final Object[] value, final JsonGenerator jgen, final SerializerProvider provider, final JsonSerializer<Object> ser) throws IOException, JsonGenerationException {
        final int len = value.length;
        final TypeSerializer typeSer = this._valueTypeSerializer;
        int i = 0;
        Object elem = null;
        try {
            while (i < len) {
                elem = value[i];
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
            throw JsonMappingException.wrapWithPath(t, elem, i);
        }
    }
    
    public void serializeTypedContents(final Object[] value, final JsonGenerator jgen, final SerializerProvider provider) throws IOException, JsonGenerationException {
        final int len = value.length;
        final TypeSerializer typeSer = this._valueTypeSerializer;
        int i = 0;
        Object elem = null;
        try {
            final PropertySerializerMap serializers = this._dynamicSerializers;
            while (i < len) {
                elem = value[i];
                if (elem == null) {
                    provider.defaultSerializeNull(jgen);
                }
                else {
                    final Class<?> cc = elem.getClass();
                    JsonSerializer<Object> serializer = serializers.serializerFor(cc);
                    if (serializer == null) {
                        serializer = this._findAndAddDynamic(serializers, cc, provider);
                    }
                    serializer.serializeWithType(elem, jgen, provider, typeSer);
                }
                ++i;
            }
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
            throw JsonMappingException.wrapWithPath(t, elem, i);
        }
    }
    
    @Override
    public JsonNode getSchema(final SerializerProvider provider, final Type typeHint) throws JsonMappingException {
        final ObjectNode o = this.createSchemaNode("array", true);
        if (typeHint != null) {
            final JavaType javaType = provider.constructType(typeHint);
            if (javaType.isArrayType()) {
                final Class<?> componentType = ((ArrayType)javaType).getContentType().getRawClass();
                if (componentType == Object.class) {
                    o.put("items", JsonSchema.getDefaultSchemaNode());
                }
                else {
                    final JsonSerializer<Object> ser = provider.findValueSerializer(componentType, this._property);
                    final JsonNode schemaNode = (ser instanceof SchemaAware) ? ((SchemaAware)ser).getSchema(provider, null) : JsonSchema.getDefaultSchemaNode();
                    o.put("items", schemaNode);
                }
            }
        }
        return o;
    }
    
    public void resolve(final SerializerProvider provider) throws JsonMappingException {
        if (this._staticTyping && this._elementSerializer == null) {
            this._elementSerializer = provider.findValueSerializer(this._elementType, this._property);
        }
    }
    
    protected final JsonSerializer<Object> _findAndAddDynamic(final PropertySerializerMap map, final Class<?> type, final SerializerProvider provider) throws JsonMappingException {
        final PropertySerializerMap.SerializerAndMapResult result = map.findAndAddSerializer(type, provider, this._property);
        if (map != result.map) {
            this._dynamicSerializers = result.map;
        }
        return result.serializer;
    }
    
    protected final JsonSerializer<Object> _findAndAddDynamic(final PropertySerializerMap map, final JavaType type, final SerializerProvider provider) throws JsonMappingException {
        final PropertySerializerMap.SerializerAndMapResult result = map.findAndAddSerializer(type, provider, this._property);
        if (map != result.map) {
            this._dynamicSerializers = result.map;
        }
        return result.serializer;
    }
}
