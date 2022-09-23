// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb.crypto.cksum;

import org.apache.kerby.kerberos.kerb.type.base.CheckSumType;
import org.apache.kerby.kerberos.kerb.crypto.cksum.provider.Sha1Provider;
import org.apache.kerby.kerberos.kerb.crypto.cksum.provider.AbstractUnkeyedCheckSumTypeHandler;

public class Sha1CheckSum extends AbstractUnkeyedCheckSumTypeHandler
{
    public Sha1CheckSum() {
        super(new Sha1Provider(), 20, 20);
    }
    
    @Override
    public CheckSumType cksumType() {
        return CheckSumType.NIST_SHA;
    }
}
