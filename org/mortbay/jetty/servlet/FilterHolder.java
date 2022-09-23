// 
// Decompiled by Procyon v0.5.36
// 

package org.mortbay.jetty.servlet;

import java.util.Enumeration;
import javax.servlet.ServletContext;
import org.mortbay.log.Log;
import javax.servlet.FilterConfig;
import javax.servlet.Filter;

public class FilterHolder extends Holder
{
    private transient Filter _filter;
    private transient Config _config;
    
    public static int dispatch(final String type) {
        if ("request".equalsIgnoreCase(type)) {
            return 1;
        }
        if ("forward".equalsIgnoreCase(type)) {
            return 2;
        }
        if ("include".equalsIgnoreCase(type)) {
            return 4;
        }
        if ("error".equalsIgnoreCase(type)) {
            return 8;
        }
        throw new IllegalArgumentException(type);
    }
    
    public FilterHolder() {
    }
    
    public FilterHolder(final Class filter) {
        super(filter);
    }
    
    public FilterHolder(final Filter filter) {
        this.setFilter(filter);
    }
    
    public void doStart() throws Exception {
        super.doStart();
        if (!Filter.class.isAssignableFrom(this._class)) {
            final String msg = this._class + " is not a javax.servlet.Filter";
            super.stop();
            throw new IllegalStateException(msg);
        }
        if (this._filter == null) {
            this._filter = (Filter)this.newInstance();
        }
        this._filter = this.getServletHandler().customizeFilter(this._filter);
        this._config = new Config();
        this._filter.init(this._config);
    }
    
    public void doStop() {
        if (this._filter != null) {
            try {
                this.destroyInstance(this._filter);
            }
            catch (Exception e) {
                Log.warn(e);
            }
        }
        if (!this._extInstance) {
            this._filter = null;
        }
        this._config = null;
        super.doStop();
    }
    
    public void destroyInstance(final Object o) throws Exception {
        if (o == null) {
            return;
        }
        final Filter f = (Filter)o;
        f.destroy();
        this.getServletHandler().customizeFilterDestroy(f);
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
    
    public String toString() {
        return this.getName();
    }
    
    class Config implements FilterConfig
    {
        public String getFilterName() {
            return FilterHolder.this._name;
        }
        
        public ServletContext getServletContext() {
            return FilterHolder.this._servletHandler.getServletContext();
        }
        
        public String getInitParameter(final String param) {
            return FilterHolder.this.getInitParameter(param);
        }
        
        public Enumeration getInitParameterNames() {
            return FilterHolder.this.getInitParameterNames();
        }
    }
}
