// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.security.http;

import org.apache.hadoop.http.FilterContainer;
import java.io.Reader;
import java.util.Iterator;
import org.apache.hadoop.yarn.security.client.RMDelegationTokenIdentifier;
import org.apache.hadoop.security.SecurityUtil;
import org.apache.commons.io.IOUtils;
import java.io.IOException;
import org.apache.hadoop.security.UserGroupInformation;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.http.FilterInitializer;

@InterfaceStability.Unstable
public class RMAuthenticationFilterInitializer extends FilterInitializer
{
    String configPrefix;
    String signatureSecretFileProperty;
    String kerberosPrincipalProperty;
    String cookiePath;
    
    public RMAuthenticationFilterInitializer() {
        this.configPrefix = "hadoop.http.authentication.";
        this.signatureSecretFileProperty = "signature.secret.file";
        this.kerberosPrincipalProperty = "kerberos.principal";
        this.cookiePath = "/";
    }
    
    protected Map<String, String> createFilterConfig(final Configuration conf) {
        final Map<String, String> filterConfig = new HashMap<String, String>();
        filterConfig.put("cookie.path", this.cookiePath);
        for (final Map.Entry<String, String> entry : conf) {
            final String propName = entry.getKey();
            if (propName.startsWith(this.configPrefix)) {
                final String value = conf.get(propName);
                final String name = propName.substring(this.configPrefix.length());
                filterConfig.put(name, value);
            }
            else {
                if (!propName.startsWith("hadoop.proxyuser")) {
                    continue;
                }
                final String value = conf.get(propName);
                final String name = propName.substring("hadoop.".length());
                filterConfig.put(name, value);
            }
        }
        final String signatureSecretFile = filterConfig.get(this.signatureSecretFileProperty);
        if (signatureSecretFile != null) {
            Reader reader = null;
            try {
                final StringBuilder secret = new StringBuilder();
                reader = new InputStreamReader(new FileInputStream(signatureSecretFile), "UTF-8");
                for (int c = reader.read(); c > -1; c = reader.read()) {
                    secret.append((char)c);
                }
                filterConfig.put("signature.secret", secret.toString());
            }
            catch (IOException ex2) {
                if (UserGroupInformation.isSecurityEnabled()) {
                    throw new RuntimeException("Could not read HTTP signature secret file: " + signatureSecretFile);
                }
            }
            finally {
                IOUtils.closeQuietly(reader);
            }
        }
        final String bindAddress = conf.get("bind.address");
        String principal = filterConfig.get(this.kerberosPrincipalProperty);
        if (principal != null) {
            try {
                principal = SecurityUtil.getServerPrincipal(principal, bindAddress);
            }
            catch (IOException ex) {
                throw new RuntimeException("Could not resolve Kerberos principal name: " + ex.toString(), ex);
            }
            filterConfig.put("kerberos.principal", principal);
        }
        filterConfig.put("delegation-token.token-kind", RMDelegationTokenIdentifier.KIND_NAME.toString());
        return filterConfig;
    }
    
    @Override
    public void initFilter(final FilterContainer container, final Configuration conf) {
        final Map<String, String> filterConfig = this.createFilterConfig(conf);
        container.addFilter("RMAuthenticationFilter", RMAuthenticationFilter.class.getName(), filterConfig);
    }
}
