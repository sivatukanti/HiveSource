// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.logging;

public abstract class InternalLoggerFactory
{
    private static volatile InternalLoggerFactory defaultFactory;
    
    public static InternalLoggerFactory getDefaultFactory() {
        return InternalLoggerFactory.defaultFactory;
    }
    
    public static void setDefaultFactory(final InternalLoggerFactory defaultFactory) {
        if (defaultFactory == null) {
            throw new NullPointerException("defaultFactory");
        }
        InternalLoggerFactory.defaultFactory = defaultFactory;
    }
    
    public static InternalLogger getInstance(final Class<?> clazz) {
        return getInstance(clazz.getName());
    }
    
    public static InternalLogger getInstance(final String name) {
        return getDefaultFactory().newInstance(name);
    }
    
    public abstract InternalLogger newInstance(final String p0);
    
    static {
        InternalLoggerFactory.defaultFactory = new JdkLoggerFactory();
    }
}
