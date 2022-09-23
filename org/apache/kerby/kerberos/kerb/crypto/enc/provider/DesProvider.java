// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb.crypto.enc.provider;

import java.security.spec.AlgorithmParameterSpec;
import java.security.Key;
import javax.crypto.spec.SecretKeySpec;
import javax.crypto.spec.IvParameterSpec;
import java.security.GeneralSecurityException;
import org.apache.kerby.kerberos.kerb.KrbException;
import javax.crypto.Cipher;

public class DesProvider extends AbstractEncryptProvider
{
    public DesProvider() {
        super(8, 7, 8);
    }
    
    @Override
    protected void doEncrypt(final byte[] input, final byte[] key, final byte[] cipherState, final boolean encrypt) throws KrbException {
        Cipher cipher = null;
        try {
            cipher = Cipher.getInstance("DES/CBC/NoPadding");
        }
        catch (GeneralSecurityException e) {
            throw new KrbException("Failed to init cipher", e);
        }
        final IvParameterSpec params = new IvParameterSpec(cipherState);
        final SecretKeySpec skSpec = new SecretKeySpec(key, "DES");
        try {
            cipher.init(encrypt ? 1 : 2, skSpec, params);
            final byte[] output = cipher.doFinal(input);
            System.arraycopy(output, 0, input, 0, output.length);
        }
        catch (GeneralSecurityException e2) {
            final KrbException ke = new KrbException(e2.getMessage());
            ke.initCause(e2);
            throw ke;
        }
    }
    
    @Override
    public byte[] cbcMac(final byte[] key, final byte[] cipherState, final byte[] data) throws KrbException {
        Cipher cipher = null;
        try {
            cipher = Cipher.getInstance("DES/CBC/NoPadding");
        }
        catch (GeneralSecurityException e) {
            throw new KrbException("Failed to init cipher", e);
        }
        final IvParameterSpec params = new IvParameterSpec(cipherState);
        final SecretKeySpec skSpec = new SecretKeySpec(key, "DES");
        byte[] output = null;
        try {
            cipher.init(1, skSpec, params);
            for (int i = 0; i < data.length / 8; ++i) {
                output = cipher.doFinal(data, i * 8, 8);
                cipher.init(1, skSpec, new IvParameterSpec(output));
            }
        }
        catch (GeneralSecurityException e2) {
            final KrbException ke = new KrbException(e2.getMessage());
            ke.initCause(e2);
            throw ke;
        }
        return output;
    }
    
    @Override
    public boolean supportCbcMac() {
        return true;
    }
}
