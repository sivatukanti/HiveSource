// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.webapp;

import org.eclipse.jetty.util.PatternMatcher;
import org.eclipse.jetty.util.log.Log;
import java.util.StringTokenizer;
import java.util.Collection;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.util.URIUtil;
import org.eclipse.jetty.server.NetworkConnector;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.util.Locale;
import org.eclipse.jetty.util.resource.JarResource;
import org.eclipse.jetty.util.IO;
import org.eclipse.jetty.util.resource.ResourceCollection;
import java.util.Set;
import java.util.Iterator;
import java.net.URL;
import java.util.List;
import java.io.File;
import java.net.URISyntaxException;
import java.net.URLClassLoader;
import java.net.URI;
import java.util.ArrayList;
import java.util.regex.Pattern;
import org.eclipse.jetty.util.JavaVersion;
import org.eclipse.jetty.util.resource.Resource;
import org.eclipse.jetty.util.log.Logger;

public class WebInfConfiguration extends AbstractConfiguration
{
    private static final Logger LOG;
    public static final String TEMPDIR_CONFIGURED = "org.eclipse.jetty.tmpdirConfigured";
    public static final String CONTAINER_JAR_PATTERN = "org.eclipse.jetty.server.webapp.ContainerIncludeJarPattern";
    public static final String WEBINF_JAR_PATTERN = "org.eclipse.jetty.server.webapp.WebInfIncludeJarPattern";
    public static final String RESOURCE_DIRS = "org.eclipse.jetty.resources";
    protected Resource _preUnpackBaseResource;
    
    @Override
    public void preConfigure(final WebAppContext context) throws Exception {
        this.resolveTempDirectory(context);
        this.unpack(context);
        this.findAndFilterContainerPaths(context);
        this.findAndFilterWebAppPaths(context);
        context.getMetaData().setWebInfClassesDirs(this.findClassDirs(context));
    }
    
    public void findAndFilterContainerPaths(final WebAppContext context) throws Exception {
        int targetPlatform = JavaVersion.VERSION.getPlatform();
        final Object target = context.getAttribute("org.eclipse.jetty.javaTargetPlatform");
        if (target != null) {
            targetPlatform = Integer.valueOf(target.toString());
        }
        String tmp = (String)context.getAttribute("org.eclipse.jetty.server.webapp.ContainerIncludeJarPattern");
        final Pattern containerPattern = (tmp == null) ? null : Pattern.compile(tmp);
        final ContainerPathNameMatcher containerPathNameMatcher = new ContainerPathNameMatcher(context, containerPattern);
        ClassLoader loader = null;
        if (context.getClassLoader() != null) {
            loader = context.getClassLoader().getParent();
        }
        final List<URI> containerUris = new ArrayList<URI>();
        while (loader != null && loader instanceof URLClassLoader) {
            final URL[] urls = ((URLClassLoader)loader).getURLs();
            if (urls != null) {
                for (final URL u : urls) {
                    try {
                        containerUris.add(u.toURI());
                    }
                    catch (URISyntaxException e) {
                        containerUris.add(new URI(u.toString().replaceAll(" ", "%20")));
                    }
                }
            }
            loader = loader.getParent();
        }
        if (WebInfConfiguration.LOG.isDebugEnabled()) {
            WebInfConfiguration.LOG.debug("Matching container urls {}", containerUris);
        }
        containerPathNameMatcher.match(containerUris);
        if (JavaVersion.VERSION.getPlatform() >= 9) {
            tmp = System.getProperty("java.class.path");
            if (tmp != null) {
                final List<URI> cpUris = new ArrayList<URI>();
                final String[] split;
                final String[] entries = split = tmp.split(File.pathSeparator);
                for (final String entry : split) {
                    final File f = new File(entry);
                    cpUris.add(f.toURI());
                }
                if (WebInfConfiguration.LOG.isDebugEnabled()) {
                    WebInfConfiguration.LOG.debug("Matching java.class.path {}", cpUris);
                }
                containerPathNameMatcher.match(cpUris);
            }
        }
        if (targetPlatform >= 9) {
            tmp = System.getProperty("jdk.module.path");
            if (tmp != null) {
                final List<URI> moduleUris = new ArrayList<URI>();
                final String[] split2;
                final String[] entries = split2 = tmp.split(File.pathSeparator);
                for (final String entry : split2) {
                    final File dir = new File(entry);
                    final File[] files = dir.listFiles();
                    if (files != null) {
                        for (final File f2 : files) {
                            moduleUris.add(f2.toURI());
                        }
                    }
                }
                if (WebInfConfiguration.LOG.isDebugEnabled()) {
                    WebInfConfiguration.LOG.debug("Matching jdk.module.path {}", moduleUris);
                }
                containerPathNameMatcher.match(moduleUris);
            }
        }
        if (WebInfConfiguration.LOG.isDebugEnabled()) {
            WebInfConfiguration.LOG.debug("Container paths selected:{}", context.getMetaData().getContainerResources());
        }
    }
    
    public void findAndFilterWebAppPaths(final WebAppContext context) throws Exception {
        final String tmp = (String)context.getAttribute("org.eclipse.jetty.server.webapp.WebInfIncludeJarPattern");
        final Pattern webInfPattern = (tmp == null) ? null : Pattern.compile(tmp);
        final WebAppPathNameMatcher matcher = new WebAppPathNameMatcher(context, webInfPattern);
        final List<Resource> jars = this.findJars(context);
        if (jars != null) {
            final List<URI> uris = new ArrayList<URI>();
            final int i = 0;
            for (final Resource r : jars) {
                uris.add(r.getURI());
            }
            matcher.match(uris);
        }
    }
    
    @Override
    public void configure(final WebAppContext context) throws Exception {
        if (context.isStarted()) {
            if (WebInfConfiguration.LOG.isDebugEnabled()) {
                WebInfConfiguration.LOG.debug("Cannot configure webapp " + context + " after it is started", new Object[0]);
            }
            return;
        }
        final Resource web_inf = context.getWebInf();
        if (web_inf != null && web_inf.isDirectory() && context.getClassLoader() instanceof WebAppClassLoader) {
            final Resource classes = web_inf.addPath("classes/");
            if (classes.exists()) {
                ((WebAppClassLoader)context.getClassLoader()).addClassPath(classes);
            }
            final Resource lib = web_inf.addPath("lib/");
            if (lib.exists() || lib.isDirectory()) {
                ((WebAppClassLoader)context.getClassLoader()).addJars(lib);
            }
        }
        final Set<Resource> resources = (Set<Resource>)context.getAttribute("org.eclipse.jetty.resources");
        if (resources != null && !resources.isEmpty()) {
            final Resource[] collection = new Resource[resources.size() + 1];
            int i = 0;
            collection[i++] = context.getBaseResource();
            for (final Resource resource : resources) {
                collection[i++] = resource;
            }
            context.setBaseResource(new ResourceCollection(collection));
        }
    }
    
    @Override
    public void deconfigure(final WebAppContext context) throws Exception {
        if (!context.isPersistTempDirectory()) {
            IO.delete(context.getTempDirectory());
        }
        final Boolean tmpdirConfigured = (Boolean)context.getAttribute("org.eclipse.jetty.tmpdirConfigured");
        if (tmpdirConfigured != null && !tmpdirConfigured) {
            context.setTempDirectory(null);
        }
        if (context.getBaseResource() != null) {
            context.getBaseResource().close();
        }
        context.setBaseResource(this._preUnpackBaseResource);
    }
    
    @Override
    public void cloneConfigure(final WebAppContext template, final WebAppContext context) throws Exception {
        final File tmpDir = File.createTempFile(getCanonicalNameForWebAppTmpDir(context), "", template.getTempDirectory().getParentFile());
        if (tmpDir.exists()) {
            IO.delete(tmpDir);
        }
        tmpDir.mkdir();
        tmpDir.deleteOnExit();
        context.setTempDirectory(tmpDir);
    }
    
    public void resolveTempDirectory(final WebAppContext context) throws Exception {
        File tmpDir = context.getTempDirectory();
        if (tmpDir != null) {
            this.configureTempDirectory(tmpDir, context);
            context.setAttribute("org.eclipse.jetty.tmpdirConfigured", Boolean.TRUE);
            return;
        }
        final File servletTmpDir = this.asFile(context.getAttribute("javax.servlet.context.tempdir"));
        if (servletTmpDir != null) {
            tmpDir = servletTmpDir;
            this.configureTempDirectory(tmpDir, context);
            context.setAttribute("javax.servlet.context.tempdir", tmpDir);
            context.setTempDirectory(tmpDir);
            return;
        }
        final File baseTemp = this.asFile(context.getAttribute("org.eclipse.jetty.webapp.basetempdir"));
        if (baseTemp != null && baseTemp.isDirectory() && baseTemp.canWrite()) {
            this.makeTempDirectory(baseTemp, context);
            return;
        }
        final File jettyBase = this.asFile(System.getProperty("jetty.base"));
        if (jettyBase != null) {
            final File work = new File(jettyBase, "work");
            if (work.exists() && work.isDirectory() && work.canWrite()) {
                context.setPersistTempDirectory(true);
                this.makeTempDirectory(work, context);
                return;
            }
        }
        this.makeTempDirectory(new File(System.getProperty("java.io.tmpdir")), context);
    }
    
    private File asFile(final Object fileattr) {
        if (fileattr == null) {
            return null;
        }
        if (fileattr instanceof File) {
            return (File)fileattr;
        }
        if (fileattr instanceof String) {
            return new File((String)fileattr);
        }
        return null;
    }
    
    public void makeTempDirectory(final File parent, final WebAppContext context) throws Exception {
        if (parent == null || !parent.exists() || !parent.canWrite() || !parent.isDirectory()) {
            throw new IllegalStateException("Parent for temp dir not configured correctly: " + ((parent == null) ? "null" : ("writeable=" + parent.canWrite())));
        }
        final String temp = getCanonicalNameForWebAppTmpDir(context);
        File tmpDir = null;
        if (context.isPersistTempDirectory()) {
            tmpDir = new File(parent, temp);
        }
        else {
            tmpDir = File.createTempFile(temp, ".dir", parent);
            tmpDir.delete();
            tmpDir.mkdirs();
        }
        this.configureTempDirectory(tmpDir, context);
        if (WebInfConfiguration.LOG.isDebugEnabled()) {
            WebInfConfiguration.LOG.debug("Set temp dir " + tmpDir, new Object[0]);
        }
        context.setTempDirectory(tmpDir);
    }
    
    public void configureTempDirectory(final File dir, final WebAppContext context) {
        if (dir == null) {
            throw new IllegalArgumentException("Null temp dir");
        }
        if (dir.exists() && !context.isPersistTempDirectory() && !IO.delete(dir)) {
            throw new IllegalStateException("Failed to delete temp dir " + dir);
        }
        if (!dir.exists()) {
            dir.mkdirs();
        }
        if (!context.isPersistTempDirectory()) {
            dir.deleteOnExit();
        }
        if (!dir.canWrite() || !dir.isDirectory()) {
            throw new IllegalStateException("Temp dir " + dir + " not useable: writeable=" + dir.canWrite() + ", dir=" + dir.isDirectory());
        }
    }
    
    public void unpack(final WebAppContext context) throws IOException {
        Resource web_app = context.getBaseResource();
        this._preUnpackBaseResource = context.getBaseResource();
        if (web_app == null) {
            final String war = context.getWar();
            if (war != null && war.length() > 0) {
                web_app = context.newResource(war);
            }
            else {
                web_app = context.getBaseResource();
            }
            if (web_app == null) {
                throw new IllegalStateException("No resourceBase or war set for context");
            }
            if (web_app.isAlias()) {
                WebInfConfiguration.LOG.debug(web_app + " anti-aliased to " + web_app.getAlias(), new Object[0]);
                web_app = context.newResource(web_app.getAlias());
            }
            if (WebInfConfiguration.LOG.isDebugEnabled()) {
                WebInfConfiguration.LOG.debug("Try webapp=" + web_app + ", exists=" + web_app.exists() + ", directory=" + web_app.isDirectory() + " file=" + web_app.getFile(), new Object[0]);
            }
            if (web_app.exists() && !web_app.isDirectory() && !web_app.toString().startsWith("jar:")) {
                final Resource jarWebApp = JarResource.newJarResource(web_app);
                if (jarWebApp.exists() && jarWebApp.isDirectory()) {
                    web_app = jarWebApp;
                }
            }
            if (web_app.exists() && ((context.isCopyWebDir() && web_app.getFile() != null && web_app.getFile().isDirectory()) || (context.isExtractWAR() && web_app.getFile() != null && !web_app.getFile().isDirectory()) || (context.isExtractWAR() && web_app.getFile() == null) || !web_app.isDirectory())) {
                File extractedWebAppDir = null;
                if (war != null) {
                    final File warfile = Resource.newResource(war).getFile();
                    if (warfile != null && warfile.getName().toLowerCase(Locale.ENGLISH).endsWith(".war")) {
                        final File sibling = new File(warfile.getParent(), warfile.getName().substring(0, warfile.getName().length() - 4));
                        if (sibling.exists() && sibling.isDirectory() && sibling.canWrite()) {
                            extractedWebAppDir = sibling;
                        }
                    }
                }
                if (extractedWebAppDir == null) {
                    extractedWebAppDir = new File(context.getTempDirectory(), "webapp");
                }
                if (web_app.getFile() != null && web_app.getFile().isDirectory()) {
                    WebInfConfiguration.LOG.debug("Copy " + web_app + " to " + extractedWebAppDir, new Object[0]);
                    web_app.copyTo(extractedWebAppDir);
                }
                else {
                    final File extractionLock = new File(context.getTempDirectory(), ".extract_lock");
                    if (!extractedWebAppDir.exists()) {
                        extractionLock.createNewFile();
                        extractedWebAppDir.mkdir();
                        WebInfConfiguration.LOG.debug("Extract " + web_app + " to " + extractedWebAppDir, new Object[0]);
                        final Resource jar_web_app = JarResource.newJarResource(web_app);
                        jar_web_app.copyTo(extractedWebAppDir);
                        extractionLock.delete();
                    }
                    else if (web_app.lastModified() > extractedWebAppDir.lastModified() || extractionLock.exists()) {
                        extractionLock.createNewFile();
                        IO.delete(extractedWebAppDir);
                        extractedWebAppDir.mkdir();
                        WebInfConfiguration.LOG.debug("Extract " + web_app + " to " + extractedWebAppDir, new Object[0]);
                        final Resource jar_web_app = JarResource.newJarResource(web_app);
                        jar_web_app.copyTo(extractedWebAppDir);
                        extractionLock.delete();
                    }
                }
                web_app = Resource.newResource(extractedWebAppDir.getCanonicalPath());
            }
            if (!web_app.exists() || !web_app.isDirectory()) {
                WebInfConfiguration.LOG.warn("Web application not found " + war, new Object[0]);
                throw new FileNotFoundException(war);
            }
            context.setBaseResource(web_app);
            if (WebInfConfiguration.LOG.isDebugEnabled()) {
                WebInfConfiguration.LOG.debug("webapp=" + web_app, new Object[0]);
            }
        }
        if (context.isCopyWebInf() && !context.isCopyWebDir()) {
            Resource web_inf = web_app.addPath("WEB-INF/");
            final File extractedWebInfDir = new File(context.getTempDirectory(), "webinf");
            if (extractedWebInfDir.exists()) {
                IO.delete(extractedWebInfDir);
            }
            extractedWebInfDir.mkdir();
            final Resource web_inf_lib = web_inf.addPath("lib/");
            final File webInfDir = new File(extractedWebInfDir, "WEB-INF");
            webInfDir.mkdir();
            if (web_inf_lib.exists()) {
                final File webInfLibDir = new File(webInfDir, "lib");
                if (webInfLibDir.exists()) {
                    IO.delete(webInfLibDir);
                }
                webInfLibDir.mkdir();
                WebInfConfiguration.LOG.debug("Copying WEB-INF/lib " + web_inf_lib + " to " + webInfLibDir, new Object[0]);
                web_inf_lib.copyTo(webInfLibDir);
            }
            final Resource web_inf_classes = web_inf.addPath("classes/");
            if (web_inf_classes.exists()) {
                final File webInfClassesDir = new File(webInfDir, "classes");
                if (webInfClassesDir.exists()) {
                    IO.delete(webInfClassesDir);
                }
                webInfClassesDir.mkdir();
                WebInfConfiguration.LOG.debug("Copying WEB-INF/classes from " + web_inf_classes + " to " + webInfClassesDir.getAbsolutePath(), new Object[0]);
                web_inf_classes.copyTo(webInfClassesDir);
            }
            web_inf = Resource.newResource(extractedWebInfDir.getCanonicalPath());
            final ResourceCollection rc = new ResourceCollection(new Resource[] { web_inf, web_app });
            if (WebInfConfiguration.LOG.isDebugEnabled()) {
                WebInfConfiguration.LOG.debug("context.resourcebase = " + rc, new Object[0]);
            }
            context.setBaseResource(rc);
        }
    }
    
    public static String getCanonicalNameForWebAppTmpDir(final WebAppContext context) {
        final StringBuffer canonicalName = new StringBuffer();
        canonicalName.append("jetty-");
        final Server server = context.getServer();
        if (server != null) {
            final Connector[] connectors = context.getServer().getConnectors();
            if (connectors.length > 0) {
                String host = null;
                int port = 0;
                if (connectors != null && connectors[0] instanceof NetworkConnector) {
                    final NetworkConnector connector = (NetworkConnector)connectors[0];
                    host = connector.getHost();
                    port = connector.getLocalPort();
                    if (port < 0) {
                        port = connector.getPort();
                    }
                }
                if (host == null) {
                    host = "0.0.0.0";
                }
                canonicalName.append(host);
                canonicalName.append("-");
                canonicalName.append(port);
                canonicalName.append("-");
            }
        }
        try {
            Resource resource = context.getBaseResource();
            if (resource == null) {
                if (context.getWar() == null || context.getWar().length() == 0) {
                    throw new IllegalStateException("No resourceBase or war set for context");
                }
                resource = context.newResource(context.getWar());
            }
            String tmp = URIUtil.decodePath(resource.getURL().getPath());
            if (tmp.endsWith("/")) {
                tmp = tmp.substring(0, tmp.length() - 1);
            }
            if (tmp.endsWith("!")) {
                tmp = tmp.substring(0, tmp.length() - 1);
            }
            final int i = tmp.lastIndexOf("/");
            canonicalName.append(tmp.substring(i + 1, tmp.length()));
            canonicalName.append("-");
        }
        catch (Exception e) {
            WebInfConfiguration.LOG.warn("Can't generate resourceBase as part of webapp tmp dir name: " + e, new Object[0]);
            WebInfConfiguration.LOG.debug(e);
        }
        String contextPath = context.getContextPath();
        contextPath = contextPath.replace('/', '_');
        contextPath = contextPath.replace('\\', '_');
        canonicalName.append(contextPath);
        canonicalName.append("-");
        final String[] vhosts = context.getVirtualHosts();
        if (vhosts == null || vhosts.length <= 0) {
            canonicalName.append("any");
        }
        else {
            canonicalName.append(vhosts[0]);
        }
        for (int i = 0; i < canonicalName.length(); ++i) {
            final char c = canonicalName.charAt(i);
            if (!Character.isJavaIdentifierPart(c) && "-.".indexOf(c) < 0) {
                canonicalName.setCharAt(i, '.');
            }
        }
        canonicalName.append("-");
        return canonicalName.toString();
    }
    
    protected List<Resource> findClassDirs(final WebAppContext context) throws Exception {
        if (context == null) {
            return null;
        }
        final List<Resource> classDirs = new ArrayList<Resource>();
        final Resource webInfClasses = this.findWebInfClassesDir(context);
        if (webInfClasses != null) {
            classDirs.add(webInfClasses);
        }
        final List<Resource> extraClassDirs = this.findExtraClasspathDirs(context);
        if (extraClassDirs != null) {
            classDirs.addAll(extraClassDirs);
        }
        return classDirs;
    }
    
    protected List<Resource> findJars(final WebAppContext context) throws Exception {
        final List<Resource> jarResources = new ArrayList<Resource>();
        final List<Resource> webInfLibJars = this.findWebInfLibJars(context);
        if (webInfLibJars != null) {
            jarResources.addAll(webInfLibJars);
        }
        final List<Resource> extraClasspathJars = this.findExtraClasspathJars(context);
        if (extraClasspathJars != null) {
            jarResources.addAll(extraClasspathJars);
        }
        return jarResources;
    }
    
    protected List<Resource> findWebInfLibJars(final WebAppContext context) throws Exception {
        final Resource web_inf = context.getWebInf();
        if (web_inf == null || !web_inf.exists()) {
            return null;
        }
        final List<Resource> jarResources = new ArrayList<Resource>();
        final Resource web_inf_lib = web_inf.addPath("/lib");
        if (web_inf_lib.exists() && web_inf_lib.isDirectory()) {
            final String[] files = web_inf_lib.list();
            for (int f = 0; files != null && f < files.length; ++f) {
                try {
                    final Resource file = web_inf_lib.addPath(files[f]);
                    final String fnlc = file.getName().toLowerCase(Locale.ENGLISH);
                    final int dot = fnlc.lastIndexOf(46);
                    final String extension = (dot < 0) ? null : fnlc.substring(dot);
                    if (extension != null && (extension.equals(".jar") || extension.equals(".zip"))) {
                        jarResources.add(file);
                    }
                }
                catch (Exception ex) {
                    WebInfConfiguration.LOG.warn("EXCEPTION ", ex);
                }
            }
        }
        return jarResources;
    }
    
    protected List<Resource> findExtraClasspathJars(final WebAppContext context) throws Exception {
        if (context == null || context.getExtraClasspath() == null) {
            return null;
        }
        final List<Resource> jarResources = new ArrayList<Resource>();
        final StringTokenizer tokenizer = new StringTokenizer(context.getExtraClasspath(), ",;");
        while (tokenizer.hasMoreTokens()) {
            final Resource resource = context.newResource(tokenizer.nextToken().trim());
            final String fnlc = resource.getName().toLowerCase(Locale.ENGLISH);
            final int dot = fnlc.lastIndexOf(46);
            final String extension = (dot < 0) ? null : fnlc.substring(dot);
            if (extension != null && (extension.equals(".jar") || extension.equals(".zip"))) {
                jarResources.add(resource);
            }
        }
        return jarResources;
    }
    
    protected Resource findWebInfClassesDir(final WebAppContext context) throws Exception {
        if (context == null) {
            return null;
        }
        final Resource web_inf = context.getWebInf();
        if (web_inf != null && web_inf.isDirectory()) {
            final Resource classes = web_inf.addPath("classes/");
            if (classes.exists()) {
                return classes;
            }
        }
        return null;
    }
    
    protected List<Resource> findExtraClasspathDirs(final WebAppContext context) throws Exception {
        if (context == null || context.getExtraClasspath() == null) {
            return null;
        }
        final List<Resource> dirResources = new ArrayList<Resource>();
        final StringTokenizer tokenizer = new StringTokenizer(context.getExtraClasspath(), ",;");
        while (tokenizer.hasMoreTokens()) {
            final Resource resource = context.newResource(tokenizer.nextToken().trim());
            if (resource.exists() && resource.isDirectory()) {
                dirResources.add(resource);
            }
        }
        return dirResources;
    }
    
    static {
        LOG = Log.getLogger(WebInfConfiguration.class);
    }
    
    public class ContainerPathNameMatcher extends PatternMatcher
    {
        protected final WebAppContext _context;
        protected final Pattern _pattern;
        
        public ContainerPathNameMatcher(final WebAppContext context, final Pattern pattern) {
            if (context == null) {
                throw new IllegalArgumentException("Context null");
            }
            this._context = context;
            this._pattern = pattern;
        }
        
        public void match(final List<URI> uris) throws Exception {
            if (uris == null) {
                return;
            }
            this.match(this._pattern, uris.toArray(new URI[uris.size()]), false);
        }
        
        @Override
        public void matched(final URI uri) throws Exception {
            this._context.getMetaData().addContainerResource(Resource.newResource(uri));
        }
    }
    
    public class WebAppPathNameMatcher extends PatternMatcher
    {
        protected final WebAppContext _context;
        protected final Pattern _pattern;
        
        public WebAppPathNameMatcher(final WebAppContext context, final Pattern pattern) {
            if (context == null) {
                throw new IllegalArgumentException("Context null");
            }
            this._context = context;
            this._pattern = pattern;
        }
        
        public void match(final List<URI> uris) throws Exception {
            this.match(this._pattern, uris.toArray(new URI[uris.size()]), true);
        }
        
        @Override
        public void matched(final URI uri) throws Exception {
            this._context.getMetaData().addWebInfJar(Resource.newResource(uri));
        }
    }
}
