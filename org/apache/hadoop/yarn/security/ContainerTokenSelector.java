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
@InterfaceStability.Stable
public class ContainerTokenSelector implements TokenSelector<ContainerTokenIdentifier>
{
    private static final Log LOG;
    
    @Override
    public Token<ContainerTokenIdentifier> selectToken(final Text service, final Collection<Token<? extends TokenIdentifier>> tokens) {
        if (service == null) {
            return null;
        }
        for (final Token<? extends TokenIdentifier> token : tokens) {
            if (ContainerTokenSelector.LOG.isDebugEnabled()) {
                ContainerTokenSelector.LOG.info("Looking for service: " + service + ". Current token is " + token);
            }
            if (ContainerTokenIdentifier.KIND.equals(token.getKind()) && service.equals(token.getService())) {
                return (Token<ContainerTokenIdentifier>)token;
            }
        }
        return null;
    }
    
    static {
        LOG = LogFactory.getLog(ContainerTokenSelector.class);
    }
}
