// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.webapp;

import org.eclipse.jetty.util.annotation.ManagedOperation;
import java.io.IOException;
import java.net.URL;
import java.util.concurrent.ConcurrentHashMap;
import org.eclipse.jetty.util.ConcurrentHashSet;
import org.eclipse.jetty.util.annotation.ManagedObject;

@ManagedObject
public class CachingWebAppClassLoader extends WebAppClassLoader
{
    private final ConcurrentHashSet<String> _notFound;
    private final ConcurrentHashMap<String, URL> _cache;
    
    public CachingWebAppClassLoader(final ClassLoader parent, final Context context) throws IOException {
        super(parent, context);
        this._notFound = new ConcurrentHashSet<String>();
        this._cache = new ConcurrentHashMap<String, URL>();
    }
    
    public CachingWebAppClassLoader(final Context context) throws IOException {
        super(context);
        this._notFound = new ConcurrentHashSet<String>();
        this._cache = new ConcurrentHashMap<String, URL>();
    }
    
    @Override
    public URL getResource(final String name) {
        if (this._notFound.contains(name)) {
            return null;
        }
        URL url = this._cache.get(name);
        if (name == null) {
            url = super.getResource(name);
            if (url == null) {
                this._notFound.add(name);
            }
            else {
                this._cache.putIfAbsent(name, url);
            }
        }
        return url;
    }
    
    @Override
    protected Class<?> loadClass(final String name, final boolean resolve) throws ClassNotFoundException {
        if (this._notFound.contains(name)) {
            throw new ClassNotFoundException(name + ": in notfound cache");
        }
        try {
            return super.loadClass(name, resolve);
        }
        catch (ClassNotFoundException nfe) {
            this._notFound.add(name);
            throw nfe;
        }
    }
    
    @Override
    protected Class<?> findClass(final String name) throws ClassNotFoundException {
        if (this._notFound.contains(name)) {
            throw new ClassNotFoundException(name + ": in notfound cache");
        }
        try {
            return super.findClass(name);
        }
        catch (ClassNotFoundException nfe) {
            this._notFound.add(name);
            throw nfe;
        }
    }
    
    @ManagedOperation
    public void clearCache() {
        this._cache.clear();
        this._notFound.clear();
    }
    
    @Override
    public String toString() {
        return "Caching[" + super.toString() + "]";
    }
}
