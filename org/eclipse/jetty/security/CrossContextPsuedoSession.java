// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.security;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;

public interface CrossContextPsuedoSession<T>
{
    T fetch(final HttpServletRequest p0);
    
    void store(final T p0, final HttpServletResponse p1);
    
    void clear(final HttpServletRequest p0);
}
