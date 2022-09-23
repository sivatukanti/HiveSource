// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.htrace.shaded.fasterxml.jackson.databind.deser.std;

import org.apache.htrace.shaded.fasterxml.jackson.databind.AnnotationIntrospector;
import org.apache.htrace.shaded.fasterxml.jackson.databind.util.Converter;
import org.apache.htrace.shaded.fasterxml.jackson.databind.introspect.Annotated;
import org.apache.htrace.shaded.fasterxml.jackson.databind.JsonMappingException;
import org.apache.htrace.shaded.fasterxml.jackson.databind.BeanProperty;
import java.util.Date;
import org.apache.htrace.shaded.fasterxml.jackson.core.io.NumberInput;
import org.apache.htrace.shaded.fasterxml.jackson.databind.DeserializationFeature;
import org.apache.htrace.shaded.fasterxml.jackson.core.JsonToken;
import java.io.IOException;
import org.apache.htrace.shaded.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import org.apache.htrace.shaded.fasterxml.jackson.databind.DeserializationContext;
import org.apache.htrace.shaded.fasterxml.jackson.core.JsonParser;
import org.apache.htrace.shaded.fasterxml.jackson.databind.KeyDeserializer;
import org.apache.htrace.shaded.fasterxml.jackson.databind.util.ClassUtil;
import org.apache.htrace.shaded.fasterxml.jackson.databind.JavaType;
import java.io.Serializable;
import org.apache.htrace.shaded.fasterxml.jackson.databind.JsonDeserializer;

public abstract class StdDeserializer<T> extends JsonDeserializer<T> implements Serializable
{
    private static final long serialVersionUID = 1L;
    protected final Class<?> _valueClass;
    
    protected StdDeserializer(final Class<?> vc) {
        this._valueClass = vc;
    }
    
    protected StdDeserializer(final JavaType valueType) {
        this._valueClass = ((valueType == null) ? null : valueType.getRawClass());
    }
    
    @Override
    public Class<?> handledType() {
        return this._valueClass;
    }
    
    @Deprecated
    public final Class<?> getValueClass() {
        return this._valueClass;
    }
    
    public JavaType getValueType() {
        return null;
    }
    
    protected boolean isDefaultDeserializer(final JsonDeserializer<?> deserializer) {
        return ClassUtil.isJacksonStdImpl(deserializer);
    }
    
    protected boolean isDefaultKeyDeserializer(final KeyDeserializer keyDeser) {
        return ClassUtil.isJacksonStdImpl(keyDeser);
    }
    
    @Override
    public Object deserializeWithType(final JsonParser jp, final DeserializationContext ctxt, final TypeDeserializer typeDeserializer) throws IOException {
        return typeDeserializer.deserializeTypedFromAny(jp, ctxt);
    }
    
    protected final boolean _parseBooleanPrimitive(final JsonParser jp, final DeserializationContext ctxt) throws IOException {
        JsonToken t = jp.getCurrentToken();
        if (t == JsonToken.VALUE_TRUE) {
            return true;
        }
        if (t == JsonToken.VALUE_FALSE) {
            return false;
        }
        if (t == JsonToken.VALUE_NULL) {
            return false;
        }
        if (t == JsonToken.VALUE_NUMBER_INT) {
            if (jp.getNumberType() == JsonParser.NumberType.INT) {
                return jp.getIntValue() != 0;
            }
            return this._parseBooleanFromNumber(jp, ctxt);
        }
        else if (t == JsonToken.VALUE_STRING) {
            final String text = jp.getText().trim();
            if ("true".equals(text) || "True".equals(text)) {
                return true;
            }
            if ("false".equals(text) || "False".equals(text) || text.length() == 0) {
                return false;
            }
            if (this._hasTextualNull(text)) {
                return false;
            }
            throw ctxt.weirdStringException(text, this._valueClass, "only \"true\" or \"false\" recognized");
        }
        else {
            if (t != JsonToken.START_ARRAY || !ctxt.isEnabled(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS)) {
                throw ctxt.mappingException(this._valueClass, t);
            }
            jp.nextToken();
            final boolean parsed = this._parseBooleanPrimitive(jp, ctxt);
            t = jp.nextToken();
            if (t != JsonToken.END_ARRAY) {
                throw ctxt.wrongTokenException(jp, JsonToken.END_ARRAY, "Attempted to unwrap single value array for single 'boolean' value but there was more than a single value in the array");
            }
            return parsed;
        }
    }
    
    protected final Boolean _parseBoolean(final JsonParser jp, final DeserializationContext ctxt) throws IOException {
        JsonToken t = jp.getCurrentToken();
        if (t == JsonToken.VALUE_TRUE) {
            return Boolean.TRUE;
        }
        if (t == JsonToken.VALUE_FALSE) {
            return Boolean.FALSE;
        }
        if (t == JsonToken.VALUE_NUMBER_INT) {
            if (jp.getNumberType() == JsonParser.NumberType.INT) {
                return (jp.getIntValue() == 0) ? Boolean.FALSE : Boolean.TRUE;
            }
            return this._parseBooleanFromNumber(jp, ctxt);
        }
        else {
            if (t == JsonToken.VALUE_NULL) {
                return this.getNullValue();
            }
            if (t == JsonToken.VALUE_STRING) {
                final String text = jp.getText().trim();
                if ("true".equals(text) || "True".equals(text)) {
                    return Boolean.TRUE;
                }
                if ("false".equals(text) || "False".equals(text)) {
                    return Boolean.FALSE;
                }
                if (text.length() == 0) {
                    return this.getEmptyValue();
                }
                if (this._hasTextualNull(text)) {
                    return this.getNullValue();
                }
                throw ctxt.weirdStringException(text, this._valueClass, "only \"true\" or \"false\" recognized");
            }
            else {
                if (t != JsonToken.START_ARRAY || !ctxt.isEnabled(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS)) {
                    throw ctxt.mappingException(this._valueClass, t);
                }
                jp.nextToken();
                final Boolean parsed = this._parseBoolean(jp, ctxt);
                t = jp.nextToken();
                if (t != JsonToken.END_ARRAY) {
                    throw ctxt.wrongTokenException(jp, JsonToken.END_ARRAY, "Attempted to unwrap single value array for single 'Boolean' value but there was more than a single value in the array");
                }
                return parsed;
            }
        }
    }
    
    protected final boolean _parseBooleanFromNumber(final JsonParser jp, final DeserializationContext ctxt) throws IOException {
        if (jp.getNumberType() == JsonParser.NumberType.LONG) {
            return (jp.getLongValue() == 0L) ? Boolean.FALSE : Boolean.TRUE;
        }
        final String str = jp.getText();
        if ("0.0".equals(str) || "0".equals(str)) {
            return Boolean.FALSE;
        }
        return Boolean.TRUE;
    }
    
    protected Byte _parseByte(final JsonParser jp, final DeserializationContext ctxt) throws IOException {
        JsonToken t = jp.getCurrentToken();
        if (t == JsonToken.VALUE_NUMBER_INT || t == JsonToken.VALUE_NUMBER_FLOAT) {
            return jp.getByteValue();
        }
        if (t == JsonToken.VALUE_STRING) {
            final String text = jp.getText().trim();
            if (this._hasTextualNull(text)) {
                return this.getNullValue();
            }
            int value;
            try {
                final int len = text.length();
                if (len == 0) {
                    return this.getEmptyValue();
                }
                value = NumberInput.parseInt(text);
            }
            catch (IllegalArgumentException iae) {
                throw ctxt.weirdStringException(text, this._valueClass, "not a valid Byte value");
            }
            if (value < -128 || value > 255) {
                throw ctxt.weirdStringException(text, this._valueClass, "overflow, value can not be represented as 8-bit value");
            }
            return (byte)value;
        }
        else {
            if (t == JsonToken.VALUE_NULL) {
                return this.getNullValue();
            }
            if (t != JsonToken.START_ARRAY || !ctxt.isEnabled(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS)) {
                throw ctxt.mappingException(this._valueClass, t);
            }
            jp.nextToken();
            final Byte parsed = this._parseByte(jp, ctxt);
            t = jp.nextToken();
            if (t != JsonToken.END_ARRAY) {
                throw ctxt.wrongTokenException(jp, JsonToken.END_ARRAY, "Attempted to unwrap single value array for single 'Byte' value but there was more than a single value in the array");
            }
            return parsed;
        }
    }
    
    protected Short _parseShort(final JsonParser jp, final DeserializationContext ctxt) throws IOException {
        JsonToken t = jp.getCurrentToken();
        if (t == JsonToken.VALUE_NUMBER_INT || t == JsonToken.VALUE_NUMBER_FLOAT) {
            return jp.getShortValue();
        }
        if (t == JsonToken.VALUE_STRING) {
            final String text = jp.getText().trim();
            int value;
            try {
                final int len = text.length();
                if (len == 0) {
                    return this.getEmptyValue();
                }
                if (this._hasTextualNull(text)) {
                    return this.getNullValue();
                }
                value = NumberInput.parseInt(text);
            }
            catch (IllegalArgumentException iae) {
                throw ctxt.weirdStringException(text, this._valueClass, "not a valid Short value");
            }
            if (value < -32768 || value > 32767) {
                throw ctxt.weirdStringException(text, this._valueClass, "overflow, value can not be represented as 16-bit value");
            }
            return (short)value;
        }
        else {
            if (t == JsonToken.VALUE_NULL) {
                return this.getNullValue();
            }
            if (t != JsonToken.START_ARRAY || !ctxt.isEnabled(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS)) {
                throw ctxt.mappingException(this._valueClass, t);
            }
            jp.nextToken();
            final Short parsed = this._parseShort(jp, ctxt);
            t = jp.nextToken();
            if (t != JsonToken.END_ARRAY) {
                throw ctxt.wrongTokenException(jp, JsonToken.END_ARRAY, "Attempted to unwrap single value array for single 'Short' value but there was more than a single value in the array");
            }
            return parsed;
        }
    }
    
    protected final short _parseShortPrimitive(final JsonParser jp, final DeserializationContext ctxt) throws IOException {
        final int value = this._parseIntPrimitive(jp, ctxt);
        if (value < -32768 || value > 32767) {
            throw ctxt.weirdStringException(String.valueOf(value), this._valueClass, "overflow, value can not be represented as 16-bit value");
        }
        return (short)value;
    }
    
    protected final int _parseIntPrimitive(final JsonParser jp, final DeserializationContext ctxt) throws IOException {
        JsonToken t = jp.getCurrentToken();
        if (t == JsonToken.VALUE_NUMBER_INT || t == JsonToken.VALUE_NUMBER_FLOAT) {
            return jp.getIntValue();
        }
        if (t == JsonToken.VALUE_STRING) {
            final String text = jp.getText().trim();
            if (this._hasTextualNull(text)) {
                return 0;
            }
            try {
                final int len = text.length();
                if (len > 9) {
                    final long l = Long.parseLong(text);
                    if (l < -2147483648L || l > 2147483647L) {
                        throw ctxt.weirdStringException(text, this._valueClass, "Overflow: numeric value (" + text + ") out of range of int (" + Integer.MIN_VALUE + " - " + Integer.MAX_VALUE + ")");
                    }
                    return (int)l;
                }
                else {
                    if (len == 0) {
                        return 0;
                    }
                    return NumberInput.parseInt(text);
                }
            }
            catch (IllegalArgumentException iae) {
                throw ctxt.weirdStringException(text, this._valueClass, "not a valid int value");
            }
        }
        if (t == JsonToken.VALUE_NULL) {
            return 0;
        }
        if (t != JsonToken.START_ARRAY || !ctxt.isEnabled(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS)) {
            throw ctxt.mappingException(this._valueClass, t);
        }
        jp.nextToken();
        final int parsed = this._parseIntPrimitive(jp, ctxt);
        t = jp.nextToken();
        if (t != JsonToken.END_ARRAY) {
            throw ctxt.wrongTokenException(jp, JsonToken.END_ARRAY, "Attempted to unwrap single value array for single 'int' value but there was more than a single value in the array");
        }
        return parsed;
    }
    
    protected final Integer _parseInteger(final JsonParser jp, final DeserializationContext ctxt) throws IOException {
        JsonToken t = jp.getCurrentToken();
        if (t == JsonToken.VALUE_NUMBER_INT || t == JsonToken.VALUE_NUMBER_FLOAT) {
            return jp.getIntValue();
        }
        if (t == JsonToken.VALUE_STRING) {
            final String text = jp.getText().trim();
            try {
                final int len = text.length();
                if (this._hasTextualNull(text)) {
                    return this.getNullValue();
                }
                if (len > 9) {
                    final long l = Long.parseLong(text);
                    if (l < -2147483648L || l > 2147483647L) {
                        throw ctxt.weirdStringException(text, this._valueClass, "Overflow: numeric value (" + text + ") out of range of Integer (" + Integer.MIN_VALUE + " - " + Integer.MAX_VALUE + ")");
                    }
                    return (int)l;
                }
                else {
                    if (len == 0) {
                        return this.getEmptyValue();
                    }
                    return NumberInput.parseInt(text);
                }
            }
            catch (IllegalArgumentException iae) {
                throw ctxt.weirdStringException(text, this._valueClass, "not a valid Integer value");
            }
        }
        if (t == JsonToken.VALUE_NULL) {
            return this.getNullValue();
        }
        if (t != JsonToken.START_ARRAY || !ctxt.isEnabled(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS)) {
            throw ctxt.mappingException(this._valueClass, t);
        }
        jp.nextToken();
        final Integer parsed = this._parseInteger(jp, ctxt);
        t = jp.nextToken();
        if (t != JsonToken.END_ARRAY) {
            throw ctxt.wrongTokenException(jp, JsonToken.END_ARRAY, "Attempted to unwrap single value array for single 'Integer' value but there was more than a single value in the array");
        }
        return parsed;
    }
    
    protected final Long _parseLong(final JsonParser jp, final DeserializationContext ctxt) throws IOException {
        JsonToken t = jp.getCurrentToken();
        if (t == JsonToken.VALUE_NUMBER_INT || t == JsonToken.VALUE_NUMBER_FLOAT) {
            return jp.getLongValue();
        }
        if (t == JsonToken.VALUE_STRING) {
            final String text = jp.getText().trim();
            if (text.length() == 0) {
                return this.getEmptyValue();
            }
            if (this._hasTextualNull(text)) {
                return this.getNullValue();
            }
            try {
                return NumberInput.parseLong(text);
            }
            catch (IllegalArgumentException iae) {
                throw ctxt.weirdStringException(text, this._valueClass, "not a valid Long value");
            }
        }
        if (t == JsonToken.VALUE_NULL) {
            return this.getNullValue();
        }
        if (t != JsonToken.START_ARRAY || !ctxt.isEnabled(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS)) {
            throw ctxt.mappingException(this._valueClass, t);
        }
        jp.nextToken();
        final Long parsed = this._parseLong(jp, ctxt);
        t = jp.nextToken();
        if (t != JsonToken.END_ARRAY) {
            throw ctxt.wrongTokenException(jp, JsonToken.END_ARRAY, "Attempted to unwrap single value array for single 'Long' value but there was more than a single value in the array");
        }
        return parsed;
    }
    
    protected final long _parseLongPrimitive(final JsonParser jp, final DeserializationContext ctxt) throws IOException {
        JsonToken t = jp.getCurrentToken();
        if (t == JsonToken.VALUE_NUMBER_INT || t == JsonToken.VALUE_NUMBER_FLOAT) {
            return jp.getLongValue();
        }
        if (t == JsonToken.VALUE_STRING) {
            final String text = jp.getText().trim();
            if (text.length() == 0 || this._hasTextualNull(text)) {
                return 0L;
            }
            try {
                return NumberInput.parseLong(text);
            }
            catch (IllegalArgumentException iae) {
                throw ctxt.weirdStringException(text, this._valueClass, "not a valid long value");
            }
        }
        if (t == JsonToken.VALUE_NULL) {
            return 0L;
        }
        if (t != JsonToken.START_ARRAY || !ctxt.isEnabled(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS)) {
            throw ctxt.mappingException(this._valueClass, t);
        }
        jp.nextToken();
        final long parsed = this._parseLongPrimitive(jp, ctxt);
        t = jp.nextToken();
        if (t != JsonToken.END_ARRAY) {
            throw ctxt.wrongTokenException(jp, JsonToken.END_ARRAY, "Attempted to unwrap single value array for single 'long' value but there was more than a single value in the array");
        }
        return parsed;
    }
    
    protected final Float _parseFloat(final JsonParser jp, final DeserializationContext ctxt) throws IOException {
        JsonToken t = jp.getCurrentToken();
        if (t == JsonToken.VALUE_NUMBER_INT || t == JsonToken.VALUE_NUMBER_FLOAT) {
            return jp.getFloatValue();
        }
        if (t == JsonToken.VALUE_STRING) {
            final String text = jp.getText().trim();
            if (text.length() == 0) {
                return this.getEmptyValue();
            }
            if (this._hasTextualNull(text)) {
                return this.getNullValue();
            }
            switch (text.charAt(0)) {
                case 'I': {
                    if (this._isPosInf(text)) {
                        return Float.POSITIVE_INFINITY;
                    }
                    break;
                }
                case 'N': {
                    if (this._isNaN(text)) {
                        return Float.NaN;
                    }
                    break;
                }
                case '-': {
                    if (this._isNegInf(text)) {
                        return Float.NEGATIVE_INFINITY;
                    }
                    break;
                }
            }
            try {
                return Float.parseFloat(text);
            }
            catch (IllegalArgumentException iae) {
                throw ctxt.weirdStringException(text, this._valueClass, "not a valid Float value");
            }
        }
        if (t == JsonToken.VALUE_NULL) {
            return this.getNullValue();
        }
        if (t != JsonToken.START_ARRAY || !ctxt.isEnabled(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS)) {
            throw ctxt.mappingException(this._valueClass, t);
        }
        jp.nextToken();
        final Float parsed = this._parseFloat(jp, ctxt);
        t = jp.nextToken();
        if (t != JsonToken.END_ARRAY) {
            throw ctxt.wrongTokenException(jp, JsonToken.END_ARRAY, "Attempted to unwrap single value array for single 'Byte' value but there was more than a single value in the array");
        }
        return parsed;
    }
    
    protected final float _parseFloatPrimitive(final JsonParser jp, final DeserializationContext ctxt) throws IOException {
        JsonToken t = jp.getCurrentToken();
        if (t == JsonToken.VALUE_NUMBER_INT || t == JsonToken.VALUE_NUMBER_FLOAT) {
            return jp.getFloatValue();
        }
        if (t == JsonToken.VALUE_STRING) {
            final String text = jp.getText().trim();
            if (text.length() == 0 || this._hasTextualNull(text)) {
                return 0.0f;
            }
            switch (text.charAt(0)) {
                case 'I': {
                    if (this._isPosInf(text)) {
                        return Float.POSITIVE_INFINITY;
                    }
                    break;
                }
                case 'N': {
                    if (this._isNaN(text)) {
                        return Float.NaN;
                    }
                    break;
                }
                case '-': {
                    if (this._isNegInf(text)) {
                        return Float.NEGATIVE_INFINITY;
                    }
                    break;
                }
            }
            try {
                return Float.parseFloat(text);
            }
            catch (IllegalArgumentException iae) {
                throw ctxt.weirdStringException(text, this._valueClass, "not a valid float value");
            }
        }
        if (t == JsonToken.VALUE_NULL) {
            return 0.0f;
        }
        if (t != JsonToken.START_ARRAY || !ctxt.isEnabled(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS)) {
            throw ctxt.mappingException(this._valueClass, t);
        }
        jp.nextToken();
        final float parsed = this._parseFloatPrimitive(jp, ctxt);
        t = jp.nextToken();
        if (t != JsonToken.END_ARRAY) {
            throw ctxt.wrongTokenException(jp, JsonToken.END_ARRAY, "Attempted to unwrap single value array for single 'float' value but there was more than a single value in the array");
        }
        return parsed;
    }
    
    protected final Double _parseDouble(final JsonParser jp, final DeserializationContext ctxt) throws IOException {
        JsonToken t = jp.getCurrentToken();
        if (t == JsonToken.VALUE_NUMBER_INT || t == JsonToken.VALUE_NUMBER_FLOAT) {
            return jp.getDoubleValue();
        }
        if (t == JsonToken.VALUE_STRING) {
            final String text = jp.getText().trim();
            if (text.length() == 0) {
                return this.getEmptyValue();
            }
            if (this._hasTextualNull(text)) {
                return this.getNullValue();
            }
            switch (text.charAt(0)) {
                case 'I': {
                    if (this._isPosInf(text)) {
                        return Double.POSITIVE_INFINITY;
                    }
                    break;
                }
                case 'N': {
                    if (this._isNaN(text)) {
                        return Double.NaN;
                    }
                    break;
                }
                case '-': {
                    if (this._isNegInf(text)) {
                        return Double.NEGATIVE_INFINITY;
                    }
                    break;
                }
            }
            try {
                return parseDouble(text);
            }
            catch (IllegalArgumentException iae) {
                throw ctxt.weirdStringException(text, this._valueClass, "not a valid Double value");
            }
        }
        if (t == JsonToken.VALUE_NULL) {
            return this.getNullValue();
        }
        if (t != JsonToken.START_ARRAY || !ctxt.isEnabled(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS)) {
            throw ctxt.mappingException(this._valueClass, t);
        }
        jp.nextToken();
        final Double parsed = this._parseDouble(jp, ctxt);
        t = jp.nextToken();
        if (t != JsonToken.END_ARRAY) {
            throw ctxt.wrongTokenException(jp, JsonToken.END_ARRAY, "Attempted to unwrap single value array for single 'Double' value but there was more than a single value in the array");
        }
        return parsed;
    }
    
    protected final double _parseDoublePrimitive(final JsonParser jp, final DeserializationContext ctxt) throws IOException {
        JsonToken t = jp.getCurrentToken();
        if (t == JsonToken.VALUE_NUMBER_INT || t == JsonToken.VALUE_NUMBER_FLOAT) {
            return jp.getDoubleValue();
        }
        if (t == JsonToken.VALUE_STRING) {
            final String text = jp.getText().trim();
            if (text.length() == 0 || this._hasTextualNull(text)) {
                return 0.0;
            }
            switch (text.charAt(0)) {
                case 'I': {
                    if (this._isPosInf(text)) {
                        return Double.POSITIVE_INFINITY;
                    }
                    break;
                }
                case 'N': {
                    if (this._isNaN(text)) {
                        return Double.NaN;
                    }
                    break;
                }
                case '-': {
                    if (this._isNegInf(text)) {
                        return Double.NEGATIVE_INFINITY;
                    }
                    break;
                }
            }
            try {
                return parseDouble(text);
            }
            catch (IllegalArgumentException iae) {
                throw ctxt.weirdStringException(text, this._valueClass, "not a valid double value");
            }
        }
        if (t == JsonToken.VALUE_NULL) {
            return 0.0;
        }
        if (t != JsonToken.START_ARRAY || !ctxt.isEnabled(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS)) {
            throw ctxt.mappingException(this._valueClass, t);
        }
        jp.nextToken();
        final double parsed = this._parseDoublePrimitive(jp, ctxt);
        t = jp.nextToken();
        if (t != JsonToken.END_ARRAY) {
            throw ctxt.wrongTokenException(jp, JsonToken.END_ARRAY, "Attempted to unwrap single value array for single 'Byte' value but there was more than a single value in the array");
        }
        return parsed;
    }
    
    protected Date _parseDate(final JsonParser jp, final DeserializationContext ctxt) throws IOException {
        JsonToken t = jp.getCurrentToken();
        if (t == JsonToken.VALUE_NUMBER_INT) {
            return new Date(jp.getLongValue());
        }
        if (t == JsonToken.VALUE_NULL) {
            return this.getNullValue();
        }
        if (t == JsonToken.VALUE_STRING) {
            String value = null;
            try {
                value = jp.getText().trim();
                if (value.length() == 0) {
                    return this.getEmptyValue();
                }
                if (this._hasTextualNull(value)) {
                    return this.getNullValue();
                }
                return ctxt.parseDate(value);
            }
            catch (IllegalArgumentException iae) {
                throw ctxt.weirdStringException(value, this._valueClass, "not a valid representation (error: " + iae.getMessage() + ")");
            }
        }
        if (t != JsonToken.START_ARRAY || !ctxt.isEnabled(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS)) {
            throw ctxt.mappingException(this._valueClass, t);
        }
        jp.nextToken();
        final Date parsed = this._parseDate(jp, ctxt);
        t = jp.nextToken();
        if (t != JsonToken.END_ARRAY) {
            throw ctxt.wrongTokenException(jp, JsonToken.END_ARRAY, "Attempted to unwrap single value array for single 'java.util.Date' value but there was more than a single value in the array");
        }
        return parsed;
    }
    
    protected static final double parseDouble(final String numStr) throws NumberFormatException {
        if ("2.2250738585072012e-308".equals(numStr)) {
            return Double.MIN_VALUE;
        }
        return Double.parseDouble(numStr);
    }
    
    protected final String _parseString(final JsonParser jp, final DeserializationContext ctxt) throws IOException {
        final JsonToken t = jp.getCurrentToken();
        if (t == JsonToken.VALUE_STRING) {
            return jp.getText();
        }
        if (t == JsonToken.START_ARRAY && ctxt.isEnabled(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS)) {
            jp.nextToken();
            final String parsed = this._parseString(jp, ctxt);
            if (jp.nextToken() != JsonToken.END_ARRAY) {
                throw ctxt.wrongTokenException(jp, JsonToken.END_ARRAY, "Attempted to unwrap single value array for single 'String' value but there was more than a single value in the array");
            }
            return parsed;
        }
        else {
            final String value = jp.getValueAsString();
            if (value != null) {
                return value;
            }
            throw ctxt.mappingException(String.class, jp.getCurrentToken());
        }
    }
    
    protected boolean _hasTextualNull(final String value) {
        return "null".equals(value);
    }
    
    protected final boolean _isNegInf(final String text) {
        return "-Infinity".equals(text) || "-INF".equals(text);
    }
    
    protected final boolean _isPosInf(final String text) {
        return "Infinity".equals(text) || "INF".equals(text);
    }
    
    protected final boolean _isNaN(final String text) {
        return "NaN".equals(text);
    }
    
    protected JsonDeserializer<Object> findDeserializer(final DeserializationContext ctxt, final JavaType type, final BeanProperty property) throws JsonMappingException {
        return ctxt.findContextualValueDeserializer(type, property);
    }
    
    protected JsonDeserializer<?> findConvertingContentDeserializer(final DeserializationContext ctxt, final BeanProperty prop, JsonDeserializer<?> existingDeserializer) throws JsonMappingException {
        final AnnotationIntrospector intr = ctxt.getAnnotationIntrospector();
        if (intr != null && prop != null) {
            final Object convDef = intr.findDeserializationContentConverter(prop.getMember());
            if (convDef != null) {
                final Converter<Object, Object> conv = ctxt.converterInstance(prop.getMember(), convDef);
                final JavaType delegateType = conv.getInputType(ctxt.getTypeFactory());
                if (existingDeserializer == null) {
                    existingDeserializer = ctxt.findContextualValueDeserializer(delegateType, prop);
                }
                return new StdDelegatingDeserializer<Object>(conv, delegateType, existingDeserializer);
            }
        }
        return existingDeserializer;
    }
    
    protected void handleUnknownProperty(final JsonParser jp, final DeserializationContext ctxt, Object instanceOrClass, final String propName) throws IOException {
        if (instanceOrClass == null) {
            instanceOrClass = this.handledType();
        }
        if (ctxt.handleUnknownProperty(jp, this, instanceOrClass, propName)) {
            return;
        }
        ctxt.reportUnknownProperty(instanceOrClass, propName, this);
        jp.skipChildren();
    }
}
