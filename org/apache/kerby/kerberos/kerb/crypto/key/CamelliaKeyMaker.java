// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb.crypto.key;

import org.apache.kerby.kerberos.kerb.crypto.util.Cmac;
import org.apache.kerby.kerberos.kerb.crypto.util.BytesUtil;
import java.security.GeneralSecurityException;
import org.apache.kerby.kerberos.kerb.crypto.util.Pbkdf;
import org.apache.kerby.kerberos.kerb.KrbException;
import org.apache.kerby.kerberos.kerb.crypto.enc.EncryptProvider;
import org.apache.kerby.kerberos.kerb.crypto.enc.provider.CamelliaProvider;

public class CamelliaKeyMaker extends DkKeyMaker
{
    public CamelliaKeyMaker(final CamelliaProvider encProvider) {
        super(encProvider);
    }
    
    @Override
    public byte[] random2Key(final byte[] randomBits) throws KrbException {
        return randomBits;
    }
    
    @Override
    public byte[] str2key(final String string, final String salt, final byte[] param) throws KrbException {
        final int iterCount = AbstractKeyMaker.getIterCount(param, 32768);
        final byte[] saltBytes = AbstractKeyMaker.getSaltBytes(salt, this.getPepper());
        final int keySize = this.encProvider().keySize();
        byte[] random;
        try {
            random = Pbkdf.pbkdf2(string.toCharArray(), saltBytes, iterCount, keySize);
        }
        catch (GeneralSecurityException e) {
            throw new KrbException("pbkdf2 failed", e);
        }
        final byte[] tmpKey = this.random2Key(random);
        return this.dk(tmpKey, CamelliaKeyMaker.KERBEROS_CONSTANT);
    }
    
    private String getPepper() {
        final int keySize = this.encProvider().keySize();
        return (keySize == 16) ? "camellia128-cts-cmac" : "camellia256-cts-cmac";
    }
    
    @Override
    protected byte[] dr(final byte[] key, final byte[] constant) throws KrbException {
        final int blocksize = this.encProvider().blockSize();
        final int keyInuptSize = this.encProvider().keyInputSize();
        final byte[] keyBytes = new byte[keyInuptSize];
        int len = 0;
        len += blocksize;
        len += 4;
        len += constant.length;
        ++len;
        len += 4;
        final byte[] ki = new byte[len];
        System.arraycopy(constant, 0, ki, blocksize + 4, constant.length);
        BytesUtil.int2bytes(keyInuptSize * 8, ki, len - 4, true);
        for (int i = 1, n = 0; n < keyInuptSize; n += blocksize, ++i) {
            BytesUtil.int2bytes(i, ki, blocksize, true);
            final byte[] tmp = Cmac.cmac(this.encProvider(), key, ki);
            System.arraycopy(tmp, 0, ki, 0, blocksize);
            if (n + blocksize >= keyInuptSize) {
                System.arraycopy(ki, 0, keyBytes, n, keyInuptSize - n);
                break;
            }
            System.arraycopy(ki, 0, keyBytes, n, blocksize);
        }
        return keyBytes;
    }
}
