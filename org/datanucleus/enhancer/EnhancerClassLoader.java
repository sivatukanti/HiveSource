// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.enhancer;

import java.net.URL;
import org.datanucleus.exceptions.ClassNotResolvedException;
import org.datanucleus.ClassLoaderResolver;

public final class EnhancerClassLoader extends ClassLoader
{
    ClassLoaderResolver delegate;
    boolean loadingClass;
    boolean loadingResource;
    
    public EnhancerClassLoader() {
        super(Thread.currentThread().getContextClassLoader());
        this.loadingClass = false;
        this.loadingResource = false;
    }
    
    public EnhancerClassLoader(final ClassLoaderResolver iDelegate) {
        this.loadingClass = false;
        this.loadingResource = false;
        this.delegate = iDelegate;
    }
    
    public synchronized void defineClass(final String fullClassName, final byte[] bytes, final ClassLoaderResolver clr) {
        final ClassLoaderResolver oldDelegate = this.delegate;
        this.delegate = clr;
        try {
            this.defineClass(fullClassName, bytes, 0, bytes.length);
        }
        finally {
            this.delegate = oldDelegate;
        }
    }
    
    @Override
    public synchronized Class loadClass(final String name) throws ClassNotFoundException {
        if (this.loadingClass) {
            throw new ClassNotFoundException("Class " + name + " not found");
        }
        this.loadingClass = true;
        try {
            if (this.delegate != null) {
                try {
                    return this.delegate.classForName(name);
                }
                catch (ClassNotResolvedException cnrex) {
                    throw new ClassNotFoundException(cnrex.toString(), cnrex);
                }
            }
            return super.loadClass(name);
        }
        catch (ClassNotFoundException ex) {
            if (this.delegate != null) {
                try {
                    return this.delegate.classForName(name);
                }
                catch (ClassNotResolvedException cnrex2) {
                    throw new ClassNotFoundException(cnrex2.toString(), cnrex2);
                }
            }
            throw ex;
        }
        finally {
            this.loadingClass = false;
        }
    }
    
    @Override
    protected synchronized URL findResource(final String name) {
        if (this.loadingResource) {
            return null;
        }
        this.loadingResource = true;
        try {
            URL url = super.findResource(name);
            if (url == null && this.delegate != null) {
                url = this.delegate.getResource(name, null);
            }
            return url;
        }
        finally {
            this.loadingResource = false;
        }
    }
}
