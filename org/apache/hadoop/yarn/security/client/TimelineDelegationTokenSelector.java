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
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;
import org.apache.hadoop.security.token.TokenSelector;

@InterfaceAudience.Public
@InterfaceStability.Unstable
public class TimelineDelegationTokenSelector implements TokenSelector<TimelineDelegationTokenIdentifier>
{
    private static final Log LOG;
    
    @Override
    public Token<TimelineDelegationTokenIdentifier> selectToken(final Text service, final Collection<Token<? extends TokenIdentifier>> tokens) {
        if (service == null) {
            return null;
        }
        if (TimelineDelegationTokenSelector.LOG.isDebugEnabled()) {
            TimelineDelegationTokenSelector.LOG.debug("Looking for a token with service " + service.toString());
        }
        for (final Token<? extends TokenIdentifier> token : tokens) {
            if (TimelineDelegationTokenSelector.LOG.isDebugEnabled()) {
                TimelineDelegationTokenSelector.LOG.debug("Token kind is " + token.getKind().toString() + " and the token's service name is " + token.getService());
            }
            if (TimelineDelegationTokenIdentifier.KIND_NAME.equals(token.getKind()) && service.equals(token.getService())) {
                return (Token<TimelineDelegationTokenIdentifier>)token;
            }
        }
        return null;
    }
    
    static {
        LOG = LogFactory.getLog(TimelineDelegationTokenSelector.class);
    }
}
