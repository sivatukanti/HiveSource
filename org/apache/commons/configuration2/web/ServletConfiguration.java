// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.configuration2.web;

import java.util.Enumeration;
import java.util.Collections;
import java.util.Iterator;
import javax.servlet.Servlet;
import javax.servlet.ServletConfig;

public class ServletConfiguration extends BaseWebConfiguration
{
    protected ServletConfig config;
    
    public ServletConfiguration(final Servlet servlet) {
        this(servlet.getServletConfig());
    }
    
    public ServletConfiguration(final ServletConfig config) {
        this.config = config;
    }
    
    @Override
    protected Object getPropertyInternal(final String key) {
        return this.handleDelimiters(this.config.getInitParameter(key));
    }
    
    @Override
    protected Iterator<String> getKeysInternal() {
        final Enumeration<String> en = this.config.getInitParameterNames();
        return Collections.list(en).iterator();
    }
}
