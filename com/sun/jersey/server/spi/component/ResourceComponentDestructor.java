// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.server.spi.component;

import java.lang.reflect.InvocationTargetException;
import java.util.Iterator;
import java.util.Collection;
import java.util.ArrayList;
import com.sun.jersey.api.model.AbstractResource;
import java.lang.reflect.Method;
import java.util.List;

public class ResourceComponentDestructor
{
    private final List<Method> preDestroys;
    
    public ResourceComponentDestructor(final AbstractResource ar) {
        (this.preDestroys = new ArrayList<Method>()).addAll(ar.getPreDestroyMethods());
    }
    
    public void destroy(final Object o) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        for (final Method preDestroy : this.preDestroys) {
            preDestroy.invoke(o, new Object[0]);
        }
    }
}
