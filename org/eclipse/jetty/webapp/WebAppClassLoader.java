// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.webapp;

import org.eclipse.jetty.util.log.Log;
import java.util.Iterator;
import java.io.InputStream;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;
import org.eclipse.jetty.util.IO;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Collection;
import java.util.Enumeration;
import java.security.PermissionCollection;
import java.security.CodeSource;
import org.eclipse.jetty.util.StringUtil;
import java.util.Locale;
import java.io.File;
import org.eclipse.jetty.util.resource.ResourceCollection;
import org.eclipse.jetty.util.resource.Resource;
import java.util.StringTokenizer;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.HashSet;
import java.net.URL;
import java.io.IOException;
import java.lang.instrument.ClassFileTransformer;
import java.util.List;
import java.util.Set;
import org.eclipse.jetty.util.log.Logger;
import java.net.URLClassLoader;

public class WebAppClassLoader extends URLClassLoader
{
    private static final Logger LOG;
    private final Context _context;
    private final ClassLoader _parent;
    private final Set<String> _extensions;
    private String _name;
    private final List<ClassFileTransformer> _transformers;
    
    public WebAppClassLoader(final Context context) throws IOException {
        this(null, context);
    }
    
    public WebAppClassLoader(final ClassLoader parent, final Context context) throws IOException {
        super(new URL[0], (parent != null) ? parent : ((Thread.currentThread().getContextClassLoader() != null) ? Thread.currentThread().getContextClassLoader() : ((WebAppClassLoader.class.getClassLoader() != null) ? WebAppClassLoader.class.getClassLoader() : ClassLoader.getSystemClassLoader())));
        this._extensions = new HashSet<String>();
        this._name = String.valueOf(this.hashCode());
        this._transformers = new CopyOnWriteArrayList<ClassFileTransformer>();
        this._parent = this.getParent();
        this._context = context;
        if (this._parent == null) {
            throw new IllegalArgumentException("no parent classloader!");
        }
        this._extensions.add(".jar");
        this._extensions.add(".zip");
        final String extensions = System.getProperty(WebAppClassLoader.class.getName() + ".extensions");
        if (extensions != null) {
            final StringTokenizer tokenizer = new StringTokenizer(extensions, ",;");
            while (tokenizer.hasMoreTokens()) {
                this._extensions.add(tokenizer.nextToken().trim());
            }
        }
        if (context.getExtraClasspath() != null) {
            this.addClassPath(context.getExtraClasspath());
        }
    }
    
    @Override
    public String getName() {
        return this._name;
    }
    
    public void setName(final String name) {
        this._name = name;
    }
    
    public Context getContext() {
        return this._context;
    }
    
    public void addClassPath(final Resource resource) throws IOException {
        if (resource instanceof ResourceCollection) {
            for (final Resource r : ((ResourceCollection)resource).getResources()) {
                this.addClassPath(r);
            }
        }
        else {
            this.addClassPath(resource.toString());
        }
    }
    
    public void addClassPath(final String classPath) throws IOException {
        if (classPath == null) {
            return;
        }
        final StringTokenizer tokenizer = new StringTokenizer(classPath, ",;");
        while (tokenizer.hasMoreTokens()) {
            final Resource resource = this._context.newResource(tokenizer.nextToken().trim());
            if (WebAppClassLoader.LOG.isDebugEnabled()) {
                WebAppClassLoader.LOG.debug("Path resource=" + resource, new Object[0]);
            }
            if (resource.isDirectory() && resource instanceof ResourceCollection) {
                this.addClassPath(resource);
            }
            else {
                final File file = resource.getFile();
                if (file != null) {
                    final URL url = resource.getURI().toURL();
                    this.addURL(url);
                }
                else {
                    if (!resource.isDirectory()) {
                        if (WebAppClassLoader.LOG.isDebugEnabled()) {
                            WebAppClassLoader.LOG.debug("Check file exists and is not nested jar: " + resource, new Object[0]);
                        }
                        throw new IllegalArgumentException("File not resolvable or incompatible with URLClassloader: " + resource);
                    }
                    this.addURL(resource.getURI().toURL());
                }
            }
        }
    }
    
    private boolean isFileSupported(final String file) {
        final int dot = file.lastIndexOf(46);
        return dot != -1 && this._extensions.contains(file.substring(dot));
    }
    
    public void addJars(final Resource lib) {
        if (lib.exists() && lib.isDirectory()) {
            final String[] files = lib.list();
            for (int f = 0; files != null && f < files.length; ++f) {
                try {
                    final Resource fn = lib.addPath(files[f]);
                    if (WebAppClassLoader.LOG.isDebugEnabled()) {
                        WebAppClassLoader.LOG.debug("addJar - {}", fn);
                    }
                    final String fnlc = fn.getName().toLowerCase(Locale.ENGLISH);
                    if (this.isFileSupported(fnlc)) {
                        String jar = fn.toString();
                        jar = StringUtil.replace(jar, ",", "%2C");
                        jar = StringUtil.replace(jar, ";", "%3B");
                        this.addClassPath(jar);
                    }
                }
                catch (Exception ex) {
                    WebAppClassLoader.LOG.warn("EXCEPTION ", ex);
                }
            }
        }
    }
    
    public PermissionCollection getPermissions(final CodeSource cs) {
        final PermissionCollection permissions = this._context.getPermissions();
        final PermissionCollection pc = (permissions == null) ? super.getPermissions(cs) : permissions;
        return pc;
    }
    
    @Override
    public Enumeration<URL> getResources(final String name) throws IOException {
        final boolean system_class = this._context.isSystemClass(name);
        final boolean server_class = this._context.isServerClass(name);
        final List<URL> from_parent = this.toList(server_class ? null : this._parent.getResources(name));
        final List<URL> from_webapp = this.toList((system_class && !from_parent.isEmpty()) ? null : this.findResources(name));
        if (this._context.isParentLoaderPriority()) {
            from_parent.addAll(from_webapp);
            return Collections.enumeration(from_parent);
        }
        from_webapp.addAll(from_parent);
        return Collections.enumeration(from_webapp);
    }
    
    private List<URL> toList(final Enumeration<URL> e) {
        if (e == null) {
            return new ArrayList<URL>();
        }
        return Collections.list(e);
    }
    
    @Override
    public URL getResource(final String name) {
        URL url = null;
        boolean tried_parent = false;
        String tmp = name;
        if (tmp != null && tmp.endsWith(".class")) {
            tmp = tmp.substring(0, tmp.length() - 6);
        }
        final boolean system_class = this._context.isSystemClass(tmp);
        final boolean server_class = this._context.isServerClass(tmp);
        if (WebAppClassLoader.LOG.isDebugEnabled()) {
            WebAppClassLoader.LOG.debug("getResource({}) system={} server={} cl={}", name, system_class, server_class, this);
        }
        if (system_class && server_class) {
            return null;
        }
        ClassLoader source = null;
        if (this._parent != null && (this._context.isParentLoaderPriority() || system_class) && !server_class) {
            tried_parent = true;
            if (this._parent != null) {
                source = this._parent;
                url = this._parent.getResource(name);
            }
        }
        if (url == null) {
            url = this.findResource(name);
            source = this;
            if (url == null && name.startsWith("/")) {
                url = this.findResource(name.substring(1));
            }
        }
        if (url == null && !tried_parent && !server_class && this._parent != null) {
            tried_parent = true;
            source = this._parent;
            url = this._parent.getResource(name);
        }
        if (WebAppClassLoader.LOG.isDebugEnabled()) {
            WebAppClassLoader.LOG.debug("gotResource({})=={} from={} tried_parent={}", name, url, source, tried_parent);
        }
        return url;
    }
    
    @Override
    public Class<?> loadClass(final String name) throws ClassNotFoundException {
        return this.loadClass(name, false);
    }
    
    @Override
    protected Class<?> loadClass(final String name, final boolean resolve) throws ClassNotFoundException {
        synchronized (this.getClassLoadingLock(name)) {
            Class<?> c = this.findLoadedClass(name);
            ClassNotFoundException ex = null;
            boolean tried_parent = false;
            final boolean system_class = this._context.isSystemClass(name);
            final boolean server_class = this._context.isServerClass(name);
            if (WebAppClassLoader.LOG.isDebugEnabled()) {
                WebAppClassLoader.LOG.debug("loadClass({}) system={} server={} cl={}", name, system_class, server_class, this);
            }
            ClassLoader source = null;
            if (system_class && server_class) {
                return null;
            }
            if (c == null && this._parent != null && (this._context.isParentLoaderPriority() || system_class) && !server_class) {
                tried_parent = true;
                source = this._parent;
                try {
                    c = this._parent.loadClass(name);
                    if (WebAppClassLoader.LOG.isDebugEnabled()) {
                        WebAppClassLoader.LOG.debug("loaded " + c, new Object[0]);
                    }
                }
                catch (ClassNotFoundException e) {
                    ex = e;
                }
            }
            if (c == null) {
                try {
                    source = this;
                    c = this.findClass(name);
                }
                catch (ClassNotFoundException e) {
                    ex = e;
                }
            }
            if (c == null && this._parent != null && !tried_parent && !server_class) {
                tried_parent = true;
                source = this._parent;
                c = this._parent.loadClass(name);
            }
            if (c == null && ex != null) {
                if (WebAppClassLoader.LOG.isDebugEnabled()) {
                    WebAppClassLoader.LOG.debug("!loadedClass({}) from={} tried_parent={}", name, this, tried_parent);
                }
                throw ex;
            }
            if (WebAppClassLoader.LOG.isDebugEnabled()) {
                WebAppClassLoader.LOG.debug("loadedClass({})=={} from={} tried_parent={}", name, c, source, tried_parent);
            }
            if (resolve) {
                this.resolveClass(c);
            }
            return c;
        }
    }
    
    @Deprecated
    public void addClassFileTransformer(final ClassFileTransformer transformer) {
        this._transformers.add(transformer);
    }
    
    @Deprecated
    public boolean removeClassFileTransformer(final ClassFileTransformer transformer) {
        return this._transformers.remove(transformer);
    }
    
    public void addTransformer(final ClassFileTransformer transformer) {
        this._transformers.add(transformer);
    }
    
    public boolean removeTransformer(final ClassFileTransformer transformer) {
        return this._transformers.remove(transformer);
    }
    
    @Override
    protected Class<?> findClass(final String name) throws ClassNotFoundException {
        Class<?> clazz = null;
        if (this._transformers.isEmpty()) {
            clazz = super.findClass(name);
        }
        else {
            final String path = name.replace('.', '/').concat(".class");
            final URL url = this.getResource(path);
            if (url == null) {
                throw new ClassNotFoundException(name);
            }
            InputStream content = null;
            try {
                content = url.openStream();
                byte[] bytes = IO.readBytes(content);
                if (WebAppClassLoader.LOG.isDebugEnabled()) {
                    WebAppClassLoader.LOG.debug("foundClass({}) url={} cl={}", name, url, this);
                }
                for (final ClassFileTransformer transformer : this._transformers) {
                    final byte[] tmp = transformer.transform(this, name, null, null, bytes);
                    if (tmp != null) {
                        bytes = tmp;
                    }
                }
                clazz = this.defineClass(name, bytes, 0, bytes.length);
            }
            catch (IOException e) {
                throw new ClassNotFoundException(name, e);
            }
            catch (IllegalClassFormatException e2) {
                throw new ClassNotFoundException(name, e2);
            }
            finally {
                if (content != null) {
                    try {
                        content.close();
                    }
                    catch (IOException e3) {
                        throw new ClassNotFoundException(name, e3);
                    }
                }
            }
        }
        return clazz;
    }
    
    @Override
    public void close() throws IOException {
        super.close();
    }
    
    @Override
    public String toString() {
        return "WebAppClassLoader=" + this._name + "@" + Long.toHexString(this.hashCode());
    }
    
    static {
        registerAsParallelCapable();
        LOG = Log.getLogger(WebAppClassLoader.class);
    }
    
    public interface Context
    {
        Resource newResource(final String p0) throws IOException;
        
        PermissionCollection getPermissions();
        
        boolean isSystemClass(final String p0);
        
        boolean isServerClass(final String p0);
        
        boolean isParentLoaderPriority();
        
        String getExtraClasspath();
    }
}
