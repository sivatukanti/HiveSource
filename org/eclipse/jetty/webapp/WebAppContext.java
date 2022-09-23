// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.webapp;

import javax.servlet.ServletContext;
import org.eclipse.jetty.util.resource.ResourceCollection;
import java.net.URL;
import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.security.ConstraintAware;
import org.eclipse.jetty.security.ConstraintMapping;
import javax.servlet.HttpConstraintElement;
import org.eclipse.jetty.security.ConstraintSecurityHandler;
import java.util.HashSet;
import java.util.Set;
import javax.servlet.ServletSecurityElement;
import javax.servlet.ServletRegistration;
import java.net.URLClassLoader;
import javax.servlet.http.HttpSessionIdListener;
import javax.servlet.http.HttpSessionListener;
import javax.servlet.http.HttpSessionBindingListener;
import javax.servlet.http.HttpSessionAttributeListener;
import javax.servlet.http.HttpSessionActivationListener;
import java.util.EventListener;
import java.util.Arrays;
import org.eclipse.jetty.util.AttributesMap;
import org.eclipse.jetty.util.component.DumpableCollection;
import org.eclipse.jetty.server.ClassLoaderDump;
import org.eclipse.jetty.util.Loader;
import java.util.Collection;
import org.eclipse.jetty.server.Server;
import java.util.concurrent.Callable;
import java.util.Collections;
import org.eclipse.jetty.util.annotation.ManagedAttribute;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.util.MultiException;
import java.util.Iterator;
import java.io.IOException;
import java.net.MalformedURLException;
import org.eclipse.jetty.util.resource.Resource;
import java.util.HashMap;
import java.util.ArrayList;
import org.eclipse.jetty.server.handler.ErrorHandler;
import org.eclipse.jetty.servlet.ServletHandler;
import org.eclipse.jetty.security.SecurityHandler;
import org.eclipse.jetty.server.session.SessionHandler;
import org.eclipse.jetty.server.HandlerContainer;
import org.eclipse.jetty.servlet.ErrorPageErrorHandler;
import org.eclipse.jetty.server.handler.ContextHandler;
import java.util.Map;
import java.io.File;
import java.security.PermissionCollection;
import java.util.List;
import org.eclipse.jetty.util.log.Logger;
import org.eclipse.jetty.util.annotation.ManagedObject;
import org.eclipse.jetty.servlet.ServletContextHandler;

@ManagedObject("Web Application ContextHandler")
public class WebAppContext extends ServletContextHandler implements WebAppClassLoader.Context
{
    private static final Logger LOG;
    public static final String TEMPDIR = "javax.servlet.context.tempdir";
    public static final String BASETEMPDIR = "org.eclipse.jetty.webapp.basetempdir";
    public static final String WEB_DEFAULTS_XML = "org/eclipse/jetty/webapp/webdefault.xml";
    public static final String ERROR_PAGE = "org.eclipse.jetty.server.error_page";
    public static final String SERVER_SYS_CLASSES = "org.eclipse.jetty.webapp.systemClasses";
    public static final String SERVER_SRV_CLASSES = "org.eclipse.jetty.webapp.serverClasses";
    private String[] __dftProtectedTargets;
    public static final String[] DEFAULT_CONFIGURATION_CLASSES;
    public static final String[] __dftSystemClasses;
    public static final String[] __dftServerClasses;
    private final List<String> _configurationClasses;
    private ClasspathPattern _systemClasses;
    private ClasspathPattern _serverClasses;
    private final List<Configuration> _configurations;
    private String _defaultsDescriptor;
    private String _descriptor;
    private final List<String> _overrideDescriptors;
    private boolean _distributable;
    private boolean _extractWAR;
    private boolean _copyDir;
    private boolean _copyWebInf;
    private boolean _logUrlOnStart;
    private boolean _parentLoaderPriority;
    private PermissionCollection _permissions;
    private String[] _contextWhiteList;
    private File _tmpDir;
    private boolean _persistTmpDir;
    private String _war;
    private String _extraClasspath;
    private Throwable _unavailableException;
    private Map<String, String> _resourceAliases;
    private boolean _ownClassLoader;
    private boolean _configurationDiscovered;
    private boolean _allowDuplicateFragmentNames;
    private boolean _throwUnavailableOnStartupException;
    private boolean _checkingServerClasses;
    private MetaData _metadata;
    
    public static WebAppContext getCurrentWebAppContext() {
        final ContextHandler.Context context = ContextHandler.getCurrentContext();
        if (context != null) {
            final ContextHandler handler = context.getContextHandler();
            if (handler instanceof WebAppContext) {
                return (WebAppContext)handler;
            }
        }
        return null;
    }
    
    public WebAppContext() {
        this(null, null, null, null, null, new ErrorPageErrorHandler(), 3);
    }
    
    public WebAppContext(final String webApp, final String contextPath) {
        this(null, contextPath, null, null, null, new ErrorPageErrorHandler(), 3);
        this.setWar(webApp);
    }
    
    public WebAppContext(final HandlerContainer parent, final String webApp, final String contextPath) {
        this(parent, contextPath, null, null, null, new ErrorPageErrorHandler(), 3);
        this.setWar(webApp);
    }
    
    public WebAppContext(final SessionHandler sessionHandler, final SecurityHandler securityHandler, final ServletHandler servletHandler, final ErrorHandler errorHandler) {
        this(null, null, sessionHandler, securityHandler, servletHandler, errorHandler, 0);
    }
    
    public WebAppContext(final HandlerContainer parent, final String contextPath, final SessionHandler sessionHandler, final SecurityHandler securityHandler, final ServletHandler servletHandler, final ErrorHandler errorHandler, final int options) {
        super(parent, contextPath, sessionHandler, securityHandler, servletHandler, errorHandler, options);
        this.__dftProtectedTargets = new String[] { "/web-inf", "/meta-inf" };
        this._configurationClasses = new ArrayList<String>();
        this._systemClasses = null;
        this._serverClasses = null;
        this._configurations = new ArrayList<Configuration>();
        this._defaultsDescriptor = "org/eclipse/jetty/webapp/webdefault.xml";
        this._descriptor = null;
        this._overrideDescriptors = new ArrayList<String>();
        this._distributable = false;
        this._extractWAR = true;
        this._copyDir = false;
        this._copyWebInf = false;
        this._logUrlOnStart = false;
        this._parentLoaderPriority = Boolean.getBoolean("org.eclipse.jetty.server.webapp.parentLoaderPriority");
        this._contextWhiteList = null;
        this._persistTmpDir = false;
        this._ownClassLoader = false;
        this._configurationDiscovered = true;
        this._allowDuplicateFragmentNames = false;
        this._throwUnavailableOnStartupException = false;
        this._checkingServerClasses = true;
        this._metadata = new MetaData();
        this._scontext = new Context();
        this.setErrorHandler((errorHandler != null) ? errorHandler : new ErrorPageErrorHandler());
        this.setProtectedTargets(this.__dftProtectedTargets);
    }
    
    @Override
    public void setDisplayName(final String servletContextName) {
        super.setDisplayName(servletContextName);
        final ClassLoader cl = this.getClassLoader();
        if (cl != null && cl instanceof WebAppClassLoader && servletContextName != null) {
            ((WebAppClassLoader)cl).setName(servletContextName);
        }
    }
    
    public Throwable getUnavailableException() {
        return this._unavailableException;
    }
    
    public void setResourceAlias(final String alias, final String uri) {
        if (this._resourceAliases == null) {
            this._resourceAliases = new HashMap<String, String>(5);
        }
        this._resourceAliases.put(alias, uri);
    }
    
    public Map<String, String> getResourceAliases() {
        if (this._resourceAliases == null) {
            return null;
        }
        return this._resourceAliases;
    }
    
    public void setResourceAliases(final Map<String, String> map) {
        this._resourceAliases = map;
    }
    
    public String getResourceAlias(final String path) {
        if (this._resourceAliases == null) {
            return null;
        }
        String alias = this._resourceAliases.get(path);
        int slash = path.length();
        while (alias == null) {
            slash = path.lastIndexOf("/", slash - 1);
            if (slash < 0) {
                break;
            }
            final String match = this._resourceAliases.get(path.substring(0, slash + 1));
            if (match == null) {
                continue;
            }
            alias = match + path.substring(slash + 1);
        }
        return alias;
    }
    
    public String removeResourceAlias(final String alias) {
        if (this._resourceAliases == null) {
            return null;
        }
        return this._resourceAliases.remove(alias);
    }
    
    @Override
    public void setClassLoader(final ClassLoader classLoader) {
        super.setClassLoader(classLoader);
        String name = this.getDisplayName();
        if (name == null) {
            name = this.getContextPath();
        }
        if (classLoader != null && classLoader instanceof WebAppClassLoader && this.getDisplayName() != null) {
            ((WebAppClassLoader)classLoader).setName(name);
        }
    }
    
    @Override
    public Resource getResource(String uriInContext) throws MalformedURLException {
        if (uriInContext == null || !uriInContext.startsWith("/")) {
            throw new MalformedURLException(uriInContext);
        }
        IOException ioe = null;
        Resource resource = null;
        int loop = 0;
        while (uriInContext != null && loop++ < 100) {
            try {
                resource = super.getResource(uriInContext);
                if (resource != null && resource.exists()) {
                    return resource;
                }
                uriInContext = this.getResourceAlias(uriInContext);
            }
            catch (IOException e) {
                WebAppContext.LOG.ignore(e);
                if (ioe != null) {
                    continue;
                }
                ioe = e;
            }
        }
        if (ioe != null && ioe instanceof MalformedURLException) {
            throw (MalformedURLException)ioe;
        }
        return resource;
    }
    
    public boolean isConfigurationDiscovered() {
        return this._configurationDiscovered;
    }
    
    public void setConfigurationDiscovered(final boolean discovered) {
        this._configurationDiscovered = discovered;
    }
    
    public void preConfigure() throws Exception {
        this.loadConfigurations();
        this.loadSystemClasses();
        this.loadServerClasses();
        this._ownClassLoader = false;
        if (this.getClassLoader() == null) {
            final WebAppClassLoader classLoader = new WebAppClassLoader(this);
            this.setClassLoader(classLoader);
            this._ownClassLoader = true;
        }
        if (WebAppContext.LOG.isDebugEnabled()) {
            ClassLoader loader = this.getClassLoader();
            WebAppContext.LOG.debug("Thread Context classloader {}", loader);
            for (loader = loader.getParent(); loader != null; loader = loader.getParent()) {
                WebAppContext.LOG.debug("Parent class loader: {} ", loader);
            }
        }
        for (final Configuration configuration : this._configurations) {
            WebAppContext.LOG.debug("preConfigure {} with {}", this, configuration);
            configuration.preConfigure(this);
        }
    }
    
    public void configure() throws Exception {
        for (final Configuration configuration : this._configurations) {
            WebAppContext.LOG.debug("configure {} with {}", this, configuration);
            configuration.configure(this);
        }
    }
    
    public void postConfigure() throws Exception {
        for (final Configuration configuration : this._configurations) {
            WebAppContext.LOG.debug("postConfigure {} with {}", this, configuration);
            configuration.postConfigure(this);
        }
    }
    
    @Override
    protected void doStart() throws Exception {
        try {
            this._metadata.setAllowDuplicateFragmentNames(this.isAllowDuplicateFragmentNames());
            this.preConfigure();
            super.doStart();
            this.postConfigure();
            if (this.isLogUrlOnStart()) {
                this.dumpUrl();
            }
        }
        catch (Exception e) {
            WebAppContext.LOG.warn("Failed startup of context " + this, e);
            this._unavailableException = e;
            this.setAvailable(false);
            if (this.isThrowUnavailableOnStartupException()) {
                throw e;
            }
        }
    }
    
    @Override
    protected void doStop() throws Exception {
        super.doStop();
    }
    
    @Override
    public void destroy() {
        final MultiException mx = new MultiException();
        if (this._configurations != null) {
            int i = this._configurations.size();
            while (i-- > 0) {
                try {
                    this._configurations.get(i).destroy(this);
                }
                catch (Exception e) {
                    mx.add(e);
                }
            }
        }
        this._configurations.clear();
        super.destroy();
        mx.ifExceptionThrowRuntime();
    }
    
    private void dumpUrl() {
        final Connector[] connectors = this.getServer().getConnectors();
        for (int i = 0; i < connectors.length; ++i) {
            String displayName = this.getDisplayName();
            if (displayName == null) {
                displayName = "WebApp@" + connectors.hashCode();
            }
            WebAppContext.LOG.info(displayName + " at http://" + connectors[i].toString() + this.getContextPath(), new Object[0]);
        }
    }
    
    @ManagedAttribute(value = "configuration classes used to configure webapp", readonly = true)
    public String[] getConfigurationClasses() {
        return this._configurationClasses.toArray(new String[this._configurationClasses.size()]);
    }
    
    public Configuration[] getConfigurations() {
        return this._configurations.toArray(new Configuration[this._configurations.size()]);
    }
    
    @ManagedAttribute(value = "default web.xml deascriptor applied before standard web.xml", readonly = true)
    public String getDefaultsDescriptor() {
        return this._defaultsDescriptor;
    }
    
    public String getOverrideDescriptor() {
        if (this._overrideDescriptors.size() != 1) {
            return null;
        }
        return this._overrideDescriptors.get(0);
    }
    
    @ManagedAttribute(value = "web.xml deascriptors applied after standard web.xml", readonly = true)
    public List<String> getOverrideDescriptors() {
        return Collections.unmodifiableList((List<? extends String>)this._overrideDescriptors);
    }
    
    @Override
    public PermissionCollection getPermissions() {
        return this._permissions;
    }
    
    @ManagedAttribute(value = "classes and packages hidden by the context classloader", readonly = true)
    public String[] getServerClasses() {
        if (this._serverClasses == null) {
            this.loadServerClasses();
        }
        return this._serverClasses.getPatterns();
    }
    
    public void addServerClass(final String classOrPackage) {
        if (this._serverClasses == null) {
            this.loadServerClasses();
        }
        this._serverClasses.add(classOrPackage);
    }
    
    public void prependServerClass(final String classOrPackage) {
        if (this._serverClasses == null) {
            this.loadServerClasses();
        }
        this._serverClasses.prependPattern(classOrPackage);
    }
    
    @ManagedAttribute(value = "classes and packages given priority by context classloader", readonly = true)
    public String[] getSystemClasses() {
        if (this._systemClasses == null) {
            this.loadSystemClasses();
        }
        return this._systemClasses.getPatterns();
    }
    
    public void addSystemClass(final String classOrPackage) {
        if (this._systemClasses == null) {
            this.loadSystemClasses();
        }
        this._systemClasses.add(classOrPackage);
    }
    
    public void prependSystemClass(final String classOrPackage) {
        if (this._systemClasses == null) {
            this.loadSystemClasses();
        }
        this._systemClasses.prependPattern(classOrPackage);
    }
    
    public void runWithoutCheckingServerClasses(final Callable<Void> callable) throws Exception {
        this._checkingServerClasses = false;
        try {
            callable.call();
        }
        finally {
            this._checkingServerClasses = true;
        }
    }
    
    @Override
    public boolean isServerClass(final String name) {
        if (!this._checkingServerClasses) {
            return false;
        }
        if (this._serverClasses == null) {
            this.loadServerClasses();
        }
        return this._serverClasses.match(name);
    }
    
    @Override
    public boolean isSystemClass(final String name) {
        if (this._systemClasses == null) {
            this.loadSystemClasses();
        }
        return this._systemClasses.match(name);
    }
    
    protected void loadSystemClasses() {
        if (this._systemClasses != null) {
            return;
        }
        final Server server = this.getServer();
        if (server != null) {
            final Object systemClasses = server.getAttribute("org.eclipse.jetty.webapp.systemClasses");
            if (systemClasses != null && systemClasses instanceof String[]) {
                this._systemClasses = new ClasspathPattern((String[])systemClasses);
            }
        }
        if (this._systemClasses == null) {
            this._systemClasses = new ClasspathPattern(WebAppContext.__dftSystemClasses);
        }
    }
    
    private void loadServerClasses() {
        if (this._serverClasses != null) {
            return;
        }
        final Server server = this.getServer();
        if (server != null) {
            final Object serverClasses = server.getAttribute("org.eclipse.jetty.webapp.serverClasses");
            if (serverClasses != null && serverClasses instanceof String[]) {
                this._serverClasses = new ClasspathPattern((String[])serverClasses);
            }
        }
        if (this._serverClasses == null) {
            this._serverClasses = new ClasspathPattern(WebAppContext.__dftServerClasses);
        }
    }
    
    @ManagedAttribute(value = "war file location", readonly = true)
    public String getWar() {
        if (this._war == null) {
            this._war = this.getResourceBase();
        }
        return this._war;
    }
    
    public Resource getWebInf() throws IOException {
        if (super.getBaseResource() == null) {
            return null;
        }
        final Resource web_inf = super.getBaseResource().addPath("WEB-INF/");
        if (!web_inf.exists() || !web_inf.isDirectory()) {
            return null;
        }
        return web_inf;
    }
    
    @ManagedAttribute("web application distributable")
    public boolean isDistributable() {
        return this._distributable;
    }
    
    @ManagedAttribute(value = "extract war", readonly = true)
    public boolean isExtractWAR() {
        return this._extractWAR;
    }
    
    @ManagedAttribute(value = "webdir copied on deploy (allows hot replacement on windows)", readonly = true)
    public boolean isCopyWebDir() {
        return this._copyDir;
    }
    
    public boolean isCopyWebInf() {
        return this._copyWebInf;
    }
    
    @ManagedAttribute(value = "parent classloader given priority", readonly = true)
    @Override
    public boolean isParentLoaderPriority() {
        return this._parentLoaderPriority;
    }
    
    public static String[] getDefaultConfigurationClasses() {
        return WebAppContext.DEFAULT_CONFIGURATION_CLASSES;
    }
    
    public String[] getDefaultServerClasses() {
        return WebAppContext.__dftServerClasses;
    }
    
    public String[] getDefaultSystemClasses() {
        return WebAppContext.__dftSystemClasses;
    }
    
    protected void loadConfigurations() throws Exception {
        if (this._configurations.size() > 0) {
            return;
        }
        if (this._configurationClasses.size() == 0) {
            this._configurationClasses.addAll(Configuration.ClassList.serverDefault(this.getServer()));
        }
        for (final String configClass : this._configurationClasses) {
            this._configurations.add(Loader.loadClass(this.getClass(), configClass).newInstance());
        }
    }
    
    @Override
    public String toString() {
        if (this._war != null) {
            String war = this._war;
            if (war.indexOf("/webapps/") >= 0) {
                war = war.substring(war.indexOf("/webapps/") + 8);
            }
            return super.toString() + "{" + war + "}";
        }
        return super.toString();
    }
    
    @Override
    public void dump(final Appendable out, final String indent) throws IOException {
        this.dumpBeans(out, indent, Collections.singletonList(new ClassLoaderDump(this.getClassLoader())), Collections.singletonList(new DumpableCollection("Systemclasses " + this, this._systemClasses)), Collections.singletonList(new DumpableCollection("Serverclasses " + this, this._serverClasses)), Collections.singletonList(new DumpableCollection("Configurations " + this, this._configurations)), Collections.singletonList(new DumpableCollection("Handler attributes " + this, ((AttributesMap)this.getAttributes()).getAttributeEntrySet())), Collections.singletonList(new DumpableCollection("Context attributes " + this, ((Context)this.getServletContext()).getAttributeEntrySet())), Collections.singletonList(new DumpableCollection("Initparams " + this, this.getInitParams().entrySet())));
    }
    
    public void setConfigurationClasses(final String[] configurations) {
        if (this.isStarted()) {
            throw new IllegalStateException();
        }
        this._configurationClasses.clear();
        if (configurations != null) {
            this._configurationClasses.addAll(Arrays.asList(configurations));
        }
        this._configurations.clear();
    }
    
    public void setConfigurationClasses(final List<String> configurations) {
        this.setConfigurationClasses(configurations.toArray(new String[configurations.size()]));
    }
    
    public void setConfigurations(final Configuration[] configurations) {
        if (this.isStarted()) {
            throw new IllegalStateException();
        }
        this._configurations.clear();
        if (configurations != null) {
            this._configurations.addAll(Arrays.asList(configurations));
        }
    }
    
    public void setDefaultsDescriptor(final String defaultsDescriptor) {
        this._defaultsDescriptor = defaultsDescriptor;
    }
    
    public void setOverrideDescriptor(final String overrideDescriptor) {
        this._overrideDescriptors.clear();
        this._overrideDescriptors.add(overrideDescriptor);
    }
    
    public void setOverrideDescriptors(final List<String> overrideDescriptors) {
        this._overrideDescriptors.clear();
        this._overrideDescriptors.addAll(overrideDescriptors);
    }
    
    public void addOverrideDescriptor(final String overrideDescriptor) {
        this._overrideDescriptors.add(overrideDescriptor);
    }
    
    @ManagedAttribute(value = "standard web.xml descriptor", readonly = true)
    public String getDescriptor() {
        return this._descriptor;
    }
    
    public void setDescriptor(final String descriptor) {
        this._descriptor = descriptor;
    }
    
    public void setDistributable(final boolean distributable) {
        this._distributable = distributable;
    }
    
    @Override
    public void setEventListeners(final EventListener[] eventListeners) {
        if (this._sessionHandler != null) {
            this._sessionHandler.clearEventListeners();
        }
        super.setEventListeners(eventListeners);
    }
    
    @Override
    public void addEventListener(final EventListener listener) {
        super.addEventListener(listener);
        if ((listener instanceof HttpSessionActivationListener || listener instanceof HttpSessionAttributeListener || listener instanceof HttpSessionBindingListener || listener instanceof HttpSessionListener || listener instanceof HttpSessionIdListener) && this._sessionHandler != null) {
            this._sessionHandler.addEventListener(listener);
        }
    }
    
    @Override
    public void removeEventListener(final EventListener listener) {
        super.removeEventListener(listener);
        if ((listener instanceof HttpSessionActivationListener || listener instanceof HttpSessionAttributeListener || listener instanceof HttpSessionBindingListener || listener instanceof HttpSessionListener || listener instanceof HttpSessionIdListener) && this._sessionHandler != null) {
            this._sessionHandler.removeEventListener(listener);
        }
    }
    
    public void setExtractWAR(final boolean extractWAR) {
        this._extractWAR = extractWAR;
    }
    
    public void setCopyWebDir(final boolean copy) {
        this._copyDir = copy;
    }
    
    public void setCopyWebInf(final boolean copyWebInf) {
        this._copyWebInf = copyWebInf;
    }
    
    public void setParentLoaderPriority(final boolean java2compliant) {
        this._parentLoaderPriority = java2compliant;
    }
    
    public void setPermissions(final PermissionCollection permissions) {
        this._permissions = permissions;
    }
    
    public void setContextWhiteList(final String[] contextWhiteList) {
        this._contextWhiteList = contextWhiteList;
    }
    
    public void setServerClasses(final String[] serverClasses) {
        this._serverClasses = new ClasspathPattern(serverClasses);
    }
    
    public void setSystemClasses(final String[] systemClasses) {
        this._systemClasses = new ClasspathPattern(systemClasses);
    }
    
    public void setTempDirectory(File dir) {
        if (this.isStarted()) {
            throw new IllegalStateException("Started");
        }
        if (dir != null) {
            try {
                dir = new File(dir.getCanonicalPath());
            }
            catch (IOException e) {
                WebAppContext.LOG.warn("EXCEPTION ", e);
            }
        }
        this.setAttribute("javax.servlet.context.tempdir", this._tmpDir = dir);
    }
    
    @ManagedAttribute(value = "temporary directory location", readonly = true)
    public File getTempDirectory() {
        return this._tmpDir;
    }
    
    public void setPersistTempDirectory(final boolean persist) {
        this._persistTmpDir = persist;
    }
    
    public boolean isPersistTempDirectory() {
        return this._persistTmpDir;
    }
    
    public void setWar(final String war) {
        this._war = war;
    }
    
    @ManagedAttribute(value = "extra classpath for context classloader", readonly = true)
    @Override
    public String getExtraClasspath() {
        return this._extraClasspath;
    }
    
    public void setExtraClasspath(final String extraClasspath) {
        this._extraClasspath = extraClasspath;
    }
    
    public boolean isLogUrlOnStart() {
        return this._logUrlOnStart;
    }
    
    public void setLogUrlOnStart(final boolean logOnStart) {
        this._logUrlOnStart = logOnStart;
    }
    
    @Override
    public void setServer(final Server server) {
        super.setServer(server);
    }
    
    public boolean isAllowDuplicateFragmentNames() {
        return this._allowDuplicateFragmentNames;
    }
    
    public void setAllowDuplicateFragmentNames(final boolean allowDuplicateFragmentNames) {
        this._allowDuplicateFragmentNames = allowDuplicateFragmentNames;
    }
    
    public void setThrowUnavailableOnStartupException(final boolean throwIfStartupException) {
        this._throwUnavailableOnStartupException = throwIfStartupException;
    }
    
    public boolean isThrowUnavailableOnStartupException() {
        return this._throwUnavailableOnStartupException;
    }
    
    @Override
    protected void startContext() throws Exception {
        this.configure();
        this._metadata.resolve(this);
        this.startWebapp();
    }
    
    @Override
    protected void stopContext() throws Exception {
        this.stopWebapp();
        try {
            int i = this._configurations.size();
            while (i-- > 0) {
                this._configurations.get(i).deconfigure(this);
            }
            if (this._metadata != null) {
                this._metadata.clear();
            }
            this._metadata = new MetaData();
        }
        finally {
            if (this._ownClassLoader) {
                final ClassLoader loader = this.getClassLoader();
                if (loader != null && loader instanceof URLClassLoader) {
                    ((URLClassLoader)loader).close();
                }
                this.setClassLoader(null);
            }
            this.setAvailable(true);
            this._unavailableException = null;
        }
    }
    
    protected void startWebapp() throws Exception {
        super.startContext();
    }
    
    protected void stopWebapp() throws Exception {
        super.stopContext();
    }
    
    @Override
    public Set<String> setServletSecurity(final ServletRegistration.Dynamic registration, final ServletSecurityElement servletSecurityElement) {
        final Set<String> unchangedURLMappings = new HashSet<String>();
        final Collection<String> pathMappings = registration.getMappings();
        if (pathMappings != null) {
            ConstraintSecurityHandler.createConstraint(registration.getName(), servletSecurityElement);
            for (final String pathSpec : pathMappings) {
                final Origin origin = this.getMetaData().getOrigin("constraint.url." + pathSpec);
                switch (origin) {
                    case NotSet: {
                        final List<ConstraintMapping> mappings = ConstraintSecurityHandler.createConstraintsWithMappingsForPath(registration.getName(), pathSpec, servletSecurityElement);
                        for (final ConstraintMapping m : mappings) {
                            ((ConstraintAware)this.getSecurityHandler()).addConstraintMapping(m);
                        }
                        ((ConstraintAware)this.getSecurityHandler()).checkPathsWithUncoveredHttpMethods();
                        this.getMetaData().setOriginAPI("constraint.url." + pathSpec);
                        continue;
                    }
                    case WebXml:
                    case WebDefaults:
                    case WebOverride:
                    case WebFragment: {
                        unchangedURLMappings.add(pathSpec);
                        continue;
                    }
                    case Annotation:
                    case API: {
                        final List<ConstraintMapping> constraintMappings = ConstraintSecurityHandler.removeConstraintMappingsForPath(pathSpec, ((ConstraintAware)this.getSecurityHandler()).getConstraintMappings());
                        final List<ConstraintMapping> freshMappings = ConstraintSecurityHandler.createConstraintsWithMappingsForPath(registration.getName(), pathSpec, servletSecurityElement);
                        constraintMappings.addAll(freshMappings);
                        ((ConstraintSecurityHandler)this.getSecurityHandler()).setConstraintMappings(constraintMappings);
                        ((ConstraintAware)this.getSecurityHandler()).checkPathsWithUncoveredHttpMethods();
                        continue;
                    }
                }
            }
        }
        return unchangedURLMappings;
    }
    
    public MetaData getMetaData() {
        return this._metadata;
    }
    
    static {
        LOG = Log.getLogger(WebAppContext.class);
        DEFAULT_CONFIGURATION_CLASSES = new String[] { "org.eclipse.jetty.webapp.WebInfConfiguration", "org.eclipse.jetty.webapp.WebXmlConfiguration", "org.eclipse.jetty.webapp.MetaInfConfiguration", "org.eclipse.jetty.webapp.FragmentConfiguration", "org.eclipse.jetty.webapp.JettyWebXmlConfiguration" };
        __dftSystemClasses = new String[] { "java.", "javax.", "org.xml.", "org.w3c.", "org.eclipse.jetty.jmx.", "org.eclipse.jetty.util.annotation.", "org.eclipse.jetty.continuation.", "org.eclipse.jetty.jndi.", "org.eclipse.jetty.jaas.", "org.eclipse.jetty.websocket.", "org.eclipse.jetty.util.log.", "org.eclipse.jetty.servlet.StatisticsServlet", "org.eclipse.jetty.servlet.DefaultServlet", "org.eclipse.jetty.jsp.JettyJspServlet", "org.eclipse.jetty.servlets.PushCacheFilter", "org.eclipse.jetty.servlets.PushSessionCacheFilter" };
        __dftServerClasses = new String[] { "-org.eclipse.jetty.jmx.", "-org.eclipse.jetty.util.annotation.", "-org.eclipse.jetty.continuation.", "-org.eclipse.jetty.jndi.", "-org.eclipse.jetty.jaas.", "-org.eclipse.jetty.servlets.", "-org.eclipse.jetty.servlet.StatisticsServlet", "-org.eclipse.jetty.servlet.DefaultServlet", "-org.eclipse.jetty.jsp.", "-org.eclipse.jetty.servlet.listener.", "-org.eclipse.jetty.websocket.", "-org.eclipse.jetty.apache.", "-org.eclipse.jetty.util.log.", "-org.eclipse.jetty.alpn.", "org.objectweb.asm.", "org.eclipse.jdt.", "org.eclipse.jetty." };
    }
    
    public class Context extends ServletContextHandler.Context
    {
        @Override
        public void checkListener(final Class<? extends EventListener> listener) throws IllegalStateException {
            try {
                super.checkListener(listener);
            }
            catch (IllegalArgumentException e) {
                boolean ok = false;
                for (final Class l : SessionHandler.SESSION_LISTENER_TYPES) {
                    if (l.isAssignableFrom(listener)) {
                        ok = true;
                        break;
                    }
                }
                if (!ok) {
                    throw new IllegalArgumentException("Inappropriate listener type " + listener.getName());
                }
            }
        }
        
        @Override
        public URL getResource(final String path) throws MalformedURLException {
            final Resource resource = WebAppContext.this.getResource(path);
            if (resource == null || !resource.exists()) {
                return null;
            }
            if (resource.isDirectory() && resource instanceof ResourceCollection && !WebAppContext.this.isExtractWAR()) {
                final Resource[] resources = ((ResourceCollection)resource).getResources();
                int i = resources.length;
                while (i-- > 0) {
                    if (resources[i].getName().startsWith("jar:file")) {
                        return resources[i].getURL();
                    }
                }
            }
            return resource.getURL();
        }
        
        @Override
        public ServletContext getContext(final String uripath) {
            final ServletContext servletContext = super.getContext(uripath);
            if (servletContext != null && WebAppContext.this._contextWhiteList != null) {
                for (final String context : WebAppContext.this._contextWhiteList) {
                    if (context.equals(uripath)) {
                        return servletContext;
                    }
                }
                return null;
            }
            return servletContext;
        }
    }
}
