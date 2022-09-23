// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.server.impl.cdi;

import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;
import javax.ws.rs.DefaultValue;
import java.lang.reflect.Type;
import java.lang.annotation.Annotation;

public class DiscoveredParameter
{
    private Annotation annotation;
    private Type type;
    private DefaultValue defaultValue;
    private boolean encoded;
    
    public DiscoveredParameter(final Annotation annotation, final Type type, final DefaultValue defaultValue, final boolean encoded) {
        this.annotation = annotation;
        this.type = type;
        this.defaultValue = defaultValue;
        this.encoded = encoded;
    }
    
    public Annotation getAnnotation() {
        return this.annotation;
    }
    
    public Type getType() {
        return this.type;
    }
    
    public DefaultValue getDefaultValue() {
        return this.defaultValue;
    }
    
    public boolean isEncoded() {
        return this.encoded;
    }
    
    public String getValue() {
        try {
            final Method valueMethod = this.annotation.annotationType().getDeclaredMethod("value", (Class<?>[])new Class[0]);
            final String name = (String)valueMethod.invoke(this.annotation, new Object[0]);
            return name;
        }
        catch (NoSuchMethodException e3) {
            return null;
        }
        catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        catch (InvocationTargetException e2) {
            throw new RuntimeException(e2);
        }
    }
    
    @Override
    public int hashCode() {
        int result = 17;
        result = 31 * result + ((this.annotation == null) ? 0 : this.annotation.hashCode());
        result = 31 * result + ((this.type == null) ? 0 : this.type.hashCode());
        result = 31 * result + ((this.defaultValue == null) ? 0 : this.defaultValue.hashCode());
        result = 31 * result + (this.encoded ? 7 : 11);
        return result;
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (obj.getClass() != this.getClass()) {
            return false;
        }
        final DiscoveredParameter that = (DiscoveredParameter)obj;
        if (this.annotation == null) {
            if (that.annotation != null) {
                return false;
            }
        }
        else if (!this.annotation.equals(that.annotation)) {
            return false;
        }
        if (this.type == null) {
            if (that.type != null) {
                return false;
            }
        }
        else if (!this.type.equals(that.type)) {
            return false;
        }
        if (this.defaultValue == null) {
            if (that.defaultValue != null) {
                return false;
            }
        }
        else if (!this.defaultValue.equals(that.defaultValue)) {
            return false;
        }
        if (this.encoded == that.encoded) {
            return true;
        }
        return false;
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("DiscoveredParameter(");
        sb.append(this.annotation);
        sb.append(',');
        sb.append(this.type);
        sb.append(',');
        sb.append(this.defaultValue);
        sb.append(',');
        sb.append(this.encoded);
        sb.append(')');
        return sb.toString();
    }
}
