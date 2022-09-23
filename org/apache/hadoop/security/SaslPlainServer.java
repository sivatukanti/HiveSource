// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.security;

import java.util.Map;
import javax.security.sasl.SaslServerFactory;
import java.security.Provider;
import javax.security.sasl.SaslException;
import javax.security.auth.callback.Callback;
import javax.security.sasl.AuthorizeCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.CallbackHandler;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;
import javax.security.sasl.SaslServer;

@InterfaceAudience.Private
@InterfaceStability.Evolving
public class SaslPlainServer implements SaslServer
{
    private CallbackHandler cbh;
    private boolean completed;
    private String authz;
    
    SaslPlainServer(final CallbackHandler callback) {
        this.cbh = callback;
    }
    
    @Override
    public String getMechanismName() {
        return "PLAIN";
    }
    
    @Override
    public byte[] evaluateResponse(final byte[] response) throws SaslException {
        if (this.completed) {
            throw new IllegalStateException("PLAIN authentication has completed");
        }
        if (response == null) {
            throw new IllegalArgumentException("Received null response");
        }
        try {
            String payload;
            try {
                payload = new String(response, "UTF-8");
            }
            catch (Exception e) {
                throw new IllegalArgumentException("Received corrupt response", e);
            }
            final String[] parts = payload.split("\u0000", 3);
            if (parts.length != 3) {
                throw new IllegalArgumentException("Received corrupt response");
            }
            if (parts[0].isEmpty()) {
                parts[0] = parts[1];
            }
            final NameCallback nc = new NameCallback("SASL PLAIN");
            nc.setName(parts[1]);
            final PasswordCallback pc = new PasswordCallback("SASL PLAIN", false);
            pc.setPassword(parts[2].toCharArray());
            final AuthorizeCallback ac = new AuthorizeCallback(parts[1], parts[0]);
            this.cbh.handle(new Callback[] { nc, pc, ac });
            if (ac.isAuthorized()) {
                this.authz = ac.getAuthorizedID();
            }
        }
        catch (Exception e2) {
            throw new SaslException("PLAIN auth failed: " + e2.toString(), e2);
        }
        finally {
            this.completed = true;
        }
        return null;
    }
    
    private void throwIfNotComplete() {
        if (!this.completed) {
            throw new IllegalStateException("PLAIN authentication not completed");
        }
    }
    
    @Override
    public boolean isComplete() {
        return this.completed;
    }
    
    @Override
    public String getAuthorizationID() {
        this.throwIfNotComplete();
        return this.authz;
    }
    
    @Override
    public Object getNegotiatedProperty(final String propName) {
        this.throwIfNotComplete();
        return "javax.security.sasl.qop".equals(propName) ? "auth" : null;
    }
    
    @Override
    public byte[] wrap(final byte[] outgoing, final int offset, final int len) throws SaslException {
        this.throwIfNotComplete();
        throw new IllegalStateException("PLAIN supports neither integrity nor privacy");
    }
    
    @Override
    public byte[] unwrap(final byte[] incoming, final int offset, final int len) throws SaslException {
        this.throwIfNotComplete();
        throw new IllegalStateException("PLAIN supports neither integrity nor privacy");
    }
    
    @Override
    public void dispose() throws SaslException {
        this.cbh = null;
        this.authz = null;
    }
    
    public static class SecurityProvider extends Provider
    {
        public SecurityProvider() {
            super("SaslPlainServer", 1.0, "SASL PLAIN Authentication Server");
            this.put("SaslServerFactory.PLAIN", SaslPlainServerFactory.class.getName());
        }
    }
    
    public static class SaslPlainServerFactory implements SaslServerFactory
    {
        @Override
        public SaslServer createSaslServer(final String mechanism, final String protocol, final String serverName, final Map<String, ?> props, final CallbackHandler cbh) throws SaslException {
            return "PLAIN".equals(mechanism) ? new SaslPlainServer(cbh) : null;
        }
        
        @Override
        public String[] getMechanismNames(final Map<String, ?> props) {
            return (props == null || "false".equals(props.get("javax.security.sasl.policy.noplaintext"))) ? new String[] { "PLAIN" } : new String[0];
        }
    }
}
