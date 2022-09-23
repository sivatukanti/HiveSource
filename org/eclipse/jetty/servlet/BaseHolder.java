// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.servlet;

import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.util.component.ContainerLifeCycle;
import java.io.IOException;
import org.eclipse.jetty.util.component.LifeCycle;
import javax.servlet.ServletContext;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.util.annotation.ManagedAttribute;
import org.eclipse.jetty.util.Loader;
import javax.servlet.UnavailableException;
import org.eclipse.jetty.util.log.Logger;
import org.eclipse.jetty.util.component.Dumpable;
import org.eclipse.jetty.util.component.AbstractLifeCycle;

public abstract class BaseHolder<T> extends AbstractLifeCycle implements Dumpable
{
    private static final Logger LOG;
    protected final Source _source;
    protected transient Class<? extends T> _class;
    protected String _className;
    protected boolean _extInstance;
    protected ServletHandler _servletHandler;
    
    protected BaseHolder(final Source source) {
        this._source = source;
    }
    
    public Source getSource() {
        return this._source;
    }
    
    public void initialize() throws Exception {
        if (!this.isStarted()) {
            throw new IllegalStateException("Not started: " + this);
        }
    }
    
    public void doStart() throws Exception {
        if (this._class == null && (this._className == null || this._className.equals(""))) {
            throw new UnavailableException("No class in holder " + this.toString());
        }
        if (this._class == null) {
            try {
                this._class = (Class<? extends T>)Loader.loadClass(Holder.class, this._className);
                if (BaseHolder.LOG.isDebugEnabled()) {
                    BaseHolder.LOG.debug("Holding {} from {}", this._class, this._class.getClassLoader());
                }
            }
            catch (Exception e) {
                BaseHolder.LOG.warn(e);
                throw new UnavailableException("Class loading error for holder " + this.toString());
            }
        }
    }
    
    public void doStop() throws Exception {
        if (!this._extInstance) {
            this._class = null;
        }
    }
    
    @ManagedAttribute(value = "Class Name", readonly = true)
    public String getClassName() {
        return this._className;
    }
    
    public Class<? extends T> getHeldClass() {
        return this._class;
    }
    
    public ServletHandler getServletHandler() {
        return this._servletHandler;
    }
    
    public void setServletHandler(final ServletHandler servletHandler) {
        this._servletHandler = servletHandler;
    }
    
    public void setClassName(final String className) {
        this._className = className;
        this._class = null;
    }
    
    public void setHeldClass(final Class<? extends T> held) {
        this._class = held;
        if (held != null) {
            this._className = held.getName();
        }
    }
    
    protected void illegalStateIfContextStarted() {
        if (this._servletHandler != null) {
            final ServletContext context = this._servletHandler.getServletContext();
            if (context instanceof ContextHandler.Context && ((ContextHandler.Context)context).getContextHandler().isStarted()) {
                throw new IllegalStateException("Started");
            }
        }
    }
    
    public boolean isInstance() {
        return this._extInstance;
    }
    
    @Override
    public void dump(final Appendable out, final String indent) throws IOException {
        out.append(this.toString()).append(" - ").append(AbstractLifeCycle.getState(this)).append("\n");
    }
    
    @Override
    public String dump() {
        return ContainerLifeCycle.dump(this);
    }
    
    static {
        LOG = Log.getLogger(BaseHolder.class);
    }
    
    public enum Source
    {
        EMBEDDED, 
        JAVAX_API, 
        DESCRIPTOR, 
        ANNOTATION;
    }
}
