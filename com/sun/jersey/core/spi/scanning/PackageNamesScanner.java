// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.core.spi.scanning;

import java.security.Permission;
import java.lang.reflect.ReflectPermission;
import com.sun.jersey.api.uri.UriComponent;
import java.net.URI;
import java.util.Enumeration;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Iterator;
import com.sun.jersey.spi.service.ServiceFinder;
import com.sun.jersey.core.spi.scanning.uri.BundleSchemeScanner;
import com.sun.jersey.core.spi.scanning.uri.VfsSchemeScanner;
import com.sun.jersey.core.spi.scanning.uri.FileSchemeScanner;
import com.sun.jersey.core.spi.scanning.uri.JarZipSchemeScanner;
import java.util.HashMap;
import java.security.AccessController;
import com.sun.jersey.core.reflection.ReflectionHelper;
import com.sun.jersey.core.spi.scanning.uri.UriSchemeScanner;
import java.util.Map;

public class PackageNamesScanner implements Scanner
{
    private final String[] packages;
    private final ClassLoader classloader;
    private final Map<String, UriSchemeScanner> scanners;
    
    public PackageNamesScanner(final String[] packages) {
        this(AccessController.doPrivileged(ReflectionHelper.getContextClassLoaderPA()), packages);
    }
    
    public PackageNamesScanner(final ClassLoader classloader, final String[] packages) {
        this.packages = packages;
        this.classloader = classloader;
        this.scanners = new HashMap<String, UriSchemeScanner>();
        this.add(new JarZipSchemeScanner());
        this.add(new FileSchemeScanner());
        this.add(new VfsSchemeScanner());
        this.add(new BundleSchemeScanner());
        for (final UriSchemeScanner s : ServiceFinder.find(UriSchemeScanner.class)) {
            this.add(s);
        }
    }
    
    private void add(final UriSchemeScanner ss) {
        for (final String s : ss.getSchemes()) {
            this.scanners.put(s.toLowerCase(), ss);
        }
    }
    
    @Override
    public void scan(final ScannerListener cfl) {
        for (final String p : this.packages) {
            try {
                final Enumeration<URL> urls = getInstance().getResources(p.replace('.', '/'), this.classloader);
                while (urls.hasMoreElements()) {
                    try {
                        this.scan(this.toURI(urls.nextElement()), cfl);
                        continue;
                    }
                    catch (URISyntaxException ex) {
                        throw new ScannerException("Error when converting a URL to a URI", ex);
                    }
                    break;
                }
            }
            catch (IOException ex2) {
                throw new ScannerException("IO error when package scanning jar", ex2);
            }
        }
    }
    
    public static void setResourcesProvider(final ResourcesProvider provider) throws SecurityException {
        setInstance(provider);
    }
    
    private void scan(final URI u, final ScannerListener cfl) {
        final UriSchemeScanner ss = this.scanners.get(u.getScheme().toLowerCase());
        if (ss != null) {
            ss.scan(u, cfl);
            return;
        }
        throw new ScannerException("The URI scheme " + u.getScheme() + " of the URI " + u + " is not supported. Package scanning deployment is not" + " supported for such URIs." + "\nTry using a different deployment mechanism such as" + " explicitly declaring root resource and provider classes" + " using an extension of javax.ws.rs.core.Application");
    }
    
    private URI toURI(final URL url) throws URISyntaxException {
        try {
            return url.toURI();
        }
        catch (URISyntaxException e) {
            return URI.create(this.toExternalForm(url));
        }
    }
    
    private String toExternalForm(final URL u) {
        int len = u.getProtocol().length() + 1;
        if (u.getAuthority() != null && u.getAuthority().length() > 0) {
            len += 2 + u.getAuthority().length();
        }
        if (u.getPath() != null) {
            len += u.getPath().length();
        }
        if (u.getQuery() != null) {
            len += 1 + u.getQuery().length();
        }
        if (u.getRef() != null) {
            len += 1 + u.getRef().length();
        }
        final StringBuilder result = new StringBuilder(len);
        result.append(u.getProtocol());
        result.append(":");
        if (u.getAuthority() != null && u.getAuthority().length() > 0) {
            result.append("//");
            result.append(u.getAuthority());
        }
        if (u.getPath() != null) {
            result.append(UriComponent.contextualEncode(u.getPath(), UriComponent.Type.PATH));
        }
        if (u.getQuery() != null) {
            result.append('?');
            result.append(UriComponent.contextualEncode(u.getQuery(), UriComponent.Type.QUERY));
        }
        if (u.getRef() != null) {
            result.append("#");
            result.append(u.getRef());
        }
        return result.toString();
    }
    
    public abstract static class ResourcesProvider
    {
        private static volatile ResourcesProvider provider;
        
        private static ResourcesProvider getInstance() {
            ResourcesProvider result = ResourcesProvider.provider;
            if (result == null) {
                synchronized (ResourcesProvider.class) {
                    result = ResourcesProvider.provider;
                    if (result == null) {
                        result = (ResourcesProvider.provider = new ResourcesProvider() {
                            @Override
                            public Enumeration<URL> getResources(final String name, final ClassLoader cl) throws IOException {
                                return cl.getResources(name);
                            }
                        });
                    }
                }
            }
            return result;
        }
        
        private static void setInstance(final ResourcesProvider provider) throws SecurityException {
            final SecurityManager security = System.getSecurityManager();
            if (security != null) {
                final ReflectPermission rp = new ReflectPermission("suppressAccessChecks");
                security.checkPermission(rp);
            }
            synchronized (ResourcesProvider.class) {
                ResourcesProvider.provider = provider;
            }
        }
        
        public abstract Enumeration<URL> getResources(final String p0, final ClassLoader p1) throws IOException;
    }
}
