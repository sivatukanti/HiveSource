// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.htrace.shaded.fasterxml.jackson.databind.exc;

import org.apache.htrace.shaded.fasterxml.jackson.core.JsonParser;
import java.util.Collection;
import org.apache.htrace.shaded.fasterxml.jackson.core.JsonLocation;

public class UnrecognizedPropertyException extends PropertyBindingException
{
    private static final long serialVersionUID = 1L;
    
    public UnrecognizedPropertyException(final String msg, final JsonLocation loc, final Class<?> referringClass, final String propName, final Collection<Object> propertyIds) {
        super(msg, loc, referringClass, propName, propertyIds);
    }
    
    public static UnrecognizedPropertyException from(final JsonParser jp, final Object fromObjectOrClass, final String propertyName, final Collection<Object> propertyIds) {
        if (fromObjectOrClass == null) {
            throw new IllegalArgumentException();
        }
        Class<?> ref;
        if (fromObjectOrClass instanceof Class) {
            ref = (Class<?>)fromObjectOrClass;
        }
        else {
            ref = fromObjectOrClass.getClass();
        }
        final String msg = "Unrecognized field \"" + propertyName + "\" (class " + ref.getName() + "), not marked as ignorable";
        final UnrecognizedPropertyException e = new UnrecognizedPropertyException(msg, jp.getCurrentLocation(), ref, propertyName, propertyIds);
        e.prependPath(fromObjectOrClass, propertyName);
        return e;
    }
    
    @Deprecated
    public String getUnrecognizedPropertyName() {
        return this.getPropertyName();
    }
}
