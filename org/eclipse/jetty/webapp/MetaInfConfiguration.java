// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.webapp;

import org.eclipse.jetty.util.log.Log;
import java.util.Enumeration;
import java.util.jar.JarFile;
import java.util.jar.JarEntry;
import java.net.JarURLConnection;
import java.io.IOException;
import java.util.Collections;
import java.io.File;
import java.util.Map;
import java.net.URI;
import java.util.Set;
import org.eclipse.jetty.util.resource.EmptyResource;
import java.util.Iterator;
import java.net.URL;
import java.util.concurrent.ConcurrentHashMap;
import org.eclipse.jetty.util.resource.Resource;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import org.eclipse.jetty.util.log.Logger;

public class MetaInfConfiguration extends AbstractConfiguration
{
    private static final Logger LOG;
    public static final String USE_CONTAINER_METAINF_CACHE = "org.eclipse.jetty.metainf.useCache";
    public static final boolean DEFAULT_USE_CONTAINER_METAINF_CACHE = true;
    public static final String CACHED_CONTAINER_TLDS = "org.eclipse.jetty.tlds.cache";
    public static final String CACHED_CONTAINER_FRAGMENTS = "org.eclipse.jetty.webFragments.cache";
    public static final String CACHED_CONTAINER_RESOURCES = "org.eclipse.jetty.resources.cache";
    public static final String METAINF_TLDS = "org.eclipse.jetty.tlds";
    public static final String METAINF_FRAGMENTS = "org.eclipse.jetty.webFragments";
    public static final String METAINF_RESOURCES = "org.eclipse.jetty.resources";
    
    @Override
    public void preConfigure(final WebAppContext context) throws Exception {
        boolean useContainerCache = true;
        final Boolean attr = (Boolean)context.getServer().getAttribute("org.eclipse.jetty.metainf.useCache");
        if (attr != null) {
            useContainerCache = attr;
        }
        if (MetaInfConfiguration.LOG.isDebugEnabled()) {
            MetaInfConfiguration.LOG.debug("{} = {}", "org.eclipse.jetty.metainf.useCache", useContainerCache);
        }
        if (context.getAttribute("org.eclipse.jetty.tlds") == null) {
            context.setAttribute("org.eclipse.jetty.tlds", new HashSet());
        }
        if (context.getAttribute("org.eclipse.jetty.resources") == null) {
            context.setAttribute("org.eclipse.jetty.resources", new HashSet());
        }
        if (context.getAttribute("org.eclipse.jetty.webFragments") == null) {
            context.setAttribute("org.eclipse.jetty.webFragments", new HashMap());
        }
        this.scanJars(context, context.getMetaData().getContainerResources(), useContainerCache);
        this.scanJars(context, context.getMetaData().getWebInfJars(), false);
    }
    
    public void scanJars(final WebAppContext context, final Collection<Resource> jars, final boolean useCaches) throws Exception {
        ConcurrentHashMap<Resource, Resource> metaInfResourceCache = null;
        ConcurrentHashMap<Resource, Resource> metaInfFragmentCache = null;
        ConcurrentHashMap<Resource, Collection<URL>> metaInfTldCache = null;
        if (useCaches) {
            metaInfResourceCache = (ConcurrentHashMap<Resource, Resource>)context.getServer().getAttribute("org.eclipse.jetty.resources.cache");
            if (metaInfResourceCache == null) {
                metaInfResourceCache = new ConcurrentHashMap<Resource, Resource>();
                context.getServer().setAttribute("org.eclipse.jetty.resources.cache", metaInfResourceCache);
            }
            metaInfFragmentCache = (ConcurrentHashMap<Resource, Resource>)context.getServer().getAttribute("org.eclipse.jetty.webFragments.cache");
            if (metaInfFragmentCache == null) {
                metaInfFragmentCache = new ConcurrentHashMap<Resource, Resource>();
                context.getServer().setAttribute("org.eclipse.jetty.webFragments.cache", metaInfFragmentCache);
            }
            metaInfTldCache = (ConcurrentHashMap<Resource, Collection<URL>>)context.getServer().getAttribute("org.eclipse.jetty.tlds.cache");
            if (metaInfTldCache == null) {
                metaInfTldCache = new ConcurrentHashMap<Resource, Collection<URL>>();
                context.getServer().setAttribute("org.eclipse.jetty.tlds.cache", metaInfTldCache);
            }
        }
        if (jars != null) {
            for (final Resource r : jars) {
                this.scanForResources(context, r, metaInfResourceCache);
                this.scanForFragment(context, r, metaInfFragmentCache);
                this.scanForTlds(context, r, metaInfTldCache);
            }
        }
    }
    
    public void scanForResources(final WebAppContext context, final Resource target, final ConcurrentHashMap<Resource, Resource> cache) throws Exception {
        Resource resourcesDir = null;
        if (cache != null && cache.containsKey(target)) {
            resourcesDir = cache.get(target);
            if (resourcesDir == EmptyResource.INSTANCE) {
                if (MetaInfConfiguration.LOG.isDebugEnabled()) {
                    MetaInfConfiguration.LOG.debug(target + " cached as containing no META-INF/resources", new Object[0]);
                }
                return;
            }
            if (MetaInfConfiguration.LOG.isDebugEnabled()) {
                MetaInfConfiguration.LOG.debug(target + " META-INF/resources found in cache ", new Object[0]);
            }
        }
        else {
            if (MetaInfConfiguration.LOG.isDebugEnabled()) {
                MetaInfConfiguration.LOG.debug(target + " META-INF/resources checked", new Object[0]);
            }
            if (target.isDirectory()) {
                resourcesDir = target.addPath("/META-INF/resources");
            }
            else {
                final URI uri = target.getURI();
                resourcesDir = Resource.newResource(this.uriJarPrefix(uri, "!/META-INF/resources"));
            }
            if (!resourcesDir.exists() || !resourcesDir.isDirectory()) {
                resourcesDir.close();
                resourcesDir = EmptyResource.INSTANCE;
            }
            if (cache != null) {
                final Resource old = cache.putIfAbsent(target, resourcesDir);
                if (old != null) {
                    resourcesDir = old;
                }
                else if (MetaInfConfiguration.LOG.isDebugEnabled()) {
                    MetaInfConfiguration.LOG.debug(target + " META-INF/resources cache updated", new Object[0]);
                }
            }
            if (resourcesDir == EmptyResource.INSTANCE) {
                return;
            }
        }
        Set<Resource> dirs = (Set<Resource>)context.getAttribute("org.eclipse.jetty.resources");
        if (dirs == null) {
            dirs = new HashSet<Resource>();
            context.setAttribute("org.eclipse.jetty.resources", dirs);
        }
        if (MetaInfConfiguration.LOG.isDebugEnabled()) {
            MetaInfConfiguration.LOG.debug(resourcesDir + " added to context", new Object[0]);
        }
        dirs.add(resourcesDir);
    }
    
    public void scanForFragment(final WebAppContext context, final Resource jar, final ConcurrentHashMap<Resource, Resource> cache) throws Exception {
        Resource webFrag = null;
        if (cache != null && cache.containsKey(jar)) {
            webFrag = cache.get(jar);
            if (webFrag == EmptyResource.INSTANCE) {
                if (MetaInfConfiguration.LOG.isDebugEnabled()) {
                    MetaInfConfiguration.LOG.debug(jar + " cached as containing no META-INF/web-fragment.xml", new Object[0]);
                }
                return;
            }
            if (MetaInfConfiguration.LOG.isDebugEnabled()) {
                MetaInfConfiguration.LOG.debug(jar + " META-INF/web-fragment.xml found in cache ", new Object[0]);
            }
        }
        else {
            if (MetaInfConfiguration.LOG.isDebugEnabled()) {
                MetaInfConfiguration.LOG.debug(jar + " META-INF/web-fragment.xml checked", new Object[0]);
            }
            if (jar.isDirectory()) {
                webFrag = jar.addPath("/META-INF/web-fragment.xml");
            }
            else {
                final URI uri = jar.getURI();
                webFrag = Resource.newResource(this.uriJarPrefix(uri, "!/META-INF/web-fragment.xml"));
            }
            if (!webFrag.exists() || webFrag.isDirectory()) {
                webFrag.close();
                webFrag = EmptyResource.INSTANCE;
            }
            if (cache != null) {
                final Resource old = cache.putIfAbsent(jar, webFrag);
                if (old != null) {
                    webFrag = old;
                }
                else if (MetaInfConfiguration.LOG.isDebugEnabled()) {
                    MetaInfConfiguration.LOG.debug(jar + " META-INF/web-fragment.xml cache updated", new Object[0]);
                }
            }
            if (webFrag == EmptyResource.INSTANCE) {
                return;
            }
        }
        Map<Resource, Resource> fragments = (Map<Resource, Resource>)context.getAttribute("org.eclipse.jetty.webFragments");
        if (fragments == null) {
            fragments = new HashMap<Resource, Resource>();
            context.setAttribute("org.eclipse.jetty.webFragments", fragments);
        }
        fragments.put(jar, webFrag);
        if (MetaInfConfiguration.LOG.isDebugEnabled()) {
            MetaInfConfiguration.LOG.debug(webFrag + " added to context", new Object[0]);
        }
    }
    
    public void scanForTlds(final WebAppContext context, final Resource jar, final ConcurrentHashMap<Resource, Collection<URL>> cache) throws Exception {
        Collection<URL> tlds = null;
        if (cache != null && cache.containsKey(jar)) {
            final Collection<URL> tmp = cache.get(jar);
            if (tmp.isEmpty()) {
                if (MetaInfConfiguration.LOG.isDebugEnabled()) {
                    MetaInfConfiguration.LOG.debug(jar + " cached as containing no tlds", new Object[0]);
                }
                return;
            }
            tlds = tmp;
            if (MetaInfConfiguration.LOG.isDebugEnabled()) {
                MetaInfConfiguration.LOG.debug(jar + " tlds found in cache ", new Object[0]);
            }
        }
        else {
            tlds = new HashSet<URL>();
            if (jar.isDirectory()) {
                tlds.addAll(this.getTlds(jar.getFile()));
            }
            else {
                final URI uri = jar.getURI();
                tlds.addAll(this.getTlds(uri));
            }
            if (cache != null) {
                if (MetaInfConfiguration.LOG.isDebugEnabled()) {
                    MetaInfConfiguration.LOG.debug(jar + " tld cache updated", new Object[0]);
                }
                final Collection<URL> old = cache.putIfAbsent(jar, tlds);
                if (old != null) {
                    tlds = old;
                }
            }
            if (tlds.isEmpty()) {
                return;
            }
        }
        Collection<URL> metaInfTlds = (Collection<URL>)context.getAttribute("org.eclipse.jetty.tlds");
        if (metaInfTlds == null) {
            metaInfTlds = new HashSet<URL>();
            context.setAttribute("org.eclipse.jetty.tlds", metaInfTlds);
        }
        metaInfTlds.addAll(tlds);
        if (MetaInfConfiguration.LOG.isDebugEnabled()) {
            MetaInfConfiguration.LOG.debug("tlds added to context", new Object[0]);
        }
    }
    
    @Override
    public void postConfigure(final WebAppContext context) throws Exception {
        context.setAttribute("org.eclipse.jetty.resources", null);
        context.setAttribute("org.eclipse.jetty.webFragments", null);
        context.setAttribute("org.eclipse.jetty.tlds", null);
    }
    
    public Collection<URL> getTlds(final File dir) throws IOException {
        if (dir == null || !dir.isDirectory()) {
            return (Collection<URL>)Collections.emptySet();
        }
        final HashSet<URL> tlds = new HashSet<URL>();
        final File[] files = dir.listFiles();
        if (files != null) {
            for (final File f : files) {
                if (f.isDirectory()) {
                    tlds.addAll((Collection<?>)this.getTlds(f));
                }
                else {
                    final String name = f.getCanonicalPath();
                    if (name.contains("META-INF") && name.endsWith(".tld")) {
                        tlds.add(f.toURI().toURL());
                    }
                }
            }
        }
        return tlds;
    }
    
    public Collection<URL> getTlds(final URI uri) throws IOException {
        final HashSet<URL> tlds = new HashSet<URL>();
        final String jarUri = this.uriJarPrefix(uri, "!/");
        final URL url = new URL(jarUri);
        final JarURLConnection jarConn = (JarURLConnection)url.openConnection();
        jarConn.setUseCaches(Resource.getDefaultUseCaches());
        final JarFile jarFile = jarConn.getJarFile();
        final Enumeration<JarEntry> entries = jarFile.entries();
        while (entries.hasMoreElements()) {
            final JarEntry e = entries.nextElement();
            final String name = e.getName();
            if (name.startsWith("META-INF") && name.endsWith(".tld")) {
                tlds.add(new URL(jarUri + name));
            }
        }
        if (!Resource.getDefaultUseCaches()) {
            jarFile.close();
        }
        return tlds;
    }
    
    private String uriJarPrefix(final URI uri, final String suffix) {
        final String uriString = uri.toString();
        if (uriString.startsWith("jar:")) {
            return uriString + suffix;
        }
        return "jar:" + uriString + suffix;
    }
    
    static {
        LOG = Log.getLogger(MetaInfConfiguration.class);
    }
}
