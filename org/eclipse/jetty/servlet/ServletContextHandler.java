// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.servlet;

import org.eclipse.jetty.util.component.LifeCycle;
import javax.servlet.SessionCookieConfig;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.SessionTrackingMode;
import javax.servlet.ServletException;
import javax.servlet.FilterRegistration;
import org.eclipse.jetty.server.Dispatcher;
import javax.servlet.RequestDispatcher;
import javax.servlet.descriptor.TaglibDescriptor;
import javax.servlet.descriptor.JspPropertyGroupDescriptor;
import org.eclipse.jetty.util.log.Log;
import java.util.ArrayList;
import java.util.EventListener;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.util.List;
import java.util.Iterator;
import java.util.Collections;
import org.eclipse.jetty.security.ConstraintMapping;
import javax.servlet.ServletSecurityElement;
import java.util.Set;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import org.eclipse.jetty.security.ConstraintAware;
import javax.servlet.ServletRegistration;
import javax.servlet.Filter;
import javax.servlet.DispatcherType;
import java.util.EnumSet;
import javax.servlet.Servlet;
import org.eclipse.jetty.util.annotation.ManagedAttribute;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.handler.HandlerWrapper;
import org.eclipse.jetty.util.Decorator;
import org.eclipse.jetty.util.DeprecationWarning;
import org.eclipse.jetty.security.ConstraintSecurityHandler;
import org.eclipse.jetty.server.handler.ErrorHandler;
import org.eclipse.jetty.server.HandlerContainer;
import javax.servlet.descriptor.JspConfigDescriptor;
import org.eclipse.jetty.server.handler.gzip.GzipHandler;
import org.eclipse.jetty.server.session.SessionHandler;
import org.eclipse.jetty.security.SecurityHandler;
import org.eclipse.jetty.util.DecoratedObjectFactory;
import org.eclipse.jetty.util.log.Logger;
import org.eclipse.jetty.util.annotation.ManagedObject;
import org.eclipse.jetty.server.handler.ContextHandler;

@ManagedObject("Servlet Context Handler")
public class ServletContextHandler extends ContextHandler
{
    private static final Logger LOG;
    public static final int SESSIONS = 1;
    public static final int SECURITY = 2;
    public static final int GZIP = 4;
    public static final int NO_SESSIONS = 0;
    public static final int NO_SECURITY = 0;
    protected final DecoratedObjectFactory _objFactory;
    protected Class<? extends SecurityHandler> _defaultSecurityHandlerClass;
    protected SessionHandler _sessionHandler;
    protected SecurityHandler _securityHandler;
    protected ServletHandler _servletHandler;
    protected GzipHandler _gzipHandler;
    protected int _options;
    protected JspConfigDescriptor _jspConfig;
    
    public ServletContextHandler() {
        this(null, null, null, null, null);
    }
    
    public ServletContextHandler(final int options) {
        this(null, null, options);
    }
    
    public ServletContextHandler(final HandlerContainer parent, final String contextPath) {
        this(parent, contextPath, null, null, null, null);
    }
    
    public ServletContextHandler(final HandlerContainer parent, final String contextPath, final int options) {
        this(parent, contextPath, null, null, null, null, options);
    }
    
    public ServletContextHandler(final HandlerContainer parent, final String contextPath, final boolean sessions, final boolean security) {
        this(parent, contextPath, (sessions ? 1 : 0) | (security ? 2 : 0));
    }
    
    public ServletContextHandler(final HandlerContainer parent, final SessionHandler sessionHandler, final SecurityHandler securityHandler, final ServletHandler servletHandler, final ErrorHandler errorHandler) {
        this(parent, null, sessionHandler, securityHandler, servletHandler, errorHandler);
    }
    
    public ServletContextHandler(final HandlerContainer parent, final String contextPath, final SessionHandler sessionHandler, final SecurityHandler securityHandler, final ServletHandler servletHandler, final ErrorHandler errorHandler) {
        this(parent, contextPath, sessionHandler, securityHandler, servletHandler, errorHandler, 0);
    }
    
    public ServletContextHandler(final HandlerContainer parent, final String contextPath, final SessionHandler sessionHandler, final SecurityHandler securityHandler, final ServletHandler servletHandler, final ErrorHandler errorHandler, final int options) {
        super((ContextHandler.Context)null);
        this._defaultSecurityHandlerClass = ConstraintSecurityHandler.class;
        this._options = options;
        this._scontext = new Context();
        this._sessionHandler = sessionHandler;
        this._securityHandler = securityHandler;
        this._servletHandler = servletHandler;
        (this._objFactory = new DecoratedObjectFactory()).addDecorator(new DeprecationWarning());
        if (contextPath != null) {
            this.setContextPath(contextPath);
        }
        if (parent instanceof HandlerWrapper) {
            ((HandlerWrapper)parent).setHandler(this);
        }
        else if (parent instanceof HandlerCollection) {
            ((HandlerCollection)parent).addHandler(this);
        }
        this.relinkHandlers();
        if (errorHandler != null) {
            this.setErrorHandler(errorHandler);
        }
    }
    
    @Override
    public void setHandler(final Handler handler) {
        if (handler != null) {
            ServletContextHandler.LOG.warn("ServletContextHandler.setHandler should not be called directly. Use insertHandler or setSessionHandler etc.", new Object[0]);
        }
        super.setHandler(handler);
    }
    
    private void doSetHandler(final HandlerWrapper wrapper, final Handler handler) {
        if (wrapper == this) {
            super.setHandler(handler);
        }
        else {
            wrapper.setHandler(handler);
        }
    }
    
    private void relinkHandlers() {
        HandlerWrapper handler = this;
        if (this.getSessionHandler() != null) {
            while (!(handler.getHandler() instanceof SessionHandler) && !(handler.getHandler() instanceof SecurityHandler) && !(handler.getHandler() instanceof GzipHandler) && !(handler.getHandler() instanceof ServletHandler) && handler.getHandler() instanceof HandlerWrapper) {
                handler = (HandlerWrapper)handler.getHandler();
            }
            if (handler.getHandler() != this._sessionHandler) {
                this.doSetHandler(handler, this._sessionHandler);
            }
            handler = this._sessionHandler;
        }
        if (this.getSecurityHandler() != null) {
            while (!(handler.getHandler() instanceof SecurityHandler) && !(handler.getHandler() instanceof GzipHandler) && !(handler.getHandler() instanceof ServletHandler) && handler.getHandler() instanceof HandlerWrapper) {
                handler = (HandlerWrapper)handler.getHandler();
            }
            if (handler.getHandler() != this._securityHandler) {
                this.doSetHandler(handler, this._securityHandler);
            }
            handler = this._securityHandler;
        }
        if (this.getGzipHandler() != null) {
            while (!(handler.getHandler() instanceof GzipHandler) && !(handler.getHandler() instanceof ServletHandler) && handler.getHandler() instanceof HandlerWrapper) {
                handler = (HandlerWrapper)handler.getHandler();
            }
            if (handler.getHandler() != this._gzipHandler) {
                this.doSetHandler(handler, this._gzipHandler);
            }
            handler = this._gzipHandler;
        }
        if (this.getServletHandler() != null) {
            while (!(handler.getHandler() instanceof ServletHandler) && handler.getHandler() instanceof HandlerWrapper) {
                handler = (HandlerWrapper)handler.getHandler();
            }
            if (handler.getHandler() != this._servletHandler) {
                this.doSetHandler(handler, this._servletHandler);
            }
            handler = this._servletHandler;
        }
    }
    
    @Override
    protected void doStart() throws Exception {
        this.getServletContext().setAttribute(DecoratedObjectFactory.ATTR, this._objFactory);
        super.doStart();
    }
    
    @Override
    protected void doStop() throws Exception {
        super.doStop();
        this._objFactory.clear();
    }
    
    public Class<? extends SecurityHandler> getDefaultSecurityHandlerClass() {
        return this._defaultSecurityHandlerClass;
    }
    
    public void setDefaultSecurityHandlerClass(final Class<? extends SecurityHandler> defaultSecurityHandlerClass) {
        this._defaultSecurityHandlerClass = defaultSecurityHandlerClass;
    }
    
    protected SessionHandler newSessionHandler() {
        return new SessionHandler();
    }
    
    protected SecurityHandler newSecurityHandler() {
        try {
            return (SecurityHandler)this._defaultSecurityHandlerClass.newInstance();
        }
        catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }
    
    protected ServletHandler newServletHandler() {
        return new ServletHandler();
    }
    
    @Override
    protected void startContext() throws Exception {
        final ServletContainerInitializerCaller sciBean = this.getBean(ServletContainerInitializerCaller.class);
        if (sciBean != null) {
            sciBean.start();
        }
        if (this._servletHandler != null && this._servletHandler.getListeners() != null) {
            for (final ListenerHolder holder : this._servletHandler.getListeners()) {
                this._objFactory.decorate(holder.getListener());
            }
        }
        super.startContext();
        if (this._servletHandler != null) {
            this._servletHandler.initialize();
        }
    }
    
    @Override
    protected void stopContext() throws Exception {
        super.stopContext();
    }
    
    @ManagedAttribute(value = "context security handler", readonly = true)
    public SecurityHandler getSecurityHandler() {
        if (this._securityHandler == null && (this._options & 0x2) != 0x0 && !this.isStarted()) {
            this._securityHandler = this.newSecurityHandler();
        }
        return this._securityHandler;
    }
    
    @ManagedAttribute(value = "context servlet handler", readonly = true)
    public ServletHandler getServletHandler() {
        if (this._servletHandler == null && !this.isStarted()) {
            this._servletHandler = this.newServletHandler();
        }
        return this._servletHandler;
    }
    
    @ManagedAttribute(value = "context session handler", readonly = true)
    public SessionHandler getSessionHandler() {
        if (this._sessionHandler == null && (this._options & 0x1) != 0x0 && !this.isStarted()) {
            this._sessionHandler = this.newSessionHandler();
        }
        return this._sessionHandler;
    }
    
    @ManagedAttribute(value = "context gzip handler", readonly = true)
    public GzipHandler getGzipHandler() {
        if (this._gzipHandler == null && (this._options & 0x4) != 0x0 && !this.isStarted()) {
            this._gzipHandler = new GzipHandler();
        }
        return this._gzipHandler;
    }
    
    public ServletHolder addServlet(final String className, final String pathSpec) {
        return this.getServletHandler().addServletWithMapping(className, pathSpec);
    }
    
    public ServletHolder addServlet(final Class<? extends Servlet> servlet, final String pathSpec) {
        return this.getServletHandler().addServletWithMapping(servlet, pathSpec);
    }
    
    public void addServlet(final ServletHolder servlet, final String pathSpec) {
        this.getServletHandler().addServletWithMapping(servlet, pathSpec);
    }
    
    public void addFilter(final FilterHolder holder, final String pathSpec, final EnumSet<DispatcherType> dispatches) {
        this.getServletHandler().addFilterWithMapping(holder, pathSpec, dispatches);
    }
    
    public FilterHolder addFilter(final Class<? extends Filter> filterClass, final String pathSpec, final EnumSet<DispatcherType> dispatches) {
        return this.getServletHandler().addFilterWithMapping(filterClass, pathSpec, dispatches);
    }
    
    public FilterHolder addFilter(final String filterClass, final String pathSpec, final EnumSet<DispatcherType> dispatches) {
        return this.getServletHandler().addFilterWithMapping(filterClass, pathSpec, dispatches);
    }
    
    protected ServletRegistration.Dynamic dynamicHolderAdded(final ServletHolder holder) {
        return holder.getRegistration();
    }
    
    protected void addRoles(final String... roleNames) {
        if (this._securityHandler != null && this._securityHandler instanceof ConstraintAware) {
            final HashSet<String> union = new HashSet<String>();
            final Set<String> existing = ((ConstraintAware)this._securityHandler).getRoles();
            if (existing != null) {
                union.addAll((Collection<?>)existing);
            }
            union.addAll((Collection<?>)Arrays.asList(roleNames));
            ((ConstraintSecurityHandler)this._securityHandler).setRoles(union);
        }
    }
    
    public Set<String> setServletSecurity(final ServletRegistration.Dynamic registration, final ServletSecurityElement servletSecurityElement) {
        final Collection<String> pathSpecs = registration.getMappings();
        if (pathSpecs != null) {
            for (final String pathSpec : pathSpecs) {
                final List<ConstraintMapping> mappings = ConstraintSecurityHandler.createConstraintsWithMappingsForPath(registration.getName(), pathSpec, servletSecurityElement);
                for (final ConstraintMapping m : mappings) {
                    ((ConstraintAware)this.getSecurityHandler()).addConstraintMapping(m);
                }
            }
        }
        return Collections.emptySet();
    }
    
    public void callContextInitialized(final ServletContextListener l, final ServletContextEvent e) {
        try {
            if (this.isProgrammaticListener(l)) {
                this.getServletContext().setEnabled(false);
            }
            super.callContextInitialized(l, e);
        }
        finally {
            this.getServletContext().setEnabled(true);
        }
    }
    
    public void callContextDestroyed(final ServletContextListener l, final ServletContextEvent e) {
        super.callContextDestroyed(l, e);
    }
    
    private boolean replaceHandler(final Handler handler, final Handler replace) {
        HandlerWrapper wrapper;
        for (wrapper = this; wrapper.getHandler() != handler; wrapper = (HandlerWrapper)wrapper.getHandler()) {
            if (!(wrapper.getHandler() instanceof HandlerWrapper)) {
                return false;
            }
        }
        this.doSetHandler(wrapper, replace);
        return true;
    }
    
    public void setSessionHandler(final SessionHandler sessionHandler) {
        if (this.isStarted()) {
            throw new IllegalStateException("STARTED");
        }
        Handler next = null;
        if (this._sessionHandler != null) {
            next = this._sessionHandler.getHandler();
            this._sessionHandler.setHandler(null);
            this.replaceHandler(this._sessionHandler, sessionHandler);
        }
        this._sessionHandler = sessionHandler;
        if (next != null && this._sessionHandler.getHandler() == null) {
            this._sessionHandler.setHandler(next);
        }
        this.relinkHandlers();
    }
    
    public void setSecurityHandler(final SecurityHandler securityHandler) {
        if (this.isStarted()) {
            throw new IllegalStateException("STARTED");
        }
        Handler next = null;
        if (this._securityHandler != null) {
            next = this._securityHandler.getHandler();
            this._securityHandler.setHandler(null);
            this.replaceHandler(this._securityHandler, securityHandler);
        }
        this._securityHandler = securityHandler;
        if (next != null && this._securityHandler.getHandler() == null) {
            this._securityHandler.setHandler(next);
        }
        this.relinkHandlers();
    }
    
    public void setGzipHandler(final GzipHandler gzipHandler) {
        if (this.isStarted()) {
            throw new IllegalStateException("STARTED");
        }
        Handler next = null;
        if (this._gzipHandler != null) {
            next = this._gzipHandler.getHandler();
            this._gzipHandler.setHandler(null);
            this.replaceHandler(this._gzipHandler, gzipHandler);
        }
        this._gzipHandler = gzipHandler;
        if (next != null && this._gzipHandler.getHandler() == null) {
            this._gzipHandler.setHandler(next);
        }
        this.relinkHandlers();
    }
    
    public void setServletHandler(final ServletHandler servletHandler) {
        if (this.isStarted()) {
            throw new IllegalStateException("STARTED");
        }
        Handler next = null;
        if (this._servletHandler != null) {
            next = this._servletHandler.getHandler();
            this._servletHandler.setHandler(null);
            this.replaceHandler(this._servletHandler, servletHandler);
        }
        this._servletHandler = servletHandler;
        if (next != null && this._servletHandler.getHandler() == null) {
            this._servletHandler.setHandler(next);
        }
        this.relinkHandlers();
    }
    
    @Override
    public void insertHandler(final HandlerWrapper handler) {
        if (handler instanceof SessionHandler) {
            this.setSessionHandler((SessionHandler)handler);
        }
        else if (handler instanceof SecurityHandler) {
            this.setSecurityHandler((SecurityHandler)handler);
        }
        else if (handler instanceof GzipHandler) {
            this.setGzipHandler((GzipHandler)handler);
        }
        else if (handler instanceof ServletHandler) {
            this.setServletHandler((ServletHandler)handler);
        }
        else {
            HandlerWrapper tail;
            for (tail = handler; tail.getHandler() instanceof HandlerWrapper; tail = (HandlerWrapper)tail.getHandler()) {}
            if (tail.getHandler() != null) {
                throw new IllegalArgumentException("bad tail of inserted wrapper chain");
            }
            HandlerWrapper h;
            HandlerWrapper wrapper;
            for (h = this; h.getHandler() instanceof HandlerWrapper; h = wrapper) {
                wrapper = (HandlerWrapper)h.getHandler();
                if (wrapper instanceof SessionHandler || wrapper instanceof SecurityHandler) {
                    break;
                }
                if (wrapper instanceof ServletHandler) {
                    break;
                }
            }
            final Handler next = h.getHandler();
            this.doSetHandler(h, handler);
            this.doSetHandler(tail, next);
        }
        this.relinkHandlers();
    }
    
    public DecoratedObjectFactory getObjectFactory() {
        return this._objFactory;
    }
    
    @Deprecated
    public List<Decorator> getDecorators() {
        final List<Decorator> ret = new ArrayList<Decorator>();
        for (final org.eclipse.jetty.util.Decorator decorator : this._objFactory) {
            ret.add(new LegacyDecorator(decorator));
        }
        return Collections.unmodifiableList((List<? extends Decorator>)ret);
    }
    
    @Deprecated
    public void setDecorators(final List<Decorator> decorators) {
        this._objFactory.setDecorators(decorators);
    }
    
    @Deprecated
    public void addDecorator(final Decorator decorator) {
        this._objFactory.addDecorator(decorator);
    }
    
    void destroyServlet(final Servlet servlet) {
        this._objFactory.destroy(servlet);
    }
    
    void destroyFilter(final Filter filter) {
        this._objFactory.destroy(filter);
    }
    
    static {
        LOG = Log.getLogger(ServletContextHandler.class);
    }
    
    public static class JspPropertyGroup implements JspPropertyGroupDescriptor
    {
        private List<String> _urlPatterns;
        private String _elIgnored;
        private String _pageEncoding;
        private String _scriptingInvalid;
        private String _isXml;
        private List<String> _includePreludes;
        private List<String> _includeCodas;
        private String _deferredSyntaxAllowedAsLiteral;
        private String _trimDirectiveWhitespaces;
        private String _defaultContentType;
        private String _buffer;
        private String _errorOnUndeclaredNamespace;
        
        public JspPropertyGroup() {
            this._urlPatterns = new ArrayList<String>();
            this._includePreludes = new ArrayList<String>();
            this._includeCodas = new ArrayList<String>();
        }
        
        @Override
        public Collection<String> getUrlPatterns() {
            return new ArrayList<String>(this._urlPatterns);
        }
        
        public void addUrlPattern(final String s) {
            if (!this._urlPatterns.contains(s)) {
                this._urlPatterns.add(s);
            }
        }
        
        @Override
        public String getElIgnored() {
            return this._elIgnored;
        }
        
        public void setElIgnored(final String s) {
            this._elIgnored = s;
        }
        
        @Override
        public String getPageEncoding() {
            return this._pageEncoding;
        }
        
        public void setPageEncoding(final String pageEncoding) {
            this._pageEncoding = pageEncoding;
        }
        
        public void setScriptingInvalid(final String scriptingInvalid) {
            this._scriptingInvalid = scriptingInvalid;
        }
        
        public void setIsXml(final String isXml) {
            this._isXml = isXml;
        }
        
        public void setDeferredSyntaxAllowedAsLiteral(final String deferredSyntaxAllowedAsLiteral) {
            this._deferredSyntaxAllowedAsLiteral = deferredSyntaxAllowedAsLiteral;
        }
        
        public void setTrimDirectiveWhitespaces(final String trimDirectiveWhitespaces) {
            this._trimDirectiveWhitespaces = trimDirectiveWhitespaces;
        }
        
        public void setDefaultContentType(final String defaultContentType) {
            this._defaultContentType = defaultContentType;
        }
        
        public void setBuffer(final String buffer) {
            this._buffer = buffer;
        }
        
        public void setErrorOnUndeclaredNamespace(final String errorOnUndeclaredNamespace) {
            this._errorOnUndeclaredNamespace = errorOnUndeclaredNamespace;
        }
        
        @Override
        public String getScriptingInvalid() {
            return this._scriptingInvalid;
        }
        
        @Override
        public String getIsXml() {
            return this._isXml;
        }
        
        @Override
        public Collection<String> getIncludePreludes() {
            return new ArrayList<String>(this._includePreludes);
        }
        
        public void addIncludePrelude(final String prelude) {
            if (!this._includePreludes.contains(prelude)) {
                this._includePreludes.add(prelude);
            }
        }
        
        @Override
        public Collection<String> getIncludeCodas() {
            return new ArrayList<String>(this._includeCodas);
        }
        
        public void addIncludeCoda(final String coda) {
            if (!this._includeCodas.contains(coda)) {
                this._includeCodas.add(coda);
            }
        }
        
        @Override
        public String getDeferredSyntaxAllowedAsLiteral() {
            return this._deferredSyntaxAllowedAsLiteral;
        }
        
        @Override
        public String getTrimDirectiveWhitespaces() {
            return this._trimDirectiveWhitespaces;
        }
        
        @Override
        public String getDefaultContentType() {
            return this._defaultContentType;
        }
        
        @Override
        public String getBuffer() {
            return this._buffer;
        }
        
        @Override
        public String getErrorOnUndeclaredNamespace() {
            return this._errorOnUndeclaredNamespace;
        }
        
        @Override
        public String toString() {
            final StringBuffer sb = new StringBuffer();
            sb.append("JspPropertyGroupDescriptor:");
            sb.append(" el-ignored=" + this._elIgnored);
            sb.append(" is-xml=" + this._isXml);
            sb.append(" page-encoding=" + this._pageEncoding);
            sb.append(" scripting-invalid=" + this._scriptingInvalid);
            sb.append(" deferred-syntax-allowed-as-literal=" + this._deferredSyntaxAllowedAsLiteral);
            sb.append(" trim-directive-whitespaces" + this._trimDirectiveWhitespaces);
            sb.append(" default-content-type=" + this._defaultContentType);
            sb.append(" buffer=" + this._buffer);
            sb.append(" error-on-undeclared-namespace=" + this._errorOnUndeclaredNamespace);
            for (final String prelude : this._includePreludes) {
                sb.append(" include-prelude=" + prelude);
            }
            for (final String coda : this._includeCodas) {
                sb.append(" include-coda=" + coda);
            }
            return sb.toString();
        }
    }
    
    public static class TagLib implements TaglibDescriptor
    {
        private String _uri;
        private String _location;
        
        @Override
        public String getTaglibURI() {
            return this._uri;
        }
        
        public void setTaglibURI(final String uri) {
            this._uri = uri;
        }
        
        @Override
        public String getTaglibLocation() {
            return this._location;
        }
        
        public void setTaglibLocation(final String location) {
            this._location = location;
        }
        
        @Override
        public String toString() {
            return "TagLibDescriptor: taglib-uri=" + this._uri + " location=" + this._location;
        }
    }
    
    public static class JspConfig implements JspConfigDescriptor
    {
        private List<TaglibDescriptor> _taglibs;
        private List<JspPropertyGroupDescriptor> _jspPropertyGroups;
        
        public JspConfig() {
            this._taglibs = new ArrayList<TaglibDescriptor>();
            this._jspPropertyGroups = new ArrayList<JspPropertyGroupDescriptor>();
        }
        
        @Override
        public Collection<TaglibDescriptor> getTaglibs() {
            return new ArrayList<TaglibDescriptor>(this._taglibs);
        }
        
        public void addTaglibDescriptor(final TaglibDescriptor d) {
            this._taglibs.add(d);
        }
        
        @Override
        public Collection<JspPropertyGroupDescriptor> getJspPropertyGroups() {
            return new ArrayList<JspPropertyGroupDescriptor>(this._jspPropertyGroups);
        }
        
        public void addJspPropertyGroup(final JspPropertyGroupDescriptor g) {
            this._jspPropertyGroups.add(g);
        }
        
        @Override
        public String toString() {
            final StringBuffer sb = new StringBuffer();
            sb.append("JspConfigDescriptor: \n");
            for (final TaglibDescriptor taglib : this._taglibs) {
                sb.append(taglib + "\n");
            }
            for (final JspPropertyGroupDescriptor jpg : this._jspPropertyGroups) {
                sb.append(jpg + "\n");
            }
            return sb.toString();
        }
    }
    
    public class Context extends ContextHandler.Context
    {
        @Override
        public RequestDispatcher getNamedDispatcher(final String name) {
            final ContextHandler context = ServletContextHandler.this;
            if (ServletContextHandler.this._servletHandler == null) {
                return null;
            }
            final ServletHolder holder = ServletContextHandler.this._servletHandler.getServlet(name);
            if (holder == null || !holder.isEnabled()) {
                return null;
            }
            return new Dispatcher(context, name);
        }
        
        @Override
        public FilterRegistration.Dynamic addFilter(final String filterName, final Class<? extends Filter> filterClass) {
            if (ServletContextHandler.this.isStarted()) {
                throw new IllegalStateException();
            }
            if (filterName == null || "".equals(filterName.trim())) {
                throw new IllegalStateException("Missing filter name");
            }
            if (!this._enabled) {
                throw new UnsupportedOperationException();
            }
            final ServletHandler handler = ServletContextHandler.this.getServletHandler();
            FilterHolder holder = handler.getFilter(filterName);
            if (holder == null) {
                holder = handler.newFilterHolder(BaseHolder.Source.JAVAX_API);
                holder.setName(filterName);
                holder.setHeldClass(filterClass);
                handler.addFilter(holder);
                return holder.getRegistration();
            }
            if (holder.getClassName() == null && holder.getHeldClass() == null) {
                holder.setHeldClass(filterClass);
                return holder.getRegistration();
            }
            return null;
        }
        
        @Override
        public FilterRegistration.Dynamic addFilter(final String filterName, final String className) {
            if (ServletContextHandler.this.isStarted()) {
                throw new IllegalStateException();
            }
            if (filterName == null || "".equals(filterName.trim())) {
                throw new IllegalStateException("Missing filter name");
            }
            if (!this._enabled) {
                throw new UnsupportedOperationException();
            }
            final ServletHandler handler = ServletContextHandler.this.getServletHandler();
            FilterHolder holder = handler.getFilter(filterName);
            if (holder == null) {
                holder = handler.newFilterHolder(BaseHolder.Source.JAVAX_API);
                holder.setName(filterName);
                holder.setClassName(className);
                handler.addFilter(holder);
                return holder.getRegistration();
            }
            if (holder.getClassName() == null && holder.getHeldClass() == null) {
                holder.setClassName(className);
                return holder.getRegistration();
            }
            return null;
        }
        
        @Override
        public FilterRegistration.Dynamic addFilter(final String filterName, final Filter filter) {
            if (ServletContextHandler.this.isStarted()) {
                throw new IllegalStateException();
            }
            if (filterName == null || "".equals(filterName.trim())) {
                throw new IllegalStateException("Missing filter name");
            }
            if (!this._enabled) {
                throw new UnsupportedOperationException();
            }
            final ServletHandler handler = ServletContextHandler.this.getServletHandler();
            FilterHolder holder = handler.getFilter(filterName);
            if (holder == null) {
                holder = handler.newFilterHolder(BaseHolder.Source.JAVAX_API);
                holder.setName(filterName);
                holder.setFilter(filter);
                handler.addFilter(holder);
                return holder.getRegistration();
            }
            if (holder.getClassName() == null && holder.getHeldClass() == null) {
                holder.setFilter(filter);
                return holder.getRegistration();
            }
            return null;
        }
        
        @Override
        public ServletRegistration.Dynamic addServlet(final String servletName, final Class<? extends Servlet> servletClass) {
            if (!ServletContextHandler.this.isStarting()) {
                throw new IllegalStateException();
            }
            if (servletName == null || "".equals(servletName.trim())) {
                throw new IllegalStateException("Missing servlet name");
            }
            if (!this._enabled) {
                throw new UnsupportedOperationException();
            }
            final ServletHandler handler = ServletContextHandler.this.getServletHandler();
            ServletHolder holder = handler.getServlet(servletName);
            if (holder == null) {
                holder = handler.newServletHolder(BaseHolder.Source.JAVAX_API);
                holder.setName(servletName);
                holder.setHeldClass(servletClass);
                handler.addServlet(holder);
                return ServletContextHandler.this.dynamicHolderAdded(holder);
            }
            if (holder.getClassName() == null && holder.getHeldClass() == null) {
                holder.setHeldClass(servletClass);
                return holder.getRegistration();
            }
            return null;
        }
        
        @Override
        public ServletRegistration.Dynamic addServlet(final String servletName, final String className) {
            if (!ServletContextHandler.this.isStarting()) {
                throw new IllegalStateException();
            }
            if (servletName == null || "".equals(servletName.trim())) {
                throw new IllegalStateException("Missing servlet name");
            }
            if (!this._enabled) {
                throw new UnsupportedOperationException();
            }
            final ServletHandler handler = ServletContextHandler.this.getServletHandler();
            ServletHolder holder = handler.getServlet(servletName);
            if (holder == null) {
                holder = handler.newServletHolder(BaseHolder.Source.JAVAX_API);
                holder.setName(servletName);
                holder.setClassName(className);
                handler.addServlet(holder);
                return ServletContextHandler.this.dynamicHolderAdded(holder);
            }
            if (holder.getClassName() == null && holder.getHeldClass() == null) {
                holder.setClassName(className);
                return holder.getRegistration();
            }
            return null;
        }
        
        @Override
        public ServletRegistration.Dynamic addServlet(final String servletName, final Servlet servlet) {
            if (!ServletContextHandler.this.isStarting()) {
                throw new IllegalStateException();
            }
            if (servletName == null || "".equals(servletName.trim())) {
                throw new IllegalStateException("Missing servlet name");
            }
            if (!this._enabled) {
                throw new UnsupportedOperationException();
            }
            final ServletHandler handler = ServletContextHandler.this.getServletHandler();
            ServletHolder holder = handler.getServlet(servletName);
            if (holder == null) {
                holder = handler.newServletHolder(BaseHolder.Source.JAVAX_API);
                holder.setName(servletName);
                holder.setServlet(servlet);
                handler.addServlet(holder);
                return ServletContextHandler.this.dynamicHolderAdded(holder);
            }
            if (holder.getClassName() == null && holder.getHeldClass() == null) {
                holder.setServlet(servlet);
                return holder.getRegistration();
            }
            return null;
        }
        
        @Override
        public boolean setInitParameter(final String name, final String value) {
            if (!ServletContextHandler.this.isStarting()) {
                throw new IllegalStateException();
            }
            if (!this._enabled) {
                throw new UnsupportedOperationException();
            }
            return super.setInitParameter(name, value);
        }
        
        @Override
        public <T extends Filter> T createFilter(final Class<T> c) throws ServletException {
            try {
                T f = this.createInstance(c);
                f = ServletContextHandler.this._objFactory.decorate(f);
                return f;
            }
            catch (Exception e) {
                throw new ServletException(e);
            }
        }
        
        @Override
        public <T extends Servlet> T createServlet(final Class<T> c) throws ServletException {
            try {
                T s = this.createInstance(c);
                s = ServletContextHandler.this._objFactory.decorate(s);
                return s;
            }
            catch (Exception e) {
                throw new ServletException(e);
            }
        }
        
        @Override
        public Set<SessionTrackingMode> getDefaultSessionTrackingModes() {
            if (ServletContextHandler.this._sessionHandler != null) {
                return ServletContextHandler.this._sessionHandler.getSessionManager().getDefaultSessionTrackingModes();
            }
            return null;
        }
        
        @Override
        public Set<SessionTrackingMode> getEffectiveSessionTrackingModes() {
            if (ServletContextHandler.this._sessionHandler != null) {
                return ServletContextHandler.this._sessionHandler.getSessionManager().getEffectiveSessionTrackingModes();
            }
            return null;
        }
        
        @Override
        public FilterRegistration getFilterRegistration(final String filterName) {
            if (!this._enabled) {
                throw new UnsupportedOperationException();
            }
            final FilterHolder holder = ServletContextHandler.this.getServletHandler().getFilter(filterName);
            return (holder == null) ? null : holder.getRegistration();
        }
        
        @Override
        public Map<String, ? extends FilterRegistration> getFilterRegistrations() {
            if (!this._enabled) {
                throw new UnsupportedOperationException();
            }
            final HashMap<String, FilterRegistration> registrations = new HashMap<String, FilterRegistration>();
            final ServletHandler handler = ServletContextHandler.this.getServletHandler();
            final FilterHolder[] holders = handler.getFilters();
            if (holders != null) {
                for (final FilterHolder holder : holders) {
                    registrations.put(holder.getName(), holder.getRegistration());
                }
            }
            return registrations;
        }
        
        @Override
        public ServletRegistration getServletRegistration(final String servletName) {
            if (!this._enabled) {
                throw new UnsupportedOperationException();
            }
            final ServletHolder holder = ServletContextHandler.this.getServletHandler().getServlet(servletName);
            return (holder == null) ? null : holder.getRegistration();
        }
        
        @Override
        public Map<String, ? extends ServletRegistration> getServletRegistrations() {
            if (!this._enabled) {
                throw new UnsupportedOperationException();
            }
            final HashMap<String, ServletRegistration> registrations = new HashMap<String, ServletRegistration>();
            final ServletHandler handler = ServletContextHandler.this.getServletHandler();
            final ServletHolder[] holders = handler.getServlets();
            if (holders != null) {
                for (final ServletHolder holder : holders) {
                    registrations.put(holder.getName(), holder.getRegistration());
                }
            }
            return registrations;
        }
        
        @Override
        public SessionCookieConfig getSessionCookieConfig() {
            if (!this._enabled) {
                throw new UnsupportedOperationException();
            }
            if (ServletContextHandler.this._sessionHandler != null) {
                return ServletContextHandler.this._sessionHandler.getSessionManager().getSessionCookieConfig();
            }
            return null;
        }
        
        @Override
        public void setSessionTrackingModes(final Set<SessionTrackingMode> sessionTrackingModes) {
            if (!ServletContextHandler.this.isStarting()) {
                throw new IllegalStateException();
            }
            if (!this._enabled) {
                throw new UnsupportedOperationException();
            }
            if (ServletContextHandler.this._sessionHandler != null) {
                ServletContextHandler.this._sessionHandler.getSessionManager().setSessionTrackingModes(sessionTrackingModes);
            }
        }
        
        @Override
        public void addListener(final String className) {
            if (!ServletContextHandler.this.isStarting()) {
                throw new IllegalStateException();
            }
            if (!this._enabled) {
                throw new UnsupportedOperationException();
            }
            super.addListener(className);
        }
        
        @Override
        public <T extends EventListener> void addListener(final T t) {
            if (!ServletContextHandler.this.isStarting()) {
                throw new IllegalStateException();
            }
            if (!this._enabled) {
                throw new UnsupportedOperationException();
            }
            super.addListener(t);
            final ListenerHolder holder = ServletContextHandler.this.getServletHandler().newListenerHolder(BaseHolder.Source.JAVAX_API);
            holder.setListener(t);
            ServletContextHandler.this.getServletHandler().addListener(holder);
        }
        
        @Override
        public void addListener(final Class<? extends EventListener> listenerClass) {
            if (!ServletContextHandler.this.isStarting()) {
                throw new IllegalStateException();
            }
            if (!this._enabled) {
                throw new UnsupportedOperationException();
            }
            super.addListener(listenerClass);
        }
        
        @Override
        public <T extends EventListener> T createListener(final Class<T> clazz) throws ServletException {
            try {
                T l = this.createInstance(clazz);
                l = ServletContextHandler.this._objFactory.decorate(l);
                return l;
            }
            catch (Exception e) {
                throw new ServletException(e);
            }
        }
        
        @Override
        public JspConfigDescriptor getJspConfigDescriptor() {
            return ServletContextHandler.this._jspConfig;
        }
        
        @Override
        public void setJspConfigDescriptor(final JspConfigDescriptor d) {
            ServletContextHandler.this._jspConfig = d;
        }
        
        @Override
        public void declareRoles(final String... roleNames) {
            if (!ServletContextHandler.this.isStarting()) {
                throw new IllegalStateException();
            }
            if (!this._enabled) {
                throw new UnsupportedOperationException();
            }
            ServletContextHandler.this.addRoles(roleNames);
        }
    }
    
    private static class LegacyDecorator implements Decorator
    {
        private org.eclipse.jetty.util.Decorator decorator;
        
        public LegacyDecorator(final org.eclipse.jetty.util.Decorator decorator) {
            this.decorator = decorator;
        }
        
        @Override
        public <T> T decorate(final T o) {
            return this.decorator.decorate(o);
        }
        
        @Override
        public void destroy(final Object o) {
            this.decorator.destroy(o);
        }
    }
    
    @Deprecated
    public interface Decorator extends org.eclipse.jetty.util.Decorator
    {
    }
    
    public interface ServletContainerInitializerCaller extends LifeCycle
    {
    }
}
