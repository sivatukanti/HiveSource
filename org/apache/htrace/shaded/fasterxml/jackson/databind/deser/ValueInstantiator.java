// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.htrace.shaded.fasterxml.jackson.databind.deser;

import org.apache.htrace.shaded.fasterxml.jackson.core.JsonProcessingException;
import org.apache.htrace.shaded.fasterxml.jackson.databind.DeserializationFeature;
import org.apache.htrace.shaded.fasterxml.jackson.databind.introspect.AnnotatedParameter;
import org.apache.htrace.shaded.fasterxml.jackson.databind.introspect.AnnotatedWithParams;
import java.io.IOException;
import org.apache.htrace.shaded.fasterxml.jackson.databind.DeserializationContext;
import org.apache.htrace.shaded.fasterxml.jackson.databind.JavaType;
import org.apache.htrace.shaded.fasterxml.jackson.databind.DeserializationConfig;

public abstract class ValueInstantiator
{
    public abstract String getValueTypeDesc();
    
    public boolean canInstantiate() {
        return this.canCreateUsingDefault() || this.canCreateUsingDelegate() || this.canCreateFromObjectWith() || this.canCreateFromString() || this.canCreateFromInt() || this.canCreateFromLong() || this.canCreateFromDouble() || this.canCreateFromBoolean();
    }
    
    public boolean canCreateFromString() {
        return false;
    }
    
    public boolean canCreateFromInt() {
        return false;
    }
    
    public boolean canCreateFromLong() {
        return false;
    }
    
    public boolean canCreateFromDouble() {
        return false;
    }
    
    public boolean canCreateFromBoolean() {
        return false;
    }
    
    public boolean canCreateUsingDefault() {
        return this.getDefaultCreator() != null;
    }
    
    public boolean canCreateUsingDelegate() {
        return false;
    }
    
    public boolean canCreateFromObjectWith() {
        return false;
    }
    
    public SettableBeanProperty[] getFromObjectArguments(final DeserializationConfig config) {
        return null;
    }
    
    public JavaType getDelegateType(final DeserializationConfig config) {
        return null;
    }
    
    public Object createUsingDefault(final DeserializationContext ctxt) throws IOException {
        throw ctxt.mappingException("Can not instantiate value of type " + this.getValueTypeDesc() + "; no default creator found");
    }
    
    public Object createFromObjectWith(final DeserializationContext ctxt, final Object[] args) throws IOException {
        throw ctxt.mappingException("Can not instantiate value of type " + this.getValueTypeDesc() + " with arguments");
    }
    
    public Object createUsingDelegate(final DeserializationContext ctxt, final Object delegate) throws IOException {
        throw ctxt.mappingException("Can not instantiate value of type " + this.getValueTypeDesc() + " using delegate");
    }
    
    public Object createFromString(final DeserializationContext ctxt, final String value) throws IOException {
        return this._createFromStringFallbacks(ctxt, value);
    }
    
    public Object createFromInt(final DeserializationContext ctxt, final int value) throws IOException {
        throw ctxt.mappingException("Can not instantiate value of type " + this.getValueTypeDesc() + " from Integer number (" + value + ", int)");
    }
    
    public Object createFromLong(final DeserializationContext ctxt, final long value) throws IOException {
        throw ctxt.mappingException("Can not instantiate value of type " + this.getValueTypeDesc() + " from Integer number (" + value + ", long)");
    }
    
    public Object createFromDouble(final DeserializationContext ctxt, final double value) throws IOException {
        throw ctxt.mappingException("Can not instantiate value of type " + this.getValueTypeDesc() + " from Floating-point number (" + value + ", double)");
    }
    
    public Object createFromBoolean(final DeserializationContext ctxt, final boolean value) throws IOException {
        throw ctxt.mappingException("Can not instantiate value of type " + this.getValueTypeDesc() + " from Boolean value (" + value + ")");
    }
    
    public AnnotatedWithParams getDefaultCreator() {
        return null;
    }
    
    public AnnotatedWithParams getDelegateCreator() {
        return null;
    }
    
    public AnnotatedWithParams getWithArgsCreator() {
        return null;
    }
    
    public AnnotatedParameter getIncompleteParameter() {
        return null;
    }
    
    protected Object _createFromStringFallbacks(final DeserializationContext ctxt, final String value) throws IOException, JsonProcessingException {
        if (this.canCreateFromBoolean()) {
            final String str = value.trim();
            if ("true".equals(str)) {
                return this.createFromBoolean(ctxt, true);
            }
            if ("false".equals(str)) {
                return this.createFromBoolean(ctxt, false);
            }
        }
        if (value.length() == 0 && ctxt.isEnabled(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT)) {
            return null;
        }
        throw ctxt.mappingException("Can not instantiate value of type " + this.getValueTypeDesc() + " from String value ('" + value + "'); no single-String constructor/factory method");
    }
}
