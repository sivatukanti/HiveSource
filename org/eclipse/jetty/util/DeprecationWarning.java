// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.util;

import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.util.log.Logger;

public class DeprecationWarning implements Decorator
{
    private static final Logger LOG;
    
    @Override
    public <T> T decorate(final T o) {
        if (o == null) {
            return null;
        }
        final Class<?> clazz = o.getClass();
        try {
            final Deprecated depr = clazz.getAnnotation(Deprecated.class);
            if (depr != null) {
                DeprecationWarning.LOG.warn("Using @Deprecated Class {}", clazz.getName());
            }
        }
        catch (Throwable t) {
            DeprecationWarning.LOG.ignore(t);
        }
        this.verifyIndirectTypes(clazz.getSuperclass(), clazz, "Class");
        for (final Class<?> ifaceClazz : clazz.getInterfaces()) {
            this.verifyIndirectTypes(ifaceClazz, clazz, "Interface");
        }
        return o;
    }
    
    private void verifyIndirectTypes(Class<?> superClazz, final Class<?> clazz, final String typeName) {
        try {
            while (superClazz != null && superClazz != Object.class) {
                final Deprecated supDepr = superClazz.getAnnotation(Deprecated.class);
                if (supDepr != null) {
                    DeprecationWarning.LOG.warn("Using indirect @Deprecated {} {} - (seen from {})", typeName, superClazz.getName(), clazz);
                }
                superClazz = superClazz.getSuperclass();
            }
        }
        catch (Throwable t) {
            DeprecationWarning.LOG.ignore(t);
        }
    }
    
    @Override
    public void destroy(final Object o) {
    }
    
    static {
        LOG = Log.getLogger(DeprecationWarning.class);
    }
}
