// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.security.client;

import org.apache.commons.logging.LogFactory;
import java.util.Iterator;
import org.apache.hadoop.security.token.TokenIdentifier;
import org.apache.hadoop.security.token.Token;
import java.util.Collection;
import org.apache.hadoop.io.Text;
import org.apache.commons.logging.Log;
import org.apache.hadoop.security.token.TokenSelector;

public class ClientToAMTokenSelector implements TokenSelector<ClientToAMTokenIdentifier>
{
    private static final Log LOG;
    
    @Override
    public Token<ClientToAMTokenIdentifier> selectToken(final Text service, final Collection<Token<? extends TokenIdentifier>> tokens) {
        if (service == null) {
            return null;
        }
        ClientToAMTokenSelector.LOG.debug("Looking for a token with service " + service.toString());
        for (final Token<? extends TokenIdentifier> token : tokens) {
            ClientToAMTokenSelector.LOG.debug("Token kind is " + token.getKind().toString() + " and the token's service name is " + token.getService());
            if (ClientToAMTokenIdentifier.KIND_NAME.equals(token.getKind()) && service.equals(token.getService())) {
                return (Token<ClientToAMTokenIdentifier>)token;
            }
        }
        return null;
    }
    
    static {
        LOG = LogFactory.getLog(ClientToAMTokenSelector.class);
    }
}
