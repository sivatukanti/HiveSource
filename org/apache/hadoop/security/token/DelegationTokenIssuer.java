// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.security.token;

import org.apache.hadoop.io.Text;
import java.util.List;
import java.util.ArrayList;
import org.apache.hadoop.security.Credentials;
import java.io.IOException;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.LimitedPrivate({ "HDFS", "MapReduce", "Yarn" })
@InterfaceStability.Unstable
public interface DelegationTokenIssuer
{
    String getCanonicalServiceName();
    
    Token<?> getDelegationToken(final String p0) throws IOException;
    
    default DelegationTokenIssuer[] getAdditionalTokenIssuers() throws IOException {
        return null;
    }
    
    default Token<?>[] addDelegationTokens(final String renewer, Credentials credentials) throws IOException {
        if (credentials == null) {
            credentials = new Credentials();
        }
        final List<Token<?>> tokens = new ArrayList<Token<?>>();
        collectDelegationTokens(this, renewer, credentials, tokens);
        return tokens.toArray(new Token[tokens.size()]);
    }
    
    @InterfaceAudience.Private
    default void collectDelegationTokens(final DelegationTokenIssuer issuer, final String renewer, final Credentials credentials, final List<Token<?>> tokens) throws IOException {
        final String serviceName = issuer.getCanonicalServiceName();
        if (serviceName != null) {
            final Text service = new Text(serviceName);
            Token<?> token = credentials.getToken(service);
            if (token == null) {
                token = issuer.getDelegationToken(renewer);
                if (token != null) {
                    tokens.add(token);
                    credentials.addToken(service, (Token<? extends TokenIdentifier>)token);
                }
            }
        }
        final DelegationTokenIssuer[] ancillary = issuer.getAdditionalTokenIssuers();
        if (ancillary != null) {
            for (final DelegationTokenIssuer subIssuer : ancillary) {
                collectDelegationTokens(subIssuer, renewer, credentials, tokens);
            }
        }
    }
}
