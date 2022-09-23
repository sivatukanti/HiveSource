// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.server.impl.uri.rules;

import com.sun.jersey.spi.uri.rules.UriRuleContext;
import com.sun.jersey.api.uri.UriTemplate;
import com.sun.jersey.spi.uri.rules.UriRule;

public abstract class BaseRule implements UriRule
{
    private final UriTemplate template;
    
    public BaseRule(final UriTemplate template) {
        assert template != null;
        this.template = template;
    }
    
    protected final void pushMatch(final UriRuleContext context) {
        context.pushMatch(this.template, this.template.getTemplateVariables());
    }
    
    protected final UriTemplate getTemplate() {
        return this.template;
    }
}
