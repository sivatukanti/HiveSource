// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.spi.template;

import javax.ws.rs.core.UriInfo;
import com.sun.jersey.api.view.Viewable;

public interface TemplateContext
{
    ResolvedViewable resolveViewable(final Viewable p0) throws TemplateContextException;
    
    ResolvedViewable resolveViewable(final Viewable p0, final UriInfo p1) throws TemplateContextException;
    
    ResolvedViewable resolveViewable(final Viewable p0, final Class<?> p1) throws TemplateContextException;
}
