// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hive.service.auth;

import java.security.Provider;
import java.util.Map;
import javax.security.sasl.SaslServerFactory;
import java.util.Deque;
import javax.security.auth.callback.UnsupportedCallbackException;
import java.io.IOException;
import javax.security.auth.callback.Callback;
import javax.security.sasl.AuthorizeCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.NameCallback;
import java.util.ArrayDeque;
import javax.security.sasl.SaslException;
import javax.security.auth.callback.CallbackHandler;
import javax.security.sasl.SaslServer;

public class PlainSaslServer implements SaslServer
{
    public static final String PLAIN_METHOD = "PLAIN";
    private String user;
    private final CallbackHandler handler;
    
    PlainSaslServer(final CallbackHandler handler, final String authMethodStr) throws SaslException {
        this.handler = handler;
        AuthenticationProviderFactory.AuthMethods.getValidAuthMethod(authMethodStr);
    }
    
    @Override
    public String getMechanismName() {
        return "PLAIN";
    }
    
    @Override
    public byte[] evaluateResponse(final byte[] response) throws SaslException {
        try {
            final Deque<String> tokenList = new ArrayDeque<String>();
            StringBuilder messageToken = new StringBuilder();
            for (final byte b : response) {
                if (b == 0) {
                    tokenList.addLast(messageToken.toString());
                    messageToken = new StringBuilder();
                }
                else {
                    messageToken.append((char)b);
                }
            }
            tokenList.addLast(messageToken.toString());
            if (tokenList.size() < 2 || tokenList.size() > 3) {
                throw new SaslException("Invalid message format");
            }
            final String passwd = tokenList.removeLast();
            this.user = tokenList.removeLast();
            String authzId;
            if (tokenList.isEmpty()) {
                authzId = this.user;
            }
            else {
                authzId = tokenList.removeLast();
            }
            if (this.user == null || this.user.isEmpty()) {
                throw new SaslException("No user name provided");
            }
            if (passwd == null || passwd.isEmpty()) {
                throw new SaslException("No password name provided");
            }
            final NameCallback nameCallback = new NameCallback("User");
            nameCallback.setName(this.user);
            final PasswordCallback pcCallback = new PasswordCallback("Password", false);
            pcCallback.setPassword(passwd.toCharArray());
            final AuthorizeCallback acCallback = new AuthorizeCallback(this.user, authzId);
            final Callback[] cbList = { nameCallback, pcCallback, acCallback };
            this.handler.handle(cbList);
            if (!acCallback.isAuthorized()) {
                throw new SaslException("Authentication failed");
            }
        }
        catch (IllegalStateException eL) {
            throw new SaslException("Invalid message format", eL);
        }
        catch (IOException eI) {
            throw new SaslException("Error validating the login", eI);
        }
        catch (UnsupportedCallbackException eU) {
            throw new SaslException("Error validating the login", eU);
        }
        return null;
    }
    
    @Override
    public boolean isComplete() {
        return this.user != null;
    }
    
    @Override
    public String getAuthorizationID() {
        return this.user;
    }
    
    @Override
    public byte[] unwrap(final byte[] incoming, final int offset, final int len) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public byte[] wrap(final byte[] outgoing, final int offset, final int len) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public Object getNegotiatedProperty(final String propName) {
        return null;
    }
    
    @Override
    public void dispose() {
    }
    
    public static class SaslPlainServerFactory implements SaslServerFactory
    {
        @Override
        public SaslServer createSaslServer(final String mechanism, final String protocol, final String serverName, final Map<String, ?> props, final CallbackHandler cbh) {
            if ("PLAIN".equals(mechanism)) {
                try {
                    return new PlainSaslServer(cbh, protocol);
                }
                catch (SaslException e) {
                    return null;
                }
            }
            return null;
        }
        
        @Override
        public String[] getMechanismNames(final Map<String, ?> props) {
            return new String[] { "PLAIN" };
        }
    }
    
    public static class SaslPlainProvider extends Provider
    {
        public SaslPlainProvider() {
            super("HiveSaslPlain", 1.0, "Hive Plain SASL provider");
            this.put("SaslServerFactory.PLAIN", SaslPlainServerFactory.class.getName());
        }
    }
}
