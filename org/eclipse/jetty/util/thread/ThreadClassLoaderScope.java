// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.util.thread;

import java.io.Closeable;

public class ThreadClassLoaderScope implements Closeable
{
    private final ClassLoader old;
    private final ClassLoader scopedClassLoader;
    
    public ThreadClassLoaderScope(final ClassLoader cl) {
        this.old = Thread.currentThread().getContextClassLoader();
        this.scopedClassLoader = cl;
        Thread.currentThread().setContextClassLoader(this.scopedClassLoader);
    }
    
    @Override
    public void close() {
        Thread.currentThread().setContextClassLoader(this.old);
    }
    
    public ClassLoader getScopedClassLoader() {
        return this.scopedClassLoader;
    }
}
