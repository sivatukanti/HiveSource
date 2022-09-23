// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb.crypto.key;

import java.security.GeneralSecurityException;
import org.apache.kerby.kerberos.kerb.crypto.util.Pbkdf;
import org.apache.kerby.kerberos.kerb.KrbException;
import org.apache.kerby.kerberos.kerb.crypto.enc.EncryptProvider;
import org.apache.kerby.kerberos.kerb.crypto.enc.provider.AesProvider;

public class AesKeyMaker extends DkKeyMaker
{
    public AesKeyMaker(final AesProvider encProvider) {
        super(encProvider);
    }
    
    @Override
    public byte[] random2Key(final byte[] randomBits) throws KrbException {
        return randomBits;
    }
    
    @Override
    public byte[] str2key(final String string, final String salt, final byte[] param) throws KrbException {
        final int iterCount = AbstractKeyMaker.getIterCount(param, 4096);
        final byte[] saltBytes = AbstractKeyMaker.getSaltBytes(salt, null);
        final int keySize = this.encProvider().keySize();
        byte[] random;
        try {
            random = Pbkdf.pbkdf2(string.toCharArray(), saltBytes, iterCount, keySize);
        }
        catch (GeneralSecurityException e) {
            throw new KrbException("pbkdf2 failed", e);
        }
        final byte[] tmpKey = this.random2Key(random);
        return this.dk(tmpKey, AesKeyMaker.KERBEROS_CONSTANT);
    }
}
