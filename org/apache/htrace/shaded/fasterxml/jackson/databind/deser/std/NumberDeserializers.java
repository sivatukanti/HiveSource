// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.htrace.shaded.fasterxml.jackson.databind.deser.std;

import org.apache.htrace.shaded.fasterxml.jackson.databind.DeserializationFeature;
import org.apache.htrace.shaded.fasterxml.jackson.core.JsonToken;
import org.apache.htrace.shaded.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import org.apache.htrace.shaded.fasterxml.jackson.core.JsonProcessingException;
import java.io.IOException;
import org.apache.htrace.shaded.fasterxml.jackson.databind.DeserializationContext;
import org.apache.htrace.shaded.fasterxml.jackson.core.JsonParser;
import org.apache.htrace.shaded.fasterxml.jackson.databind.annotation.JacksonStdImpl;
import java.math.BigInteger;
import java.math.BigDecimal;
import org.apache.htrace.shaded.fasterxml.jackson.databind.JsonDeserializer;
import java.util.HashSet;

public class NumberDeserializers
{
    private static final HashSet<String> _classNames;
    
    public static JsonDeserializer<?> find(final Class<?> rawType, final String clsName) {
        if (rawType.isPrimitive()) {
            if (rawType == Integer.TYPE) {
                return IntegerDeserializer.primitiveInstance;
            }
            if (rawType == Boolean.TYPE) {
                return BooleanDeserializer.primitiveInstance;
            }
            if (rawType == Long.TYPE) {
                return LongDeserializer.primitiveInstance;
            }
            if (rawType == Double.TYPE) {
                return DoubleDeserializer.primitiveInstance;
            }
            if (rawType == Character.TYPE) {
                return CharacterDeserializer.primitiveInstance;
            }
            if (rawType == Byte.TYPE) {
                return ByteDeserializer.primitiveInstance;
            }
            if (rawType == Short.TYPE) {
                return ShortDeserializer.primitiveInstance;
            }
            if (rawType == Float.TYPE) {
                return FloatDeserializer.primitiveInstance;
            }
        }
        else {
            if (!NumberDeserializers._classNames.contains(clsName)) {
                return null;
            }
            if (rawType == Integer.class) {
                return IntegerDeserializer.wrapperInstance;
            }
            if (rawType == Boolean.class) {
                return BooleanDeserializer.wrapperInstance;
            }
            if (rawType == Long.class) {
                return LongDeserializer.wrapperInstance;
            }
            if (rawType == Double.class) {
                return DoubleDeserializer.wrapperInstance;
            }
            if (rawType == Character.class) {
                return CharacterDeserializer.wrapperInstance;
            }
            if (rawType == Byte.class) {
                return ByteDeserializer.wrapperInstance;
            }
            if (rawType == Short.class) {
                return ShortDeserializer.wrapperInstance;
            }
            if (rawType == Float.class) {
                return FloatDeserializer.wrapperInstance;
            }
            if (rawType == Number.class) {
                return NumberDeserializer.instance;
            }
            if (rawType == BigDecimal.class) {
                return BigDecimalDeserializer.instance;
            }
            if (rawType == BigInteger.class) {
                return BigIntegerDeserializer.instance;
            }
        }
        throw new IllegalArgumentException("Internal error: can't find deserializer for " + rawType.getName());
    }
    
    static {
        _classNames = new HashSet<String>();
        final Class[] arr$;
        final Class<?>[] numberTypes = (Class<?>[])(arr$ = new Class[] { Boolean.class, Byte.class, Short.class, Character.class, Integer.class, Long.class, Float.class, Double.class, Number.class, BigDecimal.class, BigInteger.class });
        for (final Class<?> cls : arr$) {
            NumberDeserializers._classNames.add(cls.getName());
        }
    }
    
    protected abstract static class PrimitiveOrWrapperDeserializer<T> extends StdScalarDeserializer<T>
    {
        private static final long serialVersionUID = 1L;
        protected final T _nullValue;
        
        protected PrimitiveOrWrapperDeserializer(final Class<T> vc, final T nvl) {
            super(vc);
            this._nullValue = nvl;
        }
        
        @Override
        public final T getNullValue() {
            return this._nullValue;
        }
    }
    
    @JacksonStdImpl
    public static final class BooleanDeserializer extends PrimitiveOrWrapperDeserializer<Boolean>
    {
        private static final long serialVersionUID = 1L;
        private static final BooleanDeserializer primitiveInstance;
        private static final BooleanDeserializer wrapperInstance;
        
        public BooleanDeserializer(final Class<Boolean> cls, final Boolean nvl) {
            super(cls, nvl);
        }
        
        @Override
        public Boolean deserialize(final JsonParser jp, final DeserializationContext ctxt) throws IOException, JsonProcessingException {
            return this._parseBoolean(jp, ctxt);
        }
        
        @Override
        public Boolean deserializeWithType(final JsonParser jp, final DeserializationContext ctxt, final TypeDeserializer typeDeserializer) throws IOException, JsonProcessingException {
            return this._parseBoolean(jp, ctxt);
        }
        
        static {
            primitiveInstance = new BooleanDeserializer(Boolean.class, Boolean.FALSE);
            wrapperInstance = new BooleanDeserializer(Boolean.TYPE, null);
        }
    }
    
    @JacksonStdImpl
    public static class ByteDeserializer extends PrimitiveOrWrapperDeserializer<Byte>
    {
        private static final long serialVersionUID = 1L;
        private static final ByteDeserializer primitiveInstance;
        private static final ByteDeserializer wrapperInstance;
        
        public ByteDeserializer(final Class<Byte> cls, final Byte nvl) {
            super(cls, nvl);
        }
        
        @Override
        public Byte deserialize(final JsonParser jp, final DeserializationContext ctxt) throws IOException, JsonProcessingException {
            return this._parseByte(jp, ctxt);
        }
        
        static {
            primitiveInstance = new ByteDeserializer(Byte.TYPE, (Byte)0);
            wrapperInstance = new ByteDeserializer(Byte.class, null);
        }
    }
    
    @JacksonStdImpl
    public static class ShortDeserializer extends PrimitiveOrWrapperDeserializer<Short>
    {
        private static final long serialVersionUID = 1L;
        private static final ShortDeserializer primitiveInstance;
        private static final ShortDeserializer wrapperInstance;
        
        public ShortDeserializer(final Class<Short> cls, final Short nvl) {
            super(cls, nvl);
        }
        
        @Override
        public Short deserialize(final JsonParser jp, final DeserializationContext ctxt) throws IOException, JsonProcessingException {
            return this._parseShort(jp, ctxt);
        }
        
        static {
            primitiveInstance = new ShortDeserializer(Short.class, (Short)0);
            wrapperInstance = new ShortDeserializer(Short.TYPE, null);
        }
    }
    
    @JacksonStdImpl
    public static class CharacterDeserializer extends PrimitiveOrWrapperDeserializer<Character>
    {
        private static final long serialVersionUID = 1L;
        private static final CharacterDeserializer primitiveInstance;
        private static final CharacterDeserializer wrapperInstance;
        
        public CharacterDeserializer(final Class<Character> cls, final Character nvl) {
            super(cls, nvl);
        }
        
        @Override
        public Character deserialize(final JsonParser jp, final DeserializationContext ctxt) throws IOException, JsonProcessingException {
            final JsonToken t = jp.getCurrentToken();
            if (t == JsonToken.VALUE_NUMBER_INT) {
                final int value = jp.getIntValue();
                if (value >= 0 && value <= 65535) {
                    return (char)value;
                }
            }
            else if (t == JsonToken.VALUE_STRING) {
                final String text = jp.getText();
                if (text.length() == 1) {
                    return text.charAt(0);
                }
                if (text.length() == 0) {
                    return this.getEmptyValue();
                }
            }
            else if (t == JsonToken.START_ARRAY && ctxt.isEnabled(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS)) {
                jp.nextToken();
                final Character value2 = this.deserialize(jp, ctxt);
                if (jp.nextToken() != JsonToken.END_ARRAY) {
                    throw ctxt.wrongTokenException(jp, JsonToken.END_ARRAY, "Attempted to unwrap single value array for single '" + this._valueClass.getName() + "' value but there was more than a single value in the array");
                }
                return value2;
            }
            throw ctxt.mappingException(this._valueClass, t);
        }
        
        static {
            primitiveInstance = new CharacterDeserializer(Character.class, '\0');
            wrapperInstance = new CharacterDeserializer(Character.TYPE, null);
        }
    }
    
    @JacksonStdImpl
    public static final class IntegerDeserializer extends PrimitiveOrWrapperDeserializer<Integer>
    {
        private static final long serialVersionUID = 1L;
        private static final IntegerDeserializer primitiveInstance;
        private static final IntegerDeserializer wrapperInstance;
        
        public IntegerDeserializer(final Class<Integer> cls, final Integer nvl) {
            super(cls, nvl);
        }
        
        @Override
        public Integer deserialize(final JsonParser jp, final DeserializationContext ctxt) throws IOException, JsonProcessingException {
            return this._parseInteger(jp, ctxt);
        }
        
        @Override
        public Integer deserializeWithType(final JsonParser jp, final DeserializationContext ctxt, final TypeDeserializer typeDeserializer) throws IOException, JsonProcessingException {
            return this._parseInteger(jp, ctxt);
        }
        
        static {
            primitiveInstance = new IntegerDeserializer(Integer.class, 0);
            wrapperInstance = new IntegerDeserializer(Integer.TYPE, null);
        }
    }
    
    @JacksonStdImpl
    public static final class LongDeserializer extends PrimitiveOrWrapperDeserializer<Long>
    {
        private static final long serialVersionUID = 1L;
        private static final LongDeserializer primitiveInstance;
        private static final LongDeserializer wrapperInstance;
        
        public LongDeserializer(final Class<Long> cls, final Long nvl) {
            super(cls, nvl);
        }
        
        @Override
        public Long deserialize(final JsonParser jp, final DeserializationContext ctxt) throws IOException, JsonProcessingException {
            return this._parseLong(jp, ctxt);
        }
        
        static {
            primitiveInstance = new LongDeserializer(Long.class, 0L);
            wrapperInstance = new LongDeserializer(Long.TYPE, null);
        }
    }
    
    @JacksonStdImpl
    public static class FloatDeserializer extends PrimitiveOrWrapperDeserializer<Float>
    {
        private static final long serialVersionUID = 1L;
        private static final FloatDeserializer primitiveInstance;
        private static final FloatDeserializer wrapperInstance;
        
        public FloatDeserializer(final Class<Float> cls, final Float nvl) {
            super(cls, nvl);
        }
        
        @Override
        public Float deserialize(final JsonParser jp, final DeserializationContext ctxt) throws IOException, JsonProcessingException {
            return this._parseFloat(jp, ctxt);
        }
        
        static {
            primitiveInstance = new FloatDeserializer(Float.class, 0.0f);
            wrapperInstance = new FloatDeserializer(Float.TYPE, null);
        }
    }
    
    @JacksonStdImpl
    public static class DoubleDeserializer extends PrimitiveOrWrapperDeserializer<Double>
    {
        private static final long serialVersionUID = 1L;
        private static final DoubleDeserializer primitiveInstance;
        private static final DoubleDeserializer wrapperInstance;
        
        public DoubleDeserializer(final Class<Double> cls, final Double nvl) {
            super(cls, nvl);
        }
        
        @Override
        public Double deserialize(final JsonParser jp, final DeserializationContext ctxt) throws IOException, JsonProcessingException {
            return this._parseDouble(jp, ctxt);
        }
        
        @Override
        public Double deserializeWithType(final JsonParser jp, final DeserializationContext ctxt, final TypeDeserializer typeDeserializer) throws IOException, JsonProcessingException {
            return this._parseDouble(jp, ctxt);
        }
        
        static {
            primitiveInstance = new DoubleDeserializer(Double.class, 0.0);
            wrapperInstance = new DoubleDeserializer(Double.TYPE, null);
        }
    }
    
    @JacksonStdImpl
    public static class NumberDeserializer extends StdScalarDeserializer<Number>
    {
        public static final NumberDeserializer instance;
        
        public NumberDeserializer() {
            super(Number.class);
        }
        
        @Override
        public Number deserialize(final JsonParser jp, final DeserializationContext ctxt) throws IOException, JsonProcessingException {
            final JsonToken t = jp.getCurrentToken();
            if (t == JsonToken.VALUE_NUMBER_INT) {
                if (ctxt.isEnabled(DeserializationFeature.USE_BIG_INTEGER_FOR_INTS)) {
                    return jp.getBigIntegerValue();
                }
                return jp.getNumberValue();
            }
            else if (t == JsonToken.VALUE_NUMBER_FLOAT) {
                if (ctxt.isEnabled(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS)) {
                    return jp.getDecimalValue();
                }
                return jp.getDoubleValue();
            }
            else {
                if (t == JsonToken.VALUE_STRING) {
                    final String text = jp.getText().trim();
                    if (text.length() == 0) {
                        return this.getEmptyValue();
                    }
                    if (this._hasTextualNull(text)) {
                        return this.getNullValue();
                    }
                    if (this._isPosInf(text)) {
                        return Double.POSITIVE_INFINITY;
                    }
                    if (this._isNegInf(text)) {
                        return Double.NEGATIVE_INFINITY;
                    }
                    if (this._isNaN(text)) {
                        return Double.NaN;
                    }
                    try {
                        if (text.indexOf(46) >= 0) {
                            if (ctxt.isEnabled(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS)) {
                                return new BigDecimal(text);
                            }
                            return new Double(text);
                        }
                        else {
                            if (ctxt.isEnabled(DeserializationFeature.USE_BIG_INTEGER_FOR_INTS)) {
                                return new BigInteger(text);
                            }
                            final long value = Long.parseLong(text);
                            if (value <= 2147483647L && value >= -2147483648L) {
                                return (int)value;
                            }
                            return value;
                        }
                    }
                    catch (IllegalArgumentException iae) {
                        throw ctxt.weirdStringException(text, this._valueClass, "not a valid number");
                    }
                }
                if (t != JsonToken.START_ARRAY || !ctxt.isEnabled(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS)) {
                    throw ctxt.mappingException(this._valueClass, t);
                }
                jp.nextToken();
                final Number value2 = this.deserialize(jp, ctxt);
                if (jp.nextToken() != JsonToken.END_ARRAY) {
                    throw ctxt.wrongTokenException(jp, JsonToken.END_ARRAY, "Attempted to unwrap single value array for single '" + this._valueClass.getName() + "' value but there was more than a single value in the array");
                }
                return value2;
            }
        }
        
        @Override
        public Object deserializeWithType(final JsonParser jp, final DeserializationContext ctxt, final TypeDeserializer typeDeserializer) throws IOException, JsonProcessingException {
            switch (jp.getCurrentToken()) {
                case VALUE_NUMBER_INT:
                case VALUE_NUMBER_FLOAT:
                case VALUE_STRING: {
                    return this.deserialize(jp, ctxt);
                }
                default: {
                    return typeDeserializer.deserializeTypedFromScalar(jp, ctxt);
                }
            }
        }
        
        static {
            instance = new NumberDeserializer();
        }
    }
    
    @JacksonStdImpl
    public static class BigIntegerDeserializer extends StdScalarDeserializer<BigInteger>
    {
        public static final BigIntegerDeserializer instance;
        
        public BigIntegerDeserializer() {
            super(BigInteger.class);
        }
        
        @Override
        public BigInteger deserialize(final JsonParser jp, final DeserializationContext ctxt) throws IOException, JsonProcessingException {
            final JsonToken t = jp.getCurrentToken();
            if (t == JsonToken.VALUE_NUMBER_INT) {
                switch (jp.getNumberType()) {
                    case INT:
                    case LONG: {
                        return BigInteger.valueOf(jp.getLongValue());
                    }
                }
            }
            else {
                if (t == JsonToken.VALUE_NUMBER_FLOAT) {
                    return jp.getDecimalValue().toBigInteger();
                }
                if (t == JsonToken.START_ARRAY && ctxt.isEnabled(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS)) {
                    jp.nextToken();
                    final BigInteger value = this.deserialize(jp, ctxt);
                    if (jp.nextToken() != JsonToken.END_ARRAY) {
                        throw ctxt.wrongTokenException(jp, JsonToken.END_ARRAY, "Attempted to unwrap single value array for single 'BigInteger' value but there was more than a single value in the array");
                    }
                    return value;
                }
                else if (t != JsonToken.VALUE_STRING) {
                    throw ctxt.mappingException(this._valueClass, t);
                }
            }
            final String text = jp.getText().trim();
            if (text.length() == 0) {
                return null;
            }
            try {
                return new BigInteger(text);
            }
            catch (IllegalArgumentException iae) {
                throw ctxt.weirdStringException(text, this._valueClass, "not a valid representation");
            }
        }
        
        static {
            instance = new BigIntegerDeserializer();
        }
    }
    
    @JacksonStdImpl
    public static class BigDecimalDeserializer extends StdScalarDeserializer<BigDecimal>
    {
        public static final BigDecimalDeserializer instance;
        
        public BigDecimalDeserializer() {
            super(BigDecimal.class);
        }
        
        @Override
        public BigDecimal deserialize(final JsonParser jp, final DeserializationContext ctxt) throws IOException, JsonProcessingException {
            final JsonToken t = jp.getCurrentToken();
            if (t == JsonToken.VALUE_NUMBER_INT || t == JsonToken.VALUE_NUMBER_FLOAT) {
                return jp.getDecimalValue();
            }
            if (t == JsonToken.VALUE_STRING) {
                final String text = jp.getText().trim();
                if (text.length() == 0) {
                    return null;
                }
                try {
                    return new BigDecimal(text);
                }
                catch (IllegalArgumentException iae) {
                    throw ctxt.weirdStringException(text, this._valueClass, "not a valid representation");
                }
            }
            if (t != JsonToken.START_ARRAY || !ctxt.isEnabled(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS)) {
                throw ctxt.mappingException(this._valueClass, t);
            }
            jp.nextToken();
            final BigDecimal value = this.deserialize(jp, ctxt);
            if (jp.nextToken() != JsonToken.END_ARRAY) {
                throw ctxt.wrongTokenException(jp, JsonToken.END_ARRAY, "Attempted to unwrap single value array for single 'BigDecimal' value but there was more than a single value in the array");
            }
            return value;
        }
        
        static {
            instance = new BigDecimalDeserializer();
        }
    }
}
