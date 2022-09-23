// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.zookeeper.server.auth;

import org.slf4j.LoggerFactory;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.sasl.AuthorizeCallback;
import javax.security.sasl.RealmCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.Callback;
import java.util.Iterator;
import javax.security.auth.login.AppConfigurationEntry;
import java.io.IOException;
import java.util.HashMap;
import javax.security.auth.login.Configuration;
import java.util.Map;
import org.slf4j.Logger;
import javax.security.auth.callback.CallbackHandler;

public class SaslServerCallbackHandler implements CallbackHandler
{
    private static final String USER_PREFIX = "user_";
    private static final Logger LOG;
    private static final String SYSPROP_SUPER_PASSWORD = "zookeeper.SASLAuthenticationProvider.superPassword";
    private static final String SYSPROP_REMOVE_HOST = "zookeeper.kerberos.removeHostFromPrincipal";
    private static final String SYSPROP_REMOVE_REALM = "zookeeper.kerberos.removeRealmFromPrincipal";
    private String userName;
    private final Map<String, String> credentials;
    
    public SaslServerCallbackHandler(final Configuration configuration) throws IOException {
        this.credentials = new HashMap<String, String>();
        final String serverSection = System.getProperty("zookeeper.sasl.serverconfig", "Server");
        final AppConfigurationEntry[] configurationEntries = configuration.getAppConfigurationEntry(serverSection);
        if (configurationEntries == null) {
            final String errorMessage = "Could not find a '" + serverSection + "' entry in this configuration: Server cannot start.";
            SaslServerCallbackHandler.LOG.error(errorMessage);
            throw new IOException(errorMessage);
        }
        this.credentials.clear();
        for (final AppConfigurationEntry entry : configurationEntries) {
            final Map<String, ?> options = entry.getOptions();
            for (final Map.Entry<String, ?> pair : options.entrySet()) {
                final String key = pair.getKey();
                if (key.startsWith("user_")) {
                    final String userName = key.substring("user_".length());
                    this.credentials.put(userName, (String)pair.getValue());
                }
            }
        }
    }
    
    @Override
    public void handle(final Callback[] callbacks) throws UnsupportedCallbackException {
        for (final Callback callback : callbacks) {
            if (callback instanceof NameCallback) {
                this.handleNameCallback((NameCallback)callback);
            }
            else if (callback instanceof PasswordCallback) {
                this.handlePasswordCallback((PasswordCallback)callback);
            }
            else if (callback instanceof RealmCallback) {
                this.handleRealmCallback((RealmCallback)callback);
            }
            else if (callback instanceof AuthorizeCallback) {
                this.handleAuthorizeCallback((AuthorizeCallback)callback);
            }
        }
    }
    
    private void handleNameCallback(final NameCallback nc) {
        if (this.credentials.get(nc.getDefaultName()) == null) {
            SaslServerCallbackHandler.LOG.warn("User '" + nc.getDefaultName() + "' not found in list of DIGEST-MD5 authenticateable users.");
            return;
        }
        nc.setName(nc.getDefaultName());
        this.userName = nc.getDefaultName();
    }
    
    private void handlePasswordCallback(final PasswordCallback pc) {
        if ("super".equals(this.userName) && System.getProperty("zookeeper.SASLAuthenticationProvider.superPassword") != null) {
            pc.setPassword(System.getProperty("zookeeper.SASLAuthenticationProvider.superPassword").toCharArray());
        }
        else if (this.credentials.containsKey(this.userName)) {
            pc.setPassword(this.credentials.get(this.userName).toCharArray());
        }
        else {
            SaslServerCallbackHandler.LOG.warn("No password found for user: " + this.userName);
        }
    }
    
    private void handleRealmCallback(final RealmCallback rc) {
        SaslServerCallbackHandler.LOG.debug("client supplied realm: " + rc.getDefaultText());
        rc.setText(rc.getDefaultText());
    }
    
    private void handleAuthorizeCallback(final AuthorizeCallback ac) {
        final String authenticationID = ac.getAuthenticationID();
        final String authorizationID = ac.getAuthorizationID();
        SaslServerCallbackHandler.LOG.info("Successfully authenticated client: authenticationID=" + authenticationID + ";  authorizationID=" + authorizationID + ".");
        ac.setAuthorized(true);
        final KerberosName kerberosName = new KerberosName(authenticationID);
        try {
            final StringBuilder userNameBuilder = new StringBuilder(kerberosName.getShortName());
            if (this.shouldAppendHost(kerberosName)) {
                userNameBuilder.append("/").append(kerberosName.getHostName());
            }
            if (this.shouldAppendRealm(kerberosName)) {
                userNameBuilder.append("@").append(kerberosName.getRealm());
            }
            SaslServerCallbackHandler.LOG.info("Setting authorizedID: " + (Object)userNameBuilder);
            ac.setAuthorizedID(userNameBuilder.toString());
        }
        catch (IOException e) {
            SaslServerCallbackHandler.LOG.error("Failed to set name based on Kerberos authentication rules.", e);
        }
    }
    
    private boolean shouldAppendRealm(final KerberosName kerberosName) {
        return !this.isSystemPropertyTrue("zookeeper.kerberos.removeRealmFromPrincipal") && kerberosName.getRealm() != null;
    }
    
    private boolean shouldAppendHost(final KerberosName kerberosName) {
        return !this.isSystemPropertyTrue("zookeeper.kerberos.removeHostFromPrincipal") && kerberosName.getHostName() != null;
    }
    
    private boolean isSystemPropertyTrue(final String propertyName) {
        return "true".equals(System.getProperty(propertyName));
    }
    
    static {
        LOG = LoggerFactory.getLogger(SaslServerCallbackHandler.class);
    }
}
