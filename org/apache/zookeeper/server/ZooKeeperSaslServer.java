// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.zookeeper.server;

import javax.security.sasl.SaslException;
import javax.security.auth.Subject;
import org.apache.zookeeper.util.SecurityUtils;
import org.slf4j.LoggerFactory;
import org.apache.zookeeper.Login;
import javax.security.sasl.SaslServer;
import org.slf4j.Logger;

public class ZooKeeperSaslServer
{
    public static final String LOGIN_CONTEXT_NAME_KEY = "zookeeper.sasl.serverconfig";
    public static final String DEFAULT_LOGIN_CONTEXT_NAME = "Server";
    Logger LOG;
    private SaslServer saslServer;
    
    ZooKeeperSaslServer(final Login login) {
        this.LOG = LoggerFactory.getLogger(ZooKeeperSaslServer.class);
        this.saslServer = this.createSaslServer(login);
    }
    
    private SaslServer createSaslServer(final Login login) {
        synchronized (login) {
            final Subject subject = login.getSubject();
            return SecurityUtils.createSaslServer(subject, "zookeeper", "zk-sasl-md5", login.callbackHandler, this.LOG);
        }
    }
    
    public byte[] evaluateResponse(final byte[] response) throws SaslException {
        return this.saslServer.evaluateResponse(response);
    }
    
    public boolean isComplete() {
        return this.saslServer.isComplete();
    }
    
    public String getAuthorizationID() {
        return this.saslServer.getAuthorizationID();
    }
}
