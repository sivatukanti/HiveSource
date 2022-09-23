// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.server;

import java.util.Map;
import java.security.Principal;
import javax.security.auth.Subject;

public interface UserIdentity
{
    public static final UserIdentity UNAUTHENTICATED_IDENTITY = new UnauthenticatedUserIdentity() {
        @Override
        public Subject getSubject() {
            return null;
        }
        
        @Override
        public Principal getUserPrincipal() {
            return null;
        }
        
        @Override
        public boolean isUserInRole(final String role, final Scope scope) {
            return false;
        }
        
        @Override
        public String toString() {
            return "UNAUTHENTICATED";
        }
    };
    
    Subject getSubject();
    
    Principal getUserPrincipal();
    
    boolean isUserInRole(final String p0, final Scope p1);
    
    public interface UnauthenticatedUserIdentity extends UserIdentity
    {
    }
    
    public interface Scope
    {
        String getContextPath();
        
        String getName();
        
        Map<String, String> getRoleRefMap();
    }
}
