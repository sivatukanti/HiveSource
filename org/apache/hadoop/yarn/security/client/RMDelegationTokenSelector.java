// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.security.client;

import org.apache.commons.logging.LogFactory;
import java.util.Iterator;
import java.util.Collection;
import org.apache.hadoop.security.token.TokenIdentifier;
import org.apache.hadoop.security.token.Token;
import org.apache.hadoop.io.Text;
import org.apache.commons.logging.Log;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;
import org.apache.hadoop.security.token.TokenSelector;

@InterfaceAudience.Public
@InterfaceStability.Stable
public class RMDelegationTokenSelector implements TokenSelector<RMDelegationTokenIdentifier>
{
    private static final Log LOG;
    
    private boolean checkService(final Text service, final Token<? extends TokenIdentifier> token) {
        return service != null && token.getService() != null && token.getService().toString().contains(service.toString());
    }
    
    @Override
    public Token<RMDelegationTokenIdentifier> selectToken(final Text service, final Collection<Token<? extends TokenIdentifier>> tokens) {
        if (service == null) {
            return null;
        }
        RMDelegationTokenSelector.LOG.debug("Looking for a token with service " + service.toString());
        for (final Token<? extends TokenIdentifier> token : tokens) {
            RMDelegationTokenSelector.LOG.debug("Token kind is " + token.getKind().toString() + " and the token's service name is " + token.getService());
            if (RMDelegationTokenIdentifier.KIND_NAME.equals(token.getKind()) && this.checkService(service, token)) {
                return (Token<RMDelegationTokenIdentifier>)token;
            }
        }
        return null;
    }
    
    static {
        LOG = LogFactory.getLog(RMDelegationTokenSelector.class);
    }
}
