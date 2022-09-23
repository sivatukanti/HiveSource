// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb.crypto.cksum;

import org.apache.kerby.kerberos.kerb.KrbException;
import org.apache.kerby.kerberos.kerb.crypto.util.Cmac;
import org.apache.kerby.kerberos.kerb.crypto.enc.EncryptProvider;

public abstract class CmacKcCheckSum extends KcCheckSum
{
    public CmacKcCheckSum(final EncryptProvider encProvider, final int computeSize, final int outputSize) {
        super(encProvider, null, computeSize, outputSize);
    }
    
    @Override
    protected byte[] mac(final byte[] kc, final byte[] data, final int start, final int len) throws KrbException {
        return Cmac.cmac(this.encProvider(), kc, data, start, len);
    }
}
