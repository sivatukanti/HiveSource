// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.spi.container.servlet;

import javax.servlet.ServletException;
import com.sun.jersey.api.core.ResourceConfig;
import java.util.Map;
import javax.servlet.ServletContext;
import java.util.Enumeration;

public interface WebConfig
{
    ConfigType getConfigType();
    
    String getName();
    
    String getInitParameter(final String p0);
    
    Enumeration getInitParameterNames();
    
    ServletContext getServletContext();
    
    ResourceConfig getDefaultResourceConfig(final Map<String, Object> p0) throws ServletException;
    
    public enum ConfigType
    {
        ServletConfig, 
        FilterConfig;
    }
}
