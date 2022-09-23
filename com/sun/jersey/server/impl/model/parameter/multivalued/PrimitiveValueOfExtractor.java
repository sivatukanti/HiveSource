// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.server.impl.model.parameter.multivalued;

import javax.ws.rs.core.MultivaluedMap;
import com.sun.jersey.api.container.ContainerException;
import javax.ws.rs.WebApplicationException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

final class PrimitiveValueOfExtractor implements MultivaluedParameterExtractor
{
    final Method valueOf;
    final String parameter;
    final String defaultStringValue;
    final Object defaultValue;
    final Object defaultDefaultValue;
    
    public PrimitiveValueOfExtractor(final Method valueOf, final String parameter, final String defaultStringValue, final Object defaultDefaultValue) throws IllegalAccessException, InvocationTargetException {
        this.valueOf = valueOf;
        this.parameter = parameter;
        this.defaultStringValue = defaultStringValue;
        this.defaultValue = ((defaultStringValue != null) ? this.getValue(defaultStringValue) : null);
        this.defaultDefaultValue = defaultDefaultValue;
    }
    
    @Override
    public String getName() {
        return this.parameter;
    }
    
    @Override
    public String getDefaultStringValue() {
        return this.defaultStringValue;
    }
    
    private Object getValue(final String v) {
        try {
            return this.valueOf.invoke(null, v);
        }
        catch (InvocationTargetException ex) {
            final Throwable target = ex.getTargetException();
            if (target instanceof WebApplicationException) {
                throw (WebApplicationException)target;
            }
            throw new ExtractorContainerException(target);
        }
        catch (RuntimeException ex2) {
            throw new ContainerException(ex2);
        }
        catch (Exception ex3) {
            throw new ContainerException(ex3);
        }
    }
    
    @Override
    public Object extract(final MultivaluedMap<String, String> parameters) {
        final String v = parameters.getFirst(this.parameter);
        if (v != null && !v.trim().isEmpty()) {
            return this.getValue(v);
        }
        if (this.defaultValue != null) {
            return this.defaultValue;
        }
        return this.defaultDefaultValue;
    }
}
