// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.server.impl.model.method;

import javax.ws.rs.core.Response;
import com.sun.jersey.api.core.HttpContext;
import java.util.Iterator;
import com.sun.jersey.spi.dispatch.RequestDispatcher;
import javax.ws.rs.core.MediaType;
import com.sun.jersey.core.header.MediaTypes;
import com.sun.jersey.api.uri.UriTemplate;
import java.util.List;
import java.util.Map;

public final class ResourceHttpOptionsMethod extends ResourceMethod
{
    public ResourceHttpOptionsMethod(final Map<String, List<ResourceMethod>> methods) {
        super("OPTIONS", UriTemplate.EMPTY, MediaTypes.GENERAL_MEDIA_TYPE_LIST, MediaTypes.GENERAL_MEDIA_TYPE_LIST, false, new OptionsRequestDispatcher(methods));
    }
    
    @Override
    public String toString() {
        return "OPTIONS";
    }
    
    public static class OptionsRequestDispatcher implements RequestDispatcher
    {
        protected final String allow;
        
        public OptionsRequestDispatcher(final Map<String, List<ResourceMethod>> methods) {
            this.allow = this.getAllow(methods);
        }
        
        private String getAllow(final Map<String, List<ResourceMethod>> methods) {
            final StringBuilder s = new StringBuilder("OPTIONS");
            for (final String method : methods.keySet()) {
                s.append(',').append(method);
            }
            return s.toString();
        }
        
        @Override
        public void dispatch(final Object resource, final HttpContext context) {
            final Response r = Response.noContent().header("Allow", this.allow).build();
            context.getResponse().setResponse(r);
        }
    }
}
