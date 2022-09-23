// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.avro.util;

public class ClassUtils
{
    private ClassUtils() {
    }
    
    public static Class<?> forName(final String className) throws ClassNotFoundException {
        return forName(ClassUtils.class, className);
    }
    
    public static Class<?> forName(final Class<?> contextClass, final String className) throws ClassNotFoundException {
        Class<?> c = null;
        if (contextClass.getClassLoader() != null) {
            c = forName(className, contextClass.getClassLoader());
        }
        if (c == null && Thread.currentThread().getContextClassLoader() != null) {
            c = forName(className, Thread.currentThread().getContextClassLoader());
        }
        if (c == null) {
            throw new ClassNotFoundException("Failed to load class" + className);
        }
        return c;
    }
    
    public static Class<?> forName(final ClassLoader classLoader, final String className) throws ClassNotFoundException {
        Class<?> c = null;
        if (classLoader != null) {
            c = forName(className, classLoader);
        }
        if (c == null && Thread.currentThread().getContextClassLoader() != null) {
            c = forName(className, Thread.currentThread().getContextClassLoader());
        }
        if (c == null) {
            throw new ClassNotFoundException("Failed to load class" + className);
        }
        return c;
    }
    
    private static Class<?> forName(final String className, final ClassLoader classLoader) {
        Class<?> c = null;
        if (classLoader != null && className != null) {
            try {
                c = Class.forName(className, true, classLoader);
            }
            catch (ClassNotFoundException ex) {}
        }
        return c;
    }
}
