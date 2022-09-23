// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.configuration2.web;

import java.util.Enumeration;
import java.util.Collections;
import java.util.Iterator;
import javax.servlet.FilterConfig;

public class ServletFilterConfiguration extends BaseWebConfiguration
{
    protected FilterConfig config;
    
    public ServletFilterConfiguration(final FilterConfig config) {
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
