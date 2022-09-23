// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hive.service.auth;

import net.sf.jpam.Pam;
import javax.security.sasl.AuthenticationException;
import org.apache.hadoop.hive.conf.HiveConf;

public class PamAuthenticationProviderImpl implements PasswdAuthenticationProvider
{
    private final String pamServiceNames;
    
    PamAuthenticationProviderImpl() {
        final HiveConf conf = new HiveConf();
        this.pamServiceNames = conf.getVar(HiveConf.ConfVars.HIVE_SERVER2_PAM_SERVICES);
    }
    
    @Override
    public void Authenticate(final String user, final String password) throws AuthenticationException {
        if (this.pamServiceNames == null || this.pamServiceNames.trim().isEmpty()) {
            throw new AuthenticationException("No PAM services are set.");
        }
        final String[] split;
        final String[] pamServices = split = this.pamServiceNames.split(",");
        for (final String pamService : split) {
            final Pam pam = new Pam(pamService);
            final boolean isAuthenticated = pam.authenticateSuccessful(user, password);
            if (!isAuthenticated) {
                throw new AuthenticationException("Error authenticating with the PAM service: " + pamService);
            }
        }
    }
}
