// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.spi.container.servlet;

import javax.servlet.ServletException;
import com.sun.jersey.api.core.ResourceConfig;
import java.util.Map;
import javax.servlet.ServletContext;
import java.util.Enumeration;

public class WebServletConfig implements WebConfig
{
    private final ServletContainer servlet;
    
    public WebServletConfig(final ServletContainer servlet) {
        this.servlet = servlet;
    }
    
    @Override
    public ConfigType getConfigType() {
        return ConfigType.ServletConfig;
    }
    
    @Override
    public String getName() {
        return this.servlet.getServletName();
    }
    
    @Override
    public String getInitParameter(final String name) {
        return this.servlet.getInitParameter(name);
    }
    
    @Override
    public Enumeration getInitParameterNames() {
        return this.servlet.getInitParameterNames();
    }
    
    @Override
    public ServletContext getServletContext() {
        return this.servlet.getServletContext();
    }
    
    @Override
    public ResourceConfig getDefaultResourceConfig(final Map<String, Object> props) throws ServletException {
        return this.servlet.getDefaultResourceConfig(props, this.servlet.getServletConfig());
    }
}
