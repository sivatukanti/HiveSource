// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.servlet;

import java.util.Stack;
import javax.servlet.ServletSecurityElement;
import java.util.List;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.Collections;
import org.eclipse.jetty.util.log.Log;
import java.lang.reflect.Method;
import org.eclipse.jetty.util.Loader;
import java.io.IOException;
import javax.servlet.MultipartConfigElement;
import javax.servlet.ServletResponse;
import javax.servlet.ServletRequest;
import org.eclipse.jetty.server.Request;
import java.util.EventListener;
import org.eclipse.jetty.server.MultiPartCleanerListener;
import java.io.File;
import org.eclipse.jetty.server.handler.ContextHandler;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.annotation.ServletSecurity;
import javax.servlet.ServletException;
import java.util.Iterator;
import org.eclipse.jetty.util.StringUtil;
import java.util.HashMap;
import org.eclipse.jetty.util.annotation.ManagedAttribute;
import javax.servlet.SingleThreadModel;
import javax.servlet.UnavailableException;
import javax.servlet.ServletRegistration;
import org.eclipse.jetty.security.IdentityService;
import org.eclipse.jetty.security.RunAsToken;
import java.util.Map;
import org.eclipse.jetty.util.log.Logger;
import org.eclipse.jetty.util.annotation.ManagedObject;
import org.eclipse.jetty.server.UserIdentity;
import javax.servlet.Servlet;

@ManagedObject("Servlet Holder")
public class ServletHolder extends Holder<Servlet> implements UserIdentity.Scope, Comparable<ServletHolder>
{
    private static final Logger LOG;
    private int _initOrder;
    private boolean _initOnStartup;
    private Map<String, String> _roleMap;
    private String _forcedPath;
    private String _runAsRole;
    private RunAsToken _runAsToken;
    private IdentityService _identityService;
    private ServletRegistration.Dynamic _registration;
    private JspContainer _jspContainer;
    private transient Servlet _servlet;
    private transient Config _config;
    private transient long _unavailable;
    private transient boolean _enabled;
    private transient UnavailableException _unavailableEx;
    public static final String APACHE_SENTINEL_CLASS = "org.apache.tomcat.InstanceManager";
    public static final String JSP_GENERATED_PACKAGE_NAME = "org.eclipse.jetty.servlet.jspPackagePrefix";
    public static final Map<String, String> NO_MAPPED_ROLES;
    
    public ServletHolder() {
        this(Source.EMBEDDED);
    }
    
    public ServletHolder(final Source creator) {
        super(creator);
        this._initOrder = -1;
        this._initOnStartup = false;
        this._enabled = true;
    }
    
    public ServletHolder(final Servlet servlet) {
        this(Source.EMBEDDED);
        this.setServlet(servlet);
    }
    
    public ServletHolder(final String name, final Class<? extends Servlet> servlet) {
        this(Source.EMBEDDED);
        this.setName(name);
        this.setHeldClass(servlet);
    }
    
    public ServletHolder(final String name, final Servlet servlet) {
        this(Source.EMBEDDED);
        this.setName(name);
        this.setServlet(servlet);
    }
    
    public ServletHolder(final Class<? extends Servlet> servlet) {
        this(Source.EMBEDDED);
        this.setHeldClass(servlet);
    }
    
    public UnavailableException getUnavailableException() {
        return this._unavailableEx;
    }
    
    public synchronized void setServlet(final Servlet servlet) {
        if (servlet == null || servlet instanceof SingleThreadModel) {
            throw new IllegalArgumentException();
        }
        this._extInstance = true;
        this._servlet = servlet;
        this.setHeldClass(servlet.getClass());
        if (this.getName() == null) {
            this.setName(servlet.getClass().getName() + "-" + super.hashCode());
        }
    }
    
    @ManagedAttribute(value = "initialization order", readonly = true)
    public int getInitOrder() {
        return this._initOrder;
    }
    
    public void setInitOrder(final int order) {
        this._initOnStartup = (order >= 0);
        this._initOrder = order;
    }
    
    @Override
    public int compareTo(final ServletHolder sh) {
        if (sh == this) {
            return 0;
        }
        if (sh._initOrder < this._initOrder) {
            return 1;
        }
        if (sh._initOrder > this._initOrder) {
            return -1;
        }
        int c;
        if (this._className == null && sh._className == null) {
            c = 0;
        }
        else if (this._className == null) {
            c = -1;
        }
        else if (sh._className == null) {
            c = 1;
        }
        else {
            c = this._className.compareTo(sh._className);
        }
        if (c == 0) {
            c = this._name.compareTo(sh._name);
        }
        return c;
    }
    
    @Override
    public boolean equals(final Object o) {
        return o instanceof ServletHolder && this.compareTo((ServletHolder)o) == 0;
    }
    
    @Override
    public int hashCode() {
        return (this._name == null) ? System.identityHashCode(this) : this._name.hashCode();
    }
    
    public synchronized void setUserRoleLink(final String name, final String link) {
        if (this._roleMap == null) {
            this._roleMap = new HashMap<String, String>();
        }
        this._roleMap.put(name, link);
    }
    
    public String getUserRoleLink(final String name) {
        if (this._roleMap == null) {
            return name;
        }
        final String link = this._roleMap.get(name);
        return (link == null) ? name : link;
    }
    
    @ManagedAttribute(value = "forced servlet path", readonly = true)
    public String getForcedPath() {
        return this._forcedPath;
    }
    
    public void setForcedPath(final String forcedPath) {
        this._forcedPath = forcedPath;
    }
    
    public boolean isEnabled() {
        return this._enabled;
    }
    
    public void setEnabled(final boolean enabled) {
        this._enabled = enabled;
    }
    
    @Override
    public void doStart() throws Exception {
        this._unavailable = 0L;
        if (!this._enabled) {
            return;
        }
        if (this._forcedPath != null) {
            final String precompiled = this.getClassNameForJsp(this._forcedPath);
            if (!StringUtil.isBlank(precompiled)) {
                if (ServletHolder.LOG.isDebugEnabled()) {
                    ServletHolder.LOG.debug("Checking for precompiled servlet {} for jsp {}", precompiled, this._forcedPath);
                }
                ServletHolder jsp = this.getServletHandler().getServlet(precompiled);
                if (jsp != null && jsp.getClassName() != null) {
                    if (ServletHolder.LOG.isDebugEnabled()) {
                        ServletHolder.LOG.debug("JSP file {} for {} mapped to Servlet {}", this._forcedPath, this.getName(), jsp.getClassName());
                    }
                    this.setClassName(jsp.getClassName());
                }
                else {
                    jsp = this.getServletHandler().getServlet("jsp");
                    if (jsp != null) {
                        if (ServletHolder.LOG.isDebugEnabled()) {
                            ServletHolder.LOG.debug("JSP file {} for {} mapped to JspServlet class {}", this._forcedPath, this.getName(), jsp.getClassName());
                        }
                        this.setClassName(jsp.getClassName());
                        for (final Map.Entry<String, String> entry : jsp.getInitParameters().entrySet()) {
                            if (!this._initParams.containsKey(entry.getKey())) {
                                this.setInitParameter(entry.getKey(), entry.getValue());
                            }
                        }
                        this.setInitParameter("jspFile", this._forcedPath);
                    }
                }
            }
            else {
                ServletHolder.LOG.warn("Bad jsp-file {} conversion to classname in holder {}", this._forcedPath, this.getName());
            }
        }
        try {
            super.doStart();
        }
        catch (UnavailableException ue) {
            this.makeUnavailable(ue);
            if (this._servletHandler.isStartWithUnavailable()) {
                ServletHolder.LOG.ignore(ue);
                return;
            }
            throw ue;
        }
        try {
            this.checkServletType();
        }
        catch (UnavailableException ue) {
            this.makeUnavailable(ue);
            if (this._servletHandler.isStartWithUnavailable()) {
                ServletHolder.LOG.ignore(ue);
                return;
            }
            throw ue;
        }
        this.checkInitOnStartup();
        this._identityService = this._servletHandler.getIdentityService();
        if (this._identityService != null && this._runAsRole != null) {
            this._runAsToken = this._identityService.newRunAsToken(this._runAsRole);
        }
        this._config = new Config();
        if (this._class != null && SingleThreadModel.class.isAssignableFrom(this._class)) {
            this._servlet = new SingleThreadedWrapper();
        }
    }
    
    @Override
    public void initialize() throws Exception {
        Label_0057: {
            if (!this._initialized) {
                super.initialize();
                if (!this._extInstance) {
                    if (!this._initOnStartup) {
                        break Label_0057;
                    }
                }
                try {
                    this.initServlet();
                }
                catch (Exception e) {
                    if (!this._servletHandler.isStartWithUnavailable()) {
                        throw e;
                    }
                    ServletHolder.LOG.ignore(e);
                }
            }
        }
        this._initialized = true;
    }
    
    @Override
    public void doStop() throws Exception {
        Object old_run_as = null;
        if (this._servlet != null) {
            try {
                if (this._identityService != null) {
                    old_run_as = this._identityService.setRunAs(this._identityService.getSystemUserIdentity(), this._runAsToken);
                }
                this.destroyInstance(this._servlet);
            }
            catch (Exception e) {
                ServletHolder.LOG.warn(e);
            }
            finally {
                if (this._identityService != null) {
                    this._identityService.unsetRunAs(old_run_as);
                }
            }
        }
        if (!this._extInstance) {
            this._servlet = null;
        }
        this._config = null;
        this._initialized = false;
    }
    
    @Override
    public void destroyInstance(final Object o) throws Exception {
        if (o == null) {
            return;
        }
        final Servlet servlet = (Servlet)o;
        this.getServletHandler().destroyServlet(servlet);
        servlet.destroy();
    }
    
    public synchronized Servlet getServlet() throws ServletException {
        if (this._unavailable != 0L) {
            if (this._unavailable < 0L || (this._unavailable > 0L && System.currentTimeMillis() < this._unavailable)) {
                throw this._unavailableEx;
            }
            this._unavailable = 0L;
            this._unavailableEx = null;
        }
        if (this._servlet == null) {
            this.initServlet();
        }
        return this._servlet;
    }
    
    public Servlet getServletInstance() {
        return this._servlet;
    }
    
    public void checkServletType() throws UnavailableException {
        if (this._class == null || !Servlet.class.isAssignableFrom(this._class)) {
            throw new UnavailableException("Servlet " + this._class + " is not a javax.servlet.Servlet");
        }
    }
    
    public boolean isAvailable() {
        if (this.isStarted() && this._unavailable == 0L) {
            return true;
        }
        try {
            this.getServlet();
        }
        catch (Exception e) {
            ServletHolder.LOG.ignore(e);
        }
        return this.isStarted() && this._unavailable == 0L;
    }
    
    private void checkInitOnStartup() {
        if (this._class == null) {
            return;
        }
        if (this._class.getAnnotation(ServletSecurity.class) != null && !this._initOnStartup) {
            this.setInitOrder(Integer.MAX_VALUE);
        }
    }
    
    private void makeUnavailable(final UnavailableException e) {
        if (this._unavailableEx == e && this._unavailable != 0L) {
            return;
        }
        this._servletHandler.getServletContext().log("unavailable", e);
        this._unavailableEx = e;
        this._unavailable = -1L;
        if (e.isPermanent()) {
            this._unavailable = -1L;
        }
        else if (this._unavailableEx.getUnavailableSeconds() > 0) {
            this._unavailable = System.currentTimeMillis() + 1000 * this._unavailableEx.getUnavailableSeconds();
        }
        else {
            this._unavailable = System.currentTimeMillis() + 5000L;
        }
    }
    
    private void makeUnavailable(final Throwable e) {
        if (e instanceof UnavailableException) {
            this.makeUnavailable((UnavailableException)e);
        }
        else {
            final ServletContext ctx = this._servletHandler.getServletContext();
            if (ctx == null) {
                ServletHolder.LOG.info("unavailable", e);
            }
            else {
                ctx.log("unavailable", e);
            }
            this._unavailableEx = new UnavailableException(String.valueOf(e), -1) {
                {
                    this.initCause(e);
                }
            };
            this._unavailable = -1L;
        }
    }
    
    private void initServlet() throws ServletException {
        Object old_run_as = null;
        try {
            if (this._servlet == null) {
                this._servlet = this.newInstance();
            }
            if (this._config == null) {
                this._config = new Config();
            }
            if (this._identityService != null) {
                old_run_as = this._identityService.setRunAs(this._identityService.getSystemUserIdentity(), this._runAsToken);
            }
            if (this.isJspServlet()) {
                this.initJspServlet();
                this.detectJspContainer();
            }
            this.initMultiPart();
            if (this._forcedPath != null && this._jspContainer == null) {
                this.detectJspContainer();
            }
            if (ServletHolder.LOG.isDebugEnabled()) {
                ServletHolder.LOG.debug("Servlet.init {} for {}", this._servlet, this.getName());
            }
            this._servlet.init(this._config);
        }
        catch (UnavailableException e) {
            this.makeUnavailable(e);
            this._servlet = null;
            this._config = null;
            throw e;
        }
        catch (ServletException e2) {
            this.makeUnavailable((e2.getCause() == null) ? e2 : e2.getCause());
            this._servlet = null;
            this._config = null;
            throw e2;
        }
        catch (Exception e3) {
            this.makeUnavailable(e3);
            this._servlet = null;
            this._config = null;
            throw new ServletException(this.toString(), e3);
        }
        finally {
            if (this._identityService != null) {
                this._identityService.unsetRunAs(old_run_as);
            }
        }
    }
    
    protected void initJspServlet() throws Exception {
        final ContextHandler ch = ContextHandler.getContextHandler(this.getServletHandler().getServletContext());
        ch.setAttribute("org.apache.catalina.jsp_classpath", ch.getClassPath());
        if ("?".equals(this.getInitParameter("classpath"))) {
            final String classpath = ch.getClassPath();
            if (ServletHolder.LOG.isDebugEnabled()) {
                ServletHolder.LOG.debug("classpath=" + classpath, new Object[0]);
            }
            if (classpath != null) {
                this.setInitParameter("classpath", classpath);
            }
        }
        File scratch = null;
        if (this.getInitParameter("scratchdir") == null) {
            final File tmp = (File)this.getServletHandler().getServletContext().getAttribute("javax.servlet.context.tempdir");
            scratch = new File(tmp, "jsp");
            this.setInitParameter("scratchdir", scratch.getAbsolutePath());
        }
        scratch = new File(this.getInitParameter("scratchdir"));
        if (!scratch.exists()) {
            scratch.mkdir();
        }
    }
    
    protected void initMultiPart() throws Exception {
        if (((Registration)this.getRegistration()).getMultipartConfig() != null) {
            final ContextHandler ch = ContextHandler.getContextHandler(this.getServletHandler().getServletContext());
            ch.addEventListener(MultiPartCleanerListener.INSTANCE);
        }
    }
    
    @Override
    public String getContextPath() {
        return this._config.getServletContext().getContextPath();
    }
    
    @Override
    public Map<String, String> getRoleRefMap() {
        return this._roleMap;
    }
    
    @ManagedAttribute(value = "role to run servlet as", readonly = true)
    public String getRunAsRole() {
        return this._runAsRole;
    }
    
    public void setRunAsRole(final String role) {
        this._runAsRole = role;
    }
    
    protected void prepare(final Request baseRequest, final ServletRequest request, final ServletResponse response) throws ServletException, UnavailableException {
        this.ensureInstance();
        final MultipartConfigElement mpce = ((Registration)this.getRegistration()).getMultipartConfig();
        if (mpce != null) {
            baseRequest.setAttribute("org.eclipse.jetty.multipartConfig", mpce);
        }
    }
    
    public synchronized Servlet ensureInstance() throws ServletException, UnavailableException {
        if (this._class == null) {
            throw new UnavailableException("Servlet Not Initialized");
        }
        Servlet servlet = this._servlet;
        if (!this.isStarted()) {
            throw new UnavailableException("Servlet not initialized", -1);
        }
        if (this._unavailable != 0L || (!this._initOnStartup && servlet == null)) {
            servlet = this.getServlet();
        }
        if (servlet == null) {
            throw new UnavailableException("Could not instantiate " + this._class);
        }
        return servlet;
    }
    
    public void handle(final Request baseRequest, final ServletRequest request, final ServletResponse response) throws ServletException, UnavailableException, IOException {
        if (this._class == null) {
            throw new UnavailableException("Servlet Not Initialized");
        }
        final Servlet servlet = this.ensureInstance();
        boolean servlet_error = true;
        Object old_run_as = null;
        final boolean suspendable = baseRequest.isAsyncSupported();
        try {
            if (this._forcedPath != null) {
                this.adaptForcedPathToJspContainer(request);
            }
            if (this._identityService != null) {
                old_run_as = this._identityService.setRunAs(baseRequest.getResolvedUserIdentity(), this._runAsToken);
            }
            if (baseRequest.isAsyncSupported() && !this.isAsyncSupported()) {
                try {
                    baseRequest.setAsyncSupported(false, this.toString());
                    servlet.service(request, response);
                }
                finally {
                    baseRequest.setAsyncSupported(true, null);
                }
            }
            else {
                servlet.service(request, response);
            }
            servlet_error = false;
        }
        catch (UnavailableException e) {
            this.makeUnavailable(e);
            throw this._unavailableEx;
        }
        finally {
            if (this._identityService != null) {
                this._identityService.unsetRunAs(old_run_as);
            }
            if (servlet_error) {
                request.setAttribute("javax.servlet.error.servlet_name", this.getName());
            }
        }
    }
    
    private boolean isJspServlet() {
        if (this._servlet == null) {
            return false;
        }
        Class<?> c;
        boolean result;
        for (c = this._servlet.getClass(), result = false; c != null && !result; result = this.isJspServlet(c.getName()), c = c.getSuperclass()) {}
        return result;
    }
    
    private boolean isJspServlet(final String classname) {
        return classname != null && "org.apache.jasper.servlet.JspServlet".equals(classname);
    }
    
    private void adaptForcedPathToJspContainer(final ServletRequest request) {
    }
    
    private void detectJspContainer() {
        if (this._jspContainer == null) {
            try {
                Loader.loadClass(Holder.class, "org.apache.tomcat.InstanceManager");
                if (ServletHolder.LOG.isDebugEnabled()) {
                    ServletHolder.LOG.debug("Apache jasper detected", new Object[0]);
                }
                this._jspContainer = JspContainer.APACHE;
            }
            catch (ClassNotFoundException x) {
                if (ServletHolder.LOG.isDebugEnabled()) {
                    ServletHolder.LOG.debug("Other jasper detected", new Object[0]);
                }
                this._jspContainer = JspContainer.OTHER;
            }
        }
    }
    
    public String getNameOfJspClass(String jsp) {
        if (StringUtil.isBlank(jsp)) {
            return "";
        }
        jsp = jsp.trim();
        if ("/".equals(jsp)) {
            return "";
        }
        final int i = jsp.lastIndexOf(47);
        if (i == jsp.length() - 1) {
            return "";
        }
        jsp = jsp.substring(i + 1);
        try {
            final Class<?> jspUtil = (Class<?>)Loader.loadClass(Holder.class, "org.apache.jasper.compiler.JspUtil");
            final Method makeJavaIdentifier = jspUtil.getMethod("makeJavaIdentifier", String.class);
            return (String)makeJavaIdentifier.invoke(null, jsp);
        }
        catch (Exception e) {
            final String tmp = jsp.replace('.', '_');
            if (ServletHolder.LOG.isDebugEnabled()) {
                ServletHolder.LOG.warn("JspUtil.makeJavaIdentifier failed for jsp " + jsp + " using " + tmp + " instead", new Object[0]);
                ServletHolder.LOG.warn(e);
            }
            return tmp;
        }
    }
    
    public String getPackageOfJspClass(final String jsp) {
        if (jsp == null) {
            return "";
        }
        final int i = jsp.lastIndexOf(47);
        if (i <= 0) {
            return "";
        }
        try {
            final Class<?> jspUtil = (Class<?>)Loader.loadClass(Holder.class, "org.apache.jasper.compiler.JspUtil");
            final Method makeJavaPackage = jspUtil.getMethod("makeJavaPackage", String.class);
            final String p = (String)makeJavaPackage.invoke(null, jsp.substring(0, i));
            return p;
        }
        catch (Exception e) {
            String tmp = jsp;
            int s = 0;
            if ('/' == tmp.charAt(0)) {
                s = 1;
            }
            tmp = tmp.substring(s, i);
            tmp = tmp.replace('/', '.').trim();
            tmp = (".".equals(tmp) ? "" : tmp);
            if (ServletHolder.LOG.isDebugEnabled()) {
                ServletHolder.LOG.warn("JspUtil.makeJavaPackage failed for " + jsp + " using " + tmp + " instead", new Object[0]);
                ServletHolder.LOG.warn(e);
            }
            return tmp;
        }
    }
    
    public String getJspPackagePrefix() {
        String jspPackageName = null;
        if (this.getServletHandler() != null && this.getServletHandler().getServletContext() != null) {
            jspPackageName = this.getServletHandler().getServletContext().getInitParameter("org.eclipse.jetty.servlet.jspPackagePrefix");
        }
        if (jspPackageName == null) {
            jspPackageName = "org.apache.jsp";
        }
        return jspPackageName;
    }
    
    public String getClassNameForJsp(final String jsp) {
        if (jsp == null) {
            return null;
        }
        final String name = this.getNameOfJspClass(jsp);
        if (StringUtil.isBlank(name)) {
            return null;
        }
        final StringBuffer fullName = new StringBuffer();
        this.appendPath(fullName, this.getJspPackagePrefix());
        this.appendPath(fullName, this.getPackageOfJspClass(jsp));
        this.appendPath(fullName, name);
        return fullName.toString();
    }
    
    protected void appendPath(final StringBuffer path, final String element) {
        if (StringUtil.isBlank(element)) {
            return;
        }
        if (path.length() > 0) {
            path.append(".");
        }
        path.append(element);
    }
    
    public ServletRegistration.Dynamic getRegistration() {
        if (this._registration == null) {
            this._registration = new Registration();
        }
        return this._registration;
    }
    
    protected Servlet newInstance() throws ServletException, IllegalAccessException, InstantiationException {
        try {
            final ServletContext ctx = this.getServletHandler().getServletContext();
            if (ctx instanceof ServletContextHandler.Context) {
                return ((ServletContextHandler.Context)ctx).createServlet(this.getHeldClass());
            }
            return (Servlet)this.getHeldClass().newInstance();
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
    
    @Override
    public String toString() {
        return String.format("%s@%x==%s,jsp=%s,order=%d,inst=%b", this._name, this.hashCode(), this._className, this._forcedPath, this._initOrder, this._servlet != null);
    }
    
    static {
        LOG = Log.getLogger(ServletHolder.class);
        NO_MAPPED_ROLES = Collections.emptyMap();
    }
    
    public enum JspContainer
    {
        APACHE, 
        OTHER;
    }
    
    protected class Config extends HolderConfig implements ServletConfig
    {
        @Override
        public String getServletName() {
            return ServletHolder.this.getName();
        }
    }
    
    public class Registration extends HolderRegistration implements ServletRegistration.Dynamic
    {
        protected MultipartConfigElement _multipartConfig;
        
        @Override
        public Set<String> addMapping(final String... urlPatterns) {
            ServletHolder.this.illegalStateIfContextStarted();
            Set<String> clash = null;
            for (final String pattern : urlPatterns) {
                final ServletMapping mapping = ServletHolder.this._servletHandler.getServletMapping(pattern);
                if (mapping != null && !mapping.isDefault()) {
                    if (clash == null) {
                        clash = new HashSet<String>();
                    }
                    clash.add(pattern);
                }
            }
            if (clash != null) {
                return clash;
            }
            final ServletMapping mapping2 = new ServletMapping();
            mapping2.setServletName(ServletHolder.this.getName());
            mapping2.setPathSpecs(urlPatterns);
            ServletHolder.this._servletHandler.addServletMapping(mapping2);
            return Collections.emptySet();
        }
        
        @Override
        public Collection<String> getMappings() {
            final ServletMapping[] mappings = ServletHolder.this._servletHandler.getServletMappings();
            final List<String> patterns = new ArrayList<String>();
            if (mappings != null) {
                for (final ServletMapping mapping : mappings) {
                    if (mapping.getServletName().equals(this.getName())) {
                        final String[] specs = mapping.getPathSpecs();
                        if (specs != null && specs.length > 0) {
                            patterns.addAll(Arrays.asList(specs));
                        }
                    }
                }
            }
            return patterns;
        }
        
        @Override
        public String getRunAsRole() {
            return ServletHolder.this._runAsRole;
        }
        
        @Override
        public void setLoadOnStartup(final int loadOnStartup) {
            ServletHolder.this.illegalStateIfContextStarted();
            ServletHolder.this.setInitOrder(loadOnStartup);
        }
        
        public int getInitOrder() {
            return ServletHolder.this.getInitOrder();
        }
        
        @Override
        public void setMultipartConfig(final MultipartConfigElement element) {
            this._multipartConfig = element;
        }
        
        public MultipartConfigElement getMultipartConfig() {
            return this._multipartConfig;
        }
        
        @Override
        public void setRunAsRole(final String role) {
            ServletHolder.this._runAsRole = role;
        }
        
        @Override
        public Set<String> setServletSecurity(final ServletSecurityElement securityElement) {
            return ServletHolder.this._servletHandler.setServletSecurity(this, securityElement);
        }
    }
    
    private class SingleThreadedWrapper implements Servlet
    {
        Stack<Servlet> _stack;
        
        private SingleThreadedWrapper() {
            this._stack = new Stack<Servlet>();
        }
        
        @Override
        public void destroy() {
            synchronized (this) {
                while (this._stack.size() > 0) {
                    try {
                        this._stack.pop().destroy();
                    }
                    catch (Exception e) {
                        ServletHolder.LOG.warn(e);
                    }
                }
            }
        }
        
        @Override
        public ServletConfig getServletConfig() {
            return ServletHolder.this._config;
        }
        
        @Override
        public String getServletInfo() {
            return null;
        }
        
        @Override
        public void init(final ServletConfig config) throws ServletException {
            synchronized (this) {
                if (this._stack.size() == 0) {
                    try {
                        final Servlet s = ServletHolder.this.newInstance();
                        s.init(config);
                        this._stack.push(s);
                    }
                    catch (ServletException e) {
                        throw e;
                    }
                    catch (Exception e2) {
                        throw new ServletException(e2);
                    }
                }
            }
        }
        
        @Override
        public void service(final ServletRequest req, final ServletResponse res) throws ServletException, IOException {
            Servlet s;
            synchronized (this) {
                if (this._stack.size() > 0) {
                    s = this._stack.pop();
                }
                else {
                    try {
                        s = ServletHolder.this.newInstance();
                        s.init(ServletHolder.this._config);
                    }
                    catch (ServletException e) {
                        throw e;
                    }
                    catch (Exception e2) {
                        throw new ServletException(e2);
                    }
                }
            }
            try {
                s.service(req, res);
            }
            finally {
                synchronized (this) {
                    this._stack.push(s);
                }
            }
        }
    }
}
