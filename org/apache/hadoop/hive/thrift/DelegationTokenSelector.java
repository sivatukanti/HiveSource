// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.thrift;

import org.apache.hadoop.security.token.delegation.AbstractDelegationTokenSelector;

public class DelegationTokenSelector extends AbstractDelegationTokenSelector<DelegationTokenIdentifier>
{
    public DelegationTokenSelector() {
        super(DelegationTokenIdentifier.HIVE_DELEGATION_KIND);
    }
}
