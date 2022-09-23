// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.util;

import java.lang.reflect.Proxy;
import java.io.ObjectStreamClass;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;

public class ClassLoadingObjectInputStream extends ObjectInputStream
{
    public ClassLoadingObjectInputStream(final InputStream in) throws IOException {
        super(in);
    }
    
    public ClassLoadingObjectInputStream() throws IOException {
    }
    
    public Class<?> resolveClass(final ObjectStreamClass cl) throws IOException, ClassNotFoundException {
        try {
            return Class.forName(cl.getName(), false, Thread.currentThread().getContextClassLoader());
        }
        catch (ClassNotFoundException e) {
            return super.resolveClass(cl);
        }
    }
    
    @Override
    protected Class<?> resolveProxyClass(final String[] interfaces) throws IOException, ClassNotFoundException {
        final ClassLoader loader = Thread.currentThread().getContextClassLoader();
        ClassLoader nonPublicLoader = null;
        boolean hasNonPublicInterface = false;
        final Class<?>[] classObjs = (Class<?>[])new Class[interfaces.length];
        for (int i = 0; i < interfaces.length; ++i) {
            final Class<?> cl = Class.forName(interfaces[i], false, loader);
            if ((cl.getModifiers() & 0x1) == 0x0) {
                if (hasNonPublicInterface) {
                    if (nonPublicLoader != cl.getClassLoader()) {
                        throw new IllegalAccessError("conflicting non-public interface class loaders");
                    }
                }
                else {
                    nonPublicLoader = cl.getClassLoader();
                    hasNonPublicInterface = true;
                }
            }
            classObjs[i] = cl;
        }
        try {
            return Proxy.getProxyClass(hasNonPublicInterface ? nonPublicLoader : loader, classObjs);
        }
        catch (IllegalArgumentException e) {
            throw new ClassNotFoundException(null, e);
        }
    }
}
