// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb.crypto.cksum;

import org.apache.kerby.kerberos.kerb.KrbException;
import org.apache.kerby.kerberos.kerb.crypto.key.DkKeyMaker;
import org.apache.kerby.kerberos.kerb.crypto.util.BytesUtil;
import org.apache.kerby.kerberos.kerb.crypto.enc.EncryptProvider;

public abstract class KcCheckSum extends AbstractKeyedCheckSumTypeHandler
{
    public KcCheckSum(final EncryptProvider encProvider, final HashProvider hashProvider, final int computeSize, final int outputSize) {
        super(encProvider, hashProvider, computeSize, outputSize);
    }
    
    @Override
    protected byte[] doChecksumWithKey(final byte[] data, final int start, final int len, final byte[] key, final int usage) throws KrbException {
        final byte[] constant = new byte[5];
        BytesUtil.int2bytes(usage, constant, 0, true);
        constant[4] = -103;
        final byte[] kc = ((DkKeyMaker)this.keyMaker()).dk(key, constant);
        return this.mac(kc, data, start, len);
    }
    
    protected abstract byte[] mac(final byte[] p0, final byte[] p1, final int p2, final int p3) throws KrbException;
}
