// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.servlet;

import org.eclipse.jetty.util.TypeUtil;
import java.util.List;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Collection;
import javax.servlet.DispatcherType;
import java.util.EnumSet;
import org.eclipse.jetty.util.log.Log;
import java.io.IOException;
import org.eclipse.jetty.util.component.Dumpable;
import javax.servlet.ServletContext;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.FilterRegistration;
import org.eclipse.jetty.util.log.Logger;
import javax.servlet.Filter;

public class FilterHolder extends Holder<Filter>
{
    private static final Logger LOG;
    private transient Filter _filter;
    private transient Config _config;
    private transient FilterRegistration.Dynamic _registration;
    
    public FilterHolder() {
        this(Source.EMBEDDED);
    }
    
    public FilterHolder(final Source source) {
        super(source);
    }
    
    public FilterHolder(final Class<? extends Filter> filter) {
        this(Source.EMBEDDED);
        this.setHeldClass(filter);
    }
    
    public FilterHolder(final Filter filter) {
        this(Source.EMBEDDED);
        this.setFilter(filter);
    }
    
    @Override
    public void doStart() throws Exception {
        super.doStart();
        if (!Filter.class.isAssignableFrom(this._class)) {
            final String msg = this._class + " is not a javax.servlet.Filter";
            super.stop();
            throw new IllegalStateException(msg);
        }
    }
    
    @Override
    public void initialize() throws Exception {
        if (!this._initialized) {
            super.initialize();
            if (this._filter == null) {
                try {
                    final ServletContext context = this._servletHandler.getServletContext();
                    this._filter = ((context instanceof ServletContextHandler.Context) ? ((ServletContextHandler.Context)context).createFilter(this.getHeldClass()) : this.getHeldClass().newInstance());
                }
                catch (ServletException se) {
                    final Throwable cause = se.getRootCause();
                    if (cause instanceof InstantiationException) {
                        throw (InstantiationException)cause;
                    }
                    if (cause instanceof IllegalAccessException) {
                        throw (IllegalAccessException)cause;
                    }
                    throw se;
                }
            }
            this._config = new Config();
            if (FilterHolder.LOG.isDebugEnabled()) {
                FilterHolder.LOG.debug("Filter.init {}", this._filter);
            }
            this._filter.init(this._config);
        }
        this._initialized = true;
    }
    
    @Override
    public void doStop() throws Exception {
        if (this._filter != null) {
            try {
                this.destroyInstance(this._filter);
            }
            catch (Exception e) {
                FilterHolder.LOG.warn(e);
            }
        }
        if (!this._extInstance) {
            this._filter = null;
        }
        this._config = null;
        this._initialized = false;
        super.doStop();
    }
    
    @Override
    public void destroyInstance(final Object o) throws Exception {
        if (o == null) {
            return;
        }
        final Filter f = (Filter)o;
        f.destroy();
        this.getServletHandler().destroyFilter(f);
    }
    
    public synchronized void setFilter(final Filter filter) {
        this._filter = filter;
        this._extInstance = true;
        this.setHeldClass(filter.getClass());
        if (this.getName() == null) {
            this.setName(filter.getClass().getName());
        }
    }
    
    public Filter getFilter() {
        return this._filter;
    }
    
    @Override
    public String toString() {
        return this.getName();
    }
    
    @Override
    public void dump(final Appendable out, final String indent) throws IOException {
        super.dump(out, indent);
        if (this._filter instanceof Dumpable) {
            ((Dumpable)this._filter).dump(out, indent);
        }
    }
    
    public FilterRegistration.Dynamic getRegistration() {
        if (this._registration == null) {
            this._registration = new Registration();
        }
        return this._registration;
    }
    
    static {
        LOG = Log.getLogger(FilterHolder.class);
    }
    
    protected class Registration extends HolderRegistration implements FilterRegistration.Dynamic
    {
        @Override
        public void addMappingForServletNames(final EnumSet<DispatcherType> dispatcherTypes, final boolean isMatchAfter, final String... servletNames) {
            FilterHolder.this.illegalStateIfContextStarted();
            final FilterMapping mapping = new FilterMapping();
            mapping.setFilterHolder(FilterHolder.this);
            mapping.setServletNames(servletNames);
            mapping.setDispatcherTypes(dispatcherTypes);
            if (isMatchAfter) {
                FilterHolder.this._servletHandler.addFilterMapping(mapping);
            }
            else {
                FilterHolder.this._servletHandler.prependFilterMapping(mapping);
            }
        }
        
        @Override
        public void addMappingForUrlPatterns(final EnumSet<DispatcherType> dispatcherTypes, final boolean isMatchAfter, final String... urlPatterns) {
            FilterHolder.this.illegalStateIfContextStarted();
            final FilterMapping mapping = new FilterMapping();
            mapping.setFilterHolder(FilterHolder.this);
            mapping.setPathSpecs(urlPatterns);
            mapping.setDispatcherTypes(dispatcherTypes);
            if (isMatchAfter) {
                FilterHolder.this._servletHandler.addFilterMapping(mapping);
            }
            else {
                FilterHolder.this._servletHandler.prependFilterMapping(mapping);
            }
        }
        
        @Override
        public Collection<String> getServletNameMappings() {
            final FilterMapping[] mappings = FilterHolder.this._servletHandler.getFilterMappings();
            final List<String> names = new ArrayList<String>();
            for (final FilterMapping mapping : mappings) {
                if (mapping.getFilterHolder() == FilterHolder.this) {
                    final String[] servlets = mapping.getServletNames();
                    if (servlets != null && servlets.length > 0) {
                        names.addAll(Arrays.asList(servlets));
                    }
                }
            }
            return names;
        }
        
        @Override
        public Collection<String> getUrlPatternMappings() {
            final FilterMapping[] mappings = FilterHolder.this._servletHandler.getFilterMappings();
            final List<String> patterns = new ArrayList<String>();
            for (final FilterMapping mapping : mappings) {
                if (mapping.getFilterHolder() == FilterHolder.this) {
                    final String[] specs = mapping.getPathSpecs();
                    patterns.addAll(TypeUtil.asList(specs));
                }
            }
            return patterns;
        }
    }
    
    class Config extends HolderConfig implements FilterConfig
    {
        @Override
        public String getFilterName() {
            return FilterHolder.this._name;
        }
    }
}
