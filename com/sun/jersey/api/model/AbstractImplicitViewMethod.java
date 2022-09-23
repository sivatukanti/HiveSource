// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.api.model;

import java.lang.reflect.Method;

public class AbstractImplicitViewMethod extends AbstractMethod
{
    public AbstractImplicitViewMethod(final AbstractResource resource) {
        super(resource, null, resource.getAnnotations());
    }
    
    @Override
    public String toString() {
        return "AbstractImplicitViewMethod(" + this.getResource().getResourceClass().getSimpleName() + ")";
    }
}
