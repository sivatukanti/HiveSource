// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.security.token.delegation.web;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;
import org.apache.hadoop.security.token.delegation.AbstractDelegationTokenIdentifier;

@InterfaceAudience.Private
@InterfaceStability.Evolving
public class DelegationTokenIdentifier extends AbstractDelegationTokenIdentifier
{
    private Text kind;
    
    public DelegationTokenIdentifier(final Text kind) {
        this.kind = kind;
    }
    
    public DelegationTokenIdentifier(final Text kind, final Text owner, final Text renewer, final Text realUser) {
        super(owner, renewer, realUser);
        this.kind = kind;
    }
    
    @Override
    public Text getKind() {
        return this.kind;
    }
}
