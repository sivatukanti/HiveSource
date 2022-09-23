// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb.client;

import javax.security.auth.callback.UnsupportedCallbackException;
import java.io.IOException;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.Callback;
import java.util.Map;
import java.util.HashMap;
import javax.security.auth.login.AppConfigurationEntry;
import java.io.File;
import javax.security.auth.login.LoginException;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.login.Configuration;
import javax.security.auth.login.LoginContext;
import java.util.Set;
import javax.security.auth.kerberos.KerberosPrincipal;
import java.security.Principal;
import java.util.HashSet;
import javax.security.auth.Subject;

public final class JaasKrbUtil
{
    public static final boolean ENABLE_DEBUG = false;
    
    private JaasKrbUtil() {
    }
    
    public static Subject loginUsingPassword(final String principal, final String password) throws LoginException {
        final Set<Principal> principals = new HashSet<Principal>();
        principals.add(new KerberosPrincipal(principal));
        final Subject subject = new Subject(false, principals, new HashSet<Object>(), new HashSet<Object>());
        final Configuration conf = usePassword(principal);
        final String confName = "PasswordConf";
        final CallbackHandler callback = new KrbCallbackHandler(principal, password);
        final LoginContext loginContext = new LoginContext(confName, subject, callback, conf);
        loginContext.login();
        return loginContext.getSubject();
    }
    
    public static Subject loginUsingTicketCache(final String principal, final File cacheFile) throws LoginException {
        final Set<Principal> principals = new HashSet<Principal>();
        principals.add(new KerberosPrincipal(principal));
        final Subject subject = new Subject(false, principals, new HashSet<Object>(), new HashSet<Object>());
        final Configuration conf = useTicketCache(principal, cacheFile);
        final String confName = "TicketCacheConf";
        final LoginContext loginContext = new LoginContext(confName, subject, null, conf);
        loginContext.login();
        return loginContext.getSubject();
    }
    
    public static Subject loginUsingKeytab(final String principal, final File keytabFile) throws LoginException {
        final Set<Principal> principals = new HashSet<Principal>();
        principals.add(new KerberosPrincipal(principal));
        final Subject subject = new Subject(false, principals, new HashSet<Object>(), new HashSet<Object>());
        final Configuration conf = useKeytab(principal, keytabFile);
        final String confName = "KeytabConf";
        final LoginContext loginContext = new LoginContext(confName, subject, null, conf);
        loginContext.login();
        return loginContext.getSubject();
    }
    
    public static Configuration usePassword(final String principal) {
        return new PasswordJaasConf(principal);
    }
    
    public static Configuration useTicketCache(final String principal, final File credentialFile) {
        return new TicketCacheJaasConf(principal, credentialFile);
    }
    
    public static Configuration useKeytab(final String principal, final File keytabFile) {
        return new KeytabJaasConf(principal, keytabFile);
    }
    
    private static String getKrb5LoginModuleName() {
        return System.getProperty("java.vendor").contains("IBM") ? "com.ibm.security.auth.module.Krb5LoginModule" : "com.sun.security.auth.module.Krb5LoginModule";
    }
    
    static class KeytabJaasConf extends Configuration
    {
        private String principal;
        private File keytabFile;
        
        KeytabJaasConf(final String principal, final File keytab) {
            this.principal = principal;
            this.keytabFile = keytab;
        }
        
        @Override
        public AppConfigurationEntry[] getAppConfigurationEntry(final String name) {
            final Map<String, String> options = new HashMap<String, String>();
            options.put("keyTab", this.keytabFile.getAbsolutePath());
            options.put("principal", this.principal);
            options.put("useKeyTab", "true");
            options.put("storeKey", "true");
            options.put("doNotPrompt", "true");
            options.put("renewTGT", "false");
            options.put("refreshKrb5Config", "true");
            options.put("isInitiator", "true");
            options.put("debug", String.valueOf(false));
            return new AppConfigurationEntry[] { new AppConfigurationEntry(getKrb5LoginModuleName(), AppConfigurationEntry.LoginModuleControlFlag.REQUIRED, options) };
        }
    }
    
    static class TicketCacheJaasConf extends Configuration
    {
        private String principal;
        private File clientCredentialFile;
        
        TicketCacheJaasConf(final String principal, final File clientCredentialFile) {
            this.principal = principal;
            this.clientCredentialFile = clientCredentialFile;
        }
        
        @Override
        public AppConfigurationEntry[] getAppConfigurationEntry(final String name) {
            final Map<String, String> options = new HashMap<String, String>();
            options.put("principal", this.principal);
            options.put("storeKey", "false");
            options.put("doNotPrompt", "false");
            options.put("useTicketCache", "true");
            options.put("renewTGT", "true");
            options.put("refreshKrb5Config", "true");
            options.put("isInitiator", "true");
            options.put("ticketCache", this.clientCredentialFile.getAbsolutePath());
            options.put("debug", String.valueOf(false));
            return new AppConfigurationEntry[] { new AppConfigurationEntry(getKrb5LoginModuleName(), AppConfigurationEntry.LoginModuleControlFlag.REQUIRED, options) };
        }
    }
    
    static class PasswordJaasConf extends Configuration
    {
        private String principal;
        
        PasswordJaasConf(final String principal) {
            this.principal = principal;
        }
        
        @Override
        public AppConfigurationEntry[] getAppConfigurationEntry(final String name) {
            final Map<String, String> options = new HashMap<String, String>();
            options.put("principal", this.principal);
            options.put("storeKey", "true");
            options.put("useTicketCache", "true");
            options.put("useKeyTab", "false");
            options.put("renewTGT", "true");
            options.put("refreshKrb5Config", "true");
            options.put("isInitiator", "true");
            options.put("debug", String.valueOf(false));
            return new AppConfigurationEntry[] { new AppConfigurationEntry(getKrb5LoginModuleName(), AppConfigurationEntry.LoginModuleControlFlag.REQUIRED, options) };
        }
    }
    
    public static class KrbCallbackHandler implements CallbackHandler
    {
        private String principal;
        private String password;
        
        public KrbCallbackHandler(final String principal, final String password) {
            this.principal = principal;
            this.password = password;
        }
        
        @Override
        public void handle(final Callback[] callbacks) throws IOException, UnsupportedCallbackException {
            for (int i = 0; i < callbacks.length; ++i) {
                if (callbacks[i] instanceof PasswordCallback) {
                    final PasswordCallback pc = (PasswordCallback)callbacks[i];
                    if (pc.getPrompt().contains(this.principal)) {
                        pc.setPassword(this.password.toCharArray());
                        break;
                    }
                }
            }
        }
    }
}
