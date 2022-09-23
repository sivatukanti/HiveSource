// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.api.container.filter;

import java.util.Iterator;
import com.sun.jersey.api.representation.Form;
import javax.ws.rs.core.UriBuilder;
import java.util.List;
import java.util.Map;
import com.sun.jersey.core.header.MediaTypes;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import com.sun.jersey.spi.container.ContainerRequest;
import javax.ws.rs.core.MultivaluedMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.ArrayList;
import javax.ws.rs.core.Context;
import com.sun.jersey.api.core.ResourceConfig;
import com.sun.jersey.spi.container.ContainerRequestFilter;

public class PostReplaceFilter implements ContainerRequestFilter
{
    public static final String PROPERTY_POST_REPLACE_FILTER_CONFIG = "com.sun.jersey.api.container.filter.PostReplaceFilterConfig";
    private final int config;
    
    public PostReplaceFilter(@Context final ResourceConfig rc) {
        this(configStringToConfig((String)rc.getProperty("com.sun.jersey.api.container.filter.PostReplaceFilterConfig")));
    }
    
    public PostReplaceFilter(final ConfigFlag... configFlags) {
        int c = 0;
        for (final ConfigFlag cf : configFlags) {
            c |= cf.getFlag();
        }
        if (c == 0) {
            c = 3;
        }
        this.config = c;
    }
    
    private static ConfigFlag[] configStringToConfig(final String configString) {
        if (configString == null) {
            return new ConfigFlag[0];
        }
        final String[] parts = configString.toUpperCase().split(",");
        final ArrayList<ConfigFlag> result = new ArrayList<ConfigFlag>(parts.length);
        for (String part : parts) {
            part = part.trim();
            if (part.length() > 0) {
                try {
                    result.add(ConfigFlag.valueOf(part));
                }
                catch (IllegalArgumentException e) {
                    Logger.getLogger(PostReplaceFilter.class.getName()).log(Level.WARNING, "Invalid config flag for com.sun.jersey.api.container.filter.PostReplaceFilterConfig property: {0}", part.trim());
                }
            }
        }
        return result.toArray(new ConfigFlag[result.size()]);
    }
    
    private String getParamValue(final ConfigFlag configFlag, final MultivaluedMap<String, String> paramsMap, final String paramName) {
        String value = configFlag.isPresentIn(this.config) ? paramsMap.getFirst(paramName) : null;
        if (value == null) {
            return null;
        }
        value = value.trim();
        return (value.length() == 0) ? null : value.toUpperCase();
    }
    
    @Override
    public ContainerRequest filter(final ContainerRequest request) {
        if (!request.getMethod().equalsIgnoreCase("POST")) {
            return request;
        }
        final String header = this.getParamValue(ConfigFlag.HEADER, request.getRequestHeaders(), "X-HTTP-Method-Override");
        final String query = this.getParamValue(ConfigFlag.QUERY, request.getQueryParameters(), "_method");
        String override;
        if (header == null) {
            override = query;
        }
        else {
            override = header;
            if (query != null && !query.equals(header)) {
                throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST).type("text/plain").entity("Inconsistent POST override.\nX-HTTP-Method-Override: " + header + "\n_method: " + query).build());
            }
        }
        if (override == null) {
            return request;
        }
        request.setMethod(override);
        if (override.equals("GET") && MediaTypes.typeEquals(MediaType.APPLICATION_FORM_URLENCODED_TYPE, request.getMediaType())) {
            final UriBuilder ub = request.getRequestUriBuilder();
            final Form f = request.getFormParameters();
            for (final Map.Entry<String, List<String>> param : f.entrySet()) {
                ub.queryParam(param.getKey(), param.getValue().toArray());
            }
            request.setUris(request.getBaseUri(), ub.build(new Object[0]));
        }
        return request;
    }
    
    public enum ConfigFlag
    {
        HEADER(1), 
        QUERY(2);
        
        private final int flag;
        
        private ConfigFlag(final int flag) {
            this.flag = flag;
        }
        
        public int getFlag() {
            return this.flag;
        }
        
        public boolean isPresentIn(final int config) {
            return (config & this.flag) == this.flag;
        }
    }
}
