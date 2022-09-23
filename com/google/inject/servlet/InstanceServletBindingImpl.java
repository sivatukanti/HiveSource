// 
// Decompiled by Procyon v0.5.36
// 

package com.google.inject.servlet;

import com.google.inject.internal.util.$ToStringBuilder;
import java.util.Map;
import javax.servlet.http.HttpServlet;

class InstanceServletBindingImpl extends AbstractServletModuleBinding<HttpServlet> implements InstanceServletBinding
{
    InstanceServletBindingImpl(final Map<String, String> initParams, final String pattern, final HttpServlet target, final UriPatternMatcher patternMatcher) {
        super(initParams, pattern, target, patternMatcher);
    }
    
    public HttpServlet getServletInstance() {
        return this.getTarget();
    }
    
    @Override
    public String toString() {
        return new $ToStringBuilder(InstanceServletBinding.class).add("pattern", this.getPattern()).add("initParams", this.getInitParams()).add("uriPatternType", this.getUriPatternType()).add("servletInstance", this.getServletInstance()).toString();
    }
}
