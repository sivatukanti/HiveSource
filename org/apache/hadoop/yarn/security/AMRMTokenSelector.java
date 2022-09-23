// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.security;

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
@InterfaceStability.Evolving
public class AMRMTokenSelector implements TokenSelector<AMRMTokenIdentifier>
{
    private static final Log LOG;
    
    @Override
    public Token<AMRMTokenIdentifier> selectToken(final Text service, final Collection<Token<? extends TokenIdentifier>> tokens) {
        if (service == null) {
            return null;
        }
        AMRMTokenSelector.LOG.debug("Looking for a token with service " + service.toString());
        for (final Token<? extends TokenIdentifier> token : tokens) {
            AMRMTokenSelector.LOG.debug("Token kind is " + token.getKind().toString() + " and the token's service name is " + token.getService());
            if (AMRMTokenIdentifier.KIND_NAME.equals(token.getKind()) && this.checkService(service, token)) {
                return (Token<AMRMTokenIdentifier>)token;
            }
        }
        return null;
    }
    
    private boolean checkService(final Text service, final Token<? extends TokenIdentifier> token) {
        return service != null && token.getService() != null && token.getService().toString().contains(service.toString());
    }
    
    static {
        LOG = LogFactory.getLog(AMRMTokenSelector.class);
    }
}
