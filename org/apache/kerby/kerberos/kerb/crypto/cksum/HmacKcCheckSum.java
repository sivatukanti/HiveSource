// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb.crypto.cksum;

import org.apache.kerby.kerberos.kerb.KrbException;
import org.apache.kerby.kerberos.kerb.crypto.util.Hmac;
import org.apache.kerby.kerberos.kerb.crypto.cksum.provider.Sha1Provider;
import org.apache.kerby.kerberos.kerb.crypto.enc.EncryptProvider;

public abstract class HmacKcCheckSum extends KcCheckSum
{
    public HmacKcCheckSum(final EncryptProvider encProvider, final int computeSize, final int outputSize) {
        super(encProvider, new Sha1Provider(), computeSize, outputSize);
    }
    
    @Override
    protected byte[] mac(final byte[] kc, final byte[] data, final int start, final int len) throws KrbException {
        return Hmac.hmac(this.hashProvider(), kc, data, start, len);
    }
}
