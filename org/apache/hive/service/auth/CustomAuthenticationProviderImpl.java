// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hive.service.auth;

import javax.security.sasl.AuthenticationException;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.util.ReflectionUtils;
import org.apache.hadoop.hive.conf.HiveConf;

public class CustomAuthenticationProviderImpl implements PasswdAuthenticationProvider
{
    private final PasswdAuthenticationProvider customProvider;
    
    CustomAuthenticationProviderImpl() {
        final HiveConf conf = new HiveConf();
        final Class<? extends PasswdAuthenticationProvider> customHandlerClass = (Class<? extends PasswdAuthenticationProvider>)conf.getClass(HiveConf.ConfVars.HIVE_SERVER2_CUSTOM_AUTHENTICATION_CLASS.varname, PasswdAuthenticationProvider.class);
        this.customProvider = ReflectionUtils.newInstance(customHandlerClass, conf);
    }
    
    @Override
    public void Authenticate(final String user, final String password) throws AuthenticationException {
        this.customProvider.Authenticate(user, password);
    }
}
