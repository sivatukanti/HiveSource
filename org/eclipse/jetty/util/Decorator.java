// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.util;

public interface Decorator
{
     <T> T decorate(final T p0);
    
    void destroy(final Object p0);
}
