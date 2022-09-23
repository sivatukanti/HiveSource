// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.htrace.shaded.fasterxml.jackson.databind.deser.std;

import java.lang.reflect.Method;
import java.lang.reflect.Constructor;
import org.apache.htrace.shaded.fasterxml.jackson.databind.JsonMappingException;
import org.apache.htrace.shaded.fasterxml.jackson.databind.util.ClassUtil;
import org.apache.htrace.shaded.fasterxml.jackson.databind.introspect.AnnotatedMethod;
import org.apache.htrace.shaded.fasterxml.jackson.databind.util.EnumResolver;
import org.apache.htrace.shaded.fasterxml.jackson.databind.JsonDeserializer;
import org.apache.htrace.shaded.fasterxml.jackson.core.io.NumberInput;
import org.apache.htrace.shaded.fasterxml.jackson.core.JsonProcessingException;
import java.io.IOException;
import org.apache.htrace.shaded.fasterxml.jackson.databind.DeserializationFeature;
import org.apache.htrace.shaded.fasterxml.jackson.databind.DeserializationContext;
import java.util.Locale;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;
import org.apache.htrace.shaded.fasterxml.jackson.databind.annotation.JacksonStdImpl;
import java.io.Serializable;
import org.apache.htrace.shaded.fasterxml.jackson.databind.KeyDeserializer;

@JacksonStdImpl
public class StdKeyDeserializer extends KeyDeserializer implements Serializable
{
    private static final long serialVersionUID = 1L;
    public static final int TYPE_BOOLEAN = 1;
    public static final int TYPE_BYTE = 2;
    public static final int TYPE_SHORT = 3;
    public static final int TYPE_CHAR = 4;
    public static final int TYPE_INT = 5;
    public static final int TYPE_LONG = 6;
    public static final int TYPE_FLOAT = 7;
    public static final int TYPE_DOUBLE = 8;
    public static final int TYPE_LOCALE = 9;
    public static final int TYPE_DATE = 10;
    public static final int TYPE_CALENDAR = 11;
    public static final int TYPE_UUID = 12;
    protected final int _kind;
    protected final Class<?> _keyClass;
    protected final FromStringDeserializer<?> _deser;
    
    protected StdKeyDeserializer(final int kind, final Class<?> cls) {
        this(kind, cls, null);
    }
    
    protected StdKeyDeserializer(final int kind, final Class<?> cls, final FromStringDeserializer<?> deser) {
        this._kind = kind;
        this._keyClass = cls;
        this._deser = deser;
    }
    
    public static StdKeyDeserializer forType(final Class<?> raw) {
        if (raw == String.class || raw == Object.class) {
            return StringKD.forType(raw);
        }
        int kind;
        if (raw == UUID.class) {
            kind = 12;
        }
        else if (raw == Integer.class) {
            kind = 5;
        }
        else if (raw == Long.class) {
            kind = 6;
        }
        else if (raw == Date.class) {
            kind = 10;
        }
        else if (raw == Calendar.class) {
            kind = 11;
        }
        else if (raw == Boolean.class) {
            kind = 1;
        }
        else if (raw == Byte.class) {
            kind = 2;
        }
        else if (raw == Character.class) {
            kind = 4;
        }
        else if (raw == Short.class) {
            kind = 3;
        }
        else if (raw == Float.class) {
            kind = 7;
        }
        else if (raw == Double.class) {
            kind = 8;
        }
        else {
            if (raw == Locale.class) {
                final FromStringDeserializer<?> deser = FromStringDeserializer.findDeserializer(Locale.class);
                return new StdKeyDeserializer(9, raw, deser);
            }
            return null;
        }
        return new StdKeyDeserializer(kind, raw);
    }
    
    @Override
    public Object deserializeKey(final String key, final DeserializationContext ctxt) throws IOException, JsonProcessingException {
        if (key == null) {
            return null;
        }
        try {
            final Object result = this._parse(key, ctxt);
            if (result != null) {
                return result;
            }
        }
        catch (Exception re) {
            throw ctxt.weirdKeyException(this._keyClass, key, "not a valid representation: " + re.getMessage());
        }
        if (this._keyClass.isEnum() && ctxt.getConfig().isEnabled(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL)) {
            return null;
        }
        throw ctxt.weirdKeyException(this._keyClass, key, "not a valid representation");
    }
    
    public Class<?> getKeyClass() {
        return this._keyClass;
    }
    
    protected Object _parse(final String key, final DeserializationContext ctxt) throws Exception {
        switch (this._kind) {
            case 1: {
                if ("true".equals(key)) {
                    return Boolean.TRUE;
                }
                if ("false".equals(key)) {
                    return Boolean.FALSE;
                }
                throw ctxt.weirdKeyException(this._keyClass, key, "value not 'true' or 'false'");
            }
            case 2: {
                final int value = this._parseInt(key);
                if (value < -128 || value > 255) {
                    throw ctxt.weirdKeyException(this._keyClass, key, "overflow, value can not be represented as 8-bit value");
                }
                return (byte)value;
            }
            case 3: {
                final int value = this._parseInt(key);
                if (value < -32768 || value > 32767) {
                    throw ctxt.weirdKeyException(this._keyClass, key, "overflow, value can not be represented as 16-bit value");
                }
                return (short)value;
            }
            case 4: {
                if (key.length() == 1) {
                    return key.charAt(0);
                }
                throw ctxt.weirdKeyException(this._keyClass, key, "can only convert 1-character Strings");
            }
            case 5: {
                return this._parseInt(key);
            }
            case 6: {
                return this._parseLong(key);
            }
            case 7: {
                return (float)this._parseDouble(key);
            }
            case 8: {
                return this._parseDouble(key);
            }
            case 9: {
                try {
                    return this._deser._deserialize(key, ctxt);
                }
                catch (IOException e) {
                    throw ctxt.weirdKeyException(this._keyClass, key, "unable to parse key as locale");
                }
            }
            case 10: {
                return ctxt.parseDate(key);
            }
            case 11: {
                final Date date = ctxt.parseDate(key);
                return (date == null) ? null : ctxt.constructCalendar(date);
            }
            case 12: {
                return UUID.fromString(key);
            }
            default: {
                return null;
            }
        }
    }
    
    protected int _parseInt(final String key) throws IllegalArgumentException {
        return Integer.parseInt(key);
    }
    
    protected long _parseLong(final String key) throws IllegalArgumentException {
        return Long.parseLong(key);
    }
    
    protected double _parseDouble(final String key) throws IllegalArgumentException {
        return NumberInput.parseDouble(key);
    }
    
    @JacksonStdImpl
    static final class StringKD extends StdKeyDeserializer
    {
        private static final long serialVersionUID = 1L;
        private static final StringKD sString;
        private static final StringKD sObject;
        
        private StringKD(final Class<?> nominalType) {
            super(-1, nominalType);
        }
        
        public static StringKD forType(final Class<?> nominalType) {
            if (nominalType == String.class) {
                return StringKD.sString;
            }
            if (nominalType == Object.class) {
                return StringKD.sObject;
            }
            return new StringKD(nominalType);
        }
        
        @Override
        public Object deserializeKey(final String key, final DeserializationContext ctxt) throws IOException, JsonProcessingException {
            return key;
        }
        
        static {
            sString = new StringKD(String.class);
            sObject = new StringKD(Object.class);
        }
    }
    
    static final class DelegatingKD extends KeyDeserializer implements Serializable
    {
        private static final long serialVersionUID = 1L;
        protected final Class<?> _keyClass;
        protected final JsonDeserializer<?> _delegate;
        
        protected DelegatingKD(final Class<?> cls, final JsonDeserializer<?> deser) {
            this._keyClass = cls;
            this._delegate = deser;
        }
        
        @Override
        public final Object deserializeKey(final String key, final DeserializationContext ctxt) throws IOException, JsonProcessingException {
            if (key == null) {
                return null;
            }
            try {
                final Object result = this._delegate.deserialize(ctxt.getParser(), ctxt);
                if (result != null) {
                    return result;
                }
            }
            catch (Exception re) {
                throw ctxt.weirdKeyException(this._keyClass, key, "not a valid representation: " + re.getMessage());
            }
            throw ctxt.weirdKeyException(this._keyClass, key, "not a valid representation");
        }
        
        public Class<?> getKeyClass() {
            return this._keyClass;
        }
    }
    
    @JacksonStdImpl
    static final class EnumKD extends StdKeyDeserializer
    {
        private static final long serialVersionUID = 1L;
        protected final EnumResolver<?> _resolver;
        protected final AnnotatedMethod _factory;
        
        protected EnumKD(final EnumResolver<?> er, final AnnotatedMethod factory) {
            super(-1, er.getEnumClass());
            this._resolver = er;
            this._factory = factory;
        }
        
        public Object _parse(final String key, final DeserializationContext ctxt) throws JsonMappingException {
            if (this._factory != null) {
                try {
                    return this._factory.call1(key);
                }
                catch (Exception e) {
                    ClassUtil.unwrapAndThrowAsIAE(e);
                }
            }
            final Enum<?> e2 = (Enum<?>)this._resolver.findEnum(key);
            if (e2 == null && !ctxt.getConfig().isEnabled(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL)) {
                throw ctxt.weirdKeyException(this._keyClass, key, "not one of values for Enum class");
            }
            return e2;
        }
    }
    
    static final class StringCtorKeyDeserializer extends StdKeyDeserializer
    {
        private static final long serialVersionUID = 1L;
        protected final Constructor<?> _ctor;
        
        public StringCtorKeyDeserializer(final Constructor<?> ctor) {
            super(-1, ctor.getDeclaringClass());
            this._ctor = ctor;
        }
        
        public Object _parse(final String key, final DeserializationContext ctxt) throws Exception {
            return this._ctor.newInstance(key);
        }
    }
    
    static final class StringFactoryKeyDeserializer extends StdKeyDeserializer
    {
        private static final long serialVersionUID = 1L;
        final Method _factoryMethod;
        
        public StringFactoryKeyDeserializer(final Method fm) {
            super(-1, fm.getDeclaringClass());
            this._factoryMethod = fm;
        }
        
        public Object _parse(final String key, final DeserializationContext ctxt) throws Exception {
            return this._factoryMethod.invoke(null, key);
        }
    }
}
