// 
// Decompiled by Procyon v0.5.36
// 

package com.google.inject.servlet;

import java.util.Map;
import java.util.HashMap;
import javax.servlet.Filter;
import com.google.inject.Key;
import java.util.Iterator;
import com.google.inject.Provider;
import com.google.inject.internal.UniqueAnnotations;
import com.google.inject.internal.util.$Lists;
import java.util.List;
import com.google.inject.AbstractModule;

class FiltersModuleBuilder extends AbstractModule
{
    private final List<FilterDefinition> filterDefinitions;
    private final List<FilterInstanceBindingEntry> filterInstanceEntries;
    
    FiltersModuleBuilder() {
        this.filterDefinitions = (List<FilterDefinition>)$Lists.newArrayList();
        this.filterInstanceEntries = (List<FilterInstanceBindingEntry>)$Lists.newArrayList();
    }
    
    @Override
    protected void configure() {
        for (final FilterInstanceBindingEntry entry : this.filterInstanceEntries) {
            this.bind(entry.key).toInstance(entry.filter);
        }
        for (final FilterDefinition fd : this.filterDefinitions) {
            this.bind(FilterDefinition.class).annotatedWith(UniqueAnnotations.create()).toProvider(fd);
        }
    }
    
    public ServletModule.FilterKeyBindingBuilder filter(final List<String> patterns) {
        return new FilterKeyBindingBuilderImpl((List)patterns, UriPatternType.SERVLET);
    }
    
    public ServletModule.FilterKeyBindingBuilder filterRegex(final List<String> regexes) {
        return new FilterKeyBindingBuilderImpl((List)regexes, UriPatternType.REGEX);
    }
    
    private static class FilterInstanceBindingEntry
    {
        final Key<Filter> key;
        final Filter filter;
        
        FilterInstanceBindingEntry(final Key<Filter> key, final Filter filter) {
            this.key = key;
            this.filter = filter;
        }
    }
    
    class FilterKeyBindingBuilderImpl implements ServletModule.FilterKeyBindingBuilder
    {
        private final List<String> uriPatterns;
        private final UriPatternType uriPatternType;
        
        private FilterKeyBindingBuilderImpl(final List<String> uriPatterns, final UriPatternType uriPatternType) {
            this.uriPatterns = uriPatterns;
            this.uriPatternType = uriPatternType;
        }
        
        public void through(final Class<? extends Filter> filterKey) {
            this.through(Key.get(filterKey));
        }
        
        public void through(final Key<? extends Filter> filterKey) {
            this.through(filterKey, new HashMap<String, String>());
        }
        
        public void through(final Filter filter) {
            this.through(filter, new HashMap<String, String>());
        }
        
        public void through(final Class<? extends Filter> filterKey, final Map<String, String> initParams) {
            this.through(Key.get(filterKey), initParams);
        }
        
        public void through(final Key<? extends Filter> filterKey, final Map<String, String> initParams) {
            this.through(filterKey, initParams, null);
        }
        
        private void through(final Key<? extends Filter> filterKey, final Map<String, String> initParams, final Filter filterInstance) {
            for (final String pattern : this.uriPatterns) {
                FiltersModuleBuilder.this.filterDefinitions.add(new FilterDefinition(pattern, filterKey, UriPatternType.get(this.uriPatternType, pattern), initParams, filterInstance));
            }
        }
        
        public void through(final Filter filter, final Map<String, String> initParams) {
            final Key<Filter> filterKey = Key.get(Filter.class, UniqueAnnotations.create());
            FiltersModuleBuilder.this.filterInstanceEntries.add(new FilterInstanceBindingEntry(filterKey, filter));
            this.through(filterKey, initParams, filter);
        }
    }
}
