// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.security.authentication.server;

import java.util.Locale;
import com.google.common.base.Preconditions;

public final class AuthenticationHandlerUtil
{
    private AuthenticationHandlerUtil() {
    }
    
    public static String getAuthenticationHandlerClassName(final String authHandler) {
        final String handlerName = Preconditions.checkNotNull(authHandler).toLowerCase(Locale.ENGLISH);
        String authHandlerClassName = null;
        if (handlerName.equals("simple")) {
            authHandlerClassName = PseudoAuthenticationHandler.class.getName();
        }
        else if (handlerName.equals("kerberos")) {
            authHandlerClassName = KerberosAuthenticationHandler.class.getName();
        }
        else if (handlerName.equals("ldap")) {
            authHandlerClassName = LdapAuthenticationHandler.class.getName();
        }
        else if (handlerName.equals("multi-scheme")) {
            authHandlerClassName = MultiSchemeAuthenticationHandler.class.getName();
        }
        else {
            authHandlerClassName = authHandler;
        }
        return authHandlerClassName;
    }
    
    public static String checkAuthScheme(final String scheme) {
        if ("Basic".equalsIgnoreCase(scheme)) {
            return "Basic";
        }
        if ("Negotiate".equalsIgnoreCase(scheme)) {
            return "Negotiate";
        }
        if ("Digest".equalsIgnoreCase(scheme)) {
            return "Digest";
        }
        throw new IllegalArgumentException(String.format("Unsupported HTTP authentication scheme %s . Supported schemes are [%s, %s, %s]", scheme, "Basic", "Negotiate", "Digest"));
    }
    
    public static boolean matchAuthScheme(String scheme, String auth) {
        scheme = Preconditions.checkNotNull(scheme).trim();
        auth = Preconditions.checkNotNull(auth).trim();
        return auth.regionMatches(true, 0, scheme, 0, scheme.length());
    }
}
