// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.security.token.delegation;

import java.util.Iterator;
import org.apache.hadoop.security.token.TokenIdentifier;
import org.apache.hadoop.security.token.Token;
import java.util.Collection;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;
import org.apache.hadoop.security.token.TokenSelector;

@InterfaceAudience.LimitedPrivate({ "HDFS", "MapReduce" })
@InterfaceStability.Evolving
public class AbstractDelegationTokenSelector<TokenIdent extends AbstractDelegationTokenIdentifier> implements TokenSelector<TokenIdent>
{
    private Text kindName;
    
    protected AbstractDelegationTokenSelector(final Text kindName) {
        this.kindName = kindName;
    }
    
    @Override
    public Token<TokenIdent> selectToken(final Text service, final Collection<Token<? extends TokenIdentifier>> tokens) {
        if (service == null) {
            return null;
        }
        for (final Token<? extends TokenIdentifier> token : tokens) {
            if (this.kindName.equals(token.getKind()) && service.equals(token.getService())) {
                return (Token<TokenIdent>)token;
            }
        }
        return null;
    }
}
