// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.htrace.shaded.fasterxml.jackson.databind.deser.std;

import java.lang.reflect.Method;
import org.apache.htrace.shaded.fasterxml.jackson.core.JsonProcessingException;
import java.io.IOException;
import org.apache.htrace.shaded.fasterxml.jackson.databind.DeserializationFeature;
import org.apache.htrace.shaded.fasterxml.jackson.core.JsonToken;
import org.apache.htrace.shaded.fasterxml.jackson.databind.DeserializationContext;
import org.apache.htrace.shaded.fasterxml.jackson.core.JsonParser;
import java.lang.reflect.Member;
import org.apache.htrace.shaded.fasterxml.jackson.databind.util.ClassUtil;
import org.apache.htrace.shaded.fasterxml.jackson.databind.JsonDeserializer;
import org.apache.htrace.shaded.fasterxml.jackson.databind.introspect.AnnotatedMethod;
import org.apache.htrace.shaded.fasterxml.jackson.databind.DeserializationConfig;
import org.apache.htrace.shaded.fasterxml.jackson.databind.util.EnumResolver;

public class EnumDeserializer extends StdScalarDeserializer<Enum<?>>
{
    private static final long serialVersionUID = -5893263645879532318L;
    protected final EnumResolver<?> _resolver;
    
    public EnumDeserializer(final EnumResolver<?> res) {
        super(Enum.class);
        this._resolver = res;
    }
    
    public static JsonDeserializer<?> deserializerForCreator(final DeserializationConfig config, final Class<?> enumClass, final AnnotatedMethod factory) {
        Class<?> paramClass = factory.getRawParameterType(0);
        if (paramClass == String.class) {
            paramClass = null;
        }
        else if (paramClass == Integer.TYPE || paramClass == Integer.class) {
            paramClass = Integer.class;
        }
        else {
            if (paramClass != Long.TYPE && paramClass != Long.class) {
                throw new IllegalArgumentException("Parameter #0 type for factory method (" + factory + ") not suitable, must be java.lang.String or int/Integer/long/Long");
            }
            paramClass = Long.class;
        }
        if (config.canOverrideAccessModifiers()) {
            ClassUtil.checkAndFixAccess(factory.getMember());
        }
        return new FactoryBasedDeserializer(enumClass, factory, paramClass);
    }
    
    @Override
    public boolean isCachable() {
        return true;
    }
    
    @Override
    public Enum<?> deserialize(final JsonParser jp, final DeserializationContext ctxt) throws IOException {
        final JsonToken curr = jp.getCurrentToken();
        if (curr == JsonToken.VALUE_STRING || curr == JsonToken.FIELD_NAME) {
            final String name = jp.getText();
            final Enum<?> result = (Enum<?>)this._resolver.findEnum(name);
            if (result == null) {
                return this._deserializeAltString(jp, ctxt, name);
            }
            return result;
        }
        else {
            if (curr != JsonToken.VALUE_NUMBER_INT) {
                return this._deserializeOther(jp, ctxt);
            }
            if (ctxt.isEnabled(DeserializationFeature.FAIL_ON_NUMBERS_FOR_ENUMS)) {
                throw ctxt.mappingException("Not allowed to deserialize Enum value out of JSON number (disable DeserializationConfig.DeserializationFeature.FAIL_ON_NUMBERS_FOR_ENUMS to allow)");
            }
            final int index = jp.getIntValue();
            final Enum<?> result = (Enum<?>)this._resolver.getEnum(index);
            if (result == null && !ctxt.isEnabled(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL)) {
                throw ctxt.weirdNumberException(index, this._resolver.getEnumClass(), "index value outside legal index range [0.." + this._resolver.lastValidIndex() + "]");
            }
            return result;
        }
    }
    
    private final Enum<?> _deserializeAltString(final JsonParser jp, final DeserializationContext ctxt, String name) throws IOException {
        name = name.trim();
        if (name.length() == 0) {
            if (ctxt.isEnabled(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT)) {
                return null;
            }
        }
        else {
            final char c = name.charAt(0);
            if (c >= '0' && c <= '9') {
                try {
                    final int ix = Integer.parseInt(name);
                    final Enum<?> result = (Enum<?>)this._resolver.getEnum(ix);
                    if (result != null) {
                        return result;
                    }
                }
                catch (NumberFormatException ex) {}
            }
        }
        if (!ctxt.isEnabled(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL)) {
            throw ctxt.weirdStringException(name, this._resolver.getEnumClass(), "value not one of declared Enum instance names: " + this._resolver.getEnums());
        }
        return null;
    }
    
    private final Enum<?> _deserializeOther(final JsonParser jp, final DeserializationContext ctxt) throws IOException {
        JsonToken curr = jp.getCurrentToken();
        if (curr != JsonToken.START_ARRAY || !ctxt.isEnabled(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS)) {
            throw ctxt.mappingException(this._resolver.getEnumClass());
        }
        jp.nextToken();
        final Enum<?> parsed = this.deserialize(jp, ctxt);
        curr = jp.nextToken();
        if (curr != JsonToken.END_ARRAY) {
            throw ctxt.wrongTokenException(jp, JsonToken.END_ARRAY, "Attempted to unwrap single value array for single '" + this._resolver.getEnumClass().getName() + "' value but there was more than a single value in the array");
        }
        return parsed;
    }
    
    protected static class FactoryBasedDeserializer extends StdScalarDeserializer<Object>
    {
        private static final long serialVersionUID = -7775129435872564122L;
        protected final Class<?> _enumClass;
        protected final Class<?> _inputType;
        protected final Method _factory;
        
        public FactoryBasedDeserializer(final Class<?> cls, final AnnotatedMethod f, final Class<?> inputType) {
            super(Enum.class);
            this._enumClass = cls;
            this._factory = f.getAnnotated();
            this._inputType = inputType;
        }
        
        @Override
        public Object deserialize(final JsonParser jp, final DeserializationContext ctxt) throws IOException, JsonProcessingException {
            Object value;
            if (this._inputType == null) {
                value = jp.getText();
            }
            else if (this._inputType == Integer.class) {
                value = jp.getValueAsInt();
            }
            else {
                if (this._inputType != Long.class) {
                    throw ctxt.mappingException(this._enumClass);
                }
                value = jp.getValueAsLong();
            }
            try {
                return this._factory.invoke(this._enumClass, value);
            }
            catch (Exception e) {
                final Throwable t = ClassUtil.getRootCause(e);
                if (t instanceof IOException) {
                    throw (IOException)t;
                }
                throw ctxt.instantiationException(this._enumClass, t);
            }
        }
    }
}
