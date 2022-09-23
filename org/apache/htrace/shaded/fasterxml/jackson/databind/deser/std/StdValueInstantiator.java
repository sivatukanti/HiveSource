// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.htrace.shaded.fasterxml.jackson.databind.deser.std;

import org.apache.htrace.shaded.fasterxml.jackson.databind.JsonMappingException;
import org.apache.htrace.shaded.fasterxml.jackson.databind.BeanProperty;
import org.apache.htrace.shaded.fasterxml.jackson.core.JsonProcessingException;
import java.io.IOException;
import org.apache.htrace.shaded.fasterxml.jackson.databind.DeserializationContext;
import org.apache.htrace.shaded.fasterxml.jackson.databind.deser.SettableBeanProperty;
import org.apache.htrace.shaded.fasterxml.jackson.databind.DeserializationConfig;
import org.apache.htrace.shaded.fasterxml.jackson.databind.introspect.AnnotatedParameter;
import org.apache.htrace.shaded.fasterxml.jackson.databind.JavaType;
import org.apache.htrace.shaded.fasterxml.jackson.databind.deser.CreatorProperty;
import org.apache.htrace.shaded.fasterxml.jackson.databind.introspect.AnnotatedWithParams;
import org.apache.htrace.shaded.fasterxml.jackson.databind.annotation.JacksonStdImpl;
import java.io.Serializable;
import org.apache.htrace.shaded.fasterxml.jackson.databind.deser.ValueInstantiator;

@JacksonStdImpl
public class StdValueInstantiator extends ValueInstantiator implements Serializable
{
    private static final long serialVersionUID = 1L;
    protected final String _valueTypeDesc;
    protected AnnotatedWithParams _defaultCreator;
    protected AnnotatedWithParams _withArgsCreator;
    protected CreatorProperty[] _constructorArguments;
    protected JavaType _delegateType;
    protected AnnotatedWithParams _delegateCreator;
    protected CreatorProperty[] _delegateArguments;
    protected AnnotatedWithParams _fromStringCreator;
    protected AnnotatedWithParams _fromIntCreator;
    protected AnnotatedWithParams _fromLongCreator;
    protected AnnotatedWithParams _fromDoubleCreator;
    protected AnnotatedWithParams _fromBooleanCreator;
    protected AnnotatedParameter _incompleteParameter;
    
    public StdValueInstantiator(final DeserializationConfig config, final Class<?> valueType) {
        this._valueTypeDesc = ((valueType == null) ? "UNKNOWN TYPE" : valueType.getName());
    }
    
    public StdValueInstantiator(final DeserializationConfig config, final JavaType valueType) {
        this._valueTypeDesc = ((valueType == null) ? "UNKNOWN TYPE" : valueType.toString());
    }
    
    protected StdValueInstantiator(final StdValueInstantiator src) {
        this._valueTypeDesc = src._valueTypeDesc;
        this._defaultCreator = src._defaultCreator;
        this._constructorArguments = src._constructorArguments;
        this._withArgsCreator = src._withArgsCreator;
        this._delegateType = src._delegateType;
        this._delegateCreator = src._delegateCreator;
        this._delegateArguments = src._delegateArguments;
        this._fromStringCreator = src._fromStringCreator;
        this._fromIntCreator = src._fromIntCreator;
        this._fromLongCreator = src._fromLongCreator;
        this._fromDoubleCreator = src._fromDoubleCreator;
        this._fromBooleanCreator = src._fromBooleanCreator;
    }
    
    public void configureFromObjectSettings(final AnnotatedWithParams defaultCreator, final AnnotatedWithParams delegateCreator, final JavaType delegateType, final CreatorProperty[] delegateArgs, final AnnotatedWithParams withArgsCreator, final CreatorProperty[] constructorArgs) {
        this._defaultCreator = defaultCreator;
        this._delegateCreator = delegateCreator;
        this._delegateType = delegateType;
        this._delegateArguments = delegateArgs;
        this._withArgsCreator = withArgsCreator;
        this._constructorArguments = constructorArgs;
    }
    
    public void configureFromStringCreator(final AnnotatedWithParams creator) {
        this._fromStringCreator = creator;
    }
    
    public void configureFromIntCreator(final AnnotatedWithParams creator) {
        this._fromIntCreator = creator;
    }
    
    public void configureFromLongCreator(final AnnotatedWithParams creator) {
        this._fromLongCreator = creator;
    }
    
    public void configureFromDoubleCreator(final AnnotatedWithParams creator) {
        this._fromDoubleCreator = creator;
    }
    
    public void configureFromBooleanCreator(final AnnotatedWithParams creator) {
        this._fromBooleanCreator = creator;
    }
    
    public void configureIncompleteParameter(final AnnotatedParameter parameter) {
        this._incompleteParameter = parameter;
    }
    
    @Override
    public String getValueTypeDesc() {
        return this._valueTypeDesc;
    }
    
    @Override
    public boolean canCreateFromString() {
        return this._fromStringCreator != null;
    }
    
    @Override
    public boolean canCreateFromInt() {
        return this._fromIntCreator != null;
    }
    
    @Override
    public boolean canCreateFromLong() {
        return this._fromLongCreator != null;
    }
    
    @Override
    public boolean canCreateFromDouble() {
        return this._fromDoubleCreator != null;
    }
    
    @Override
    public boolean canCreateFromBoolean() {
        return this._fromBooleanCreator != null;
    }
    
    @Override
    public boolean canCreateUsingDefault() {
        return this._defaultCreator != null;
    }
    
    @Override
    public boolean canCreateUsingDelegate() {
        return this._delegateType != null;
    }
    
    @Override
    public boolean canCreateFromObjectWith() {
        return this._withArgsCreator != null;
    }
    
    @Override
    public JavaType getDelegateType(final DeserializationConfig config) {
        return this._delegateType;
    }
    
    @Override
    public SettableBeanProperty[] getFromObjectArguments(final DeserializationConfig config) {
        return this._constructorArguments;
    }
    
    @Override
    public Object createUsingDefault(final DeserializationContext ctxt) throws IOException, JsonProcessingException {
        if (this._defaultCreator == null) {
            throw new IllegalStateException("No default constructor for " + this.getValueTypeDesc());
        }
        try {
            return this._defaultCreator.call();
        }
        catch (ExceptionInInitializerError e) {
            throw this.wrapException(e);
        }
        catch (Exception e2) {
            throw this.wrapException(e2);
        }
    }
    
    @Override
    public Object createFromObjectWith(final DeserializationContext ctxt, final Object[] args) throws IOException, JsonProcessingException {
        if (this._withArgsCreator == null) {
            throw new IllegalStateException("No with-args constructor for " + this.getValueTypeDesc());
        }
        try {
            return this._withArgsCreator.call(args);
        }
        catch (ExceptionInInitializerError e) {
            throw this.wrapException(e);
        }
        catch (Exception e2) {
            throw this.wrapException(e2);
        }
    }
    
    @Override
    public Object createUsingDelegate(final DeserializationContext ctxt, final Object delegate) throws IOException, JsonProcessingException {
        if (this._delegateCreator == null) {
            throw new IllegalStateException("No delegate constructor for " + this.getValueTypeDesc());
        }
        try {
            if (this._delegateArguments == null) {
                return this._delegateCreator.call1(delegate);
            }
            final int len = this._delegateArguments.length;
            final Object[] args = new Object[len];
            for (int i = 0; i < len; ++i) {
                final CreatorProperty prop = this._delegateArguments[i];
                if (prop == null) {
                    args[i] = delegate;
                }
                else {
                    args[i] = ctxt.findInjectableValue(prop.getInjectableValueId(), prop, null);
                }
            }
            return this._delegateCreator.call(args);
        }
        catch (ExceptionInInitializerError e) {
            throw this.wrapException(e);
        }
        catch (Exception e2) {
            throw this.wrapException(e2);
        }
    }
    
    @Override
    public Object createFromString(final DeserializationContext ctxt, final String value) throws IOException, JsonProcessingException {
        if (this._fromStringCreator != null) {
            try {
                return this._fromStringCreator.call1(value);
            }
            catch (Exception e) {
                throw this.wrapException(e);
            }
            catch (ExceptionInInitializerError e2) {
                throw this.wrapException(e2);
            }
        }
        return this._createFromStringFallbacks(ctxt, value);
    }
    
    @Override
    public Object createFromInt(final DeserializationContext ctxt, final int value) throws IOException, JsonProcessingException {
        try {
            if (this._fromIntCreator != null) {
                return this._fromIntCreator.call1(value);
            }
            if (this._fromLongCreator != null) {
                return this._fromLongCreator.call1((long)value);
            }
        }
        catch (Exception e) {
            throw this.wrapException(e);
        }
        catch (ExceptionInInitializerError e2) {
            throw this.wrapException(e2);
        }
        throw ctxt.mappingException("Can not instantiate value of type " + this.getValueTypeDesc() + " from Integral number (" + value + "); no single-int-arg constructor/factory method");
    }
    
    @Override
    public Object createFromLong(final DeserializationContext ctxt, final long value) throws IOException, JsonProcessingException {
        try {
            if (this._fromLongCreator != null) {
                return this._fromLongCreator.call1(value);
            }
        }
        catch (Exception e) {
            throw this.wrapException(e);
        }
        catch (ExceptionInInitializerError e2) {
            throw this.wrapException(e2);
        }
        throw ctxt.mappingException("Can not instantiate value of type " + this.getValueTypeDesc() + " from Long integral number (" + value + "); no single-long-arg constructor/factory method");
    }
    
    @Override
    public Object createFromDouble(final DeserializationContext ctxt, final double value) throws IOException, JsonProcessingException {
        try {
            if (this._fromDoubleCreator != null) {
                return this._fromDoubleCreator.call1(value);
            }
        }
        catch (Exception e) {
            throw this.wrapException(e);
        }
        catch (ExceptionInInitializerError e2) {
            throw this.wrapException(e2);
        }
        throw ctxt.mappingException("Can not instantiate value of type " + this.getValueTypeDesc() + " from Floating-point number (" + value + "); no one-double/Double-arg constructor/factory method");
    }
    
    @Override
    public Object createFromBoolean(final DeserializationContext ctxt, final boolean value) throws IOException, JsonProcessingException {
        try {
            if (this._fromBooleanCreator != null) {
                return this._fromBooleanCreator.call1(value);
            }
        }
        catch (Exception e) {
            throw this.wrapException(e);
        }
        catch (ExceptionInInitializerError e2) {
            throw this.wrapException(e2);
        }
        throw ctxt.mappingException("Can not instantiate value of type " + this.getValueTypeDesc() + " from Boolean value (" + value + "); no single-boolean/Boolean-arg constructor/factory method");
    }
    
    @Override
    public AnnotatedWithParams getDelegateCreator() {
        return this._delegateCreator;
    }
    
    @Override
    public AnnotatedWithParams getDefaultCreator() {
        return this._defaultCreator;
    }
    
    @Override
    public AnnotatedWithParams getWithArgsCreator() {
        return this._withArgsCreator;
    }
    
    @Override
    public AnnotatedParameter getIncompleteParameter() {
        return this._incompleteParameter;
    }
    
    protected JsonMappingException wrapException(Throwable t) {
        while (t.getCause() != null) {
            t = t.getCause();
        }
        if (t instanceof JsonMappingException) {
            return (JsonMappingException)t;
        }
        return new JsonMappingException("Instantiation of " + this.getValueTypeDesc() + " value failed: " + t.getMessage(), t);
    }
}
