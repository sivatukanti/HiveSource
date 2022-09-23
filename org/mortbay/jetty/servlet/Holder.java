// 
// Decompiled by Procyon v0.5.36
// 

package org.mortbay.jetty.servlet;

import java.util.HashMap;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import org.mortbay.log.Log;
import org.mortbay.util.Loader;
import javax.servlet.UnavailableException;
import java.util.Map;
import java.io.Serializable;
import org.mortbay.component.AbstractLifeCycle;

public class Holder extends AbstractLifeCycle implements Serializable
{
    protected transient Class _class;
    protected String _className;
    protected String _displayName;
    protected Map _initParams;
    protected boolean _extInstance;
    protected String _name;
    protected ServletHandler _servletHandler;
    
    protected Holder() {
    }
    
    protected Holder(final Class held) {
        this._class = held;
        if (held != null) {
            this._className = held.getName();
            this._name = held.getName() + "-" + this.hashCode();
        }
    }
    
    public void doStart() throws Exception {
        if (this._class == null && (this._className == null || this._className.equals(""))) {
            throw new UnavailableException("No class for Servlet or Filter", -1);
        }
        if (this._class == null) {
            try {
                this._class = Loader.loadClass(Holder.class, this._className);
                if (Log.isDebugEnabled()) {
                    Log.debug("Holding {}", this._class);
                }
            }
            catch (Exception e) {
                Log.warn(e);
                throw new UnavailableException(e.getMessage(), -1);
            }
        }
    }
    
    public void doStop() {
        if (!this._extInstance) {
            this._class = null;
        }
    }
    
    public String getClassName() {
        return this._className;
    }
    
    public Class getHeldClass() {
        return this._class;
    }
    
    public String getDisplayName() {
        return this._displayName;
    }
    
    public String getInitParameter(final String param) {
        if (this._initParams == null) {
            return null;
        }
        return this._initParams.get(param);
    }
    
    public Enumeration getInitParameterNames() {
        if (this._initParams == null) {
            return Collections.enumeration((Collection<Object>)Collections.EMPTY_LIST);
        }
        return Collections.enumeration((Collection<Object>)this._initParams.keySet());
    }
    
    public Map getInitParameters() {
        return this._initParams;
    }
    
    public String getName() {
        return this._name;
    }
    
    public ServletHandler getServletHandler() {
        return this._servletHandler;
    }
    
    public synchronized Object newInstance() throws InstantiationException, IllegalAccessException {
        if (this._class == null) {
            throw new InstantiationException("!" + this._className);
        }
        return this._class.newInstance();
    }
    
    public void destroyInstance(final Object instance) throws Exception {
    }
    
    public void setClassName(final String className) {
        this._className = className;
        this._class = null;
    }
    
    public void setHeldClass(final Class held) {
        this._class = held;
        this._className = ((held != null) ? held.getName() : null);
    }
    
    public void setDisplayName(final String name) {
        this._displayName = name;
    }
    
    public void setInitParameter(final String param, final String value) {
        if (this._initParams == null) {
            this._initParams = new HashMap(3);
        }
        this._initParams.put(param, value);
    }
    
    public void setInitParameters(final Map map) {
        this._initParams = map;
    }
    
    public void setName(final String name) {
        this._name = name;
    }
    
    public void setServletHandler(final ServletHandler servletHandler) {
        this._servletHandler = servletHandler;
    }
    
    public String toString() {
        return this._name;
    }
}
