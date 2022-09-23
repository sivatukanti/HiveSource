// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.webapp;

import org.eclipse.jetty.util.log.Log;
import java.io.InputStream;
import java.util.jar.JarInputStream;
import org.eclipse.jetty.util.resource.Resource;
import java.util.Locale;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.regex.Pattern;
import java.util.jar.JarEntry;
import java.net.URI;
import org.eclipse.jetty.util.log.Logger;
import org.eclipse.jetty.util.PatternMatcher;

@Deprecated
public abstract class JarScanner extends PatternMatcher
{
    private static final Logger LOG;
    
    public abstract void processEntry(final URI p0, final JarEntry p1);
    
    public void scan(final Pattern pattern, final URI[] uris, final boolean isNullInclusive) throws Exception {
        super.match(pattern, uris, isNullInclusive);
    }
    
    public void scan(final Pattern pattern, ClassLoader loader, final boolean isNullInclusive, final boolean visitParent) throws Exception {
        while (loader != null) {
            if (loader instanceof URLClassLoader) {
                final URL[] urls = ((URLClassLoader)loader).getURLs();
                if (urls != null) {
                    final URI[] uris = new URI[urls.length];
                    int i = 0;
                    for (final URL u : urls) {
                        uris[i++] = u.toURI();
                    }
                    this.scan(pattern, uris, isNullInclusive);
                }
            }
            if (visitParent) {
                loader = loader.getParent();
            }
            else {
                loader = null;
            }
        }
    }
    
    @Override
    public void matched(final URI uri) throws Exception {
        JarScanner.LOG.debug("Search of {}", uri);
        if (uri.toString().toLowerCase(Locale.ENGLISH).endsWith(".jar")) {
            final InputStream in = Resource.newResource(uri).getInputStream();
            if (in == null) {
                return;
            }
            final JarInputStream jar_in = new JarInputStream(in);
            try {
                for (JarEntry entry = jar_in.getNextJarEntry(); entry != null; entry = jar_in.getNextJarEntry()) {
                    this.processEntry(uri, entry);
                }
            }
            finally {
                jar_in.close();
            }
        }
    }
    
    static {
        LOG = Log.getLogger(JarScanner.class);
    }
}
