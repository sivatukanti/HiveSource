// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.util.preventers;

import java.lang.reflect.Method;

public class GCThreadLeakPreventer extends AbstractLeakPreventer
{
    @Override
    public void prevent(final ClassLoader loader) {
        try {
            final Class<?> clazz = Class.forName("sun.misc.GC");
            final Method requestLatency = clazz.getMethod("requestLatency", Long.TYPE);
            requestLatency.invoke(null, 9223372036854775806L);
        }
        catch (ClassNotFoundException e) {
            GCThreadLeakPreventer.LOG.ignore(e);
        }
        catch (Exception e2) {
            GCThreadLeakPreventer.LOG.warn(e2);
        }
    }
}
