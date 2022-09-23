// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb.crypto.enc.provider;

import javax.crypto.SecretKey;
import java.security.spec.KeySpec;
import java.security.spec.AlgorithmParameterSpec;
import java.security.Key;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;
import javax.crypto.spec.IvParameterSpec;
import java.security.GeneralSecurityException;
import org.apache.kerby.kerberos.kerb.KrbException;
import javax.crypto.Cipher;

public class Des3Provider extends AbstractEncryptProvider
{
    public Des3Provider() {
        super(8, 21, 24);
    }
    
    @Override
    protected void doEncrypt(final byte[] input, final byte[] key, final byte[] cipherState, final boolean encrypt) throws KrbException {
        Cipher cipher = null;
        try {
            cipher = Cipher.getInstance("DESede/CBC/NoPadding");
        }
        catch (GeneralSecurityException e) {
            throw new KrbException("Failed to init cipher", e);
        }
        try {
            final IvParameterSpec params = new IvParameterSpec(cipherState);
            final KeySpec skSpec = new DESedeKeySpec(key, 0);
            final SecretKeyFactory skf = SecretKeyFactory.getInstance("desede");
            final SecretKey secretKey = skf.generateSecret(skSpec);
            cipher.init(encrypt ? 1 : 2, secretKey, params);
            final byte[] output = cipher.doFinal(input);
            System.arraycopy(output, 0, input, 0, output.length);
        }
        catch (GeneralSecurityException e) {
            throw new KrbException("Failed to doEncrypt", e);
        }
    }
}
