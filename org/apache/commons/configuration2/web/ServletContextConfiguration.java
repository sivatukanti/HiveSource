// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.configuration2.web;

import java.util.Enumeration;
import java.util.Collections;
import java.util.Iterator;
import javax.servlet.Servlet;
import javax.servlet.ServletContext;

public class ServletContextConfiguration extends BaseWebConfiguration
{
    protected ServletContext context;
    
    public ServletContextConfiguration(final Servlet servlet) {
        this.context = servlet.getServletConfig().getServletContext();
    }
    
    public ServletContextConfiguration(final ServletContext context) {
        this.context = context;
    }
    
    @Override
    protected Object getPropertyInternal(final String key) {
        return this.handleDelimiters(this.context.getInitParameter(key));
    }
    
    @Override
    protected Iterator<String> getKeysInternal() {
        final Enumeration<String> en = this.context.getInitParameterNames();
        return Collections.list(en).iterator();
    }
}
