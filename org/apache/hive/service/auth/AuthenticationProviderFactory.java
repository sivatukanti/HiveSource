// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hive.service.auth;

import javax.security.sasl.AuthenticationException;

public final class AuthenticationProviderFactory
{
    private AuthenticationProviderFactory() {
    }
    
    public static PasswdAuthenticationProvider getAuthenticationProvider(final AuthMethods authMethod) throws AuthenticationException {
        if (authMethod == AuthMethods.LDAP) {
            return new LdapAuthenticationProviderImpl();
        }
        if (authMethod == AuthMethods.PAM) {
            return new PamAuthenticationProviderImpl();
        }
        if (authMethod == AuthMethods.CUSTOM) {
            return new CustomAuthenticationProviderImpl();
        }
        if (authMethod == AuthMethods.NONE) {
            return new AnonymousAuthenticationProviderImpl();
        }
        throw new AuthenticationException("Unsupported authentication method");
    }
    
    public enum AuthMethods
    {
        LDAP("LDAP"), 
        PAM("PAM"), 
        CUSTOM("CUSTOM"), 
        NONE("NONE");
        
        private final String authMethod;
        
        private AuthMethods(final String authMethod) {
            this.authMethod = authMethod;
        }
        
        public String getAuthMethod() {
            return this.authMethod;
        }
        
        public static AuthMethods getValidAuthMethod(final String authMethodStr) throws AuthenticationException {
            for (final AuthMethods auth : values()) {
                if (authMethodStr.equals(auth.getAuthMethod())) {
                    return auth;
                }
            }
            throw new AuthenticationException("Not a valid authentication method");
        }
    }
}
