// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.api.core;

import javax.ws.rs.core.PathSegment;
import com.sun.jersey.api.uri.UriTemplate;
import java.util.regex.MatchResult;
import java.util.List;
import com.sun.jersey.api.model.AbstractResourceMethod;
import javax.ws.rs.core.UriInfo;

public interface ExtendedUriInfo extends UriInfo
{
    AbstractResourceMethod getMatchedMethod();
    
    Throwable getMappedThrowable();
    
    List<MatchResult> getMatchedResults();
    
    List<UriTemplate> getMatchedTemplates();
    
    List<PathSegment> getPathSegments(final String p0);
    
    List<PathSegment> getPathSegments(final String p0, final boolean p1);
}
