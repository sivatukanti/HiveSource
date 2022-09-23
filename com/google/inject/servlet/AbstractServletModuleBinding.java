// 
// Decompiled by Procyon v0.5.36
// 

package com.google.inject.servlet;

import java.util.Map;

class AbstractServletModuleBinding<T> implements ServletModuleBinding
{
    private final Map<String, String> initParams;
    private final String pattern;
    private final T target;
    private final UriPatternMatcher patternMatcher;
    
    AbstractServletModuleBinding(final Map<String, String> initParams, final String pattern, final T target, final UriPatternMatcher patternMatcher) {
        this.initParams = initParams;
        this.pattern = pattern;
        this.target = target;
        this.patternMatcher = patternMatcher;
    }
    
    public Map<String, String> getInitParams() {
        return this.initParams;
    }
    
    public String getPattern() {
        return this.pattern;
    }
    
    protected T getTarget() {
        return this.target;
    }
    
    public UriPatternType getUriPatternType() {
        return this.patternMatcher.getPatternType();
    }
    
    public boolean matchesUri(final String uri) {
        return this.patternMatcher.matches(uri);
    }
}
