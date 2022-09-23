// 
// Decompiled by Procyon v0.5.36
// 

package parquet.org.apache.thrift.transport;

import parquet.org.slf4j.LoggerFactory;
import javax.security.sasl.SaslServer;
import javax.security.sasl.SaslException;
import javax.security.sasl.Sasl;
import javax.security.auth.callback.CallbackHandler;
import java.util.Map;
import javax.security.sasl.SaslClient;
import parquet.org.slf4j.Logger;

public class TSaslClientTransport extends TSaslTransport
{
    private static final Logger LOGGER;
    private final String mechanism;
    
    public TSaslClientTransport(final SaslClient saslClient, final TTransport transport) {
        super(saslClient, transport);
        this.mechanism = saslClient.getMechanismName();
    }
    
    public TSaslClientTransport(final String mechanism, final String authorizationId, final String protocol, final String serverName, final Map<String, String> props, final CallbackHandler cbh, final TTransport transport) throws SaslException {
        super(Sasl.createSaslClient(new String[] { mechanism }, authorizationId, protocol, serverName, props, cbh), transport);
        this.mechanism = mechanism;
    }
    
    @Override
    protected SaslRole getRole() {
        return SaslRole.CLIENT;
    }
    
    @Override
    protected void handleSaslStartMessage() throws TTransportException, SaslException {
        final SaslClient saslClient = this.getSaslClient();
        byte[] initialResponse = new byte[0];
        if (saslClient.hasInitialResponse()) {
            initialResponse = saslClient.evaluateChallenge(initialResponse);
        }
        TSaslClientTransport.LOGGER.debug("Sending mechanism name {} and initial response of length {}", this.mechanism, initialResponse.length);
        final byte[] mechanismBytes = this.mechanism.getBytes();
        this.sendSaslMessage(NegotiationStatus.START, mechanismBytes);
        this.sendSaslMessage(saslClient.isComplete() ? NegotiationStatus.COMPLETE : NegotiationStatus.OK, initialResponse);
        this.underlyingTransport.flush();
    }
    
    static {
        LOGGER = LoggerFactory.getLogger(TSaslClientTransport.class);
    }
}
