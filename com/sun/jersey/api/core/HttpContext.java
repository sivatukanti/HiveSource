// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.api.core;

import java.util.Map;

public interface HttpContext extends Traceable
{
    ExtendedUriInfo getUriInfo();
    
    HttpRequestContext getRequest();
    
    HttpResponseContext getResponse();
    
    Map<String, Object> getProperties();
}
