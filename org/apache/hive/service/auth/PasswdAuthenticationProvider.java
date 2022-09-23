// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hive.service.auth;

import javax.security.sasl.AuthenticationException;

public interface PasswdAuthenticationProvider
{
    void Authenticate(final String p0, final String p1) throws AuthenticationException;
}
