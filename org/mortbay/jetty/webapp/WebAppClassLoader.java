// 
// Decompiled by Procyon v0.5.36
// 

package org.mortbay.jetty.webapp;

import org.mortbay.util.LazyList;
import java.security.PermissionCollection;
import java.security.CodeSource;
import org.mortbay.util.StringUtil;
import java.io.InputStream;
import java.io.OutputStream;
import org.mortbay.util.IO;
import java.io.FileOutputStream;
import java.io.File;
import org.mortbay.log.Log;
import org.mortbay.resource.Resource;
import org.mortbay.jetty.handler.ContextHandler;
import java.util.StringTokenizer;
import java.net.URL;
import java.io.IOException;
import java.util.HashSet;
import java.net.URLClassLoader;

public class WebAppClassLoader extends URLClassLoader
{
    private String _name;
    private WebAppContext _context;
    private ClassLoader _parent;
    private HashSet _extensions;
    
    public WebAppClassLoader(final WebAppContext context) throws IOException {
        this(null, context);
    }
    
    public WebAppClassLoader(final ClassLoader parent, final WebAppContext context) throws IOException {
        super(new URL[0], (parent != null) ? parent : ((Thread.currentThread().getContextClassLoader() != null) ? Thread.currentThread().getContextClassLoader() : ((WebAppClassLoader.class.getClassLoader() != null) ? WebAppClassLoader.class.getClassLoader() : ClassLoader.getSystemClassLoader())));
        this._parent = this.getParent();
        this._context = context;
        if (this._parent == null) {
            throw new IllegalArgumentException("no parent classloader!");
        }
        (this._extensions = new HashSet()).add(".jar");
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
    
    public String getName() {
        return this._name;
    }
    
    public void setName(final String name) {
        this._name = name;
    }
    
    public ContextHandler getContext() {
        return this._context;
    }
    
    public void addClassPath(final String classPath) throws IOException {
        if (classPath == null) {
            return;
        }
        final StringTokenizer tokenizer = new StringTokenizer(classPath, ",;");
        while (tokenizer.hasMoreTokens()) {
            final Resource resource = Resource.newResource(tokenizer.nextToken());
            if (Log.isDebugEnabled()) {
                Log.debug("Path resource=" + resource);
            }
            final File file = resource.getFile();
            if (file != null) {
                final URL url = resource.getURL();
                this.addURL(url);
            }
            else if (!resource.isDirectory() && file == null) {
                final InputStream in = resource.getInputStream();
                File tmp_dir = this._context.getTempDirectory();
                if (tmp_dir == null) {
                    tmp_dir = File.createTempFile("jetty.cl.lib", null);
                    tmp_dir.mkdir();
                    tmp_dir.deleteOnExit();
                }
                final File lib = new File(tmp_dir, "lib");
                if (!lib.exists()) {
                    lib.mkdir();
                    lib.deleteOnExit();
                }
                final File jar = File.createTempFile("Jetty-", ".jar", lib);
                jar.deleteOnExit();
                if (Log.isDebugEnabled()) {
                    Log.debug("Extract " + resource + " to " + jar);
                }
                FileOutputStream out = null;
                try {
                    out = new FileOutputStream(jar);
                    IO.copy(in, out);
                }
                finally {
                    IO.close(out);
                }
                final URL url2 = jar.toURL();
                this.addURL(url2);
            }
            else {
                final URL url = resource.getURL();
                this.addURL(url);
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
                    final String fnlc = fn.getName().toLowerCase();
                    if (this.isFileSupported(fnlc)) {
                        String jar = fn.toString();
                        jar = StringUtil.replace(jar, ",", "%2C");
                        jar = StringUtil.replace(jar, ";", "%3B");
                        this.addClassPath(jar);
                    }
                }
                catch (Exception ex) {
                    Log.warn("EXCEPTION ", ex);
                }
            }
        }
    }
    
    public void destroy() {
        this._parent = null;
    }
    
    public PermissionCollection getPermissions(final CodeSource cs) {
        final PermissionCollection permissions = this._context.getPermissions();
        final PermissionCollection pc = (permissions == null) ? super.getPermissions(cs) : permissions;
        return pc;
    }
    
    public URL getResource(final String name) {
        URL url = null;
        boolean tried_parent = false;
        if (this._context.isParentLoaderPriority() || this.isSystemPath(name)) {
            tried_parent = true;
            if (this._parent != null) {
                url = this._parent.getResource(name);
            }
        }
        if (url == null) {
            url = this.findResource(name);
            if (url == null && name.startsWith("/")) {
                if (Log.isDebugEnabled()) {
                    Log.debug("HACK leading / off " + name);
                }
                url = this.findResource(name.substring(1));
            }
        }
        if (url == null && !tried_parent && this._parent != null) {
            url = this._parent.getResource(name);
        }
        if (url != null && Log.isDebugEnabled()) {
            Log.debug("getResource(" + name + ")=" + url);
        }
        return url;
    }
    
    public boolean isServerPath(String name) {
        for (name = name.replace('/', '.'); name.startsWith("."); name = name.substring(1)) {}
        final String[] server_classes = this._context.getServerClasses();
        if (server_classes != null) {
            for (int i = 0; i < server_classes.length; ++i) {
                boolean result = true;
                String c = server_classes[i];
                if (c.startsWith("-")) {
                    c = c.substring(1);
                    result = false;
                }
                if (c.endsWith(".")) {
                    if (name.startsWith(c)) {
                        return result;
                    }
                }
                else if (name.equals(c)) {
                    return result;
                }
            }
        }
        return false;
    }
    
    public boolean isSystemPath(String name) {
        for (name = name.replace('/', '.'); name.startsWith("."); name = name.substring(1)) {}
        final String[] system_classes = this._context.getSystemClasses();
        if (system_classes != null) {
            for (int i = 0; i < system_classes.length; ++i) {
                boolean result = true;
                String c = system_classes[i];
                if (c.startsWith("-")) {
                    c = c.substring(1);
                    result = false;
                }
                if (c.endsWith(".")) {
                    if (name.startsWith(c)) {
                        return result;
                    }
                }
                else if (name.equals(c)) {
                    return result;
                }
            }
        }
        return false;
    }
    
    public Class loadClass(final String name) throws ClassNotFoundException {
        return this.loadClass(name, false);
    }
    
    protected synchronized Class loadClass(final String name, final boolean resolve) throws ClassNotFoundException {
        Class c = this.findLoadedClass(name);
        ClassNotFoundException ex = null;
        boolean tried_parent = false;
        if (c == null && this._parent != null && (this._context.isParentLoaderPriority() || this.isSystemPath(name))) {
            tried_parent = true;
            try {
                c = this._parent.loadClass(name);
                if (Log.isDebugEnabled()) {
                    Log.debug("loaded " + c);
                }
            }
            catch (ClassNotFoundException e) {
                ex = e;
            }
        }
        if (c == null) {
            try {
                c = this.findClass(name);
            }
            catch (ClassNotFoundException e) {
                ex = e;
            }
        }
        if (c == null && this._parent != null && !tried_parent && !this.isServerPath(name)) {
            c = this._parent.loadClass(name);
        }
        if (c == null) {
            throw ex;
        }
        if (resolve) {
            this.resolveClass(c);
        }
        if (Log.isDebugEnabled()) {
            Log.debug("loaded " + c + " from " + c.getClassLoader());
        }
        return c;
    }
    
    public String toString() {
        if (Log.isDebugEnabled()) {
            return "ContextLoader@" + this._name + "(" + LazyList.array2List(this.getURLs()) + ") / " + this._parent;
        }
        return "ContextLoader@" + this._name;
    }
}
