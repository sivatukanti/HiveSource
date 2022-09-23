// 
// Decompiled by Procyon v0.5.36
// 

package com.google.inject.servlet;

import com.google.inject.internal.util.$ToStringBuilder;
import java.util.Map;
import javax.servlet.Filter;

class InstanceFilterBindingImpl extends AbstractServletModuleBinding<Filter> implements InstanceFilterBinding
{
    InstanceFilterBindingImpl(final Map<String, String> initParams, final String pattern, final Filter target, final UriPatternMatcher patternMatcher) {
        super(initParams, pattern, target, patternMatcher);
    }
    
    public Filter getFilterInstance() {
        return this.getTarget();
    }
    
    @Override
    public String toString() {
        return new $ToStringBuilder(InstanceFilterBinding.class).add("pattern", this.getPattern()).add("initParams", this.getInitParams()).add("uriPatternType", this.getUriPatternType()).add("filterInstance", this.getFilterInstance()).toString();
    }
}
