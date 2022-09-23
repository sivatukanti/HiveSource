// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.htrace.shaded.fasterxml.jackson.databind.ser.std;

import org.apache.htrace.shaded.fasterxml.jackson.databind.jsontype.TypeSerializer;
import org.apache.htrace.shaded.fasterxml.jackson.core.JsonGenerationException;
import java.io.IOException;
import org.apache.htrace.shaded.fasterxml.jackson.core.JsonGenerator;
import org.apache.htrace.shaded.fasterxml.jackson.databind.annotation.JacksonStdImpl;
import org.apache.htrace.shaded.fasterxml.jackson.annotation.JsonFormat;
import org.apache.htrace.shaded.fasterxml.jackson.databind.introspect.Annotated;
import org.apache.htrace.shaded.fasterxml.jackson.databind.BeanProperty;
import org.apache.htrace.shaded.fasterxml.jackson.databind.JsonMappingException;
import org.apache.htrace.shaded.fasterxml.jackson.databind.jsonFormatVisitors.JsonIntegerFormatVisitor;
import org.apache.htrace.shaded.fasterxml.jackson.databind.JavaType;
import org.apache.htrace.shaded.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatVisitorWrapper;
import org.apache.htrace.shaded.fasterxml.jackson.databind.JsonNode;
import java.lang.reflect.Type;
import org.apache.htrace.shaded.fasterxml.jackson.databind.SerializerProvider;
import org.apache.htrace.shaded.fasterxml.jackson.core.JsonParser;
import org.apache.htrace.shaded.fasterxml.jackson.databind.ser.ContextualSerializer;
import org.apache.htrace.shaded.fasterxml.jackson.databind.JsonSerializer;
import java.util.Map;

public class NumberSerializers
{
    protected NumberSerializers() {
    }
    
    public static void addAll(final Map<String, JsonSerializer<?>> allDeserializers) {
        final JsonSerializer<?> intS = new IntegerSerializer();
        allDeserializers.put(Integer.class.getName(), intS);
        allDeserializers.put(Integer.TYPE.getName(), intS);
        allDeserializers.put(Long.class.getName(), LongSerializer.instance);
        allDeserializers.put(Long.TYPE.getName(), LongSerializer.instance);
        allDeserializers.put(Byte.class.getName(), IntLikeSerializer.instance);
        allDeserializers.put(Byte.TYPE.getName(), IntLikeSerializer.instance);
        allDeserializers.put(Short.class.getName(), ShortSerializer.instance);
        allDeserializers.put(Short.TYPE.getName(), ShortSerializer.instance);
        allDeserializers.put(Float.class.getName(), FloatSerializer.instance);
        allDeserializers.put(Float.TYPE.getName(), FloatSerializer.instance);
        allDeserializers.put(Double.class.getName(), DoubleSerializer.instance);
        allDeserializers.put(Double.TYPE.getName(), DoubleSerializer.instance);
    }
    
    protected abstract static class Base<T> extends StdScalarSerializer<T> implements ContextualSerializer
    {
        protected final JsonParser.NumberType _numberType;
        protected final String _schemaType;
        
        protected Base(final Class<T> cls, final JsonParser.NumberType numberType, final String schemaType) {
            super(cls);
            this._numberType = numberType;
            this._schemaType = schemaType;
        }
        
        @Override
        public JsonNode getSchema(final SerializerProvider provider, final Type typeHint) {
            return this.createSchemaNode(this._schemaType, true);
        }
        
        @Override
        public void acceptJsonFormatVisitor(final JsonFormatVisitorWrapper visitor, final JavaType typeHint) throws JsonMappingException {
            final JsonIntegerFormatVisitor v2 = visitor.expectIntegerFormat(typeHint);
            if (v2 != null) {
                v2.numberType(this._numberType);
            }
        }
        
        @Override
        public JsonSerializer<?> createContextual(final SerializerProvider prov, final BeanProperty property) throws JsonMappingException {
            if (property != null) {
                final JsonFormat.Value format = prov.getAnnotationIntrospector().findFormat(property.getMember());
                if (format != null) {
                    switch (format.getShape()) {
                        case STRING: {
                            return ToStringSerializer.instance;
                        }
                    }
                }
            }
            return this;
        }
    }
    
    @JacksonStdImpl
    public static final class ShortSerializer extends Base<Short>
    {
        static final ShortSerializer instance;
        
        public ShortSerializer() {
            super(Short.class, JsonParser.NumberType.INT, "number");
        }
        
        @Override
        public void serialize(final Short value, final JsonGenerator jgen, final SerializerProvider provider) throws IOException {
            jgen.writeNumber(value);
        }
        
        static {
            instance = new ShortSerializer();
        }
    }
    
    @JacksonStdImpl
    public static final class IntegerSerializer extends Base<Integer>
    {
        public IntegerSerializer() {
            super(Integer.class, JsonParser.NumberType.INT, "integer");
        }
        
        @Override
        public void serialize(final Integer value, final JsonGenerator jgen, final SerializerProvider provider) throws IOException, JsonGenerationException {
            jgen.writeNumber(value);
        }
        
        @Override
        public void serializeWithType(final Integer value, final JsonGenerator jgen, final SerializerProvider provider, final TypeSerializer typeSer) throws IOException {
            this.serialize(value, jgen, provider);
        }
    }
    
    @JacksonStdImpl
    public static final class IntLikeSerializer extends Base<Number>
    {
        static final IntLikeSerializer instance;
        
        public IntLikeSerializer() {
            super(Number.class, JsonParser.NumberType.INT, "integer");
        }
        
        @Override
        public void serialize(final Number value, final JsonGenerator jgen, final SerializerProvider provider) throws IOException, JsonGenerationException {
            jgen.writeNumber(value.intValue());
        }
        
        static {
            instance = new IntLikeSerializer();
        }
    }
    
    @JacksonStdImpl
    public static final class LongSerializer extends Base<Long>
    {
        static final LongSerializer instance;
        
        public LongSerializer() {
            super(Long.class, JsonParser.NumberType.LONG, "number");
        }
        
        @Override
        public void serialize(final Long value, final JsonGenerator jgen, final SerializerProvider provider) throws IOException, JsonGenerationException {
            jgen.writeNumber(value);
        }
        
        static {
            instance = new LongSerializer();
        }
    }
    
    @JacksonStdImpl
    public static final class FloatSerializer extends Base<Float>
    {
        static final FloatSerializer instance;
        
        public FloatSerializer() {
            super(Float.class, JsonParser.NumberType.FLOAT, "number");
        }
        
        @Override
        public void serialize(final Float value, final JsonGenerator jgen, final SerializerProvider provider) throws IOException, JsonGenerationException {
            jgen.writeNumber(value);
        }
        
        static {
            instance = new FloatSerializer();
        }
    }
    
    @JacksonStdImpl
    public static final class DoubleSerializer extends Base<Double>
    {
        static final DoubleSerializer instance;
        
        public DoubleSerializer() {
            super(Double.class, JsonParser.NumberType.DOUBLE, "number");
        }
        
        @Override
        public void serialize(final Double value, final JsonGenerator jgen, final SerializerProvider provider) throws IOException, JsonGenerationException {
            jgen.writeNumber(value);
        }
        
        @Override
        public void serializeWithType(final Double value, final JsonGenerator jgen, final SerializerProvider provider, final TypeSerializer typeSer) throws IOException {
            this.serialize(value, jgen, provider);
        }
        
        static {
            instance = new DoubleSerializer();
        }
    }
}
