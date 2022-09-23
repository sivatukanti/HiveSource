// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.server.wadl;

import javax.xml.bind.JAXBContext;
import com.sun.research.ws.wadl.Application;
import com.sun.jersey.api.model.AbstractResource;
import javax.ws.rs.core.UriInfo;

public interface WadlApplicationContext
{
    ApplicationDescription getApplication(final UriInfo p0);
    
    Application getApplication(final UriInfo p0, final AbstractResource p1, final String p2);
    
    JAXBContext getJAXBContext();
    
    void setWadlGenerationEnabled(final boolean p0);
    
    boolean isWadlGenerationEnabled();
}
