// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class Uptime
{
    public static final int NOIMPL = -1;
    private static final Uptime INSTANCE;
    private Impl impl;
    
    public static Uptime getInstance() {
        return Uptime.INSTANCE;
    }
    
    private Uptime() {
        try {
            this.impl = new DefaultImpl();
        }
        catch (UnsupportedOperationException e) {
            System.err.printf("Defaulting Uptime to NOIMPL due to (%s) %s%n", e.getClass().getName(), e.getMessage());
            this.impl = null;
        }
    }
    
    public Impl getImpl() {
        return this.impl;
    }
    
    public void setImpl(final Impl impl) {
        this.impl = impl;
    }
    
    public static long getUptime() {
        final Uptime u = getInstance();
        if (u == null || u.impl == null) {
            return -1L;
        }
        return u.impl.getUptime();
    }
    
    static {
        INSTANCE = new Uptime();
    }
    
    public static class DefaultImpl implements Impl
    {
        public Object mxBean;
        public Method uptimeMethod;
        
        public DefaultImpl() {
            final ClassLoader cl = Thread.currentThread().getContextClassLoader();
            try {
                final Class<?> mgmtFactory = Class.forName("java.lang.management.ManagementFactory", true, cl);
                final Class<?> runtimeClass = Class.forName("java.lang.management.RuntimeMXBean", true, cl);
                final Class<?>[] noparams = (Class<?>[])new Class[0];
                final Method mxBeanMethod = mgmtFactory.getMethod("getRuntimeMXBean", noparams);
                if (mxBeanMethod == null) {
                    throw new UnsupportedOperationException("method getRuntimeMXBean() not found");
                }
                this.mxBean = mxBeanMethod.invoke(mgmtFactory, new Object[0]);
                if (this.mxBean == null) {
                    throw new UnsupportedOperationException("getRuntimeMXBean() method returned null");
                }
                this.uptimeMethod = runtimeClass.getMethod("getUptime", noparams);
                if (this.mxBean == null) {
                    throw new UnsupportedOperationException("method getUptime() not found");
                }
            }
            catch (ClassNotFoundException | NoClassDefFoundError | NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
                final Throwable t;
                final Throwable e = t;
                throw new UnsupportedOperationException("Implementation not available in this environment", e);
            }
        }
        
        @Override
        public long getUptime() {
            try {
                return (long)this.uptimeMethod.invoke(this.mxBean, new Object[0]);
            }
            catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ex2) {
                final Exception ex;
                final Exception e = ex;
                return -1L;
            }
        }
    }
    
    public interface Impl
    {
        long getUptime();
    }
}
