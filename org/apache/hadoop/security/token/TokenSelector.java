// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.security.token;

import java.util.Collection;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Public
@InterfaceStability.Evolving
public interface TokenSelector<T extends TokenIdentifier>
{
    Token<T> selectToken(final Text p0, final Collection<Token<? extends TokenIdentifier>> p1);
}
