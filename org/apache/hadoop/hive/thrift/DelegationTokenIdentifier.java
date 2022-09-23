// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.thrift;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.security.token.delegation.AbstractDelegationTokenIdentifier;

public class DelegationTokenIdentifier extends AbstractDelegationTokenIdentifier
{
    public static final Text HIVE_DELEGATION_KIND;
    
    public DelegationTokenIdentifier() {
    }
    
    public DelegationTokenIdentifier(final Text owner, final Text renewer, final Text realUser) {
        super(owner, renewer, realUser);
    }
    
    @Override
    public Text getKind() {
        return DelegationTokenIdentifier.HIVE_DELEGATION_KIND;
    }
    
    static {
        HIVE_DELEGATION_KIND = new Text("HIVE_DELEGATION_TOKEN");
    }
}
