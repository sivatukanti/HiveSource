// 
// Decompiled by Procyon v0.5.36
// 

package org.mortbay.jetty.servlet;

import org.mortbay.log.Log;
import org.mortbay.util.URIUtil;
import javax.servlet.RequestDispatcher;
import org.mortbay.jetty.Handler;
import org.mortbay.jetty.handler.ErrorHandler;
import org.mortbay.jetty.HandlerContainer;
import org.mortbay.jetty.security.SecurityHandler;
import org.mortbay.jetty.handler.ContextHandler;

public class Context extends ContextHandler
{
    public static final int SESSIONS = 1;
    public static final int SECURITY = 2;
    public static final int NO_SESSIONS = 0;
    public static final int NO_SECURITY = 0;
    protected SecurityHandler _securityHandler;
    protected ServletHandler _servletHandler;
    protected SessionHandler _sessionHandler;
    
    public Context() {
        this(null, null, null, null, null);
    }
    
    public Context(final int options) {
        this(null, null, options);
    }
    
    public Context(final HandlerContainer parent, final String contextPath) {
        this(parent, contextPath, null, null, null, null);
    }
    
    public Context(final HandlerContainer parent, final String contextPath, final int options) {
        this(parent, contextPath, ((options & 0x1) != 0x0) ? new SessionHandler() : null, ((options & 0x2) != 0x0) ? new SecurityHandler() : null, null, null);
    }
    
    public Context(final HandlerContainer parent, final String contextPath, final boolean sessions, final boolean security) {
        this(parent, contextPath, (sessions ? 1 : 0) | (security ? 2 : 0));
    }
    
    public Context(final HandlerContainer parent, final SessionHandler sessionHandler, final SecurityHandler securityHandler, final ServletHandler servletHandler, final ErrorHandler errorHandler) {
        this(parent, null, sessionHandler, securityHandler, servletHandler, errorHandler);
    }
    
    public Context(final HandlerContainer parent, final String contextPath, final SessionHandler sessionHandler, final SecurityHandler securityHandler, final ServletHandler servletHandler, final ErrorHandler errorHandler) {
        super((ContextHandler.SContext)null);
        this._scontext = new SContext();
        this._sessionHandler = sessionHandler;
        this._securityHandler = securityHandler;
        this._servletHandler = ((servletHandler != null) ? servletHandler : new ServletHandler());
        if (this._sessionHandler != null) {
            this.setHandler(this._sessionHandler);
            if (securityHandler != null) {
                this._sessionHandler.setHandler(this._securityHandler);
                this._securityHandler.setHandler(this._servletHandler);
            }
            else {
                this._sessionHandler.setHandler(this._servletHandler);
            }
        }
        else if (this._securityHandler != null) {
            this.setHandler(this._securityHandler);
            this._securityHandler.setHandler(this._servletHandler);
        }
        else {
            this.setHandler(this._servletHandler);
        }
        if (errorHandler != null) {
            this.setErrorHandler(errorHandler);
        }
        if (contextPath != null) {
            this.setContextPath(contextPath);
        }
        if (parent != null) {
            parent.addHandler(this);
        }
    }
    
    protected void startContext() throws Exception {
        super.startContext();
        if (this._servletHandler != null && this._servletHandler.isStarted()) {
            this._servletHandler.initialize();
        }
    }
    
    public SecurityHandler getSecurityHandler() {
        return this._securityHandler;
    }
    
    public ServletHandler getServletHandler() {
        return this._servletHandler;
    }
    
    public SessionHandler getSessionHandler() {
        return this._sessionHandler;
    }
    
    public ServletHolder addServlet(final String className, final String pathSpec) {
        return this._servletHandler.addServletWithMapping(className, pathSpec);
    }
    
    public ServletHolder addServlet(final Class servlet, final String pathSpec) {
        return this._servletHandler.addServletWithMapping(servlet.getName(), pathSpec);
    }
    
    public void addServlet(final ServletHolder servlet, final String pathSpec) {
        this._servletHandler.addServletWithMapping(servlet, pathSpec);
    }
    
    public void addFilter(final FilterHolder holder, final String pathSpec, final int dispatches) {
        this._servletHandler.addFilterWithMapping(holder, pathSpec, dispatches);
    }
    
    public FilterHolder addFilter(final Class filterClass, final String pathSpec, final int dispatches) {
        return this._servletHandler.addFilterWithMapping(filterClass, pathSpec, dispatches);
    }
    
    public FilterHolder addFilter(final String filterClass, final String pathSpec, final int dispatches) {
        return this._servletHandler.addFilterWithMapping(filterClass, pathSpec, dispatches);
    }
    
    public void setSessionHandler(final SessionHandler sessionHandler) {
        if (this._sessionHandler == sessionHandler) {
            return;
        }
        if (this._sessionHandler != null) {
            this._sessionHandler.setHandler(null);
        }
        this.setHandler(this._sessionHandler = sessionHandler);
        if (this._securityHandler != null) {
            this._sessionHandler.setHandler(this._securityHandler);
        }
        else if (this._servletHandler != null) {
            this._sessionHandler.setHandler(this._servletHandler);
        }
    }
    
    public void setSecurityHandler(final SecurityHandler securityHandler) {
        if (this._securityHandler == securityHandler) {
            return;
        }
        if (this._securityHandler != null) {
            this._securityHandler.setHandler(null);
        }
        this._securityHandler = securityHandler;
        if (this._securityHandler == null) {
            if (this._sessionHandler != null) {
                this._sessionHandler.setHandler(this._servletHandler);
            }
            else {
                this.setHandler(this._servletHandler);
            }
        }
        else {
            if (this._sessionHandler != null) {
                this._sessionHandler.setHandler(this._securityHandler);
            }
            else {
                this.setHandler(this._securityHandler);
            }
            if (this._servletHandler != null) {
                this._securityHandler.setHandler(this._servletHandler);
            }
        }
    }
    
    public void setServletHandler(final ServletHandler servletHandler) {
        if (this._servletHandler == servletHandler) {
            return;
        }
        this._servletHandler = servletHandler;
        if (this._securityHandler != null) {
            this._securityHandler.setHandler(this._servletHandler);
        }
        else if (this._sessionHandler != null) {
            this._sessionHandler.setHandler(this._servletHandler);
        }
        else {
            this.setHandler(this._servletHandler);
        }
    }
    
    public class SContext extends ContextHandler.SContext
    {
        public RequestDispatcher getNamedDispatcher(final String name) {
            final ContextHandler context = Context.this;
            if (Context.this._servletHandler == null || Context.this._servletHandler.getServlet(name) == null) {
                return null;
            }
            return new Dispatcher(context, name);
        }
        
        public RequestDispatcher getRequestDispatcher(String uriInContext) {
            if (uriInContext == null) {
                return null;
            }
            if (!uriInContext.startsWith("/")) {
                return null;
            }
            try {
                String query = null;
                int q = 0;
                if ((q = uriInContext.indexOf(63)) > 0) {
                    query = uriInContext.substring(q + 1);
                    uriInContext = uriInContext.substring(0, q);
                }
                if ((q = uriInContext.indexOf(59)) > 0) {
                    uriInContext = uriInContext.substring(0, q);
                }
                final String pathInContext = URIUtil.canonicalPath(URIUtil.decodePath(uriInContext));
                final String uri = URIUtil.addPaths(this.getContextPath(), uriInContext);
                final ContextHandler context = Context.this;
                return new Dispatcher(context, uri, pathInContext, query);
            }
            catch (Exception e) {
                Log.ignore(e);
                return null;
            }
        }
    }
}
