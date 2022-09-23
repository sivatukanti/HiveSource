// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.htrace.shaded.fasterxml.jackson.databind.deser.std;

import org.apache.htrace.shaded.fasterxml.jackson.databind.exc.InvalidFormatException;
import org.apache.htrace.shaded.fasterxml.jackson.databind.util.ClassUtil;
import java.io.IOException;
import org.apache.htrace.shaded.fasterxml.jackson.databind.JsonMappingException;
import org.apache.htrace.shaded.fasterxml.jackson.databind.DeserializationFeature;
import org.apache.htrace.shaded.fasterxml.jackson.core.JsonToken;
import org.apache.htrace.shaded.fasterxml.jackson.databind.DeserializationContext;
import org.apache.htrace.shaded.fasterxml.jackson.core.JsonParser;
import java.net.InetSocketAddress;
import java.net.InetAddress;
import java.util.TimeZone;
import java.nio.charset.Charset;
import java.util.Locale;
import java.util.regex.Pattern;
import java.util.Currency;
import org.apache.htrace.shaded.fasterxml.jackson.databind.JavaType;
import java.net.URI;
import java.net.URL;
import java.io.File;

public abstract class FromStringDeserializer<T> extends StdScalarDeserializer<T>
{
    public static Class<?>[] types() {
        return (Class<?>[])new Class[] { File.class, URL.class, URI.class, Class.class, JavaType.class, Currency.class, Pattern.class, Locale.class, Charset.class, TimeZone.class, InetAddress.class, InetSocketAddress.class };
    }
    
    protected FromStringDeserializer(final Class<?> vc) {
        super(vc);
    }
    
    public static Std findDeserializer(final Class<?> rawType) {
        int kind = 0;
        if (rawType == File.class) {
            kind = 1;
        }
        else if (rawType == URL.class) {
            kind = 2;
        }
        else if (rawType == URI.class) {
            kind = 3;
        }
        else if (rawType == Class.class) {
            kind = 4;
        }
        else if (rawType == JavaType.class) {
            kind = 5;
        }
        else if (rawType == Currency.class) {
            kind = 6;
        }
        else if (rawType == Pattern.class) {
            kind = 7;
        }
        else if (rawType == Locale.class) {
            kind = 8;
        }
        else if (rawType == Charset.class) {
            kind = 9;
        }
        else if (rawType == TimeZone.class) {
            kind = 10;
        }
        else if (rawType == InetAddress.class) {
            kind = 11;
        }
        else {
            if (rawType != InetSocketAddress.class) {
                return null;
            }
            kind = 12;
        }
        return new Std(rawType, kind);
    }
    
    @Override
    public T deserialize(final JsonParser jp, final DeserializationContext ctxt) throws IOException {
        if (jp.getCurrentToken() == JsonToken.START_ARRAY && ctxt.isEnabled(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS)) {
            jp.nextToken();
            final T value = this.deserialize(jp, ctxt);
            if (jp.nextToken() != JsonToken.END_ARRAY) {
                throw ctxt.wrongTokenException(jp, JsonToken.END_ARRAY, "Attempted to unwrap single value array for single '" + this._valueClass.getName() + "' value but there was more than a single value in the array");
            }
            return value;
        }
        else {
            String text = jp.getValueAsString();
            if (text != null) {
                if (text.length() == 0 || (text = text.trim()).length() == 0) {
                    return this._deserializeFromEmptyString();
                }
                Exception cause = null;
                try {
                    final T result = this._deserialize(text, ctxt);
                    if (result != null) {
                        return result;
                    }
                }
                catch (IllegalArgumentException iae) {
                    cause = iae;
                }
                String msg = "not a valid textual representation";
                if (cause != null) {
                    msg = msg + "problem: " + cause.getMessage();
                }
                final JsonMappingException e = ctxt.weirdStringException(text, this._valueClass, msg);
                if (cause != null) {
                    e.initCause(cause);
                }
                throw e;
            }
            else {
                if (jp.getCurrentToken() != JsonToken.VALUE_EMBEDDED_OBJECT) {
                    throw ctxt.mappingException(this._valueClass);
                }
                final Object ob = jp.getEmbeddedObject();
                if (ob == null) {
                    return null;
                }
                if (this._valueClass.isAssignableFrom(ob.getClass())) {
                    return (T)ob;
                }
                return this._deserializeEmbedded(ob, ctxt);
            }
        }
    }
    
    protected abstract T _deserialize(final String p0, final DeserializationContext p1) throws IOException;
    
    protected T _deserializeEmbedded(final Object ob, final DeserializationContext ctxt) throws IOException {
        throw ctxt.mappingException("Don't know how to convert embedded Object of type " + ob.getClass().getName() + " into " + this._valueClass.getName());
    }
    
    protected T _deserializeFromEmptyString() throws IOException {
        return null;
    }
    
    public static class Std extends FromStringDeserializer<Object>
    {
        private static final long serialVersionUID = 1L;
        public static final int STD_FILE = 1;
        public static final int STD_URL = 2;
        public static final int STD_URI = 3;
        public static final int STD_CLASS = 4;
        public static final int STD_JAVA_TYPE = 5;
        public static final int STD_CURRENCY = 6;
        public static final int STD_PATTERN = 7;
        public static final int STD_LOCALE = 8;
        public static final int STD_CHARSET = 9;
        public static final int STD_TIME_ZONE = 10;
        public static final int STD_INET_ADDRESS = 11;
        public static final int STD_INET_SOCKET_ADDRESS = 12;
        protected final int _kind;
        
        protected Std(final Class<?> valueType, final int kind) {
            super(valueType);
            this._kind = kind;
        }
        
        @Override
        protected Object _deserialize(String value, final DeserializationContext ctxt) throws IOException {
            switch (this._kind) {
                case 1: {
                    return new File(value);
                }
                case 2: {
                    return new URL(value);
                }
                case 3: {
                    return URI.create(value);
                }
                case 4: {
                    try {
                        return ctxt.findClass(value);
                    }
                    catch (Exception e) {
                        throw ctxt.instantiationException(this._valueClass, ClassUtil.getRootCause(e));
                    }
                }
                case 5: {
                    return ctxt.getTypeFactory().constructFromCanonical(value);
                }
                case 6: {
                    return Currency.getInstance(value);
                }
                case 7: {
                    return Pattern.compile(value);
                }
                case 8: {
                    int ix = value.indexOf(95);
                    if (ix < 0) {
                        return new Locale(value);
                    }
                    final String first = value.substring(0, ix);
                    value = value.substring(ix + 1);
                    ix = value.indexOf(95);
                    if (ix < 0) {
                        return new Locale(first, value);
                    }
                    final String second = value.substring(0, ix);
                    return new Locale(first, second, value.substring(ix + 1));
                }
                case 9: {
                    return Charset.forName(value);
                }
                case 10: {
                    return TimeZone.getTimeZone(value);
                }
                case 11: {
                    return InetAddress.getByName(value);
                }
                case 12: {
                    if (value.startsWith("[")) {
                        final int i = value.lastIndexOf(93);
                        if (i == -1) {
                            throw new InvalidFormatException("Bracketed IPv6 address must contain closing bracket", value, InetSocketAddress.class);
                        }
                        final int j = value.indexOf(58, i);
                        final int port = (j > -1) ? Integer.parseInt(value.substring(j + 1)) : 0;
                        return new InetSocketAddress(value.substring(0, i + 1), port);
                    }
                    else {
                        final int ix = value.indexOf(58);
                        if (ix >= 0 && value.indexOf(58, ix + 1) < 0) {
                            final int port2 = Integer.parseInt(value.substring(ix + 1));
                            return new InetSocketAddress(value.substring(0, ix), port2);
                        }
                        return new InetSocketAddress(value, 0);
                    }
                    break;
                }
                default: {
                    throw new IllegalArgumentException();
                }
            }
        }
        
        @Override
        protected Object _deserializeFromEmptyString() throws IOException {
            if (this._kind == 3) {
                return URI.create("");
            }
            return super._deserializeFromEmptyString();
        }
    }
}
