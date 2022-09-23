// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.zookeeper.server.quorum.auth;

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
import java.util.Set;
import java.util.Map;
import org.slf4j.Logger;
import javax.security.auth.callback.CallbackHandler;

public class SaslQuorumServerCallbackHandler implements CallbackHandler
{
    private static final String USER_PREFIX = "user_";
    private static final Logger LOG;
    private String userName;
    private final Map<String, String> credentials;
    private final Set<String> authzHosts;
    
    public SaslQuorumServerCallbackHandler(final Configuration configuration, final String serverSection, final Set<String> authzHosts) throws IOException {
        this.credentials = new HashMap<String, String>();
        final AppConfigurationEntry[] configurationEntries = configuration.getAppConfigurationEntry(serverSection);
        if (configurationEntries == null) {
            final String errorMessage = "Could not find a '" + serverSection + "' entry in this configuration: Server cannot start.";
            SaslQuorumServerCallbackHandler.LOG.error(errorMessage);
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
        this.authzHosts = authzHosts;
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
            SaslQuorumServerCallbackHandler.LOG.warn("User '{}' not found in list of DIGEST-MD5 authenticateable users.", nc.getDefaultName());
            return;
        }
        nc.setName(nc.getDefaultName());
        this.userName = nc.getDefaultName();
    }
    
    private void handlePasswordCallback(final PasswordCallback pc) {
        if (this.credentials.containsKey(this.userName)) {
            pc.setPassword(this.credentials.get(this.userName).toCharArray());
        }
        else {
            SaslQuorumServerCallbackHandler.LOG.warn("No password found for user: {}", this.userName);
        }
    }
    
    private void handleRealmCallback(final RealmCallback rc) {
        SaslQuorumServerCallbackHandler.LOG.debug("QuorumLearner supplied realm: {}", rc.getDefaultText());
        rc.setText(rc.getDefaultText());
    }
    
    private void handleAuthorizeCallback(final AuthorizeCallback ac) {
        final String authenticationID = ac.getAuthenticationID();
        final String authorizationID = ac.getAuthorizationID();
        boolean authzFlag = false;
        authzFlag = authenticationID.equals(authorizationID);
        if (authzFlag) {
            final String[] components = authorizationID.split("[/@]");
            if (components.length == 3) {
                authzFlag = this.authzHosts.contains(components[1]);
            }
            if (!authzFlag) {
                SaslQuorumServerCallbackHandler.LOG.error("SASL authorization completed, {} is not authorized to connect", components[1]);
            }
        }
        ac.setAuthorized(authzFlag);
        if (ac.isAuthorized()) {
            ac.setAuthorizedID(authorizationID);
            SaslQuorumServerCallbackHandler.LOG.info("Successfully authenticated learner: authenticationID={};  authorizationID={}.", authenticationID, authorizationID);
        }
        SaslQuorumServerCallbackHandler.LOG.debug("SASL authorization completed, authorized flag set to {}", (Object)ac.isAuthorized());
    }
    
    static {
        LOG = LoggerFactory.getLogger(SaslQuorumServerCallbackHandler.class);
    }
}
