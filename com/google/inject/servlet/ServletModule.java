// 
// Decompiled by Procyon v0.5.36
// 

package com.google.inject.servlet;

import javax.servlet.Filter;
import java.util.Map;
import com.google.inject.Key;
import javax.servlet.http.HttpServlet;
import javax.servlet.ServletContext;
import java.util.List;
import com.google.inject.internal.util.$Lists;
import com.google.inject.Module;
import com.google.inject.internal.util.$Preconditions;
import com.google.inject.AbstractModule;

public class ServletModule extends AbstractModule
{
    private FiltersModuleBuilder filtersModuleBuilder;
    private ServletsModuleBuilder servletsModuleBuilder;
    
    @Override
    protected final void configure() {
        $Preconditions.checkState(this.filtersModuleBuilder == null, (Object)"Re-entry is not allowed.");
        $Preconditions.checkState(this.servletsModuleBuilder == null, (Object)"Re-entry is not allowed.");
        this.filtersModuleBuilder = new FiltersModuleBuilder();
        this.servletsModuleBuilder = new ServletsModuleBuilder();
        try {
            this.install(new InternalServletModule());
            this.configureServlets();
            this.install(this.filtersModuleBuilder);
            this.install(this.servletsModuleBuilder);
        }
        finally {
            this.filtersModuleBuilder = null;
            this.servletsModuleBuilder = null;
        }
    }
    
    protected void configureServlets() {
    }
    
    protected final FilterKeyBindingBuilder filter(final String urlPattern, final String... morePatterns) {
        return this.filtersModuleBuilder.filter($Lists.newArrayList(urlPattern, morePatterns));
    }
    
    protected final FilterKeyBindingBuilder filterRegex(final String regex, final String... regexes) {
        return this.filtersModuleBuilder.filterRegex($Lists.newArrayList(regex, regexes));
    }
    
    protected final ServletKeyBindingBuilder serve(final String urlPattern, final String... morePatterns) {
        return this.servletsModuleBuilder.serve($Lists.newArrayList(urlPattern, morePatterns));
    }
    
    protected final ServletKeyBindingBuilder serveRegex(final String regex, final String... regexes) {
        return this.servletsModuleBuilder.serveRegex($Lists.newArrayList(regex, regexes));
    }
    
    protected final ServletContext getServletContext() {
        return GuiceFilter.getServletContext();
    }
    
    public interface ServletKeyBindingBuilder
    {
        void with(final Class<? extends HttpServlet> p0);
        
        void with(final Key<? extends HttpServlet> p0);
        
        void with(final HttpServlet p0);
        
        void with(final Class<? extends HttpServlet> p0, final Map<String, String> p1);
        
        void with(final Key<? extends HttpServlet> p0, final Map<String, String> p1);
        
        void with(final HttpServlet p0, final Map<String, String> p1);
    }
    
    public interface FilterKeyBindingBuilder
    {
        void through(final Class<? extends Filter> p0);
        
        void through(final Key<? extends Filter> p0);
        
        void through(final Filter p0);
        
        void through(final Class<? extends Filter> p0, final Map<String, String> p1);
        
        void through(final Key<? extends Filter> p0, final Map<String, String> p1);
        
        void through(final Filter p0, final Map<String, String> p1);
    }
}
