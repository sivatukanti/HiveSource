// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb.crypto.enc.provider;

import java.security.spec.AlgorithmParameterSpec;
import java.security.Key;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.GeneralSecurityException;
import org.apache.kerby.kerberos.kerb.KrbException;
import javax.crypto.Cipher;

public abstract class AesProvider extends AbstractEncryptProvider
{
    public AesProvider(final int blockSize, final int keyInputSize, final int keySize) {
        super(blockSize, keyInputSize, keySize);
    }
    
    @Override
    protected void doEncrypt(final byte[] data, final byte[] key, final byte[] cipherState, final boolean encrypt) throws KrbException {
        Cipher cipher = null;
        try {
            cipher = Cipher.getInstance("AES/CTS/NoPadding");
        }
        catch (GeneralSecurityException e) {
            final KrbException ke = new KrbException("JCE provider may not be installed. " + e.getMessage());
            ke.initCause(e);
            throw ke;
        }
        try {
            final SecretKeySpec secretKey = new SecretKeySpec(key, "AES");
            final IvParameterSpec param = new IvParameterSpec(cipherState);
            cipher.init(encrypt ? 1 : 2, secretKey, param);
            final byte[] output = cipher.doFinal(data);
            System.arraycopy(output, 0, data, 0, output.length);
        }
        catch (GeneralSecurityException e) {
            final KrbException ke = new KrbException(e.getMessage());
            ke.initCause(e);
            throw ke;
        }
    }
}
