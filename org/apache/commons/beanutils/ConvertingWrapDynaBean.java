// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.beanutils;

import java.lang.reflect.InvocationTargetException;

public class ConvertingWrapDynaBean extends WrapDynaBean
{
    public ConvertingWrapDynaBean(final Object instance) {
        super(instance);
    }
    
    @Override
    public void set(final String name, final Object value) {
        try {
            BeanUtils.copyProperty(this.instance, name, value);
        }
        catch (InvocationTargetException ite) {
            final Throwable cause = ite.getTargetException();
            throw new IllegalArgumentException("Error setting property '" + name + "' nested exception - " + cause);
        }
        catch (Throwable t) {
            final IllegalArgumentException iae = new IllegalArgumentException("Error setting property '" + name + "', exception - " + t);
            BeanUtils.initCause(iae, t);
            throw iae;
        }
    }
}
