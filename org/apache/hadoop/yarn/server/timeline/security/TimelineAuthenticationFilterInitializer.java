// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.timeline.security;

import java.io.Reader;
import java.util.Iterator;
import org.apache.hadoop.yarn.security.client.TimelineDelegationTokenIdentifier;
import org.apache.hadoop.security.SecurityUtil;
import org.apache.hadoop.security.token.delegation.web.KerberosDelegationTokenAuthenticationHandler;
import org.apache.hadoop.security.token.delegation.web.PseudoDelegationTokenAuthenticationHandler;
import java.io.Closeable;
import org.apache.hadoop.io.IOUtils;
import java.io.IOException;
import java.io.FileReader;
import java.util.HashMap;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.http.FilterContainer;
import com.google.common.annotations.VisibleForTesting;
import java.util.Map;
import org.apache.hadoop.http.FilterInitializer;

public class TimelineAuthenticationFilterInitializer extends FilterInitializer
{
    public static final String PREFIX = "yarn.timeline-service.http-authentication.";
    private static final String SIGNATURE_SECRET_FILE = "signature.secret.file";
    @VisibleForTesting
    Map<String, String> filterConfig;
    
    @Override
    public void initFilter(final FilterContainer container, final Configuration conf) {
        (this.filterConfig = new HashMap<String, String>()).put("cookie.path", "/");
        for (final Map.Entry<String, String> entry : conf) {
            String name = entry.getKey();
            if (name.startsWith("hadoop.proxyuser")) {
                final String value = conf.get(name);
                name = name.substring("hadoop.".length());
                this.filterConfig.put(name, value);
            }
        }
        for (final Map.Entry<String, String> entry : conf) {
            String name = entry.getKey();
            if (name.startsWith("yarn.timeline-service.http-authentication.")) {
                final String value = conf.get(name);
                name = name.substring("yarn.timeline-service.http-authentication.".length());
                this.filterConfig.put(name, value);
            }
        }
        final String signatureSecretFile = this.filterConfig.get("signature.secret.file");
        if (signatureSecretFile != null) {
            Reader reader = null;
            try {
                final StringBuilder secret = new StringBuilder();
                reader = new FileReader(signatureSecretFile);
                for (int c = reader.read(); c > -1; c = reader.read()) {
                    secret.append((char)c);
                }
                this.filterConfig.put("signature.secret", secret.toString());
            }
            catch (IOException ex2) {
                throw new RuntimeException("Could not read HTTP signature secret file: " + signatureSecretFile);
            }
            finally {
                IOUtils.closeStream(reader);
            }
        }
        final String authType = this.filterConfig.get("type");
        if (authType.equals("simple")) {
            this.filterConfig.put("type", PseudoDelegationTokenAuthenticationHandler.class.getName());
        }
        else if (authType.equals("kerberos")) {
            this.filterConfig.put("type", KerberosDelegationTokenAuthenticationHandler.class.getName());
            final String bindAddress = conf.get("bind.address");
            String principal = this.filterConfig.get("kerberos.principal");
            if (principal != null) {
                try {
                    principal = SecurityUtil.getServerPrincipal(principal, bindAddress);
                }
                catch (IOException ex) {
                    throw new RuntimeException("Could not resolve Kerberos principal name: " + ex.toString(), ex);
                }
                this.filterConfig.put("kerberos.principal", principal);
            }
        }
        this.filterConfig.put("delegation-token.token-kind", TimelineDelegationTokenIdentifier.KIND_NAME.toString());
        container.addGlobalFilter("Timeline Authentication Filter", TimelineAuthenticationFilter.class.getName(), this.filterConfig);
    }
}
