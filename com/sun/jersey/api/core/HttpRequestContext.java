// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.api.core;

import com.sun.jersey.api.representation.Form;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import javax.ws.rs.WebApplicationException;
import com.sun.jersey.core.header.QualitySourceMediaType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.PathSegment;
import java.util.List;
import javax.ws.rs.core.UriBuilder;
import java.net.URI;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.HttpHeaders;

public interface HttpRequestContext extends HttpHeaders, Request, SecurityContext, Traceable
{
    URI getBaseUri();
    
    UriBuilder getBaseUriBuilder();
    
    URI getRequestUri();
    
    UriBuilder getRequestUriBuilder();
    
    URI getAbsolutePath();
    
    UriBuilder getAbsolutePathBuilder();
    
    String getPath();
    
    String getPath(final boolean p0);
    
    List<PathSegment> getPathSegments();
    
    List<PathSegment> getPathSegments(final boolean p0);
    
    MultivaluedMap<String, String> getQueryParameters();
    
    MultivaluedMap<String, String> getQueryParameters(final boolean p0);
    
    String getHeaderValue(final String p0);
    
    @Deprecated
    MediaType getAcceptableMediaType(final List<MediaType> p0);
    
    @Deprecated
    List<MediaType> getAcceptableMediaTypes(final List<QualitySourceMediaType> p0);
    
    MultivaluedMap<String, String> getCookieNameValueMap();
    
     <T> T getEntity(final Class<T> p0) throws WebApplicationException;
    
     <T> T getEntity(final Class<T> p0, final Type p1, final Annotation[] p2) throws WebApplicationException;
    
    Form getFormParameters();
}
