// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.htrace.shaded.fasterxml.jackson.databind.ser.std;

import org.apache.htrace.shaded.fasterxml.jackson.databind.SerializationFeature;
import org.apache.htrace.shaded.fasterxml.jackson.core.JsonProcessingException;
import org.apache.htrace.shaded.fasterxml.jackson.databind.type.TypeFactory;
import org.apache.htrace.shaded.fasterxml.jackson.databind.JsonMappingException;
import org.apache.htrace.shaded.fasterxml.jackson.databind.jsonFormatVisitors.JsonArrayFormatVisitor;
import org.apache.htrace.shaded.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatTypes;
import org.apache.htrace.shaded.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatVisitorWrapper;
import org.apache.htrace.shaded.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.htrace.shaded.fasterxml.jackson.databind.JsonNode;
import java.lang.reflect.Type;
import org.apache.htrace.shaded.fasterxml.jackson.core.JsonGenerationException;
import java.io.IOException;
import org.apache.htrace.shaded.fasterxml.jackson.databind.SerializerProvider;
import org.apache.htrace.shaded.fasterxml.jackson.core.JsonGenerator;
import org.apache.htrace.shaded.fasterxml.jackson.databind.ser.ContainerSerializer;
import org.apache.htrace.shaded.fasterxml.jackson.databind.JavaType;
import org.apache.htrace.shaded.fasterxml.jackson.databind.annotation.JacksonStdImpl;
import org.apache.htrace.shaded.fasterxml.jackson.databind.BeanProperty;
import org.apache.htrace.shaded.fasterxml.jackson.databind.jsontype.TypeSerializer;
import org.apache.htrace.shaded.fasterxml.jackson.databind.JsonSerializer;
import java.util.HashMap;

public class StdArraySerializers
{
    protected static final HashMap<String, JsonSerializer<?>> _arraySerializers;
    
    protected StdArraySerializers() {
    }
    
    public static JsonSerializer<?> findStandardImpl(final Class<?> cls) {
        return StdArraySerializers._arraySerializers.get(cls.getName());
    }
    
    static {
        (_arraySerializers = new HashMap<String, JsonSerializer<?>>()).put(boolean[].class.getName(), new BooleanArraySerializer());
        StdArraySerializers._arraySerializers.put(byte[].class.getName(), new ByteArraySerializer());
        StdArraySerializers._arraySerializers.put(char[].class.getName(), new CharArraySerializer());
        StdArraySerializers._arraySerializers.put(short[].class.getName(), new ShortArraySerializer());
        StdArraySerializers._arraySerializers.put(int[].class.getName(), new IntArraySerializer());
        StdArraySerializers._arraySerializers.put(long[].class.getName(), new LongArraySerializer());
        StdArraySerializers._arraySerializers.put(float[].class.getName(), new FloatArraySerializer());
        StdArraySerializers._arraySerializers.put(double[].class.getName(), new DoubleArraySerializer());
    }
    
    protected abstract static class TypedPrimitiveArraySerializer<T> extends ArraySerializerBase<T>
    {
        protected final TypeSerializer _valueTypeSerializer;
        
        protected TypedPrimitiveArraySerializer(final Class<T> cls) {
            super(cls);
            this._valueTypeSerializer = null;
        }
        
        protected TypedPrimitiveArraySerializer(final TypedPrimitiveArraySerializer<T> src, final BeanProperty prop, final TypeSerializer vts) {
            super(src, prop);
            this._valueTypeSerializer = vts;
        }
    }
    
    @JacksonStdImpl
    public static final class BooleanArraySerializer extends ArraySerializerBase<boolean[]>
    {
        private static final JavaType VALUE_TYPE;
        
        public BooleanArraySerializer() {
            super(boolean[].class, null);
        }
        
        public ContainerSerializer<?> _withValueTypeSerializer(final TypeSerializer vts) {
            return this;
        }
        
        @Override
        public JavaType getContentType() {
            return BooleanArraySerializer.VALUE_TYPE;
        }
        
        @Override
        public JsonSerializer<?> getContentSerializer() {
            return null;
        }
        
        @Override
        public boolean isEmpty(final boolean[] value) {
            return value == null || value.length == 0;
        }
        
        @Override
        public boolean hasSingleElement(final boolean[] value) {
            return value.length == 1;
        }
        
        public void serializeContents(final boolean[] value, final JsonGenerator jgen, final SerializerProvider provider) throws IOException, JsonGenerationException {
            for (int i = 0, len = value.length; i < len; ++i) {
                jgen.writeBoolean(value[i]);
            }
        }
        
        @Override
        public JsonNode getSchema(final SerializerProvider provider, final Type typeHint) {
            final ObjectNode o = this.createSchemaNode("array", true);
            o.set("items", this.createSchemaNode("boolean"));
            return o;
        }
        
        @Override
        public void acceptJsonFormatVisitor(final JsonFormatVisitorWrapper visitor, final JavaType typeHint) throws JsonMappingException {
            if (visitor != null) {
                final JsonArrayFormatVisitor v2 = visitor.expectArrayFormat(typeHint);
                if (v2 != null) {
                    v2.itemsFormat(JsonFormatTypes.BOOLEAN);
                }
            }
        }
        
        static {
            VALUE_TYPE = TypeFactory.defaultInstance().uncheckedSimpleType(Boolean.class);
        }
    }
    
    @JacksonStdImpl
    public static final class ByteArraySerializer extends StdSerializer<byte[]>
    {
        public ByteArraySerializer() {
            super(byte[].class);
        }
        
        @Override
        public boolean isEmpty(final byte[] value) {
            return value == null || value.length == 0;
        }
        
        @Override
        public void serialize(final byte[] value, final JsonGenerator jgen, final SerializerProvider provider) throws IOException, JsonGenerationException {
            jgen.writeBinary(provider.getConfig().getBase64Variant(), value, 0, value.length);
        }
        
        @Override
        public void serializeWithType(final byte[] value, final JsonGenerator jgen, final SerializerProvider provider, final TypeSerializer typeSer) throws IOException, JsonGenerationException {
            typeSer.writeTypePrefixForScalar(value, jgen);
            jgen.writeBinary(provider.getConfig().getBase64Variant(), value, 0, value.length);
            typeSer.writeTypeSuffixForScalar(value, jgen);
        }
        
        @Override
        public JsonNode getSchema(final SerializerProvider provider, final Type typeHint) {
            final ObjectNode o = this.createSchemaNode("array", true);
            final ObjectNode itemSchema = this.createSchemaNode("string");
            return o.set("items", itemSchema);
        }
        
        @Override
        public void acceptJsonFormatVisitor(final JsonFormatVisitorWrapper visitor, final JavaType typeHint) throws JsonMappingException {
            if (visitor != null) {
                final JsonArrayFormatVisitor v2 = visitor.expectArrayFormat(typeHint);
                if (v2 != null) {
                    v2.itemsFormat(JsonFormatTypes.STRING);
                }
            }
        }
    }
    
    @JacksonStdImpl
    public static final class ShortArraySerializer extends TypedPrimitiveArraySerializer<short[]>
    {
        private static final JavaType VALUE_TYPE;
        
        public ShortArraySerializer() {
            super(short[].class);
        }
        
        public ShortArraySerializer(final ShortArraySerializer src, final BeanProperty prop, final TypeSerializer vts) {
            super(src, prop, vts);
        }
        
        public ContainerSerializer<?> _withValueTypeSerializer(final TypeSerializer vts) {
            return new ShortArraySerializer(this, this._property, vts);
        }
        
        @Override
        public JavaType getContentType() {
            return ShortArraySerializer.VALUE_TYPE;
        }
        
        @Override
        public JsonSerializer<?> getContentSerializer() {
            return null;
        }
        
        @Override
        public boolean isEmpty(final short[] value) {
            return value == null || value.length == 0;
        }
        
        @Override
        public boolean hasSingleElement(final short[] value) {
            return value.length == 1;
        }
        
        public void serializeContents(final short[] value, final JsonGenerator jgen, final SerializerProvider provider) throws IOException, JsonGenerationException {
            if (this._valueTypeSerializer != null) {
                for (int i = 0, len = value.length; i < len; ++i) {
                    this._valueTypeSerializer.writeTypePrefixForScalar(null, jgen, Short.TYPE);
                    jgen.writeNumber(value[i]);
                    this._valueTypeSerializer.writeTypeSuffixForScalar(null, jgen);
                }
                return;
            }
            for (int i = 0, len = value.length; i < len; ++i) {
                jgen.writeNumber((int)value[i]);
            }
        }
        
        @Override
        public JsonNode getSchema(final SerializerProvider provider, final Type typeHint) {
            final ObjectNode o = this.createSchemaNode("array", true);
            return o.set("items", this.createSchemaNode("integer"));
        }
        
        @Override
        public void acceptJsonFormatVisitor(final JsonFormatVisitorWrapper visitor, final JavaType typeHint) throws JsonMappingException {
            if (visitor != null) {
                final JsonArrayFormatVisitor v2 = visitor.expectArrayFormat(typeHint);
                if (v2 != null) {
                    v2.itemsFormat(JsonFormatTypes.INTEGER);
                }
            }
        }
        
        static {
            VALUE_TYPE = TypeFactory.defaultInstance().uncheckedSimpleType(Short.TYPE);
        }
    }
    
    @JacksonStdImpl
    public static final class CharArraySerializer extends StdSerializer<char[]>
    {
        public CharArraySerializer() {
            super(char[].class);
        }
        
        @Override
        public boolean isEmpty(final char[] value) {
            return value == null || value.length == 0;
        }
        
        @Override
        public void serialize(final char[] value, final JsonGenerator jgen, final SerializerProvider provider) throws IOException, JsonGenerationException {
            if (provider.isEnabled(SerializationFeature.WRITE_CHAR_ARRAYS_AS_JSON_ARRAYS)) {
                jgen.writeStartArray();
                this._writeArrayContents(jgen, value);
                jgen.writeEndArray();
            }
            else {
                jgen.writeString(value, 0, value.length);
            }
        }
        
        @Override
        public void serializeWithType(final char[] value, final JsonGenerator jgen, final SerializerProvider provider, final TypeSerializer typeSer) throws IOException, JsonGenerationException {
            if (provider.isEnabled(SerializationFeature.WRITE_CHAR_ARRAYS_AS_JSON_ARRAYS)) {
                typeSer.writeTypePrefixForArray(value, jgen);
                this._writeArrayContents(jgen, value);
                typeSer.writeTypeSuffixForArray(value, jgen);
            }
            else {
                typeSer.writeTypePrefixForScalar(value, jgen);
                jgen.writeString(value, 0, value.length);
                typeSer.writeTypeSuffixForScalar(value, jgen);
            }
        }
        
        private final void _writeArrayContents(final JsonGenerator jgen, final char[] value) throws IOException, JsonGenerationException {
            for (int i = 0, len = value.length; i < len; ++i) {
                jgen.writeString(value, i, 1);
            }
        }
        
        @Override
        public JsonNode getSchema(final SerializerProvider provider, final Type typeHint) {
            final ObjectNode o = this.createSchemaNode("array", true);
            final ObjectNode itemSchema = this.createSchemaNode("string");
            itemSchema.put("type", "string");
            return o.set("items", itemSchema);
        }
        
        @Override
        public void acceptJsonFormatVisitor(final JsonFormatVisitorWrapper visitor, final JavaType typeHint) throws JsonMappingException {
            if (visitor != null) {
                final JsonArrayFormatVisitor v2 = visitor.expectArrayFormat(typeHint);
                if (v2 != null) {
                    v2.itemsFormat(JsonFormatTypes.STRING);
                }
            }
        }
    }
    
    @JacksonStdImpl
    public static final class IntArraySerializer extends ArraySerializerBase<int[]>
    {
        private static final JavaType VALUE_TYPE;
        
        public IntArraySerializer() {
            super(int[].class, null);
        }
        
        public ContainerSerializer<?> _withValueTypeSerializer(final TypeSerializer vts) {
            return this;
        }
        
        @Override
        public JavaType getContentType() {
            return IntArraySerializer.VALUE_TYPE;
        }
        
        @Override
        public JsonSerializer<?> getContentSerializer() {
            return null;
        }
        
        @Override
        public boolean isEmpty(final int[] value) {
            return value == null || value.length == 0;
        }
        
        @Override
        public boolean hasSingleElement(final int[] value) {
            return value.length == 1;
        }
        
        public void serializeContents(final int[] value, final JsonGenerator jgen, final SerializerProvider provider) throws IOException, JsonGenerationException {
            for (int i = 0, len = value.length; i < len; ++i) {
                jgen.writeNumber(value[i]);
            }
        }
        
        @Override
        public JsonNode getSchema(final SerializerProvider provider, final Type typeHint) {
            return this.createSchemaNode("array", true).set("items", this.createSchemaNode("integer"));
        }
        
        @Override
        public void acceptJsonFormatVisitor(final JsonFormatVisitorWrapper visitor, final JavaType typeHint) throws JsonMappingException {
            if (visitor != null) {
                final JsonArrayFormatVisitor v2 = visitor.expectArrayFormat(typeHint);
                if (v2 != null) {
                    v2.itemsFormat(JsonFormatTypes.INTEGER);
                }
            }
        }
        
        static {
            VALUE_TYPE = TypeFactory.defaultInstance().uncheckedSimpleType(Integer.TYPE);
        }
    }
    
    @JacksonStdImpl
    public static final class LongArraySerializer extends TypedPrimitiveArraySerializer<long[]>
    {
        private static final JavaType VALUE_TYPE;
        
        public LongArraySerializer() {
            super(long[].class);
        }
        
        public LongArraySerializer(final LongArraySerializer src, final BeanProperty prop, final TypeSerializer vts) {
            super(src, prop, vts);
        }
        
        public ContainerSerializer<?> _withValueTypeSerializer(final TypeSerializer vts) {
            return new LongArraySerializer(this, this._property, vts);
        }
        
        @Override
        public JavaType getContentType() {
            return LongArraySerializer.VALUE_TYPE;
        }
        
        @Override
        public JsonSerializer<?> getContentSerializer() {
            return null;
        }
        
        @Override
        public boolean isEmpty(final long[] value) {
            return value == null || value.length == 0;
        }
        
        @Override
        public boolean hasSingleElement(final long[] value) {
            return value.length == 1;
        }
        
        public void serializeContents(final long[] value, final JsonGenerator jgen, final SerializerProvider provider) throws IOException, JsonGenerationException {
            if (this._valueTypeSerializer != null) {
                for (int i = 0, len = value.length; i < len; ++i) {
                    this._valueTypeSerializer.writeTypePrefixForScalar(null, jgen, Long.TYPE);
                    jgen.writeNumber(value[i]);
                    this._valueTypeSerializer.writeTypeSuffixForScalar(null, jgen);
                }
                return;
            }
            for (int i = 0, len = value.length; i < len; ++i) {
                jgen.writeNumber(value[i]);
            }
        }
        
        @Override
        public JsonNode getSchema(final SerializerProvider provider, final Type typeHint) {
            return this.createSchemaNode("array", true).set("items", this.createSchemaNode("number", true));
        }
        
        @Override
        public void acceptJsonFormatVisitor(final JsonFormatVisitorWrapper visitor, final JavaType typeHint) throws JsonMappingException {
            if (visitor != null) {
                final JsonArrayFormatVisitor v2 = visitor.expectArrayFormat(typeHint);
                if (v2 != null) {
                    v2.itemsFormat(JsonFormatTypes.NUMBER);
                }
            }
        }
        
        static {
            VALUE_TYPE = TypeFactory.defaultInstance().uncheckedSimpleType(Long.TYPE);
        }
    }
    
    @JacksonStdImpl
    public static final class FloatArraySerializer extends TypedPrimitiveArraySerializer<float[]>
    {
        private static final JavaType VALUE_TYPE;
        
        public FloatArraySerializer() {
            super(float[].class);
        }
        
        public FloatArraySerializer(final FloatArraySerializer src, final BeanProperty prop, final TypeSerializer vts) {
            super(src, prop, vts);
        }
        
        public ContainerSerializer<?> _withValueTypeSerializer(final TypeSerializer vts) {
            return new FloatArraySerializer(this, this._property, vts);
        }
        
        @Override
        public JavaType getContentType() {
            return FloatArraySerializer.VALUE_TYPE;
        }
        
        @Override
        public JsonSerializer<?> getContentSerializer() {
            return null;
        }
        
        @Override
        public boolean isEmpty(final float[] value) {
            return value == null || value.length == 0;
        }
        
        @Override
        public boolean hasSingleElement(final float[] value) {
            return value.length == 1;
        }
        
        public void serializeContents(final float[] value, final JsonGenerator jgen, final SerializerProvider provider) throws IOException, JsonGenerationException {
            if (this._valueTypeSerializer != null) {
                for (int i = 0, len = value.length; i < len; ++i) {
                    this._valueTypeSerializer.writeTypePrefixForScalar(null, jgen, Float.TYPE);
                    jgen.writeNumber(value[i]);
                    this._valueTypeSerializer.writeTypeSuffixForScalar(null, jgen);
                }
                return;
            }
            for (int i = 0, len = value.length; i < len; ++i) {
                jgen.writeNumber(value[i]);
            }
        }
        
        @Override
        public JsonNode getSchema(final SerializerProvider provider, final Type typeHint) {
            return this.createSchemaNode("array", true).set("items", this.createSchemaNode("number"));
        }
        
        @Override
        public void acceptJsonFormatVisitor(final JsonFormatVisitorWrapper visitor, final JavaType typeHint) throws JsonMappingException {
            if (visitor != null) {
                final JsonArrayFormatVisitor v2 = visitor.expectArrayFormat(typeHint);
                if (v2 != null) {
                    v2.itemsFormat(JsonFormatTypes.NUMBER);
                }
            }
        }
        
        static {
            VALUE_TYPE = TypeFactory.defaultInstance().uncheckedSimpleType(Float.TYPE);
        }
    }
    
    @JacksonStdImpl
    public static final class DoubleArraySerializer extends ArraySerializerBase<double[]>
    {
        private static final JavaType VALUE_TYPE;
        
        public DoubleArraySerializer() {
            super(double[].class, null);
        }
        
        public ContainerSerializer<?> _withValueTypeSerializer(final TypeSerializer vts) {
            return this;
        }
        
        @Override
        public JavaType getContentType() {
            return DoubleArraySerializer.VALUE_TYPE;
        }
        
        @Override
        public JsonSerializer<?> getContentSerializer() {
            return null;
        }
        
        @Override
        public boolean isEmpty(final double[] value) {
            return value == null || value.length == 0;
        }
        
        @Override
        public boolean hasSingleElement(final double[] value) {
            return value.length == 1;
        }
        
        public void serializeContents(final double[] value, final JsonGenerator jgen, final SerializerProvider provider) throws IOException, JsonGenerationException {
            for (int i = 0, len = value.length; i < len; ++i) {
                jgen.writeNumber(value[i]);
            }
        }
        
        @Override
        public JsonNode getSchema(final SerializerProvider provider, final Type typeHint) {
            return this.createSchemaNode("array", true).set("items", this.createSchemaNode("number"));
        }
        
        @Override
        public void acceptJsonFormatVisitor(final JsonFormatVisitorWrapper visitor, final JavaType typeHint) throws JsonMappingException {
            if (visitor != null) {
                final JsonArrayFormatVisitor v2 = visitor.expectArrayFormat(typeHint);
                if (v2 != null) {
                    v2.itemsFormat(JsonFormatTypes.NUMBER);
                }
            }
        }
        
        static {
            VALUE_TYPE = TypeFactory.defaultInstance().uncheckedSimpleType(Double.TYPE);
        }
    }
}
