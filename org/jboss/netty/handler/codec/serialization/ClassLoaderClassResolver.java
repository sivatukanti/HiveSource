// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.handler.codec.serialization;

class ClassLoaderClassResolver implements ClassResolver
{
    private final ClassLoader classLoader;
    
    ClassLoaderClassResolver(final ClassLoader classLoader) {
        this.classLoader = classLoader;
    }
    
    public Class<?> resolve(final String className) throws ClassNotFoundException {
        try {
            return this.classLoader.loadClass(className);
        }
        catch (ClassNotFoundException e) {
            return Class.forName(className, false, this.classLoader);
        }
    }
}
