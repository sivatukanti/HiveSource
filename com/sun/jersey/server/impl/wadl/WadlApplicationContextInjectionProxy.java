// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.server.impl.wadl;

import javax.xml.bind.JAXBContext;
import com.sun.research.ws.wadl.Application;
import com.sun.jersey.api.model.AbstractResource;
import com.sun.jersey.server.wadl.ApplicationDescription;
import javax.ws.rs.core.UriInfo;
import com.sun.jersey.server.wadl.WadlApplicationContext;

public class WadlApplicationContextInjectionProxy implements WadlApplicationContext
{
    private WadlApplicationContext wadlApplicationContext;
    
    public void init(final WadlFactory wadlFactory) {
        this.wadlApplicationContext = wadlFactory.getWadlApplicationContext();
    }
    
    @Override
    public ApplicationDescription getApplication(final UriInfo ui) {
        return this.getWadlApplicationContext().getApplication(ui);
    }
    
    @Override
    public Application getApplication(final UriInfo info, final AbstractResource resource, final String path) {
        return this.getWadlApplicationContext().getApplication(info, resource, path);
    }
    
    @Override
    public JAXBContext getJAXBContext() {
        return this.getWadlApplicationContext().getJAXBContext();
    }
    
    @Override
    public void setWadlGenerationEnabled(final boolean wadlGenerationEnabled) {
        this.getWadlApplicationContext().setWadlGenerationEnabled(wadlGenerationEnabled);
    }
    
    @Override
    public boolean isWadlGenerationEnabled() {
        return this.getWadlApplicationContext().isWadlGenerationEnabled();
    }
    
    private WadlApplicationContext getWadlApplicationContext() {
        if (this.wadlApplicationContext == null) {
            throw new IllegalStateException("WadlApplicationContext is not yet initialized.");
        }
        return this.wadlApplicationContext;
    }
}
