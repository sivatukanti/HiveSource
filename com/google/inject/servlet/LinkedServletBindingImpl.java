// 
// Decompiled by Procyon v0.5.36
// 

package com.google.inject.servlet;

import com.google.inject.internal.util.$ToStringBuilder;
import java.util.Map;
import javax.servlet.http.HttpServlet;
import com.google.inject.Key;

class LinkedServletBindingImpl extends AbstractServletModuleBinding<Key<? extends HttpServlet>> implements LinkedServletBinding
{
    LinkedServletBindingImpl(final Map<String, String> initParams, final String pattern, final Key<? extends HttpServlet> target, final UriPatternMatcher patternMatcher) {
        super(initParams, pattern, target, patternMatcher);
    }
    
    public Key<? extends HttpServlet> getLinkedKey() {
        return this.getTarget();
    }
    
    @Override
    public String toString() {
        return new $ToStringBuilder(LinkedServletBinding.class).add("pattern", this.getPattern()).add("initParams", this.getInitParams()).add("uriPatternType", this.getUriPatternType()).add("linkedServletKey", this.getLinkedKey()).toString();
    }
}
