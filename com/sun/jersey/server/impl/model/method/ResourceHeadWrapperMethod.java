// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.server.impl.model.method;

import com.sun.jersey.api.container.ContainerException;

public final class ResourceHeadWrapperMethod extends ResourceMethod
{
    private final ResourceMethod m;
    
    public ResourceHeadWrapperMethod(final ResourceMethod m) {
        super("HEAD", m.getTemplate(), m.getConsumes(), m.getProduces(), m.isProducesDeclared(), m.getDispatcher(), m.getRequestFilters(), m.getResponseFilters());
        if (!m.getHttpMethod().equals("GET")) {
            throw new ContainerException("");
        }
        this.m = m;
    }
    
    @Override
    public String toString() {
        return this.m.toString();
    }
}
