// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.security;

import java.util.Set;
import java.util.List;

public interface ConstraintAware
{
    List<ConstraintMapping> getConstraintMappings();
    
    Set<String> getRoles();
    
    void setConstraintMappings(final List<ConstraintMapping> p0, final Set<String> p1);
    
    void addConstraintMapping(final ConstraintMapping p0);
    
    void addRole(final String p0);
    
    void setDenyUncoveredHttpMethods(final boolean p0);
    
    boolean isDenyUncoveredHttpMethods();
    
    boolean checkPathsWithUncoveredHttpMethods();
}
