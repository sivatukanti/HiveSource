// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hive.service.auth;

import org.apache.hive.service.cli.thrift.TCLIService;
import org.apache.thrift.TProcessor;
import java.io.IOException;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.sasl.AuthorizeCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.Callback;
import java.security.Provider;
import java.security.Security;
import javax.security.sasl.SaslException;
import org.apache.thrift.transport.TSaslClientTransport;
import org.apache.thrift.transport.TTransport;
import javax.security.sasl.AuthenticationException;
import javax.security.auth.login.LoginException;
import javax.security.auth.callback.CallbackHandler;
import java.util.Map;
import java.util.HashMap;
import org.apache.thrift.transport.TSaslServerTransport;
import org.apache.thrift.transport.TTransportFactory;
import org.apache.thrift.TProcessorFactory;
import org.apache.hive.service.cli.thrift.ThriftCLIService;

public final class PlainSaslHelper
{
    public static TProcessorFactory getPlainProcessorFactory(final ThriftCLIService service) {
        return new SQLPlainProcessorFactory(service);
    }
    
    public static TTransportFactory getPlainTransportFactory(final String authTypeStr) throws LoginException {
        final TSaslServerTransport.Factory saslFactory = new TSaslServerTransport.Factory();
        try {
            saslFactory.addServerDefinition("PLAIN", authTypeStr, null, new HashMap<String, String>(), new PlainServerCallbackHandler(authTypeStr));
        }
        catch (AuthenticationException e) {
            throw new LoginException("Error setting callback handler" + e);
        }
        return saslFactory;
    }
    
    public static TTransport getPlainTransport(final String username, final String password, final TTransport underlyingTransport) throws SaslException {
        return new TSaslClientTransport("PLAIN", null, null, null, new HashMap<String, String>(), new PlainCallbackHandler(username, password), underlyingTransport);
    }
    
    private PlainSaslHelper() {
        throw new UnsupportedOperationException("Can't initialize class");
    }
    
    static {
        Security.addProvider(new PlainSaslServer.SaslPlainProvider());
    }
    
    private static final class PlainServerCallbackHandler implements CallbackHandler
    {
        private final AuthenticationProviderFactory.AuthMethods authMethod;
        
        PlainServerCallbackHandler(final String authMethodStr) throws AuthenticationException {
            this.authMethod = AuthenticationProviderFactory.AuthMethods.getValidAuthMethod(authMethodStr);
        }
        
        @Override
        public void handle(final Callback[] callbacks) throws IOException, UnsupportedCallbackException {
            String username = null;
            String password = null;
            AuthorizeCallback ac = null;
            for (final Callback callback : callbacks) {
                if (callback instanceof NameCallback) {
                    final NameCallback nc = (NameCallback)callback;
                    username = nc.getName();
                }
                else if (callback instanceof PasswordCallback) {
                    final PasswordCallback pc = (PasswordCallback)callback;
                    password = new String(pc.getPassword());
                }
                else {
                    if (!(callback instanceof AuthorizeCallback)) {
                        throw new UnsupportedCallbackException(callback);
                    }
                    ac = (AuthorizeCallback)callback;
                }
            }
            final PasswdAuthenticationProvider provider = AuthenticationProviderFactory.getAuthenticationProvider(this.authMethod);
            provider.Authenticate(username, password);
            if (ac != null) {
                ac.setAuthorized(true);
            }
        }
    }
    
    public static class PlainCallbackHandler implements CallbackHandler
    {
        private final String username;
        private final String password;
        
        public PlainCallbackHandler(final String username, final String password) {
            this.username = username;
            this.password = password;
        }
        
        @Override
        public void handle(final Callback[] callbacks) throws IOException, UnsupportedCallbackException {
            for (final Callback callback : callbacks) {
                if (callback instanceof NameCallback) {
                    final NameCallback nameCallback = (NameCallback)callback;
                    nameCallback.setName(this.username);
                }
                else {
                    if (!(callback instanceof PasswordCallback)) {
                        throw new UnsupportedCallbackException(callback);
                    }
                    final PasswordCallback passCallback = (PasswordCallback)callback;
                    passCallback.setPassword(this.password.toCharArray());
                }
            }
        }
    }
    
    private static final class SQLPlainProcessorFactory extends TProcessorFactory
    {
        private final ThriftCLIService service;
        
        SQLPlainProcessorFactory(final ThriftCLIService service) {
            super(null);
            this.service = service;
        }
        
        @Override
        public TProcessor getProcessor(final TTransport trans) {
            return new TSetIpAddressProcessor<Object>(this.service);
        }
    }
}
