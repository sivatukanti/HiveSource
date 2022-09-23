// 
// Decompiled by Procyon v0.5.36
// 

package com.google.inject.servlet;

import com.google.inject.internal.util.$ToStringBuilder;
import java.util.Map;
import javax.servlet.Filter;
import com.google.inject.Key;

class LinkedFilterBindingImpl extends AbstractServletModuleBinding<Key<? extends Filter>> implements LinkedFilterBinding
{
    LinkedFilterBindingImpl(final Map<String, String> initParams, final String pattern, final Key<? extends Filter> target, final UriPatternMatcher patternMatcher) {
        super(initParams, pattern, target, patternMatcher);
    }
    
    public Key<? extends Filter> getLinkedKey() {
        return this.getTarget();
    }
    
    @Override
    public String toString() {
        return new $ToStringBuilder(LinkedFilterBinding.class).add("pattern", this.getPattern()).add("initParams", this.getInitParams()).add("uriPatternType", this.getUriPatternType()).add("linkedFilterKey", this.getLinkedKey()).toString();
    }
}
