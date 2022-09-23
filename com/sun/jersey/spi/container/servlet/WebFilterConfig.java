// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.spi.container.servlet;

import javax.servlet.ServletException;
import com.sun.jersey.api.core.ResourceConfig;
import java.util.Map;
import javax.servlet.ServletContext;
import java.util.Enumeration;
import javax.servlet.FilterConfig;

public class WebFilterConfig implements WebConfig
{
    private final FilterConfig filterConfig;
    
    public WebFilterConfig(final FilterConfig filterConfig) {
        this.filterConfig = filterConfig;
    }
    
    @Override
    public ConfigType getConfigType() {
        return ConfigType.FilterConfig;
    }
    
    @Override
    public String getName() {
        return this.filterConfig.getFilterName();
    }
    
    @Override
    public String getInitParameter(final String name) {
        return this.filterConfig.getInitParameter(name);
    }
    
    @Override
    public Enumeration getInitParameterNames() {
        return this.filterConfig.getInitParameterNames();
    }
    
    @Override
    public ServletContext getServletContext() {
        return this.filterConfig.getServletContext();
    }
    
    @Override
    public ResourceConfig getDefaultResourceConfig(final Map<String, Object> props) throws ServletException {
        return null;
    }
}
