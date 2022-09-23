// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.thrift.transport;

import java.util.Collections;
import java.util.WeakHashMap;
import java.lang.ref.WeakReference;
import org.slf4j.LoggerFactory;
import javax.security.sasl.SaslClient;
import javax.security.sasl.SaslException;
import javax.security.sasl.SaslServer;
import javax.security.sasl.Sasl;
import javax.security.auth.callback.CallbackHandler;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;

public class TSaslServerTransport extends TSaslTransport
{
    private static final Logger LOGGER;
    private Map<String, TSaslServerDefinition> serverDefinitionMap;
    
    public TSaslServerTransport(final TTransport transport) {
        super(transport);
        this.serverDefinitionMap = new HashMap<String, TSaslServerDefinition>();
    }
    
    public TSaslServerTransport(final String mechanism, final String protocol, final String serverName, final Map<String, String> props, final CallbackHandler cbh, final TTransport transport) {
        super(transport);
        this.serverDefinitionMap = new HashMap<String, TSaslServerDefinition>();
        this.addServerDefinition(mechanism, protocol, serverName, props, cbh);
    }
    
    private TSaslServerTransport(final Map<String, TSaslServerDefinition> serverDefinitionMap, final TTransport transport) {
        super(transport);
        (this.serverDefinitionMap = new HashMap<String, TSaslServerDefinition>()).putAll(serverDefinitionMap);
    }
    
    public void addServerDefinition(final String mechanism, final String protocol, final String serverName, final Map<String, String> props, final CallbackHandler cbh) {
        this.serverDefinitionMap.put(mechanism, new TSaslServerDefinition(mechanism, protocol, serverName, props, cbh));
    }
    
    @Override
    protected SaslRole getRole() {
        return SaslRole.SERVER;
    }
    
    @Override
    protected void handleSaslStartMessage() throws TTransportException, SaslException {
        final SaslResponse message = this.receiveSaslMessage();
        TSaslServerTransport.LOGGER.debug("Received start message with status {}", message.status);
        if (message.status != NegotiationStatus.START) {
            throw this.sendAndThrowMessage(NegotiationStatus.ERROR, "Expecting START status, received " + message.status);
        }
        final String mechanismName = new String(message.payload);
        final TSaslServerDefinition serverDefinition = this.serverDefinitionMap.get(mechanismName);
        TSaslServerTransport.LOGGER.debug("Received mechanism name '{}'", mechanismName);
        if (serverDefinition == null) {
            throw this.sendAndThrowMessage(NegotiationStatus.BAD, "Unsupported mechanism type " + mechanismName);
        }
        final SaslServer saslServer = Sasl.createSaslServer(serverDefinition.mechanism, serverDefinition.protocol, serverDefinition.serverName, serverDefinition.props, serverDefinition.cbh);
        this.setSaslServer(saslServer);
    }
    
    static {
        LOGGER = LoggerFactory.getLogger(TSaslServerTransport.class);
    }
    
    private static class TSaslServerDefinition
    {
        public String mechanism;
        public String protocol;
        public String serverName;
        public Map<String, String> props;
        public CallbackHandler cbh;
        
        public TSaslServerDefinition(final String mechanism, final String protocol, final String serverName, final Map<String, String> props, final CallbackHandler cbh) {
            this.mechanism = mechanism;
            this.protocol = protocol;
            this.serverName = serverName;
            this.props = props;
            this.cbh = cbh;
        }
    }
    
    public static class Factory extends TTransportFactory
    {
        private static Map<TTransport, WeakReference<TSaslServerTransport>> transportMap;
        private Map<String, TSaslServerDefinition> serverDefinitionMap;
        
        public Factory() {
            this.serverDefinitionMap = new HashMap<String, TSaslServerDefinition>();
        }
        
        public Factory(final String mechanism, final String protocol, final String serverName, final Map<String, String> props, final CallbackHandler cbh) {
            this.serverDefinitionMap = new HashMap<String, TSaslServerDefinition>();
            this.addServerDefinition(mechanism, protocol, serverName, props, cbh);
        }
        
        public void addServerDefinition(final String mechanism, final String protocol, final String serverName, final Map<String, String> props, final CallbackHandler cbh) {
            this.serverDefinitionMap.put(mechanism, new TSaslServerDefinition(mechanism, protocol, serverName, props, cbh));
        }
        
        @Override
        public TTransport getTransport(final TTransport base) {
            WeakReference<TSaslServerTransport> ret = Factory.transportMap.get(base);
            if (ret == null || ret.get() == null) {
                TSaslServerTransport.LOGGER.debug("transport map does not contain key", base);
                ret = new WeakReference<TSaslServerTransport>(new TSaslServerTransport(this.serverDefinitionMap, base, null));
                try {
                    ret.get().open();
                }
                catch (TTransportException e) {
                    TSaslServerTransport.LOGGER.debug("failed to open server transport", e);
                    throw new RuntimeException(e);
                }
                Factory.transportMap.put(base, ret);
            }
            else {
                TSaslServerTransport.LOGGER.debug("transport map does contain key {}", base);
            }
            return ret.get();
        }
        
        static {
            Factory.transportMap = Collections.synchronizedMap(new WeakHashMap<TTransport, WeakReference<TSaslServerTransport>>());
        }
    }
}
