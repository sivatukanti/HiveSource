// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hive.service.auth;

import javax.security.sasl.AuthenticationException;

public class AnonymousAuthenticationProviderImpl implements PasswdAuthenticationProvider
{
    @Override
    public void Authenticate(final String user, final String password) throws AuthenticationException {
    }
}
