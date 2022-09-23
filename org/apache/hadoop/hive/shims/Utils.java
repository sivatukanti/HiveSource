// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.shims;

import java.util.Map;
import org.apache.hadoop.security.authentication.util.KerberosUtil;
import java.util.HashMap;
import javax.security.auth.login.AppConfigurationEntry;
import javax.security.auth.login.Configuration;
import org.apache.hadoop.security.SecurityUtil;
import org.apache.hadoop.hive.thrift.DelegationTokenIdentifier;
import org.apache.hadoop.security.token.Token;
import org.apache.hadoop.security.token.TokenIdentifier;
import org.apache.hadoop.security.token.TokenSelector;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.hive.thrift.DelegationTokenSelector;
import java.io.IOException;
import javax.security.auth.login.LoginException;
import org.apache.hadoop.security.UserGroupInformation;

public class Utils
{
    private static final boolean IBM_JAVA;
    
    public static UserGroupInformation getUGI() throws LoginException, IOException {
        final String doAs = System.getenv("HADOOP_USER_NAME");
        if (doAs != null && doAs.length() > 0) {
            return UserGroupInformation.createProxyUser(doAs, UserGroupInformation.getLoginUser());
        }
        return UserGroupInformation.getCurrentUser();
    }
    
    public static String getTokenStrForm(final String tokenSignature) throws IOException {
        final UserGroupInformation ugi = UserGroupInformation.getCurrentUser();
        final TokenSelector<? extends TokenIdentifier> tokenSelector = new DelegationTokenSelector();
        final Token<? extends TokenIdentifier> token = tokenSelector.selectToken((tokenSignature == null) ? new Text() : new Text(tokenSignature), ugi.getTokens());
        return (token != null) ? token.encodeToUrlString() : null;
    }
    
    public static void setTokenStr(final UserGroupInformation ugi, final String tokenStr, final String tokenService) throws IOException {
        final Token<DelegationTokenIdentifier> delegationToken = createToken(tokenStr, tokenService);
        ugi.addToken(delegationToken);
    }
    
    public static String addServiceToToken(final String tokenStr, final String tokenService) throws IOException {
        final Token<DelegationTokenIdentifier> delegationToken = createToken(tokenStr, tokenService);
        return delegationToken.encodeToUrlString();
    }
    
    private static Token<DelegationTokenIdentifier> createToken(final String tokenStr, final String tokenService) throws IOException {
        final Token<DelegationTokenIdentifier> delegationToken = new Token<DelegationTokenIdentifier>();
        delegationToken.decodeFromUrlString(tokenStr);
        delegationToken.setService(new Text(tokenService));
        return delegationToken;
    }
    
    public static void setZookeeperClientKerberosJaasConfig(String principal, final String keyTabFile) throws IOException {
        final String SASL_LOGIN_CONTEXT_NAME = "HiveZooKeeperClient";
        System.setProperty("zookeeper.sasl.clientconfig", "HiveZooKeeperClient");
        principal = SecurityUtil.getServerPrincipal(principal, "0.0.0.0");
        final JaasConfiguration jaasConf = new JaasConfiguration("HiveZooKeeperClient", principal, keyTabFile);
        Configuration.setConfiguration(jaasConf);
    }
    
    static {
        IBM_JAVA = System.getProperty("java.vendor").contains("IBM");
    }
    
    private static class JaasConfiguration extends Configuration
    {
        private static final boolean IBM_JAVA;
        private final Configuration baseConfig;
        private final String loginContextName;
        private final String principal;
        private final String keyTabFile;
        
        public JaasConfiguration(final String hiveLoginContextName, final String principal, final String keyTabFile) {
            this.baseConfig = Configuration.getConfiguration();
            this.loginContextName = hiveLoginContextName;
            this.principal = principal;
            this.keyTabFile = keyTabFile;
        }
        
        @Override
        public AppConfigurationEntry[] getAppConfigurationEntry(final String appName) {
            if (this.loginContextName.equals(appName)) {
                final Map<String, String> krbOptions = new HashMap<String, String>();
                if (JaasConfiguration.IBM_JAVA) {
                    krbOptions.put("credsType", "both");
                    krbOptions.put("useKeytab", this.keyTabFile);
                }
                else {
                    krbOptions.put("doNotPrompt", "true");
                    krbOptions.put("storeKey", "true");
                    krbOptions.put("useKeyTab", "true");
                    krbOptions.put("keyTab", this.keyTabFile);
                }
                krbOptions.put("principal", this.principal);
                krbOptions.put("refreshKrb5Config", "true");
                final AppConfigurationEntry hiveZooKeeperClientEntry = new AppConfigurationEntry(KerberosUtil.getKrb5LoginModuleName(), AppConfigurationEntry.LoginModuleControlFlag.REQUIRED, krbOptions);
                return new AppConfigurationEntry[] { hiveZooKeeperClientEntry };
            }
            if (this.baseConfig != null) {
                return this.baseConfig.getAppConfigurationEntry(appName);
            }
            return null;
        }
        
        static {
            IBM_JAVA = System.getProperty("java.vendor").contains("IBM");
        }
    }
}
