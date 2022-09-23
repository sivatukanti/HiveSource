// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.util;

public final class ExternalResourceUtil
{
    public static void release(final ExternalResourceReleasable... releasables) {
        final ExternalResourceReleasable[] releasablesCopy = new ExternalResourceReleasable[releasables.length];
        for (int i = 0; i < releasables.length; ++i) {
            if (releasables[i] == null) {
                throw new NullPointerException("releasables[" + i + ']');
            }
            releasablesCopy[i] = releasables[i];
        }
        for (final ExternalResourceReleasable e : releasablesCopy) {
            e.releaseExternalResources();
        }
    }
    
    private ExternalResourceUtil() {
    }
}
