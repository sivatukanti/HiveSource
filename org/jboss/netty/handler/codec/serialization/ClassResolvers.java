// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.handler.codec.serialization;

import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;
import java.lang.ref.Reference;
import java.util.HashMap;

public final class ClassResolvers
{
    public static ClassResolver cacheDisabled(final ClassLoader classLoader) {
        return new ClassLoaderClassResolver(defaultClassLoader(classLoader));
    }
    
    public static ClassResolver weakCachingResolver(final ClassLoader classLoader) {
        return new CachingClassResolver(new ClassLoaderClassResolver(defaultClassLoader(classLoader)), new WeakReferenceMap<String, Class<?>>(new HashMap<String, Reference<Class<?>>>()));
    }
    
    public static ClassResolver softCachingResolver(final ClassLoader classLoader) {
        return new CachingClassResolver(new ClassLoaderClassResolver(defaultClassLoader(classLoader)), new SoftReferenceMap<String, Class<?>>(new HashMap<String, Reference<Class<?>>>()));
    }
    
    public static ClassResolver weakCachingConcurrentResolver(final ClassLoader classLoader) {
        return new CachingClassResolver(new ClassLoaderClassResolver(defaultClassLoader(classLoader)), new WeakReferenceMap<String, Class<?>>(new ConcurrentHashMap<String, Reference<Class<?>>>()));
    }
    
    public static ClassResolver softCachingConcurrentResolver(final ClassLoader classLoader) {
        return new CachingClassResolver(new ClassLoaderClassResolver(defaultClassLoader(classLoader)), new SoftReferenceMap<String, Class<?>>(new ConcurrentHashMap<String, Reference<Class<?>>>()));
    }
    
    static ClassLoader defaultClassLoader(final ClassLoader classLoader) {
        if (classLoader != null) {
            return classLoader;
        }
        final ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
        if (contextClassLoader != null) {
            return contextClassLoader;
        }
        return ClassResolvers.class.getClassLoader();
    }
    
    private ClassResolvers() {
    }
}
