// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.zookeeper;

import org.slf4j.LoggerFactory;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.sasl.AuthorizeCallback;
import javax.security.sasl.RealmCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.Callback;
import org.slf4j.Logger;
import javax.security.auth.callback.CallbackHandler;

public class SaslClientCallbackHandler implements CallbackHandler
{
    private String password;
    private static final Logger LOG;
    private final String entity;
    
    public SaslClientCallbackHandler(final String password, final String client) {
        this.password = null;
        this.password = password;
        this.entity = client;
    }
    
    @Override
    public void handle(final Callback[] callbacks) throws UnsupportedCallbackException {
        for (final Callback callback : callbacks) {
            if (callback instanceof NameCallback) {
                final NameCallback nc = (NameCallback)callback;
                nc.setName(nc.getDefaultName());
            }
            else if (callback instanceof PasswordCallback) {
                final PasswordCallback pc = (PasswordCallback)callback;
                if (this.password != null) {
                    pc.setPassword(this.password.toCharArray());
                }
                else {
                    SaslClientCallbackHandler.LOG.warn("Could not login: the {} is being asked for a password, but the ZooKeeper {} code does not currently support obtaining a password from the user. Make sure that the {} is configured to use a ticket cache (using the JAAS configuration setting 'useTicketCache=true)' and restart the {}. If you still get this message after that, the TGT in the ticket cache has expired and must be manually refreshed. To do so, first determine if you are using a password or a keytab. If the former, run kinit in a Unix shell in the environment of the user who is running this Zookeeper {} using the command 'kinit <princ>' (where <princ> is the name of the {}'s Kerberos principal). If the latter, do 'kinit -k -t <keytab> <princ>' (where <princ> is the name of the Kerberos principal, and <keytab> is the location of the keytab file). After manually refreshing your cache, restart this {}. If you continue to see this message after manually refreshing your cache, ensure that your KDC host's clock is in sync with this host's clock.", this.entity, this.entity, this.entity, this.entity, this.entity, this.entity, this.entity);
                }
            }
            else if (callback instanceof RealmCallback) {
                final RealmCallback rc = (RealmCallback)callback;
                rc.setText(rc.getDefaultText());
            }
            else {
                if (!(callback instanceof AuthorizeCallback)) {
                    throw new UnsupportedCallbackException(callback, "Unrecognized SASL " + this.entity + "Callback");
                }
                final AuthorizeCallback ac = (AuthorizeCallback)callback;
                final String authid = ac.getAuthenticationID();
                final String authzid = ac.getAuthorizationID();
                if (authid.equals(authzid)) {
                    ac.setAuthorized(true);
                }
                else {
                    ac.setAuthorized(false);
                }
                if (ac.isAuthorized()) {
                    ac.setAuthorizedID(authzid);
                }
            }
        }
    }
    
    static {
        LOG = LoggerFactory.getLogger(SaslClientCallbackHandler.class);
    }
}
