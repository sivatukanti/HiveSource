// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.servlet.api;

import java.util.Collection;
import org.eclipse.jetty.server.DispatcherType;
import java.util.EnumSet;

public interface FilterRegistration
{
    void addMappingForServletNames(final EnumSet<DispatcherType> p0, final boolean p1, final String... p2);
    
    Collection<String> getServletNameMappings();
    
    void addMappingForUrlPatterns(final EnumSet<DispatcherType> p0, final boolean p1, final String... p2);
    
    Collection<String> getUrlPatternMappings();
    
    public interface Dynamic extends FilterRegistration, Registration.Dynamic
    {
    }
}
