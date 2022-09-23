// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.servlet;

import java.util.Iterator;
import java.util.HashSet;
import java.util.Set;
import javax.servlet.Registration;
import javax.servlet.ServletContext;
import org.eclipse.jetty.util.log.Log;
import java.io.IOException;
import org.eclipse.jetty.util.component.ContainerLifeCycle;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import org.eclipse.jetty.util.annotation.ManagedAttribute;
import java.util.HashMap;
import java.util.Map;
import org.eclipse.jetty.util.log.Logger;
import org.eclipse.jetty.util.annotation.ManagedObject;

@ManagedObject("Holder - a container for servlets and the like")
public class Holder<T> extends BaseHolder<T>
{
    private static final Logger LOG;
    protected final Map<String, String> _initParams;
    protected String _displayName;
    protected boolean _asyncSupported;
    protected String _name;
    protected boolean _initialized;
    
    protected Holder(final Source source) {
        super(source);
        this._initParams = new HashMap<String, String>(3);
        this._initialized = false;
        switch (this._source) {
            case JAVAX_API:
            case DESCRIPTOR:
            case ANNOTATION: {
                this._asyncSupported = false;
                break;
            }
            default: {
                this._asyncSupported = true;
                break;
            }
        }
    }
    
    @ManagedAttribute(value = "Display Name", readonly = true)
    public String getDisplayName() {
        return this._displayName;
    }
    
    public String getInitParameter(final String param) {
        if (this._initParams == null) {
            return null;
        }
        return this._initParams.get(param);
    }
    
    public Enumeration<String> getInitParameterNames() {
        if (this._initParams == null) {
            return Collections.enumeration((Collection<String>)Collections.EMPTY_LIST);
        }
        return Collections.enumeration(this._initParams.keySet());
    }
    
    @ManagedAttribute(value = "Initial Parameters", readonly = true)
    public Map<String, String> getInitParameters() {
        return this._initParams;
    }
    
    @ManagedAttribute(value = "Name", readonly = true)
    public String getName() {
        return this._name;
    }
    
    public void destroyInstance(final Object instance) throws Exception {
    }
    
    @Override
    public void setClassName(final String className) {
        super.setClassName(className);
        if (this._name == null) {
            this._name = className + "-" + Integer.toHexString(this.hashCode());
        }
    }
    
    @Override
    public void setHeldClass(final Class<? extends T> held) {
        super.setHeldClass(held);
        if (held != null && this._name == null) {
            this._name = held.getName() + "-" + Integer.toHexString(this.hashCode());
        }
    }
    
    public void setDisplayName(final String name) {
        this._displayName = name;
    }
    
    public void setInitParameter(final String param, final String value) {
        this._initParams.put(param, value);
    }
    
    public void setInitParameters(final Map<String, String> map) {
        this._initParams.clear();
        this._initParams.putAll(map);
    }
    
    public void setName(final String name) {
        this._name = name;
    }
    
    public void setAsyncSupported(final boolean suspendable) {
        this._asyncSupported = suspendable;
    }
    
    public boolean isAsyncSupported() {
        return this._asyncSupported;
    }
    
    @Override
    public void dump(final Appendable out, final String indent) throws IOException {
        super.dump(out, indent);
        ContainerLifeCycle.dump(out, indent, this._initParams.entrySet());
    }
    
    @Override
    public String dump() {
        return super.dump();
    }
    
    @Override
    public String toString() {
        return String.format("%s@%x==%s", this._name, this.hashCode(), this._className);
    }
    
    static {
        LOG = Log.getLogger(Holder.class);
    }
    
    protected class HolderConfig
    {
        public ServletContext getServletContext() {
            return Holder.this._servletHandler.getServletContext();
        }
        
        public String getInitParameter(final String param) {
            return Holder.this.getInitParameter(param);
        }
        
        public Enumeration<String> getInitParameterNames() {
            return Holder.this.getInitParameterNames();
        }
    }
    
    protected class HolderRegistration implements Registration.Dynamic
    {
        @Override
        public void setAsyncSupported(final boolean isAsyncSupported) {
            Holder.this.illegalStateIfContextStarted();
            Holder.this.setAsyncSupported(isAsyncSupported);
        }
        
        public void setDescription(final String description) {
            if (Holder.LOG.isDebugEnabled()) {
                Holder.LOG.debug(this + " is " + description, new Object[0]);
            }
        }
        
        @Override
        public String getClassName() {
            return Holder.this.getClassName();
        }
        
        @Override
        public String getInitParameter(final String name) {
            return Holder.this.getInitParameter(name);
        }
        
        @Override
        public Map<String, String> getInitParameters() {
            return Holder.this.getInitParameters();
        }
        
        @Override
        public String getName() {
            return Holder.this.getName();
        }
        
        @Override
        public boolean setInitParameter(final String name, final String value) {
            Holder.this.illegalStateIfContextStarted();
            if (name == null) {
                throw new IllegalArgumentException("init parameter name required");
            }
            if (value == null) {
                throw new IllegalArgumentException("non-null value required for init parameter " + name);
            }
            if (Holder.this.getInitParameter(name) != null) {
                return false;
            }
            Holder.this.setInitParameter(name, value);
            return true;
        }
        
        @Override
        public Set<String> setInitParameters(final Map<String, String> initParameters) {
            Holder.this.illegalStateIfContextStarted();
            Set<String> clash = null;
            for (final Map.Entry<String, String> entry : initParameters.entrySet()) {
                if (entry.getKey() == null) {
                    throw new IllegalArgumentException("init parameter name required");
                }
                if (entry.getValue() == null) {
                    throw new IllegalArgumentException("non-null value required for init parameter " + entry.getKey());
                }
                if (Holder.this.getInitParameter(entry.getKey()) == null) {
                    continue;
                }
                if (clash == null) {
                    clash = new HashSet<String>();
                }
                clash.add(entry.getKey());
            }
            if (clash != null) {
                return clash;
            }
            Holder.this.getInitParameters().putAll(initParameters);
            return Collections.emptySet();
        }
    }
}
