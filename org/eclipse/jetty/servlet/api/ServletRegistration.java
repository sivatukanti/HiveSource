// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.servlet.api;

import java.util.Collection;
import java.util.Set;

public interface ServletRegistration
{
    Set<String> addMapping(final String... p0);
    
    Collection<String> getMappings();
    
    String getRunAsRole();
    
    public interface Dynamic extends ServletRegistration, Registration.Dynamic
    {
        void setLoadOnStartup(final int p0);
        
        void setRunAsRole(final String p0);
    }
}
