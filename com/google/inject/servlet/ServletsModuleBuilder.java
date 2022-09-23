// 
// Decompiled by Procyon v0.5.36
// 

package com.google.inject.servlet;

import java.util.Map;
import java.util.HashMap;
import javax.servlet.http.HttpServlet;
import java.util.Set;
import java.util.Iterator;
import com.google.inject.Provider;
import com.google.inject.Key;
import com.google.inject.internal.UniqueAnnotations;
import com.google.inject.internal.util.$Sets;
import com.google.inject.internal.util.$Lists;
import java.util.List;
import com.google.inject.AbstractModule;

class ServletsModuleBuilder extends AbstractModule
{
    private final List<ServletDefinition> servletDefinitions;
    private final List<ServletInstanceBindingEntry> servletInstanceEntries;
    
    ServletsModuleBuilder() {
        this.servletDefinitions = (List<ServletDefinition>)$Lists.newArrayList();
        this.servletInstanceEntries = (List<ServletInstanceBindingEntry>)$Lists.newArrayList();
    }
    
    @Override
    protected void configure() {
        for (final ServletInstanceBindingEntry entry : this.servletInstanceEntries) {
            this.bind(entry.key).toInstance(entry.servlet);
        }
        final Set<String> servletUris = (Set<String>)$Sets.newHashSet();
        for (final ServletDefinition servletDefinition : this.servletDefinitions) {
            if (servletUris.contains(servletDefinition.getPattern())) {
                this.addError("More than one servlet was mapped to the same URI pattern: " + servletDefinition.getPattern(), new Object[0]);
            }
            else {
                this.bind((Key<Object>)Key.get((Class<T>)ServletDefinition.class, UniqueAnnotations.create())).toProvider(servletDefinition);
                servletUris.add(servletDefinition.getPattern());
            }
        }
    }
    
    public ServletModule.ServletKeyBindingBuilder serve(final List<String> urlPatterns) {
        return new ServletKeyBindingBuilderImpl((List)urlPatterns, UriPatternType.SERVLET);
    }
    
    public ServletModule.ServletKeyBindingBuilder serveRegex(final List<String> regexes) {
        return new ServletKeyBindingBuilderImpl((List)regexes, UriPatternType.REGEX);
    }
    
    private static class ServletInstanceBindingEntry
    {
        final Key<HttpServlet> key;
        final HttpServlet servlet;
        
        ServletInstanceBindingEntry(final Key<HttpServlet> key, final HttpServlet servlet) {
            this.key = key;
            this.servlet = servlet;
        }
    }
    
    class ServletKeyBindingBuilderImpl implements ServletModule.ServletKeyBindingBuilder
    {
        private final List<String> uriPatterns;
        private final UriPatternType uriPatternType;
        
        private ServletKeyBindingBuilderImpl(final List<String> uriPatterns, final UriPatternType uriPatternType) {
            this.uriPatterns = uriPatterns;
            this.uriPatternType = uriPatternType;
        }
        
        public void with(final Class<? extends HttpServlet> servletKey) {
            this.with(Key.get(servletKey));
        }
        
        public void with(final Key<? extends HttpServlet> servletKey) {
            this.with(servletKey, new HashMap<String, String>());
        }
        
        public void with(final HttpServlet servlet) {
            this.with(servlet, new HashMap<String, String>());
        }
        
        public void with(final Class<? extends HttpServlet> servletKey, final Map<String, String> initParams) {
            this.with(Key.get(servletKey), initParams);
        }
        
        public void with(final Key<? extends HttpServlet> servletKey, final Map<String, String> initParams) {
            this.with(servletKey, initParams, null);
        }
        
        private void with(final Key<? extends HttpServlet> servletKey, final Map<String, String> initParams, final HttpServlet servletInstance) {
            for (final String pattern : this.uriPatterns) {
                ServletsModuleBuilder.this.servletDefinitions.add(new ServletDefinition(pattern, servletKey, UriPatternType.get(this.uriPatternType, pattern), initParams, servletInstance));
            }
        }
        
        public void with(final HttpServlet servlet, final Map<String, String> initParams) {
            final Key<HttpServlet> servletKey = Key.get(HttpServlet.class, UniqueAnnotations.create());
            ServletsModuleBuilder.this.servletInstanceEntries.add(new ServletInstanceBindingEntry(servletKey, servlet));
            this.with(servletKey, initParams, servlet);
        }
    }
}
