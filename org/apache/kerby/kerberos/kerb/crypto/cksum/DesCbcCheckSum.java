// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb.crypto.cksum;

import org.apache.kerby.kerberos.kerb.type.base.CheckSumType;

public class DesCbcCheckSum extends ConfounderedDesCheckSum
{
    public DesCbcCheckSum() {
        super(null, 8, 8);
    }
    
    @Override
    public CheckSumType cksumType() {
        return CheckSumType.DES_CBC;
    }
}
