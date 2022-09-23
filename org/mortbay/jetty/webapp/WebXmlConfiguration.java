// 
// Decompiled by Procyon v0.5.36
// 

package org.mortbay.jetty.webapp;

import org.mortbay.jetty.security.UserRealm;
import org.mortbay.jetty.security.Authenticator;
import org.mortbay.jetty.handler.ContextHandler;
import org.mortbay.jetty.security.ClientCertAuthenticator;
import org.mortbay.jetty.security.DigestAuthenticator;
import org.mortbay.jetty.security.BasicAuthenticator;
import org.mortbay.jetty.security.FormAuthenticator;
import org.mortbay.jetty.security.Constraint;
import java.util.HashMap;
import java.io.File;
import org.mortbay.util.Loader;
import org.mortbay.jetty.servlet.Dispatcher;
import java.util.ArrayList;
import java.util.Iterator;
import javax.servlet.UnavailableException;
import org.mortbay.util.LazyList;
import org.mortbay.jetty.servlet.ErrorPageErrorHandler;
import org.mortbay.jetty.security.ConstraintMapping;
import java.util.EventListener;
import org.mortbay.jetty.servlet.ServletMapping;
import org.mortbay.jetty.servlet.ServletHolder;
import org.mortbay.jetty.servlet.FilterMapping;
import org.mortbay.jetty.servlet.FilterHolder;
import java.net.MalformedURLException;
import java.io.IOException;
import org.mortbay.resource.Resource;
import org.mortbay.log.Log;
import java.net.URL;
import org.mortbay.jetty.servlet.ServletHandler;
import java.util.Map;
import org.mortbay.xml.XmlParser;

public class WebXmlConfiguration implements Configuration
{
    protected WebAppContext _context;
    protected XmlParser _xmlParser;
    protected Object _filters;
    protected Object _filterMappings;
    protected Object _servlets;
    protected Object _servletMappings;
    protected Object _welcomeFiles;
    protected Object _constraintMappings;
    protected Object _listeners;
    protected Map _errorPages;
    protected boolean _hasJSP;
    protected String _jspServletName;
    protected String _jspServletClass;
    protected boolean _defaultWelcomeFileList;
    protected ServletHandler _servletHandler;
    protected int _version;
    
    public WebXmlConfiguration() {
        this._xmlParser = webXmlParser();
    }
    
    public static XmlParser webXmlParser() {
        final XmlParser xmlParser = new XmlParser();
        final URL dtd22 = WebAppContext.class.getResource("/javax/servlet/resources/web-app_2_2.dtd");
        final URL dtd23 = WebAppContext.class.getResource("/javax/servlet/resources/web-app_2_3.dtd");
        final URL jsp20xsd = WebAppContext.class.getResource("/javax/servlet/resources/jsp_2_0.xsd");
        final URL jsp21xsd = WebAppContext.class.getResource("/javax/servlet/resources/jsp_2_1.xsd");
        final URL j2ee14xsd = WebAppContext.class.getResource("/javax/servlet/resources/j2ee_1_4.xsd");
        final URL webapp24xsd = WebAppContext.class.getResource("/javax/servlet/resources/web-app_2_4.xsd");
        final URL webapp25xsd = WebAppContext.class.getResource("/javax/servlet/resources/web-app_2_5.xsd");
        final URL schemadtd = WebAppContext.class.getResource("/javax/servlet/resources/XMLSchema.dtd");
        final URL xmlxsd = WebAppContext.class.getResource("/javax/servlet/resources/xml.xsd");
        final URL webservice11xsd = WebAppContext.class.getResource("/javax/servlet/resources/j2ee_web_services_client_1_1.xsd");
        final URL webservice12xsd = WebAppContext.class.getResource("/javax/servlet/resources/javaee_web_services_client_1_2.xsd");
        final URL datatypesdtd = WebAppContext.class.getResource("/javax/servlet/resources/datatypes.dtd");
        xmlParser.redirectEntity("web-app_2_2.dtd", dtd22);
        xmlParser.redirectEntity("-//Sun Microsystems, Inc.//DTD Web Application 2.2//EN", dtd22);
        xmlParser.redirectEntity("web.dtd", dtd23);
        xmlParser.redirectEntity("web-app_2_3.dtd", dtd23);
        xmlParser.redirectEntity("-//Sun Microsystems, Inc.//DTD Web Application 2.3//EN", dtd23);
        xmlParser.redirectEntity("XMLSchema.dtd", schemadtd);
        xmlParser.redirectEntity("http://www.w3.org/2001/XMLSchema.dtd", schemadtd);
        xmlParser.redirectEntity("-//W3C//DTD XMLSCHEMA 200102//EN", schemadtd);
        xmlParser.redirectEntity("jsp_2_0.xsd", jsp20xsd);
        xmlParser.redirectEntity("http://java.sun.com/xml/ns/j2ee/jsp_2_0.xsd", jsp20xsd);
        xmlParser.redirectEntity("jsp_2_1.xsd", jsp21xsd);
        xmlParser.redirectEntity("http://java.sun.com/xml/ns/javaee/jsp_2_1.xsd", jsp21xsd);
        xmlParser.redirectEntity("j2ee_1_4.xsd", j2ee14xsd);
        xmlParser.redirectEntity("http://java.sun.com/xml/ns/j2ee/j2ee_1_4.xsd", j2ee14xsd);
        xmlParser.redirectEntity("web-app_2_4.xsd", webapp24xsd);
        xmlParser.redirectEntity("http://java.sun.com/xml/ns/j2ee/web-app_2_4.xsd", webapp24xsd);
        xmlParser.redirectEntity("web-app_2_5.xsd", webapp25xsd);
        xmlParser.redirectEntity("http://java.sun.com/xml/ns/javaee/web-app_2_5.xsd", webapp25xsd);
        xmlParser.redirectEntity("xml.xsd", xmlxsd);
        xmlParser.redirectEntity("http://www.w3.org/2001/xml.xsd", xmlxsd);
        xmlParser.redirectEntity("datatypes.dtd", datatypesdtd);
        xmlParser.redirectEntity("http://www.w3.org/2001/datatypes.dtd", datatypesdtd);
        xmlParser.redirectEntity("j2ee_web_services_client_1_1.xsd", webservice11xsd);
        xmlParser.redirectEntity("http://www.ibm.com/webservices/xsd/j2ee_web_services_client_1_1.xsd", webservice11xsd);
        xmlParser.redirectEntity("javaee_web_services_client_1_2.xsd", webservice12xsd);
        xmlParser.redirectEntity("http://www.ibm.com/webservices/xsd/javaee_web_services_client_1_2.xsd", webservice12xsd);
        return xmlParser;
    }
    
    public void setWebAppContext(final WebAppContext context) {
        this._context = context;
    }
    
    public WebAppContext getWebAppContext() {
        return this._context;
    }
    
    public void configureClassLoader() throws Exception {
    }
    
    public void configureDefaults() throws Exception {
        if (this._context.isStarted()) {
            if (Log.isDebugEnabled()) {
                Log.debug("Cannot configure webapp after it is started");
            }
            return;
        }
        final String defaultsDescriptor = this.getWebAppContext().getDefaultsDescriptor();
        if (defaultsDescriptor != null && defaultsDescriptor.length() > 0) {
            Resource dftResource = Resource.newSystemResource(defaultsDescriptor);
            if (dftResource == null) {
                dftResource = Resource.newResource(defaultsDescriptor);
            }
            this.configure(dftResource.getURL().toString());
            this._defaultWelcomeFileList = (this._welcomeFiles != null);
        }
    }
    
    public void configureWebApp() throws Exception {
        if (this._context.isStarted()) {
            if (Log.isDebugEnabled()) {
                Log.debug("Cannot configure webapp after it is started");
            }
            return;
        }
        final URL webxml = this.findWebXml();
        if (webxml != null) {
            this.configure(webxml.toString());
        }
        final String overrideDescriptor = this.getWebAppContext().getOverrideDescriptor();
        if (overrideDescriptor != null && overrideDescriptor.length() > 0) {
            Resource orideResource = Resource.newSystemResource(overrideDescriptor);
            if (orideResource == null) {
                orideResource = Resource.newResource(overrideDescriptor);
            }
            this._xmlParser.setValidating(false);
            this.configure(orideResource.getURL().toString());
        }
    }
    
    protected URL findWebXml() throws IOException, MalformedURLException {
        final String descriptor = this.getWebAppContext().getDescriptor();
        if (descriptor != null) {
            final Resource web = Resource.newResource(descriptor);
            if (web.exists() && !web.isDirectory()) {
                return web.getURL();
            }
        }
        final Resource web_inf = this.getWebAppContext().getWebInf();
        if (web_inf != null && web_inf.isDirectory()) {
            final Resource web2 = web_inf.addPath("web.xml");
            if (web2.exists()) {
                return web2.getURL();
            }
            Log.debug("No WEB-INF/web.xml in " + this.getWebAppContext().getWar() + ". Serving files and default/dynamic servlets only");
        }
        return null;
    }
    
    public void configure(final String webXml) throws Exception {
        XmlParser.Node config = null;
        config = this._xmlParser.parse(webXml);
        this.initialize(config);
    }
    
    public void deconfigureWebApp() throws Exception {
        (this._servletHandler = this.getWebAppContext().getServletHandler()).setFilters(null);
        this._servletHandler.setFilterMappings(null);
        this._servletHandler.setServlets(null);
        this._servletHandler.setServletMappings(null);
        this.getWebAppContext().setEventListeners(null);
        this.getWebAppContext().setWelcomeFiles(null);
        if (this.getWebAppContext().getSecurityHandler() != null) {
            this.getWebAppContext().getSecurityHandler().setConstraintMappings(null);
        }
        if (this.getWebAppContext().getErrorHandler() instanceof ErrorPageErrorHandler) {
            ((ErrorPageErrorHandler)this.getWebAppContext().getErrorHandler()).setErrorPages(null);
        }
    }
    
    protected void initialize(final XmlParser.Node config) throws ClassNotFoundException, UnavailableException {
        this._servletHandler = this.getWebAppContext().getServletHandler();
        this._filters = LazyList.array2List(this._servletHandler.getFilters());
        this._filterMappings = LazyList.array2List(this._servletHandler.getFilterMappings());
        this._servlets = LazyList.array2List(this._servletHandler.getServlets());
        this._servletMappings = LazyList.array2List(this._servletHandler.getServletMappings());
        this._listeners = LazyList.array2List(this.getWebAppContext().getEventListeners());
        this._welcomeFiles = LazyList.array2List(this.getWebAppContext().getWelcomeFiles());
        this._constraintMappings = LazyList.array2List(this.getWebAppContext().getSecurityHandler().getConstraintMappings());
        this._errorPages = ((this.getWebAppContext().getErrorHandler() instanceof ErrorPageErrorHandler) ? ((ErrorPageErrorHandler)this.getWebAppContext().getErrorHandler()).getErrorPages() : null);
        final String version = config.getAttribute("version", "DTD");
        if ("2.5".equals(version)) {
            this._version = 25;
        }
        else if ("2.4".equals(version)) {
            this._version = 24;
        }
        else if ("DTD".equals(version)) {
            this._version = 23;
            final String dtd = this._xmlParser.getDTD();
            if (dtd != null && dtd.indexOf("web-app_2_2") >= 0) {
                this._version = 22;
            }
        }
        final Iterator iter = config.iterator();
        XmlParser.Node node = null;
        while (iter.hasNext()) {
            try {
                final Object o = iter.next();
                if (!(o instanceof XmlParser.Node)) {
                    continue;
                }
                node = (XmlParser.Node)o;
                final String name = node.getTag();
                this.initWebXmlElement(name, node);
                continue;
            }
            catch (ClassNotFoundException e) {
                throw e;
            }
            catch (Exception e2) {
                Log.warn("Configuration problem at " + node + ": " + e2);
                Log.debug(e2);
                throw new UnavailableException("Configuration problem");
            }
            break;
        }
        this._servletHandler.setFilters((FilterHolder[])LazyList.toArray(this._filters, FilterHolder.class));
        this._servletHandler.setFilterMappings((FilterMapping[])LazyList.toArray(this._filterMappings, FilterMapping.class));
        this._servletHandler.setServlets((ServletHolder[])LazyList.toArray(this._servlets, ServletHolder.class));
        this._servletHandler.setServletMappings((ServletMapping[])LazyList.toArray(this._servletMappings, ServletMapping.class));
        this.getWebAppContext().setEventListeners((EventListener[])LazyList.toArray(this._listeners, EventListener.class));
        this.getWebAppContext().setWelcomeFiles((String[])LazyList.toArray(this._welcomeFiles, String.class));
        this.getWebAppContext().getSecurityHandler().setConstraintMappings((ConstraintMapping[])LazyList.toArray(this._constraintMappings, ConstraintMapping.class));
        if (this._errorPages != null && this.getWebAppContext().getErrorHandler() instanceof ErrorPageErrorHandler) {
            ((ErrorPageErrorHandler)this.getWebAppContext().getErrorHandler()).setErrorPages(this._errorPages);
        }
    }
    
    protected void initWebXmlElement(final String element, final XmlParser.Node node) throws Exception {
        if ("display-name".equals(element)) {
            this.initDisplayName(node);
        }
        else if (!"description".equals(element)) {
            if ("context-param".equals(element)) {
                this.initContextParam(node);
            }
            else if ("servlet".equals(element)) {
                this.initServlet(node);
            }
            else if ("servlet-mapping".equals(element)) {
                this.initServletMapping(node);
            }
            else if ("session-config".equals(element)) {
                this.initSessionConfig(node);
            }
            else if ("mime-mapping".equals(element)) {
                this.initMimeConfig(node);
            }
            else if ("welcome-file-list".equals(element)) {
                this.initWelcomeFileList(node);
            }
            else if ("locale-encoding-mapping-list".equals(element)) {
                this.initLocaleEncodingList(node);
            }
            else if ("error-page".equals(element)) {
                this.initErrorPage(node);
            }
            else if ("taglib".equals(element)) {
                this.initTagLib(node);
            }
            else if ("jsp-config".equals(element)) {
                this.initJspConfig(node);
            }
            else if ("resource-ref".equals(element)) {
                if (Log.isDebugEnabled()) {
                    Log.debug("No implementation: " + node);
                }
            }
            else if ("security-constraint".equals(element)) {
                this.initSecurityConstraint(node);
            }
            else if ("login-config".equals(element)) {
                this.initLoginConfig(node);
            }
            else if ("security-role".equals(element)) {
                this.initSecurityRole(node);
            }
            else if ("filter".equals(element)) {
                this.initFilter(node);
            }
            else if ("filter-mapping".equals(element)) {
                this.initFilterMapping(node);
            }
            else if ("listener".equals(element)) {
                this.initListener(node);
            }
            else if ("distributable".equals(element)) {
                this.initDistributable(node);
            }
            else if (Log.isDebugEnabled()) {
                Log.debug("Element {} not handled in {}", element, this);
                Log.debug(node.toString());
            }
        }
    }
    
    protected void initDisplayName(final XmlParser.Node node) {
        this.getWebAppContext().setDisplayName(node.toString(false, true));
    }
    
    protected void initContextParam(final XmlParser.Node node) {
        final String name = node.getString("param-name", false, true);
        final String value = node.getString("param-value", false, true);
        if (Log.isDebugEnabled()) {
            Log.debug("ContextParam: " + name + "=" + value);
        }
        this.getWebAppContext().getInitParams().put(name, value);
    }
    
    protected void initFilter(final XmlParser.Node node) {
        final String name = node.getString("filter-name", false, true);
        FilterHolder holder = this._servletHandler.getFilter(name);
        if (holder == null) {
            holder = this._servletHandler.newFilterHolder();
            holder.setName(name);
            this._filters = LazyList.add(this._filters, holder);
        }
        final String filter_class = node.getString("filter-class", false, true);
        if (filter_class != null) {
            holder.setClassName(filter_class);
        }
        final Iterator iter = node.iterator("init-param");
        while (iter.hasNext()) {
            final XmlParser.Node paramNode = iter.next();
            final String pname = paramNode.getString("param-name", false, true);
            final String pvalue = paramNode.getString("param-value", false, true);
            holder.setInitParameter(pname, pvalue);
        }
    }
    
    protected void initFilterMapping(final XmlParser.Node node) {
        final String filter_name = node.getString("filter-name", false, true);
        final FilterMapping mapping = new FilterMapping();
        mapping.setFilterName(filter_name);
        final ArrayList paths = new ArrayList();
        Iterator iter = node.iterator("url-pattern");
        while (iter.hasNext()) {
            String p = iter.next().toString(false, true);
            p = this.normalizePattern(p);
            paths.add(p);
        }
        mapping.setPathSpecs(paths.toArray(new String[paths.size()]));
        final ArrayList names = new ArrayList();
        iter = node.iterator("servlet-name");
        while (iter.hasNext()) {
            final String n = iter.next().toString(false, true);
            names.add(n);
        }
        mapping.setServletNames(names.toArray(new String[names.size()]));
        int dispatcher = 0;
        iter = node.iterator("dispatcher");
        while (iter.hasNext()) {
            final String d = iter.next().toString(false, true);
            dispatcher |= Dispatcher.type(d);
        }
        mapping.setDispatches(dispatcher);
        this._filterMappings = LazyList.add(this._filterMappings, mapping);
    }
    
    protected String normalizePattern(final String p) {
        if (p != null && p.length() > 0 && !p.startsWith("/") && !p.startsWith("*")) {
            return "/" + p;
        }
        return p;
    }
    
    protected void initServlet(final XmlParser.Node node) {
        final String id = node.getAttribute("id");
        final String servlet_name = node.getString("servlet-name", false, true);
        ServletHolder holder = this._servletHandler.getServlet(servlet_name);
        if (holder == null) {
            holder = this._servletHandler.newServletHolder();
            holder.setName(servlet_name);
            this._servlets = LazyList.add(this._servlets, holder);
        }
        final Iterator iParamsIter = node.iterator("init-param");
        while (iParamsIter.hasNext()) {
            final XmlParser.Node paramNode = iParamsIter.next();
            final String pname = paramNode.getString("param-name", false, true);
            final String pvalue = paramNode.getString("param-value", false, true);
            holder.setInitParameter(pname, pvalue);
        }
        String servlet_class = node.getString("servlet-class", false, true);
        if (id != null && id.equals("jsp")) {
            this._jspServletName = servlet_name;
            this._jspServletClass = servlet_class;
            try {
                Loader.loadClass(this.getClass(), servlet_class);
                this._hasJSP = true;
            }
            catch (ClassNotFoundException e2) {
                Log.info("NO JSP Support for {}, did not find {}", this._context.getContextPath(), servlet_class);
                this._hasJSP = false;
                servlet_class = (this._jspServletClass = "org.mortbay.servlet.NoJspServlet");
            }
            if (holder.getInitParameter("scratchdir") == null) {
                final File tmp = this.getWebAppContext().getTempDirectory();
                final File scratch = new File(tmp, "jsp");
                if (!scratch.exists()) {
                    scratch.mkdir();
                }
                holder.setInitParameter("scratchdir", scratch.getAbsolutePath());
                if ("?".equals(holder.getInitParameter("classpath"))) {
                    final String classpath = this.getWebAppContext().getClassPath();
                    Log.debug("classpath=" + classpath);
                    if (classpath != null) {
                        holder.setInitParameter("classpath", classpath);
                    }
                }
            }
        }
        if (servlet_class != null) {
            holder.setClassName(servlet_class);
        }
        final String jsp_file = node.getString("jsp-file", false, true);
        if (jsp_file != null) {
            holder.setForcedPath(jsp_file);
            holder.setClassName(this._jspServletClass);
        }
        final XmlParser.Node startup = node.get("load-on-startup");
        if (startup != null) {
            final String s = startup.toString(false, true).toLowerCase();
            if (s.startsWith("t")) {
                Log.warn("Deprecated boolean load-on-startup.  Please use integer");
                holder.setInitOrder(1);
            }
            else {
                int order = 0;
                try {
                    if (s != null && s.trim().length() > 0) {
                        order = Integer.parseInt(s);
                    }
                }
                catch (Exception e) {
                    Log.warn("Cannot parse load-on-startup " + s + ". Please use integer");
                    Log.ignore(e);
                }
                holder.setInitOrder(order);
            }
        }
        final Iterator sRefsIter = node.iterator("security-role-ref");
        while (sRefsIter.hasNext()) {
            final XmlParser.Node securityRef = sRefsIter.next();
            final String roleName = securityRef.getString("role-name", false, true);
            final String roleLink = securityRef.getString("role-link", false, true);
            if (roleName != null && roleName.length() > 0 && roleLink != null && roleLink.length() > 0) {
                if (Log.isDebugEnabled()) {
                    Log.debug("link role " + roleName + " to " + roleLink + " for " + this);
                }
                holder.setUserRoleLink(roleName, roleLink);
            }
            else {
                Log.warn("Ignored invalid security-role-ref element: servlet-name=" + holder.getName() + ", " + securityRef);
            }
        }
        final XmlParser.Node run_as = node.get("run-as");
        if (run_as != null) {
            final String roleName = run_as.getString("role-name", false, true);
            if (roleName != null) {
                holder.setRunAs(roleName);
            }
        }
    }
    
    protected void initServletMapping(final XmlParser.Node node) {
        final String servlet_name = node.getString("servlet-name", false, true);
        final ServletMapping mapping = new ServletMapping();
        mapping.setServletName(servlet_name);
        final ArrayList paths = new ArrayList();
        final Iterator iter = node.iterator("url-pattern");
        while (iter.hasNext()) {
            String p = iter.next().toString(false, true);
            p = this.normalizePattern(p);
            paths.add(p);
        }
        mapping.setPathSpecs(paths.toArray(new String[paths.size()]));
        this._servletMappings = LazyList.add(this._servletMappings, mapping);
    }
    
    protected void initListener(final XmlParser.Node node) {
        final String className = node.getString("listener-class", false, true);
        Object listener = null;
        try {
            final Class listenerClass = this.getWebAppContext().loadClass(className);
            listener = this.newListenerInstance(listenerClass);
            if (!(listener instanceof EventListener)) {
                Log.warn("Not an EventListener: " + listener);
                return;
            }
            this._listeners = LazyList.add(this._listeners, listener);
        }
        catch (Exception e) {
            Log.warn("Could not instantiate listener " + className, e);
        }
    }
    
    protected Object newListenerInstance(final Class clazz) throws InstantiationException, IllegalAccessException {
        return clazz.newInstance();
    }
    
    protected void initDistributable(final XmlParser.Node node) {
        final WebAppContext wac = this.getWebAppContext();
        if (!wac.isDistributable()) {
            wac.setDistributable(true);
        }
    }
    
    protected void initSessionConfig(final XmlParser.Node node) {
        final XmlParser.Node tNode = node.get("session-timeout");
        if (tNode != null) {
            final int timeout = Integer.parseInt(tNode.toString(false, true));
            this.getWebAppContext().getSessionHandler().getSessionManager().setMaxInactiveInterval(timeout * 60);
        }
    }
    
    protected void initMimeConfig(final XmlParser.Node node) {
        String extension = node.getString("extension", false, true);
        if (extension != null && extension.startsWith(".")) {
            extension = extension.substring(1);
        }
        final String mimeType = node.getString("mime-type", false, true);
        this.getWebAppContext().getMimeTypes().addMimeMapping(extension, mimeType);
    }
    
    protected void initWelcomeFileList(final XmlParser.Node node) {
        if (this._defaultWelcomeFileList) {
            this._welcomeFiles = null;
        }
        this._defaultWelcomeFileList = false;
        final Iterator iter = node.iterator("welcome-file");
        while (iter.hasNext()) {
            final XmlParser.Node indexNode = iter.next();
            final String welcome = indexNode.toString(false, true);
            this._welcomeFiles = LazyList.add(this._welcomeFiles, welcome);
        }
    }
    
    protected void initLocaleEncodingList(final XmlParser.Node node) {
        final Iterator iter = node.iterator("locale-encoding-mapping");
        while (iter.hasNext()) {
            final XmlParser.Node mapping = iter.next();
            final String locale = mapping.getString("locale", false, true);
            final String encoding = mapping.getString("encoding", false, true);
            this.getWebAppContext().addLocaleEncoding(locale, encoding);
        }
    }
    
    protected void initErrorPage(final XmlParser.Node node) {
        String error = node.getString("error-code", false, true);
        if (error == null || error.length() == 0) {
            error = node.getString("exception-type", false, true);
        }
        final String location = node.getString("location", false, true);
        if (this._errorPages == null) {
            this._errorPages = new HashMap();
        }
        this._errorPages.put(error, location);
    }
    
    protected void initTagLib(final XmlParser.Node node) {
        final String uri = node.getString("taglib-uri", false, true);
        final String location = node.getString("taglib-location", false, true);
        this.getWebAppContext().setResourceAlias(uri, location);
    }
    
    protected void initJspConfig(final XmlParser.Node node) {
        for (int i = 0; i < node.size(); ++i) {
            final Object o = node.get(i);
            if (o instanceof XmlParser.Node && "taglib".equals(((XmlParser.Node)o).getTag())) {
                this.initTagLib((XmlParser.Node)o);
            }
        }
        final Iterator iter = node.iterator("jsp-property-group");
        Object paths = null;
        while (iter.hasNext()) {
            final XmlParser.Node group = iter.next();
            final Iterator iter2 = group.iterator("url-pattern");
            while (iter2.hasNext()) {
                String url = iter2.next().toString(false, true);
                url = this.normalizePattern(url);
                paths = LazyList.add(paths, url);
            }
        }
        if (LazyList.size(paths) > 0) {
            final String jspName = this.getJSPServletName();
            if (jspName != null) {
                final ServletMapping mapping = new ServletMapping();
                mapping.setServletName(jspName);
                mapping.setPathSpecs(LazyList.toStringArray(paths));
                this._servletMappings = LazyList.add(this._servletMappings, mapping);
            }
        }
    }
    
    protected void initSecurityConstraint(final XmlParser.Node node) {
        final Constraint scBase = new Constraint();
        try {
            final XmlParser.Node auths = node.get("auth-constraint");
            if (auths != null) {
                scBase.setAuthenticate(true);
                final Iterator iter = auths.iterator("role-name");
                Object roles = null;
                while (iter.hasNext()) {
                    final String role = iter.next().toString(false, true);
                    roles = LazyList.add(roles, role);
                }
                scBase.setRoles(LazyList.toStringArray(roles));
            }
            XmlParser.Node data = node.get("user-data-constraint");
            if (data != null) {
                data = data.get("transport-guarantee");
                final String guarantee = data.toString(false, true).toUpperCase();
                if (guarantee == null || guarantee.length() == 0 || "NONE".equals(guarantee)) {
                    scBase.setDataConstraint(0);
                }
                else if ("INTEGRAL".equals(guarantee)) {
                    scBase.setDataConstraint(1);
                }
                else if ("CONFIDENTIAL".equals(guarantee)) {
                    scBase.setDataConstraint(2);
                }
                else {
                    Log.warn("Unknown user-data-constraint:" + guarantee);
                    scBase.setDataConstraint(2);
                }
            }
            final Iterator iter2 = node.iterator("web-resource-collection");
            while (iter2.hasNext()) {
                final XmlParser.Node collection = iter2.next();
                final String name = collection.getString("web-resource-name", false, true);
                final Constraint sc = (Constraint)scBase.clone();
                sc.setName(name);
                final Iterator iter3 = collection.iterator("url-pattern");
                while (iter3.hasNext()) {
                    String url = iter3.next().toString(false, true);
                    url = this.normalizePattern(url);
                    final Iterator iter4 = collection.iterator("http-method");
                    if (iter4.hasNext()) {
                        while (iter4.hasNext()) {
                            final String method = iter4.next().toString(false, true);
                            final ConstraintMapping mapping = new ConstraintMapping();
                            mapping.setMethod(method);
                            mapping.setPathSpec(url);
                            mapping.setConstraint(sc);
                            this._constraintMappings = LazyList.add(this._constraintMappings, mapping);
                        }
                    }
                    else {
                        final ConstraintMapping mapping2 = new ConstraintMapping();
                        mapping2.setPathSpec(url);
                        mapping2.setConstraint(sc);
                        this._constraintMappings = LazyList.add(this._constraintMappings, mapping2);
                    }
                }
            }
        }
        catch (CloneNotSupportedException e) {
            Log.warn(e);
        }
    }
    
    protected void initLoginConfig(final XmlParser.Node node) {
        final XmlParser.Node method = node.get("auth-method");
        FormAuthenticator _formAuthenticator = null;
        if (method != null) {
            Authenticator authenticator = null;
            final String m = method.toString(false, true);
            if ("FORM".equals(m)) {
                _formAuthenticator = (FormAuthenticator)(authenticator = new FormAuthenticator());
            }
            else if ("BASIC".equals(m)) {
                authenticator = new BasicAuthenticator();
            }
            else if ("DIGEST".equals(m)) {
                authenticator = new DigestAuthenticator();
            }
            else if ("CLIENT_CERT".equals(m)) {
                authenticator = new ClientCertAuthenticator();
            }
            else if ("CLIENT-CERT".equals(m)) {
                authenticator = new ClientCertAuthenticator();
            }
            else {
                Log.warn("UNKNOWN AUTH METHOD: " + m);
            }
            this.getWebAppContext().getSecurityHandler().setAuthenticator(authenticator);
        }
        final XmlParser.Node name = node.get("realm-name");
        final UserRealm[] realms = ContextHandler.getCurrentContext().getContextHandler().getServer().getUserRealms();
        final String realm_name = (name == null) ? "default" : name.toString(false, true);
        UserRealm realm = this.getWebAppContext().getSecurityHandler().getUserRealm();
        for (int i = 0; realm == null && realms != null && i < realms.length; ++i) {
            if (realms[i] != null && realm_name.equals(realms[i].getName())) {
                realm = realms[i];
            }
        }
        if (realm == null) {
            final String msg = "Unknown realm: " + realm_name;
            Log.warn(msg);
        }
        else {
            this.getWebAppContext().getSecurityHandler().setUserRealm(realm);
        }
        final XmlParser.Node formConfig = node.get("form-login-config");
        if (formConfig != null) {
            if (_formAuthenticator == null) {
                Log.warn("FORM Authentication miss-configured");
            }
            else {
                final XmlParser.Node loginPage = formConfig.get("form-login-page");
                if (loginPage != null) {
                    _formAuthenticator.setLoginPage(loginPage.toString(false, true));
                }
                final XmlParser.Node errorPage = formConfig.get("form-error-page");
                if (errorPage != null) {
                    final String ep = errorPage.toString(false, true);
                    _formAuthenticator.setErrorPage(ep);
                }
            }
        }
    }
    
    protected void initSecurityRole(final XmlParser.Node node) {
    }
    
    protected String getJSPServletName() {
        if (this._jspServletName == null) {
            final Map.Entry entry = this._context.getServletHandler().getHolderEntry("test.jsp");
            if (entry != null) {
                final ServletHolder holder = entry.getValue();
                this._jspServletName = holder.getName();
            }
        }
        return this._jspServletName;
    }
}
