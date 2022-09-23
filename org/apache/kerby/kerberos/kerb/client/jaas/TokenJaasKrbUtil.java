// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb.client.jaas;

import java.util.Map;
import java.util.HashMap;
import javax.security.auth.login.AppConfigurationEntry;
import javax.security.auth.kerberos.KerberosPrincipal;
import javax.security.auth.login.LoginException;
import javax.security.auth.login.Configuration;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.login.LoginContext;
import java.util.Set;
import java.security.Principal;
import java.util.HashSet;
import javax.security.auth.Subject;
import java.io.File;

public class TokenJaasKrbUtil
{
    public static Subject loginUsingToken(final String principal, final File tokenCache, final File armorCache, final File ccache, final File signKeyFile) throws LoginException {
        final Subject subject = new Subject(false, new HashSet<Principal>(), new HashSet<Object>(), new HashSet<Object>());
        final Configuration conf = useTokenCache(principal, tokenCache, armorCache, ccache, signKeyFile);
        final String confName = "TokenCacheConf";
        final LoginContext loginContext = new LoginContext(confName, subject, null, conf);
        loginContext.login();
        return loginContext.getSubject();
    }
    
    public static Subject loginUsingToken(final String principal, final String tokenStr, final File armorCache, final File ccache, final File signKeyFile) throws LoginException {
        final Set<Principal> principals = new HashSet<Principal>();
        principals.add(new KerberosPrincipal(principal));
        final Subject subject = new Subject(false, principals, new HashSet<Object>(), new HashSet<Object>());
        final Configuration conf = useTokenStr(principal, tokenStr, armorCache, ccache, signKeyFile);
        final String confName = "TokenStrConf";
        final LoginContext loginContext = new LoginContext(confName, subject, null, conf);
        loginContext.login();
        return loginContext.getSubject();
    }
    
    private static Configuration useTokenCache(final String principal, final File tokenCache, final File armorCache, final File tgtCache, final File signKeyFile) {
        return new TokenJaasConf(principal, tokenCache, armorCache, tgtCache, signKeyFile);
    }
    
    private static Configuration useTokenStr(final String principal, final String tokenStr, final File armorCache, final File tgtCache, final File signKeyFile) {
        return new TokenJaasConf(principal, tokenStr, armorCache, tgtCache, signKeyFile);
    }
    
    static class TokenJaasConf extends Configuration
    {
        private String principal;
        private File tokenCache;
        private String tokenStr;
        private File armorCache;
        private File ccache;
        private File signKeyFile;
        
        TokenJaasConf(final String principal, final File tokenCache, final File armorCache, final File ccache, final File signKeyFile) {
            this.principal = principal;
            this.tokenCache = tokenCache;
            this.armorCache = armorCache;
            this.ccache = ccache;
            this.signKeyFile = signKeyFile;
        }
        
        TokenJaasConf(final String principal, final String tokenStr, final File armorCache, final File ccache, final File signKeyFile) {
            this.principal = principal;
            this.tokenStr = tokenStr;
            this.armorCache = armorCache;
            this.ccache = ccache;
            this.signKeyFile = signKeyFile;
        }
        
        @Override
        public AppConfigurationEntry[] getAppConfigurationEntry(final String name) {
            final Map<String, String> options = new HashMap<String, String>();
            options.put("principal", this.principal);
            if (this.tokenCache != null) {
                options.put("tokenCache", this.tokenCache.getAbsolutePath());
            }
            else if (this.tokenStr != null) {
                options.put("token", this.tokenStr);
            }
            options.put("armorCache", this.armorCache.getAbsolutePath());
            if (this.ccache != null) {
                options.put("credentialCache", this.ccache.getAbsolutePath());
            }
            if (this.signKeyFile != null) {
                options.put("signKeyFile", this.signKeyFile.getAbsolutePath());
            }
            return new AppConfigurationEntry[] { new AppConfigurationEntry("org.apache.kerby.kerberos.kerb.client.jaas.TokenAuthLoginModule", AppConfigurationEntry.LoginModuleControlFlag.REQUIRED, options) };
        }
    }
}
