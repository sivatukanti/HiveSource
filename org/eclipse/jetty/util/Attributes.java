// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.util;

import java.util.Enumeration;

public interface Attributes
{
    void removeAttribute(final String p0);
    
    void setAttribute(final String p0, final Object p1);
    
    Object getAttribute(final String p0);
    
    Enumeration<String> getAttributeNames();
    
    void clearAttributes();
}
