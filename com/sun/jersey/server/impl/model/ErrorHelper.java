// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.server.impl.model;

import javax.ws.rs.HttpMethod;
import java.lang.reflect.Method;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import com.sun.jersey.impl.ImplMessages;
import com.sun.jersey.api.container.ContainerException;

public final class ErrorHelper
{
    public static ContainerException objectNotAWebResource(final Class resourceClass) {
        return new ContainerException(ImplMessages.OBJECT_NOT_A_WEB_RESOURCE(resourceClass.getName()));
    }
    
    public static ContainerException badClassConsumes(final Exception e, final Class resourceClass, final Consumes c) {
        return new ContainerException(ImplMessages.BAD_CLASS_CONSUMEMIME(resourceClass, c.value()), e);
    }
    
    public static ContainerException badClassProduces(final Exception e, final Class resourceClass, final Produces p) {
        return new ContainerException(ImplMessages.BAD_CLASS_PRODUCEMIME(resourceClass, p.value()), e);
    }
    
    public static ContainerException badMethodHttpMethod(final Class resourceClass, final Method m, final HttpMethod hm) {
        return new ContainerException(ImplMessages.BAD_METHOD_HTTPMETHOD(resourceClass, hm.value(), m.toString()));
    }
    
    public static ContainerException badMethodConsumes(final Exception e, final Class resourceClass, final Method m, final Consumes c) {
        return new ContainerException(ImplMessages.BAD_METHOD_CONSUMEMIME(resourceClass, c.value(), m.toString()), e);
    }
    
    public static ContainerException badMethodProduces(final Exception e, final Class resourceClass, final Method m, final Produces p) {
        return new ContainerException(ImplMessages.BAD_METHOD_PRODUCEMIME(resourceClass, p.value(), m.toString()), e);
    }
}
